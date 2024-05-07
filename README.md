
# Retail Store Discount

This Scala application calculates discounts for retail store transactions based on various rules.

## Features

- **Discount Rules**: The application applies discounts based on the following rules:
  1. Remaining days until expiry: If the product's expiry date is within 30 days from the transaction date, a discount is applied.
  2. Product-based discounts:
     - 10% discount for purchasing cheese.
     - 5% discount for purchasing wine.
  3. Special date discount: On March 23rd, a 50% discount is applied.
  4. Quantity-based discounts:
     - 5% discount for purchasing 6 to 9 items.
     - 7% discount for purchasing 10 to 14 items.
     - 10% discount for purchasing 15 or more items.
  5. Channel-based discounts: If the purchase is made through the app, a discount is applied based on the quantity purchased.
  6. Payment method-based discounts: If payment is made using Visa, a 5% discount is applied.

- **Input**: The application reads transaction data from a CSV file.
- **Output**: The application generates an output CSV file containing details of each transaction along with the applied discount and total price after discount.
- **Logging**: The application logs details of each transaction along with the applied discount and total price after discount.

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/YousefSaber390/RetailStoreDiscount.git
   ```

2. Navigate to the project directory:

   ```bash
   cd RetailStoreDiscount
   ```

3. Compile the Scala code:

   ```bash
   scalac RetailStoreDiscount.scala
   ```

## Usage

1. Place your transaction data in a CSV file following the format specified below:

   ```csv
   timestamp,productName,expiryDate,quantity,unitPrice,channel,paymentMethod
   ```

2. Execute the Scala code with the following command:

   ```bash
   scala RetailStoreDiscount
   ```

3. After execution, the application will generate two files:
   - `output.csv`: Contains details of each transaction along with the applied discount and total price after discount.
   - `rules_engine.log`: Contains details of each transaction logged along with the applied discount and total price after discount.

## Input Format

The input CSV file should follow this format:

```
timestamp,productName,expiryDate,quantity,unitPrice,channel,paymentMethod
```

- `timestamp`: Transaction timestamp in the format `yyyy-MM-ddTHH:mm:ss`.
- `productName`: Name of the product.
- `expiryDate`: Expiry date of the product in the format `yyyy-MM-ddTHH:mm:ss`.
- `quantity`: Quantity of the product purchased.
- `unitPrice`: Unit price of the product.
- `channel`: Purchase channel (e.g., "store", "app").
- `paymentMethod`: Payment method (e.g., "cash", "visa").

## Output Format

The output CSV file (`output.csv`) will have the following format:

```
timestamp,product_name,expiry_date,quantity,unit_price,channel,payment_method,discount,total_price_after_discount
```

- `timestamp`: Transaction timestamp.
- `product_name`: Name of the product.
- `expiry_date`: Expiry date of the product.
- `quantity`: Quantity of the product purchased.
- `unit_price`: Unit price of the product.
- `channel`: Purchase channel.
- `payment_method`: Payment method.
- `discount`: Applied discount.
- `total_price_after_discount`: Total price after discount.

