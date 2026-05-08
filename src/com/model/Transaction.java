package com.model;
import java.time.LocalDateTime;
 
/**
 * Represents a single expense transaction in the Masroofy budgeting system.
 * Stores all details of a recorded expense including amount, category, timestamp, and note.
 */


public class Transaction {
    private int transactionId;
    private double amount;
    private int categoryId;
    private LocalDateTime timestamp;
    private String note;
    private int cycleId;
    
    /**
    * Default constructor for Transaction.
    */
    public Transaction() {}
 
    /**
    * Creates a new Transaction with all fields specified.
    * @param transactionId unique identifier for the transaction
    * @param amount the expense amount in EGP
    * @param categoryId the category this expense belongs to
    * @param timestamp the date and time the expense was recorded
    * @param note optional note about the expense
    * @param cycleId the budget cycle this transaction belongs to
     */

    public Transaction(int transactionId, double amount, int categoryId,
                       LocalDateTime timestamp, String note, int cycleId) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.categoryId = categoryId;
        this.timestamp = timestamp;
        this.note = note;
        this.cycleId = cycleId;
    }
    
    /**
    * Records a new expense by setting amount, category, note and current timestamp.
    * @param amount the expense amount in EGP
    * @param catId the category ID for this expense
    * @param note optional description of the expense
    */

    public void logExpense(double amount, int catId, String note) {
        this.amount = amount;
        this.categoryId = catId;
        this.note = note;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Updates an existing transaction with new values.
    * @param amount the new expense amount in EGP
    * @param catId the new category ID
    * @param note the new note
    */

    public void editEntry(double amount, int catId, String note) {
        this.amount = amount;
        this.categoryId = catId;
        this.note = note;
    }
    
    /**
    * Marks this transaction for deletion.
    * Actual deletion is handled by HistoryController.
    */

    public void deleteEntry() {
       // deletion handled by Controller ya janaaa
    }
    /** @return the transaction ID */
    public int getTransactionId() { return transactionId; }
    /** @param transactionId the transaction ID to set */
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    
    /** @return the expense amount */
    public double getAmount() { return amount; }
    /** @param amount the expense amount to set */
    public void setAmount(double amount) { this.amount = amount; }
        /** @return the category ID */
    public int getCategoryId() { return categoryId; }
        /** @param categoryId the category ID to set */
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    /** @return the timestamp */
    public LocalDateTime getTimestamp() { return timestamp; }
    /** @param timestamp the timestamp to set */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    /** @return the note attached to this transaction */
    public String getNote() { return note; }
    /** @param note the note to set */
    public void setNote(String note) { this.note = note; }
    /** @return the cycle ID this transaction belongs to */
    public int getCycleId() { return cycleId; }
    /** @param cycleId the cycle ID to set */
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }
}
 