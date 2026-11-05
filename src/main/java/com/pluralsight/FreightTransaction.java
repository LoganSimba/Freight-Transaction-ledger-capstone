package com.pluralsight;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Scanner;

public class FreightTransaction {
    //initialize a final string to hold the csv file
    public static final String FILE_NAME = "transactions.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // initialize an arraylist hand populate it with the csv
        ArrayList<Transaction> transactions = loadTransaction(FILE_NAME);
        homeScreen(transactions, scanner);


    }

    //method to load transactions in the arrayList
    public static ArrayList<Transaction> loadTransaction(String fileName) {
        //initialize arraylist that will hold the transactions
        ArrayList<Transaction> transactions = new ArrayList<>();
        //try catch block to handle the file
        try {//initialize buffered and FileReader to read the file
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line; //initializing the line we will split

            reader.readLine(); //skip the header(first line) line

            while ((line = reader.readLine()) != null) {  // while the line in csv is not null continue iterating
                String[] parts = line.split("\\|"); //split the line into arrays by " | " as a separator

                LocalDate date = LocalDate.parse(parts[0]);  // store the split line according to their datatypes to
                LocalTime time = LocalTime.parse(parts[1]); // so we store them in the array
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);

                //add the parsed to line in the arrayList
                transactions.add(new Transaction(date, time, description, vendor, amount));

            }
            reader.close();//close the reader
            // catch the exception and print a message if found
        } catch (Exception e) {
            System.out.println("could not read the file " + e.getMessage());

        }//handle edge case scenario and check if the arraylist is empty, if so the csv is empty, return a message
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        }
        return transactions; // return the arraylist holding data from the csv
    }

    // added a method to print transactions
    public static void printTransactions(ArrayList<Transaction> transactions) {
        if (transactions.isEmpty()) {// check edge case scenario to see if the arraylist is empty, if so print a message
            System.out.println("No transactions found.");
            return;// return of transactions arraylist is empty
        }
        // sort the transaction by date, then time, and then reverse the sorted order
        transactions.sort(
                Comparator.comparing(Transaction::getDate)
                        .thenComparing(Transaction::getTime)
                        .reversed()
        );

        //print the header with appropriate spaces for UI
        System.out.printf("%-12s %-10s %-45s %-30s %12s%n",
                "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println("-".repeat(120));// make a line of dashes for UI

        // iterate through the list and print the transaction in the desired format, spacing for UI
        for (Transaction transaction : transactions) {
            //add color green if the amount > 0 else make the color Red
            String color = transaction.getAmount() > 0 ? Colors.GREEN : Colors.RED;

            System.out.printf("%-12s %-10s %-50s %-35s " + color + "%,12.2f" + Colors.RESET + "%n",
                    transaction.getDate(),
                    transaction.getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    transaction.getDescription(),
                    transaction.getVendor(),
                    transaction.getAmount());
        }
    }

    // method to add a transaction to the file
    public static void addTransaction(ArrayList<Transaction> transactions, Scanner scanner, boolean isDeposit, String FileName) {
        System.out.println("Adding a transaction...");
        if (isDeposit) {
            System.out.println("Making a deposit transaction");//if boolean is true, we make a deposit transaction
        } else {
            System.out.println("Making a payment transaction");//if boolean is false, we make a payment
        }
        // ask user for the description and store it in a variable
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        //ask user for the vendor and store it in a variable
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine().trim();

        //if boolean is true, print deposit amount, else print payment amount and save the in a vairiable
        double amount;
        while (true) {//loop to handle cases where user inputs invalid (-amount) and loops until user enters valid number
            System.out.println(isDeposit ? "Deposit amount: " : "Payment amount: ");
            String input = scanner.nextLine().trim();//store the user input in a variable

            try {//try catch to handle if the user
                amount = Double.parseDouble(input);
                if (amount <= 0) {// input should be a positive number
                    System.out.println(Colors.RED + "Amount must be greater than 0." + Colors.RESET);
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println(Colors.RED + "Invalid amount. Enter a number, e.g. 150.75" + Colors.RESET);
            }
            //if boolean is false, then its a payment, so we make it a negative number
        }
        if (!isDeposit) {
            amount = -amount;
        }
        LocalDate date = LocalDate.now();//we get the today for now
        LocalTime time = LocalTime.now();//we get the time for now
        //we add the user inputs to the transaction arralylist
        transactions.add(new Transaction(date, time, description, vendor, amount));
        //format in the desired format to match csv file
        String line = String.format("%s|%s|%s|%s|%.2f",
                date,
                time.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                description,
                vendor,
                amount);
        //try catch block to write to the file and handle all issues
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FileName, true));
            writer.write(line);
            writer.newLine();
            //if isDeposit(boolean) true, print deposit saved else payment saved
            System.out.println(isDeposit ? "Deposit saved" : "Payment saved");
            writer.close();
        }//catch the exception
        catch (Exception e) {
            System.out.println("Could not save to file: " + e.getMessage());
        }

    }

    // add method do display screen/menu
    public static void homeScreen(ArrayList<Transaction> transactions, Scanner scanner) {
        boolean running = true;

        while (running) {//while the boolean is running, display menu and prompt user for an input

            System.out.println();
            System.out.println(Colors.CYAN + "================================================");
            System.out.println("              FREIGHT LEDGER HOME SCREEN");
            System.out.println("================================================" + Colors.RESET);

            System.out.println("  Manage your freight finances");
            System.out.println();

            System.out.println(Colors.GREEN + "  D)  Add Deposit" + Colors.RESET);
            System.out.println(Colors.RED + "  P)  Make Payment" + Colors.RESET);
            System.out.println(Colors.CYAN + "  L)  View Ledger" + Colors.RESET);

            System.out.println();
            System.out.println("  --------------------------------------------");
            System.out.println("  X)  Exit");
            System.out.println(Colors.CYAN + "================================================" + Colors.RESET);

            System.out.print("  Select an option (D/P/L/X): ");

            // store the input from the user in a variable
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "D":// if user picks D, call the addTransaction method to add deposit
                    addTransaction(transactions, scanner, true, FILE_NAME);
                    break;
                case "P": // if user picks P, call the addTransaction method to add a payment
                    addTransaction(transactions, scanner, false, FILE_NAME);
                    break;
                case "L": // if user picks L, print a message to let the user know the ledger is being built
                    ledgerScreen(transactions, scanner);
                    break;
                case "X": // if user picks X, print a message and turn boolean to false to stop the loop
                    System.out.println("GoodBye!");
                    running = false;
                    break;
                default:// if user picks an invalid letter, print a message
                    System.out.println("Invalid option. Please choose D, P, L, or X.");
            }

        }

    }

    //method to get deposit or payment
    public static ArrayList<Transaction> getDepositsOrPayments(ArrayList<Transaction> transactions, boolean deposit) {
        ArrayList<Transaction> results = new ArrayList<>();

        //iterate through the transactions list
        for (Transaction transaction : transactions) {
            if (deposit && transaction.getAmount() >= 0) {//if the boolean is true and getAmount > 0, add to results
                results.add(transaction);
            } else if (!deposit && transaction.getAmount() < 0) {//if false and amount < 0, add to result
                results.add(transaction);
            }
        }
        return results;//return the arraylist
    }

    //added a method to display the ledger menu
    public static void ledgerScreen(ArrayList<Transaction> transactions, Scanner scanner) {
        //booleant to help track the while loop
        boolean running = true;

        while (running) {//while the boolean is running, display menu and prompt user for an input

            System.out.println();
            System.out.println(Colors.CYAN + "================================================");
            System.out.println("                    LEDGER");
            System.out.println("================================================" + Colors.RESET);

            System.out.println("  View and analyze your transactions");
            System.out.println();

            System.out.println("  A)  All Transactions");
            System.out.println(Colors.GREEN + "  D)  View Deposits" + Colors.RESET);
            System.out.println(Colors.RED + "  P)  View Payments" + Colors.RESET);
            System.out.println(Colors.CYAN + "  R)  Reports & Insights" + Colors.RESET);

            System.out.println();
            System.out.println("  --------------------------------------------");
            System.out.println("  H)  Return to Home");

            System.out.println(Colors.CYAN + "================================================" + Colors.RESET);
            System.out.print("  Select an option (A/D/P/R/H): ");
            //store user selection in a viriable
            String choice = scanner.nextLine().trim().toUpperCase();


            switch (choice) {
                case "A"://if choice is A, call the printTransaction method
                    printTransactions(transactions);
                    printTotals(transactions);
                    break;
                case "D":// if choice is D, call the printTransaction an
                    ArrayList<Transaction> getDeposits = getDepositsOrPayments(transactions, true);
                    printTransactions(getDeposits);
                    printTotals(getDeposits);
                    break;
                case "P"://if the user choice is P, call the printTransaction and pass it getPayment which stores paymets
                    ArrayList<Transaction> getPayments = getDepositsOrPayments(transactions, false);
                    printTransactions(getPayments);
                    printTotals(getPayments);
                    break;
                case "R"://if user choice is R, call the reports method and print the reports menu
                    reports(transactions, scanner);
                    break;
                case "H"://if user choice is H, stop running loop. loop will return to method that called it which is home
                    running = false;   // ends this loop, back to the home screen
                    break;
                default://if user choice is an invalid input, print a message
                    System.out.println("Invalid option. Please choose A, D, P, R, or H.");
            }
        }

    }

    //method to get the month to date transactions
    public static ArrayList<Transaction> monthToDate(ArrayList<Transaction> transactions) {
        //initalize the arrayList that will store the list of month to date
        ArrayList<Transaction> result = new ArrayList<>();
        LocalDate today = LocalDate.now();// get the date now

        //iterate through the arraylist
        for (Transaction transaction : transactions) {
            //if the transaction year == todays year and the transaction month == todays month, then add it
            if (transaction.getDate().getYear() == today.getYear()
                    && transaction.getDate().getMonth() == today.getMonth()) {
                result.add(transaction); //add it to the result arraylist
            }
        }
        return result; // return the arraylist
    }

    // method to get transactions from previous month
    public static ArrayList<Transaction> previousMonth(ArrayList<Transaction> transactions) {
        //initialize arraylist to store the result
        ArrayList<Transaction> result = new ArrayList();
        LocalDate lastMonth = LocalDate.now().minusMonths(1);//get the previous months

        //iterate through the transactions list and check for the transaction is in the previous month
        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == lastMonth.getYear()
                    && transaction.getDate().getMonth() == lastMonth.getMonth()) {
                result.add(transaction);// if the transaction in previous month, add it to the result arraylist
            }
        }

        return result;// return the arraylist that stores transactions from previous months

    }

    // method to get transactions from the beginning of the year
    public static ArrayList<Transaction> yearToDate(ArrayList<Transaction> transactions) {
        //arraylist to store the result
        ArrayList<Transaction> result = new ArrayList<>();
        int thisYear = LocalDate.now().getYear();//get the current year and store it in a variable

        //iterate through the transaction list, and of the year is the same, add it to the result list
        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == thisYear) {
                result.add(transaction);// adding transactions of current year to the result arraylist
            }
        }
        return result; // return the arraylist
    }

    //method to get all transactions of previous year
    public static ArrayList<Transaction> previousYear(ArrayList<Transaction> transactions) {
        //initializie arraylist that will store all transactions of previous year
        ArrayList<Transaction> result = new ArrayList<>();
        int previousYear = LocalDate.now().getYear() - 1;//get the previous year

        //iterate through the transactions list and check if transactions are of previous year
        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == previousYear) {
                result.add(transaction);//add transactions of the previous year to the arraylist
            }

        }
        return result;//return arraylist holding transactions of previous year
    }

    // method to search by vendor
    public static ArrayList<Transaction> searchByVendor(ArrayList<Transaction> transactions, String vendor) {
        //initialize arrayList to hold the result
        ArrayList<Transaction> result = new ArrayList<>();
        //check edge case scenario,check if the vendor is null return
        if (vendor == null) {
            System.out.println("No match found...");//print a message if no vendor name
            return result;
        }
        //iterate through the arralist, if vendor name is in the transaction, add it to the list
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().toLowerCase().contains(vendor.toLowerCase())) {
                result.add(transaction);//add the transactions to the result
            }
        }
        return result;//return the result

    }


    //method to display the reports screen
    public static void reports(ArrayList<Transaction> transactions, Scanner scanner) {
        //boolean to keep track of the loop
        boolean running = true;
        while (running) {//while the boolean is running, display menu and prompt user for an input

            System.out.println();
            System.out.println(Colors.CYAN + "================================================");
            System.out.println("                    REPORTS");
            System.out.println("================================================" + Colors.RESET);

            System.out.println("  View financial reports and search transactions");
            System.out.println();

            System.out.println(Colors.YELLOW + "  PERIOD REPORTS" + Colors.RESET);
            System.out.println("  --------------------------------------------");
            System.out.println("  1)  Month to Date");
            System.out.println("  2)  Previous Month");
            System.out.println("  3)  Year to Date");
            System.out.println("  4)  Previous Year");

            System.out.println();
            System.out.println(Colors.YELLOW + "  TRANSACTION SEARCH" + Colors.RESET);
            System.out.println("  --------------------------------------------");
            System.out.println("  5)  Search by Vendor");
            System.out.println("  6)  Custom Search");

            System.out.println();
            System.out.println("  --------------------------------------------");
            System.out.println("  0)  Return to Ledger");

            System.out.println(Colors.CYAN + "================================================" + Colors.RESET);
            System.out.print("  Select an option (0-6): ");

            //store the user input in a variable
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1"://if user choice is one, pass monthToDate to the printTransactions
                    ArrayList<Transaction> monthToCurrentDate = monthToDate(transactions);
                    printTransactions(monthToCurrentDate);
                    printTotals(monthToCurrentDate);//print totals foe the current month
                    break;


                case "2"://if user choice is 2, print previouse month transactions
                    ArrayList<Transaction> previousMonthTransactions = previousMonth(transactions);
                    printTransactions(previousMonthTransactions);
                    printTotals(previousMonthTransactions);
                    break;


                case "3":// if user choice is 3, print year to date transactions
                    ArrayList<Transaction> yearToCurrentDate = yearToDate(transactions);
                    printTransactions(yearToCurrentDate);
                    printTotals(yearToCurrentDate);
                    break;

                case "4"://if user choice is 4, print previous year transactions
                    ArrayList<Transaction> lastYear = previousYear(transactions);
                    printTransactions(lastYear);
                    printTotals(lastYear);//print totals of previous year
                    break;

                case "5"://if user choice is 5, print a massage to let them know
                    System.out.print("Vendor Name: ");
                    String vendor = scanner.nextLine().trim();
                    //store the search by vendor returned list in vendorSearch
                    ArrayList<Transaction> vendorNameSearch = searchByVendor(transactions, vendor);
                    printTransactions(vendorNameSearch);//print the vendorSearch which stored return values
                    printTotals(vendorNameSearch);//print totals by vendor name
                    break;

                case "6"://if user choice is 6, exit! turn boolean false to exit

                    //store it as LocalDate type if the user typed null else parse the string they enter
                    LocalDate startSearch = null;
                    while (true) {//continue looping until user enters desired format
                        System.out.println("Enter start date(yyyy-MM-dd) (Press Enter to skip)");
                        String start = scanner.nextLine().trim();

                        if (start.isEmpty()) {//try catch block to handle cases where user inp
                            break;
                        }
                        try {
                            startSearch = LocalDate.parse(start);
                            break;
                        } catch (DateTimeParseException e) {
                            System.out.println(Colors.RED + "Invalid date, Use the format yyyy-MM-dd, e.g 2026-10-30 " +
                                    Colors.RESET);
                        }
                    }


                    LocalDate endSearch = null;
                    while (true) {//continue iterating until user enter desired format
                        System.out.println("Enter end date(yyy-MM-dd) (Press Enter to skip)");
                        String end = scanner.nextLine().trim();
                        if (end.isEmpty()) {//try catch block to check for edge case scebnarios where user enter invalid date
                            break;
                        }
                        try {
                            endSearch = LocalDate.parse(end);
                            break;
                        } catch (DateTimeParseException e) {//catch block to catch the exception
                            System.out.println(Colors.RED + "Invalid date, Use the format yyyy-MM-dd, e.g 2026-10-30"
                                    + Colors.RESET);//print a message in red color
                        }
                    }

                    System.out.println("Enter description (Press Enter to skip)");
                    String description = scanner.nextLine().trim();//store in the description variable
                    //if user choice is empty, set string to null, else store it in a variable
                    String descriptionSearch = description.isEmpty() ? null : description;

                    System.out.println("Enter vendor name (Press Enter to skip)");
                    String v = scanner.nextLine().trim();
                    // if is empty, set to null to skip the filter
                    String vendorSearch = v.isEmpty() ? null : v;

                    System.out.println("Enter amount (Press Enter to skip)");
                    String amount = scanner.nextLine().trim();
                    //if user press enter, set it to null for wrapper class,else parse the amount to wrapper class Double
                    Double amountSearch = amount.isEmpty() ? null : Double.parseDouble(amount);

                    //store the customSearch method in an arrayList to not call it again or have the program filter again
                    ArrayList<Transaction> holdsCustomSearch = customSearch(transactions, startSearch, endSearch,
                            description, vendorSearch, amountSearch);
                    //print transaction and pass it the list that stores the customSearch
                    printTransactions(holdsCustomSearch);
                    printTotals(holdsCustomSearch);//print totals of the customSearch
                    break;


                case "0":// if the use choice is 0, go back
                    running = false;
                    break;

                default:// if user inputs an invalid option,
                    System.out.println("Invalid option, try again");
            }


        }

    }

    //method to add custom search
    public static ArrayList<Transaction> customSearch(ArrayList<Transaction> transactions, LocalDate startDateSearch,
                                                      LocalDate endDateSearch, String descriptionSearch, String vendorSearch,
                                                      Double amountSearch) {
        //arraylist to store the result
        ArrayList<Transaction> result = new ArrayList<>();
        //iterate through the arraylist
        for (Transaction transaction : transactions) {
            boolean matches = true;
            //filter all the dates before the startdateSearch, turn boolean to false
            if (startDateSearch != null && transaction.getDate().isBefore(startDateSearch)) {
                matches = false;
            }
            //if endDatesearch is not empty, and all the dates after the end date
            if (endDateSearch != null && transaction.getDate().isAfter(endDateSearch)) {
                matches = false;
            }// if description is not null and teh description searchcontains words in description of transactions
            if (descriptionSearch != null && !transaction.getDescription().toLowerCase().contains(descriptionSearch
                    .toLowerCase())) {
                matches = false;
            }//if vendorSearch not null and the transaction vendor does not contain the vendorsearch rutn boolean false
            if (vendorSearch != null && !transaction.getVendor().toLowerCase().contains(vendorSearch.toLowerCase())) {
                matches = false;
            }//if amount isnt null and it is not equal the amount in our transaction turn false
            if (amountSearch != null && transaction.getAmount() != amountSearch) {
                matches = false;
            }
            if (matches) {// if boolean was true the above add the transaction to the result arraylist
                result.add(transaction);
            }
        }
        return result;//return result

    }

    //method to print the totals
    public static void printTotals(ArrayList<Transaction> transactions) {
        if (transactions.isEmpty()) {//edge case scenario where transactions are empty, return
            return;
        }
        double deposits = 0;//initialize deposits to increment count
        double payments = 0;//initialize payments to increment payments

        //iterate through the list, and if transaction amount > 0 increment count, else increment payments
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                deposits += transaction.getAmount();
            } else {
                payments += transaction.getAmount();
            }
        }//calculat the total/net
        double net = deposits + payments;
        String netColor = net >= 0 ? Colors.GREEN : Colors.RED;//ternary statemtns to turn color green or bad

        System.out.println("-".repeat(123));//make a line with repear method
        if (deposits != 0) {//if there is deposits, display their sum
            System.out.printf("%-110s %s %,12.2f%s%n", "Total deposits:", Colors.GREEN, deposits, Colors.RESET);
        }
        if (payments != 0) {//if there is a payments, display their total payments
            System.out.printf("%-110s %s %,12.2f%s%n", "Total payments:", Colors.RED, payments, Colors.RESET);
        }
        if (deposits != 0 && payments != 0) {//if there is deposits and payments, display their difference
            System.out.printf("%-110s %s %,12.2f%s%n", "Net:", netColor, net, Colors.RESET);//display net in colors
        }

    }


}
