package com.appdev.wallettracker;

public class Transaction {
    private String account, action, note, type;
    private Float amount;
    private Long timestamp;

    public Transaction() {
    }

    public Transaction(String account, String action, String note, Float amount, Long timestamp, String type) {
        this.account = account;
        this.action = action;
        this.note = note;
        this.amount = amount;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
