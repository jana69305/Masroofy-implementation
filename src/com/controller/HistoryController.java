package com.controller;

import com.model.BudgetCycle;
import com.model.Transaction;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages transaction history and budget cycle data for the Masroofy application.
 * Handles reading, writing, filtering, and modifying transactions stored in files,
 * and coordinates with {@link LimitEngine} and {@link AlertNotifier} after changes.
 */

public class HistoryController {

    /** Path to the file where all transactions are stored. */

    private static final String TRANSACTIONS_FILE = "data/transactions.txt";
        /** Path to the file where the current budget cycle is stored. */
    private static final String CYCLE_FILE        = "data/cycle.txt";
    /** Calculates and updates the safe daily spending limit. */
    private LimitEngine   limitEngine;
    /** Sends budget alerts when spending thresholds are reached. */
    private AlertNotifier alertNotifier;
 /**
     * Constructs a HistoryController with the required engine and notifier dependencies.
     *
     * @param limitEngine   the engine used to recalculate the daily spending limit
     * @param alertNotifier the notifier used to send budget threshold alerts
     */
    public HistoryController(LimitEngine limitEngine, AlertNotifier alertNotifier) {
        this.limitEngine   = limitEngine;
        this.alertNotifier = alertNotifier;
    }

    // ── private file helpers ─────────────────────────────────────────────
 /**
     * Reads all transactions from the transactions file.
     *
     * @return a list of all stored {@link Transaction} objects
     */
    private List<Transaction> readTransactions() {
        List<Transaction> list = new ArrayList<>();
        File f = new File(TRANSACTIONS_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", 6);
                if (p.length < 6) continue;
                try {
                    Transaction t = new Transaction();
                    t.setTransactionId(Integer.parseInt(p[0]));
                    t.setAmount(Double.parseDouble(p[1]));
                    t.setCategoryId(Integer.parseInt(p[2]));
                    t.setTimestamp(LocalDateTime.parse(p[3]));
                    t.setNote(p[4]);
                    t.setCycleId(Integer.parseInt(p[5]));
                    list.add(t);
                } catch (Exception ignored) {}
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions.");
        }
        return list;
    }
    /**
     * Writes the given list of transactions to the transactions file,
     * overwriting any existing data.
     *
     * @param list the list of {@link Transaction} objects to save
     */
    private void writeTransactions(List<Transaction> list) {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transaction t : list) {
                pw.println(t.getTransactionId() + "," +
                           t.getAmount()        + "," +
                           t.getCategoryId()    + "," +
                           t.getTimestamp()     + "," +
                           t.getNote()          + "," +
                           t.getCycleId());
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions.");
        }
    }

    /**
     * Reads the current budget cycle from the cycle file.
     *
     * @return the loaded {@link BudgetCycle}, or {@code null} if unavailable
     */

    private BudgetCycle readCycle() {
        File f = new File(CYCLE_FILE);
        if (!f.exists()) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            if (line == null || line.isBlank()) return null;
            String[] p = line.split(",");
            if (p.length < 6) return null;
            BudgetCycle c = new BudgetCycle();
            c.setCycleId(Integer.parseInt(p[0]));
            c.setTotalAllowance(Double.parseDouble(p[1]));
            c.setStartDate(LocalDate.parse(p[2]));
            c.setEndDate(LocalDate.parse(p[3]));
            c.setRemainingBalance(Double.parseDouble(p[4]));
            c.setSafeDailyLimit(Double.parseDouble(p[5]));
            return c;
        } catch (IOException e) {
            return null;
        }
    }
    /**
     * Writes the given budget cycle to the cycle file.
     *
     * @param cycle the {@link BudgetCycle} to persist
     */
    private void writeCycle(BudgetCycle cycle) {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(CYCLE_FILE))) {
            pw.println(cycle.getCycleId()          + "," +
                       cycle.getTotalAllowance()   + "," +
                       cycle.getStartDate()        + "," +
                       cycle.getEndDate()          + "," +
                       cycle.getRemainingBalance() + "," +
                       cycle.getSafeDailyLimit());
        } catch (IOException e) {
            System.out.println("Error saving cycle.");
        }
    }
/**
     * Generates the next available transaction ID by finding the current maximum.
     *
     * @return the next transaction ID as an integer
     */
    private int getNextId() {
        List<Transaction> all = readTransactions();
        return all.stream()
                  .mapToInt(Transaction::getTransactionId)
                  .max().orElse(0) + 1;
    }
