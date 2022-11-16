package com.appdev.wallettracker;

public class Account {
    private String name, color;
    private Float balance;

    public Account(){}

    public Account(String name, Float balance, String color){
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

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
