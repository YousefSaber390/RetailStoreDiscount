import java.io.{File, PrintWriter}
import java.sql.{Connection, DriverManager}
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, Period}

object RetailStoreDiscount {

  // Define a case class to represent a transaction
  case class Transaction(timestamp: String, productName: String, expiryDate: String, quantity: Int, unitPrice: Double, channel: String, paymentMethod: String) {
    // Define date formatters for parsing dates
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    // Convert expiryDate and timestamp to LocalDate objects
    val expiryLocalDate: LocalDate = LocalDate.parse(expiryDate.split("T")(0), dateFormatter)
    val transactionLocalDate: LocalDate = LocalDate.parse(timestamp.split("T")(0), dateFormatter)
  }

  // Define a case class to represent discount rules
  case class DiscountRule(qualifyingRule: Transaction => Boolean, calculationRule: Transaction => Double)

  // Define a list of discount rules
  val discountRules: List[DiscountRule] = List(
    // Rule 1: Discount based on remaining days until expiry
    DiscountRule(transaction => {
      val expiry = transaction.expiryLocalDate
      val transactionDate = transaction.transactionLocalDate
      Period.between(transactionDate, expiry).getDays < 30
    },
      transaction => {
        val expiry = transaction.expiryLocalDate
        val transactionDate = transaction.transactionLocalDate
        val daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(transactionDate, expiry).toInt

        if (daysRemaining >= 30) 0.0
        else if (daysRemaining >= 1) {0.30 - (daysRemaining * 0.01)}
        else 0.0
      }),

    // Rule 2: Discount for purchasing cheese & wine
    DiscountRule(transaction => transaction.productName.toLowerCase.contains("cheese"), _ => 0.10),
    DiscountRule(transaction => transaction.productName.toLowerCase.contains("wine"), _ => 0.05),


    // Rule 3: Special discount on a specific date
    DiscountRule(transaction => {
      val date = transaction.transactionLocalDate
      date.getMonthValue == 3 && date.getDayOfMonth == 23
    },
      _ => 0.50),

    // Rule 4: Quantity-based discounts
    DiscountRule(transaction => transaction.quantity >= 6 && transaction.quantity <= 9, _ => 0.05),
    DiscountRule(transaction => transaction.quantity >= 10 && transaction.quantity <= 14, _ => 0.07),
    DiscountRule(transaction => transaction.quantity >= 15, _ => 0.10),

    // Rule 5: Discount based on purchase channel
    DiscountRule(transaction => transaction.channel.toLowerCase == "app", transaction => {
      val roundedQuantity =( Math.ceil(transaction.quantity.toDouble / 5) * 5 )/100
      roundedQuantity
    }),

    // Rule 6: Discount for using Visa
    DiscountRule(transaction => transaction.paymentMethod.toLowerCase == "visa", _ => 0.05)
  )

  // Function to calculate the total discount for a transaction
  def calculateDiscount(transaction: Transaction): Double = {
    // Filter discount rules that apply to the transaction
    val qualifiedRules = discountRules.filter(_.qualifyingRule(transaction))

    // Get discounts applicable to the transaction and sort them in descending order
    val discounts = qualifiedRules.map(_.calculationRule(transaction)).filter(_>0).sorted.reverse

    // Apply a maximum of two discounts
    if (discounts.length >= 2) {
      (discounts.take(2).sum / 2.0)
    } else {
      discounts.headOption.getOrElse(0.0)
    }
  }

