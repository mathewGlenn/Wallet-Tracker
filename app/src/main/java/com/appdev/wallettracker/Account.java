package com.appdev.wallettracker;

public class Account {
    private String name, balance, color;

    public Account(){}

    public Account(String name, String balance, String color){
        this.name = name;
        this.balance = balance;
        this.color = color;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
