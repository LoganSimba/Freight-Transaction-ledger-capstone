# Freight Transaction Ledger

A Java command-line application for a freight and logistics company to track money coming in (customer invoices) and going out (fuel, repairs, payroll, rent). All transactions are saved to and loaded from a CSV file, so data persists between sessions.

## Features

- **Add deposits and payments**: saved instantly to `transactions.csv` with the current date and time
- **Ledger view**: all transactions, deposits only, or payments only, always newest first
- **Reports**: Month to Date, Previous Month, Year to Date, Previous Year, and Search by Vendor
- **Custom Search**: filter by any combination of start date, end date, description, vendor, and amount
- **Totals**: deposits, payments, and net shown under every list
- **Color-coded output**: green for money in, red for money out

## How to Run

1. Clone the repository:
   ```
   git clone https://github.com/<your-username>/freight-transaction-ledger.git
   ```
2. Open the project in IntelliJ IDEA.
3. Make sure `transactions.csv` is in the project root folder.
4. Run `FreightTransaction.java`.

Requires Java 17 or later.

## Data Format

Each transaction is one line in `transactions.csv`, separated by pipes:

```
date|time|description|vendor|amount
2026-10-28|08:15:00|Invoice 1001 paid - Dallas to Houston load|Walmart|2400.00
2026-10-27|06:30:00|Diesel fuel|Love's Travel Stop|-650.75
```

Positive amounts are deposits; negative amounts are payments.

## Project Structure

| Class | Purpose |
|---|---|
| `FreightTransaction` | Main class: menus, file reading and writing, reports, and search |
| `Transaction` | Holds one transaction's date, time, description, vendor, and amount |
| `Colors` | ANSI color constants for terminal output |

## Interesting Code

Custom Search checks every filter the user filled in and skips the ones left blank. A transaction is kept only if it passes all of them:

```java
boolean matches = true;
if (startSearch != null && transaction.getDate().isBefore(startSearch)) {
    matches = false;
}
if (vendorSearch != null && !transaction.getVendor().toLowerCase().contains(vendorSearch.toLowerCase())) {
    matches = false;
}
// ...same pattern for end date, description, and amount
if (matches) {
    result.add(transaction);
}
```
