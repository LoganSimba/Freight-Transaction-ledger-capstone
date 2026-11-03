package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class FreightTransaction {
    //initialize a final string to hold the csv file
    public static final String FILE_NAME = "transactions.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // initialize an arraylist hand populate it with the csv
        ArrayList<Transaction> transactions = loadTransaction(FILE_NAME);
        printTransactions(previousMonth(transactions));


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
                    System.out.println("ledger screen/Menu is coming");
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
                case "R"://ledger coming soon
                    System.out.println("Reports coming soon...");
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


}