/**
     * Recalculates the remaining balance of a cycle based on all transactions.
     *
     * @param cycle the {@link BudgetCycle} whose balance will be updated
     * @param all   the full list of transactions to calculate from
     */

    private void recalculateBalance(BudgetCycle cycle, List<Transaction> all) {
        double totalSpent = all.stream()
                .filter(t -> t.getCycleId() == cycle.getCycleId())
                .mapToDouble(Transaction::getAmount)
                .sum();
        cycle.setRemainingBalance(cycle.getTotalAllowance() - totalSpent);
    }

   //Methods
/**
     * Retrieves all transactions belonging to a specific budget cycle,
     * sorted from most recent to oldest.
     *
     * @param cycleId the ID of the budget cycle to filter by
     * @return a sorted list of {@link Transaction} objects for that cycle
     */

    public List<Transaction> getAll(int cycleId) {
        return readTransactions().stream()
                .filter(t -> t.getCycleId() == cycleId)
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }
/**
     * Filters transactions in the current cycle by a specific category.
     *
     * @param catId the category ID to filter by
     * @return a sorted list of matching {@link Transaction} objects
     */

    public List<Transaction> filterByCategory(int catId) {
        BudgetCycle cycle = readCycle();
        if (cycle == null) return new ArrayList<>();
        return readTransactions().stream()
                .filter(t -> t.getCycleId() == cycle.getCycleId()
                          && t.getCategoryId() == catId)
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }
 /**
     * Filters transactions in the current cycle by a specific date.
     *
     * @param date the {@link LocalDate} to filter by
     * @return a sorted list of matching {@link Transaction} objects
     */

    public List<Transaction> filterByDate(LocalDate date) {
        BudgetCycle cycle = readCycle();
        if (cycle == null) return new ArrayList<>();
        return readTransactions().stream()
                .filter(t -> t.getCycleId() == cycle.getCycleId()
                          && t.getTimestamp().toLocalDate().equals(date))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }
/**
     * Edits an existing transaction's amount, category, and note.
     * Recalculates the balance and daily limit after the update.
     *
     * @param id     the ID of the transaction to edit
     * @param amount the new expense amount
     * @param catId  the new category ID
     * @param note   the new note
     * @return {@code true} if the transaction was found and updated, {@code false} otherwise
     */

    public boolean editTransaction(int id, double amount, int catId, String note) {
        List<Transaction> all = readTransactions();
        boolean found = false;
        for (Transaction t : all) {
            if (t.getTransactionId() == id) {
                t.editEntry(amount, catId, note);
                found = true;
                break;
            }
        }
        if (!found) return false;

        writeTransactions(all);
        BudgetCycle cycle = readCycle();
        if (cycle != null) {
            recalculateBalance(cycle, all);
            limitEngine.calcDailyLimit(cycle);
            writeCycle(cycle);
            alertNotifier.check80Percent(cycle);
        }
        return true;
    }
/**
     * Deletes a transaction by its ID and recalculates the balance and daily limit.
     *
     * @param id the ID of the transaction to delete
     */

    public void deleteTransaction(int id) {
        List<Transaction> all = readTransactions();
        all.removeIf(t -> t.getTransactionId() == id);
        writeTransactions(all);
        BudgetCycle cycle = readCycle();
        if (cycle != null) {
            recalculateBalance(cycle, all);
            limitEngine.calcDailyLimit(cycle);
            writeCycle(cycle);
        }
    }
    /**
     * Logs a new expense transaction, saves it, and updates the cycle balance and daily limit.
     * Triggers a budget alert if the spending threshold is reached.
     *
     * @param amount  the expense amount
     * @param catId   the category ID for this expense
     * @param note    an optional note describing the expense
     * @param cycleId the ID of the current budget cycle
     */

    public void logExpense(double amount, int catId, String note, int cycleId) {
    List<Transaction> all = readTransactions();

    Transaction t = new Transaction();
    t.setTransactionId(getNextId());
    t.logExpense(amount, catId, note);
    t.setCycleId(cycleId);

    all.add(t);
    writeTransactions(all);

    BudgetCycle cycle = readCycle();
    if (cycle != null) {
        recalculateBalance(cycle, all);
        limitEngine.calcDailyLimit(cycle);
        writeCycle(cycle);
        alertNotifier.check80Percent(cycle);
    }
}

  /**
     * Clears all stored transactions by overwriting the transactions file with empty content.
     */
    public void deleteTransactions() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
            // write nothing — file is now empty
        } catch (IOException e) {
            System.out.println("Error clearing transactions.");
        }
    }
    /**
     * Clears the Stored budget cycle data by overwriting the cycle file with empty content.
     */
    public void deleteCycle() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(CYCLE_FILE))) {
            // write nothing — file is now empty
        } catch (IOException e) {
            System.out.println("Error clearing cycle data.");
        }
    }
}