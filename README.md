Sure, here's a README file for your Scala project:

```markdown
# Retail Store Discount Calculator

This Scala project calculates discounts for transactions in a retail store based on predefined discount rules and then stores the results in an Oracle database and CSV file. It also logs the discount calculation process.

## Table of Contents

- [Project Structure](#project-structure)
- [Usage](#usage)
- [Discount Rules](#discount-rules)
- [Input Data](#input-data)
- [Output](#output)
- [Logging](#logging)
- [Database Setup](#database-setup)

## Project Structure

The project structure is as follows:

```
├── src
│   └── main
│       └── scala
│           └── RetailStoreDiscount.scala
└── src
    └── main
        └── resources
            ├── TRX1000.csv
            └── output.csv
            └── rules_engine.log
```

## Usage

To run the project:

1. Clone this repository:

   ```bash
   git clone https://github.com/YousefSaber390/RetailStoreDiscount.git
   ```

2. Open the project in an IDE supporting Scala, such as IntelliJ IDEA.

3. Run the `RetailStoreDiscount.scala` file.

## Discount Rules

The discount calculation is based on the following rules:

1. **Discount based on remaining days until expiry**: 
   - If the expiry date is within 30 days of the transaction date, a discount is applied. The discount decreases as the expiry date approaches.

2. **Discount for purchasing cheese & wine**: 
   - If the product name contains "cheese", a 10% discount is applied.
   - If the product name contains "wine", a 5% discount is applied.

3. **Special discount on a specific date**: 
   - On March 23rd, a 50% discount is applied.

4. **Quantity-based discounts**: 
   - If the quantity purchased is between 6 and 9, a 5% discount is applied.
   - If the quantity purchased is between 10 and 14, a 7% discount is applied.
   - If the quantity purchased is 15 or more, a 10% discount is applied.

5. **Discount based on purchase channel**: 
   - If the purchase is made through the app, a discount is applied based on the quantity purchased:
     - Every 5 items purchased through the app will get a 1% discount.

6. **Discount for using Visa**: 
   - If the payment method is Visa, a 5% discount is applied.

## Input Data

The input data is provided in a CSV file named `TRX1000.csv`. Each row represents a transaction with the following columns:

1. `timestamp`: Transaction timestamp (format: "yyyy-MM-dd'T'HH:mm:ss").
2. `productName`: Name of the product.
3. `expiryDate`: Expiry date of the product (format: "yyyy-MM-dd'T'HH:mm:ss").
4. `quantity`: Quantity purchased.
5. `unitPrice`: Unit price of the product.
6. `channel`: Purchase channel (e.g., "app", "website", "store").
7. `paymentMethod`: Payment method (e.g., "Visa", "MasterCard", "cash").

## Output

The project generates two types of output:

1. **Database Table**: 
   - The calculated discounts along with transaction details are stored in an Oracle database table named `OUTPUT_TABLE`.

2. **CSV File**: 
   - The calculated discounts along with transaction details are stored in a CSV file named `output.csv`.

## Logging

The discount calculation process is logged, and the log messages are stored in an Oracle database table named `LOG_TABLE`. Additionally, a log file named `rules_engine.log` is created.

## Database Setup

Make sure you have an Oracle database set up with the following details:

- URL
- Username
- Password

Create the following tables in your Oracle database:

```sql
CREATE TABLE HR.OUTPUT_TABLE (
    TIMESTAMP VARCHAR2(100),
    PRODUCT_NAME VARCHAR2(100),
    EXPIRY_DATE VARCHAR2(100),
    QUANTITY NUMBER,
    UNIT_PRICE NUMBER,
    CHANNEL VARCHAR2(100),
    PAYMENT_METHOD VARCHAR2(100),
    DISCOUNT NUMBER,
    TOTAL_PRICE_AFTER_DISCOUNT NUMBER
);

CREATE TABLE HR.LOG_TABLE (
    LOG_MESSAGE VARCHAR2(1000)
);
```

```

This README file should provide a comprehensive understanding of your project and how to run it. If you have any questions or need further assistance, feel free to ask!
