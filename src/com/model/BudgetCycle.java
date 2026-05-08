package com.model;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
/**
 * Represents a budget cycle in the Masroofy application.
 * A budget cycle defines a time period with a total spending allowance,
 * and tracks the remaining balance and safe daily spending limit.
 */
public class BudgetCycle {
    /** Unique identifier for this budget cycle. */
    private int cycleId;

    /** The total spending allowance for this cycle. */
    private double totalAllowance;

    /** The start date of this budget cycle. */
    private LocalDate startDate;

    /** The end date of this budget cycle. */
    private LocalDate endDate;

    /** The remaining unspent balance in this cycle. */
    private double remainingBalance;

    /** The calculated safe amount the user can spend per day. */
    private double safeDailyLimit;

    /** Default no-argument constructor. */
    public BudgetCycle() {}
 /**
     * Constructs a BudgetCycle with the specified parameters.
     * Initializes the remaining balance to the total allowance and sets the daily limit to zero.
     *
     * @param cycleId        the unique ID for this cycle
     * @param totalAllowance the total spending allowance
     * @param startDate      the start date of the cycle
     * @param endDate        the end date of the cycle
     */
    public BudgetCycle(int cycleId, double totalAllowance, LocalDate startDate, LocalDate endDate) {
        this.cycleId = cycleId;
        this.totalAllowance = totalAllowance;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainingBalance = totalAllowance;
        this.safeDailyLimit = 0;
    }
/**
     * Initializes the cycle by resetting the remaining balance to the total allowance
     * and recalculating the safe daily limit based on remaining days.
     */
    public void initializeCycle() {
        this.remainingBalance = totalAllowance;
        int days = getRemainingDays();
        if (days > 0) {
            this.safeDailyLimit = remainingBalance / days;
        }
    }
 /**
     * Deducts the spent amount from the remaining balance and
     * recalculates the safe daily limit based on the updated balance.
     *
     * @param spentAmount the amount spent to deduct from the balance
     */
    public void updateBalance(double spentAmount) {
        this.remainingBalance -= spentAmount;
          int days = getRemainingDays();
    if (days > 0) {
        this.safeDailyLimit = remainingBalance / days;
    }
    }
/**
     * Returns the current safe daily spending limit.
     *
     * @return the safe daily limit as a double
     */
    public double getSafeDailyLimit() {
        return safeDailyLimit;
    }
 /**
     * Calculates the number of days remaining in this budget cycle, inclusive of today.
     * Returns 0 if the cycle has already ended, or the full duration if it hasn't started yet.
     *
     * @return the number of remaining days as an integer
     */
    public int getRemainingDays() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(endDate)) return 0;
        if (today.isBefore(startDate)) return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return (int) ChronoUnit.DAYS.between(today, endDate) + 1;
    }

    /**
     * Checks whether this budget cycle has expired (i.e. the end date has passed).
     *
     * @return {@code true} if today is after the end date, {@code false} otherwise
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

/** @return the cycle ID */
    public int getCycleId() { return cycleId; }

    /** @param cycleId the cycle ID to set */
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }

    /** @return the total allowance for this cycle */
    public double getTotalAllowance() { return totalAllowance; }

    /** @param totalAllowance the total allowance to set */
    public void setTotalAllowance(double totalAllowance) { this.totalAllowance = totalAllowance; }

    /** @return the start date of this cycle */
    public LocalDate getStartDate() { return startDate; }

    /** @param startDate the start date to set */
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    /** @return the end date of this cycle */
    public LocalDate getEndDate() { return endDate; }

    /** @param endDate the end date to set */
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    /** @return the remaining balance in this cycle */
    public double getRemainingBalance() { return remainingBalance; }

    /** @param remainingBalance the remaining balance to set */
    public void setRemainingBalance(double remainingBalance) { this.remainingBalance = remainingBalance; }

    /** @param safeDailyLimit the safe daily limit to set */
    public void setSafeDailyLimit(double safeDailyLimit) { this.safeDailyLimit = safeDailyLimit; }
}
