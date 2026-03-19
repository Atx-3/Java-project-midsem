# Expense Tracker

This is my Java Swing midsem project for tracking daily expenses.

## Features

- Add expense with amount, description, category, and date
- View totals for today, this month, this year, and all time
- Filter records by category and month
- Delete selected expense
- Clear all data
- Export data to CSV
- Auto-save data in `expenses.dat`

## Project Files

- `ExpenseTracker.java`
- `expenses.dat`
- `README.md`

## Requirements

- JDK 8 or above

Check setup:

```bash
javac -version
java -version
```

## Run

```bash
javac -encoding UTF-8 ExpenseTracker.java
java ExpenseTracker
```

## Notes

- Data is saved in `expenses.dat`
- Export creates `expenses_export.csv`
- If symbols look broken, compile with UTF-8 as shown above

