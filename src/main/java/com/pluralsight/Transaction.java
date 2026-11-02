package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {

    //attributes of the class
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;

    // constructor for Transaction with attributes
    public Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }
    //get date method
    public LocalDate getDate() {
        return date;
    }
    //setDate method
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    //tostring method to display the transaction
    public String toString() {
        return date + "|" + time + "|" + description + "|" + vendor + "|" + amount;
    }
}


