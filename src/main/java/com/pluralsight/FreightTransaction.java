package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class FreightTransaction {
    public static final String FILE_NAME = "transactions.csv";

    public static void main(String[] args) {
        // initialize an arraylist hand populate it with the csv
        ArrayList<Transaction> transactions = loadTransaction(FILE_NAME);
        System.out.println(transactions.toString());//test for loading the csv

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

}
