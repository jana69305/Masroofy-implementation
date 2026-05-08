package com.controller;

import com.model.BudgetCycle;

import java.io.*;
import java.time.LocalDate;
/**
 * Manages the creation, loading, and validation of budget cycles
 * for the Masroofy application.
 * Handles persisting cycle data to a file and provides methods
 * to initialize and reset budget cycles.
 */
public class SetupController {
    /** Path to the file where the active budget cycle is stored. */
    private static final String CYCLE_FILE = "data/cycle.txt";
/**
     * Reads the current budget cycle from the cycle file.
     *
     * @return the loaded {@link BudgetCycle}, or {@code null} if the file
     *         does not exist, is empty, or cannot be read
     */
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
/**
     * Writes the given budget cycle to the cycle file, overwriting any existing data.
     *
     * @param cycle the {@link BudgetCycle} to persist
     */
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
/**
     * Checks whether there is a currently active (non-expired) budget cycle.
     *
     * @return {@code true} if an active cycle exists, {@code false} otherwise
     */
    // ── detect if there is an active cycle ───────────────
    public boolean detectActiveCycle() {
        BudgetCycle cycle = readCycle();
        return cycle != null && !cycle.isExpired();
    }
/**
     * Creates a new budget cycle with the given parameters, initializes it,
     * saves it to file, and returns it.
     *
     * @param allowance the total spending allowance for the cycle
     * @param start     the start date of the cycle
     * @param end       the end date of the cycle
     * @return the newly created and saved {@link BudgetCycle}
     */
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

    /**
     * Resets an existing budget cycle to its initial state and saves the updated cycle.
     * Does nothing if the provided cycle is {@code null}.
     *
     * @param cycle the {@link BudgetCycle} to reset
     */
    public void resetCycle(BudgetCycle cycle) {
        if (cycle != null) {
            cycle.initializeCycle();
            writeCycle(cycle);
        }
    }
/**
     * Checks whether the given allowance amount is a positive value.
     *
     * @param allowance the allowance amount to validate
     * @return {@code true} if the allowance is greater than zero, {@code false} otherwise
     */
    public boolean isPositive(double allowance) {
        return allowance > 0;
    }
/**
     * Validates the budget cycle input parameters.
     * Ensures the allowance is positive, the dates are non-null,
     * and the end date is strictly after the start date.
     *
     * @param allowance the total allowance amount
     * @param start     the proposed start date
     * @param end       the proposed end date
     * @return {@code true} if all inputs are valid, {@code false} otherwise
     */
    public boolean isValidate(double allowance, LocalDate start, LocalDate end) {
        if (!isPositive(allowance)) return false;
        if (start == null || end == null) return false;
        if (!end.isAfter(start)) return false;
        return true;
    }

    /**
     * Retrieves the current active budget cycle from the file.
     *
     * @return the current {@link BudgetCycle}, or {@code null} if none exists
     */
    public BudgetCycle getCurrentCycle() {
        return readCycle();
    }
/**
     * Retrieves the saved budget cycle from the file.
     * Functionally identical to {@link #getCurrentCycle()}.
     *
     * @return the saved {@link BudgetCycle}, or {@code null} if none exists
     */
    public BudgetCycle getSavedCycle() {
        return readCycle();
    }
/**
     * Saves an updated budget cycle to the cycle file.
     *
     * @param cycle the {@link BudgetCycle} to save
     */
    public void saveUpdatedCycle(BudgetCycle cycle) {
        writeCycle(cycle);
    }
}