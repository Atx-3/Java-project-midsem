# Expense Tracker (Java Swing)

A modern desktop expense tracker built with pure Java Swing.

This project helps you record daily spending, view quick totals, filter history, and export data to CSV, all in a single lightweight application with no external dependencies.

## Features

- Add expenses with:
  - amount
  - description
  - category
  - date
- Real-time summary cards:
  - Today
  - This Month
  - This Year
  - All Time
- Filter expense history by:
  - category
  - month
- Delete a selected row with confirmation.
- Clear all data with a safety warning.
- Auto-save data locally to `expenses.dat`.
- Export all expenses to CSV (`expenses_export.csv`).
- Keyboard-friendly input flow:
  - `Enter` in amount field moves focus
  - `Enter` in description adds an expense

## Tech Stack

- Java (Swing / AWT)
- File persistence with Java serialization (`ObjectInputStream` / `ObjectOutputStream`)
- No external libraries

## Project Structure

```text
.
|-- ExpenseTracker.java   # Main application (UI + logic + persistence)
|-- expenses.dat          # Auto-generated local data store
|-- README.md
```

## Requirements

- JDK 8 or higher
- Windows, macOS, or Linux with Java installed

Check your installation:

```bash
javac -version
java -version
```

If `javac` is missing, install a JDK (not only JRE) and add it to your system `PATH`.

## Run Locally

From the project root:

```bash
javac -encoding UTF-8 ExpenseTracker.java
java ExpenseTracker
```

## Data and Export

- App data is stored in `expenses.dat` in the same directory.
- CSV export is available from the UI (`Export CSV` button).
- CSV format:

```csv
Date,Description,Category,Amount
2026-03-19,Lunch,Food,250.00
```

## Usage Flow

1. Enter amount and description.
2. Select category and date.
3. Click `Add`.
4. Use filters to inspect spending.
5. Export CSV when needed.

## Troubleshooting

### `javac` not recognized

Install a JDK and ensure both `java` and `javac` are available from terminal.

### Garbled symbols or currency characters

Compile with UTF-8:

```bash
javac -encoding UTF-8 ExpenseTracker.java
```

### Data file reset

To start fresh, close the app and remove `expenses.dat`.

## Future Improvements

- Edit existing expense entries
- Category management (custom categories)
- Charts and analytics dashboard
- Search and advanced filters
- Import from CSV

## Author

Built for learning and practical personal finance tracking using Java desktop UI.
.

