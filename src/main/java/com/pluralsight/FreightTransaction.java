package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
            System.out.printf("%-12s %-10s %-45s %-30s %,12.2f%n",
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
        System.out.println(isDeposit ? "Deposit amount: " : "Payment amount: ");
        double amount = Double.parseDouble(scanner.nextLine().trim());
        //if boolean is false, then its a payment, so we make it a negative number
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
        boolean running = true;//initialize boolean conditional to run the loop

        while (running) { //while the boolean is true, print menu to prompt the user for an input
            System.out.println();
            System.out.println("===== FREIGHT TRANSACTION LEDGER =====");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("Choose an option: ");

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
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:// if user picks an invalid letter, print a message
                    System.out.println("Invalid option. Please choose D, P, L, or X.");
            }

        }

    }

    //method to add deposits
    public static ArrayList<Transaction> getDeposits(ArrayList<Transaction> transactions) {
        // arrayList to store the transactions with deposits
        ArrayList<Transaction> deposits = new ArrayList<>();
        // iterate through the transactions arraylist and if amount > 0, add the transaction to deposit list
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                deposits.add(transaction);//add transaction to the deposit list
            }
        }
        return deposits; //return the deposit list
    }

    // method to get all payments
    public static ArrayList<Transaction> getPayments(ArrayList<Transaction> transactions) {
        // initialize arraylist to store all payments
        ArrayList<Transaction> payments = new ArrayList<>();
        //iterate through the loop and check each transaction
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) { //check if the transaction amount is negative, if so
                payments.add(transaction);// add the negative/payment to the payments arraylist
            }
        }
        return payments; //return the payments arraylist
    }

    //added a method to display the ledger menu
    public static void ledgerScreen(ArrayList<Transaction> transactions, Scanner scanner) {
        //booleant to help track the while loop
        boolean running = true;

        while (running) {//while boolean is true, ask display Menu and prompt user to pick an option
            System.out.println();
            System.out.println("===== LEDGER =====");
            System.out.println("A) All Transactions");
            System.out.println("D) View Deposits");
            System.out.println("P) View Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Choose an option: ");
            // store the user input in a variable
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "A"://if choice is A, call the printTransaction method
                    printTransactions(transactions);
                    break;
                case "D":// if choice is D, call the printTransaction and pass it a getDeposits to print it
                    printTransactions(getDeposits(transactions));
                    break;
                case "P"://if the user choice is P, call the printTransaction and pass it getDeposit method to print
                    printTransactions(getPayments(transactions));
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

        while (running) { //while the app is running, display menu and prompt user for an input
            System.out.println();
            System.out.println("===== REPORTS =====");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");
            System.out.print("Choose an option: ");

            //store the user input in a variable
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1"://if user choice is one, pass monthToDate to the printTransactions
                    printTransactions(monthToDate(transactions));
                    break;


                case "2"://if user choice is 2, print previouse month transactions
                    printTransactions(previousMonth(transactions));
                    break;


                case "3":// if user choice is 3, print year to date transactions
                    printTransactions(yearToDate(transactions));
                    break;

                case "4"://if user choice is 4, print previous year transactions
                    printTransactions(previousYear(transactions));
                    break;

                case "5"://if user choice is 5, print a massage to let the user know the feature is coming
                    System.out.print("Vendor Name: ");
                    String vendor = scanner.nextLine().trim();
                    //print transactions by the vendor name
                    printTransactions(searchByVendor(transactions, vendor));
                    break;

                case "6"://if user choice is 6, exit! turn boolean false to exit
                    running = false;
                    break;

                default:// if user inputs an invalid option,
                    System.out.println("Invalid option, try again");
            }


        }

    }


}