  def main(args: Array[String]): Unit = {
    // Input file path
    val inputFilePath = "D:/1/18.Scala/Retail_store_discount/src/main/resources/TRX1000.csv"
    // Output file path
    val outputFilePath = "D:/1/18.Scala/Retail_store_discount/src/main/resources/output.csv"
    // Log file path
    val logFilePath = "D:/1/18.Scala/Retail_store_discount/src/main/resources/rules_engine.log"

    // Read transactions from input file
    val transactions = scala.io.Source.fromFile(inputFilePath).getLines().drop(1).map(line => {
      val cols = line.split(",")
      Transaction(cols(0), cols(1), cols(2), cols(3).toInt, cols(4).toDouble, cols(5), cols(6))
    }).toList

    // Calculate discounts and generate output
    val output = transactions.map(transaction => {
      val discount = calculateDiscount(transaction)
      val totalPriceAfterDiscount = transaction.quantity * transaction.unitPrice * (1 - discount)
      (transaction.timestamp, transaction.productName, transaction.expiryDate, transaction.quantity, transaction.unitPrice, transaction.channel, transaction.paymentMethod, discount, totalPriceAfterDiscount)
    })

    // Generate log messages
    val log = output.map(line => {
      val timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
      s"$timestamp,INFO,$line"
    })

    // JDBC Connection details
    val url = "jdbc:oracle:thin:@localhost:1521:xe"
    val driver = "oracle.jdbc.OracleDriver"
    val username = "HR"
    val password = "1"

    // Output table name
    val outputTableName = "HR.OUTPUT_TABLE"
    // Log table name
    val logTableName = "HR.LOG_TABLE"

    // Write output to Oracle database
    writeToOracle(output, url, driver, username, password, outputTableName)

    // Write log messages to Oracle database
    writeToOracleLog(log, url, driver, username, password, logTableName)

    // Write output to CSV file
    writeOutputToCSV(output, outputFilePath)

    // Write log messages to file
    writeLogToFile(log, logFilePath)
  }

  def writeToOracle(output: List[(String, String, String, Int, Double, String, String, Double, Double)], url: String, driver: String, username: String, password: String, tableName: String): Unit = {
    var connection: Connection = null

    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()

      // Delete old records from the table
      statement.executeUpdate(s"DELETE FROM $tableName")

      // Insert new records into the table
      output.foreach { case (timestamp, productName, expiryDate, quantity, unitPrice, channel, paymentMethod, discount, totalPriceAfterDiscount) =>
        val sql = s"INSERT INTO $tableName (timestamp, product_name, expiry_date, quantity, unit_price, channel, payment_method, discount, total_price_after_discount) VALUES ('$timestamp', '$productName', '$expiryDate', $quantity, $unitPrice, '$channel', '$paymentMethod', $discount, $totalPriceAfterDiscount)"
        statement.executeUpdate(sql)
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      if (connection != null) connection.close()
    }
  }

  def writeToOracleLog(log: List[String], url: String, driver: String, username: String, password: String, tableName: String): Unit = {
    var connection: Connection = null

    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()

      // Delete old records from the table
      statement.executeUpdate(s"DELETE FROM $tableName")

      // Insert new records into the table
      log.foreach { logMessage =>
        val sql = s"INSERT INTO $tableName (log_message) VALUES ('$logMessage')"
        statement.executeUpdate(sql)
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      if (connection != null) connection.close()
    }
  }

  def writeOutputToCSV(output: List[(String, String, String, Int, Double, String, String, Double, Double)], filePath: String): Unit = {
    val writer = new PrintWriter(new File(filePath))
    try {
      writer.println("timestamp,product_name,expiry_date,quantity,unit_price,channel,payment_method,discount,total_price_after_discount")
      output.foreach { case (timestamp, productName, expiryDate, quantity, unitPrice, channel, paymentMethod, discount, totalPriceAfterDiscount) =>
        writer.println(s"$timestamp,$productName,$expiryDate,$quantity,$unitPrice,$channel,$paymentMethod,$discount,$totalPriceAfterDiscount")
      }
    } finally {
      writer.close()
    }
  }

  def writeLogToFile(log: List[String], filePath: String): Unit = {
    val logWriter = new PrintWriter(new File(filePath))
    try {
      log.foreach(logWriter.println)
    } finally {
      logWriter.close()
    }
  }
}
