package com.model;
import java.time.LocalDateTime;
 
public class Transaction {
    private int transactionId;
    private double amount;
    private int categoryId;
    private LocalDateTime timestamp;
    private String note;
    private int cycleId;
 
    public Transaction() {}
 
    public Transaction(int transactionId, double amount, int categoryId,
                       LocalDateTime timestamp, String note, int cycleId) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.categoryId = categoryId;
        this.timestamp = timestamp;
        this.note = note;
        this.cycleId = cycleId;
    }
 
    public void logExpense(double amount, int catId, String note) {
        this.amount = amount;
        this.categoryId = catId;
        this.note = note;
        this.timestamp = LocalDateTime.now();
    }
 
    public void editEntry(double amount, int catId, String note) {
        this.amount = amount;
        this.categoryId = catId;
        this.note = note;
    }
 
    public void deleteEntry() {
       // deletion handled by Controller ya janaaa
    }
 
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }
}
 