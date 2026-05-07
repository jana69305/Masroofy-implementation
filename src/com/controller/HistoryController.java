package com.controller;

import com.model.BudgetCycle;
import com.model.Transaction;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class HistoryController {

   

    private static final String TRANSACTIONS_FILE = "data/transactions.txt";
    private static final String CYCLE_FILE        = "data/cycle.txt";

    private LimitEngine   limitEngine;
    private AlertNotifier alertNotifier;

    public HistoryController(LimitEngine limitEngine, AlertNotifier alertNotifier) {
        this.limitEngine   = limitEngine;
        this.alertNotifier = alertNotifier;
    }

    // ── private file helpers ─────────────────────────────────────────────

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

    private int getNextId() {
        List<Transaction> all = readTransactions();
        return all.stream()
                  .mapToInt(Transaction::getTransactionId)
                  .max().orElse(0) + 1;
    }

    private void recalculateBalance(BudgetCycle cycle, List<Transaction> all) {
        double totalSpent = all.stream()
                .filter(t -> t.getCycleId() == cycle.getCycleId())
                .mapToDouble(Transaction::getAmount)
                .sum();
        cycle.setRemainingBalance(cycle.getTotalAllowance() - totalSpent);
    }

   //Methods

    public List<Transaction> getAll(int cycleId) {
        return readTransactions().stream()
                .filter(t -> t.getCycleId() == cycleId)
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<Transaction> filterByCategory(int catId) {
        BudgetCycle cycle = readCycle();
        if (cycle == null) return new ArrayList<>();
        return readTransactions().stream()
                .filter(t -> t.getCycleId() == cycle.getCycleId()
                          && t.getCategoryId() == catId)
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<Transaction> filterByDate(LocalDate date) {
        BudgetCycle cycle = readCycle();
        if (cycle == null) return new ArrayList<>();
        return readTransactions().stream()
                .filter(t -> t.getCycleId() == cycle.getCycleId()
                          && t.getTimestamp().toLocalDate().equals(date))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

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

  
    public void deleteTransactions() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
            // write nothing — file is now empty
        } catch (IOException e) {
            System.out.println("Error clearing transactions.");
        }
    }

    public void deleteCycle() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(CYCLE_FILE))) {
            // write nothing — file is now empty
        } catch (IOException e) {
            System.out.println("Error clearing cycle data.");
        }
    }
}