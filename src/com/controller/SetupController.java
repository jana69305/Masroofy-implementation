package com.controller;

import com.model.BudgetCycle;

import java.io.*;
import java.time.LocalDate;

public class SetupController {

    private static final String CYCLE_FILE = "data/cycle.txt";

    // ── load cycle from file ─────────────────────────────
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

    // ── save cycle to file ───────────────────────────────
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

    // ── detect if there is an active cycle ───────────────
    public boolean detectActiveCycle() {
        BudgetCycle cycle = readCycle();
        return cycle != null && !cycle.isExpired();
    }

    // ── create and save new cycle ────────────────────────
    public BudgetCycle startNewCycle(double allowance, LocalDate start, LocalDate end) {
        BudgetCycle cycle = new BudgetCycle();

        cycle.setCycleId(1);
        cycle.setTotalAllowance(allowance);
        cycle.setStartDate(start);
        cycle.setEndDate(end);
        cycle.initializeCycle();

        writeCycle(cycle);

        return cycle;
    }

    public void resetCycle(BudgetCycle cycle) {
        if (cycle != null) {
            cycle.initializeCycle();
            writeCycle(cycle);
        }
    }

    public boolean isPositive(double allowance) {
        return allowance > 0;
    }

    public boolean isValidate(double allowance, LocalDate start, LocalDate end) {
        if (!isPositive(allowance)) return false;
        if (start == null || end == null) return false;
        if (!end.isAfter(start)) return false;
        return true;
    }

    public BudgetCycle getSavedCycle() {
    return readCycle();
}
public void saveUpdatedCycle(BudgetCycle cycle) {
    writeCycle(cycle);
}
}
