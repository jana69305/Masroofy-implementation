package com.model;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BudgetCycle {
    private int cycleId;
    private double totalAllowance;
    private LocalDate startDate;
    private LocalDate endDate;
    private double remainingBalance;
    private double safeDailyLimit;

    public BudgetCycle() {}

    public BudgetCycle(int cycleId, double totalAllowance, LocalDate startDate, LocalDate endDate) {
        this.cycleId = cycleId;
        this.totalAllowance = totalAllowance;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainingBalance = totalAllowance;
        this.safeDailyLimit = 0;
    }

    public void initializeCycle() {
        this.remainingBalance = totalAllowance;
        int days = getRemainingDays();
        if (days > 0) {
            this.safeDailyLimit = remainingBalance / days;
        }
    }

    public void updateBalance(double spentAmount) {
        this.remainingBalance -= spentAmount;
          int days = getRemainingDays();
    if (days > 0) {
        this.safeDailyLimit = remainingBalance / days;
    }
    }

    public double getSafeDailyLimit() {
        return safeDailyLimit;
    }

    public int getRemainingDays() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(endDate)) return 0;
        if (today.isBefore(startDate)) return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return (int) ChronoUnit.DAYS.between(today, endDate) + 1;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }
    public double getTotalAllowance() { return totalAllowance; }
    public void setTotalAllowance(double totalAllowance) { this.totalAllowance = totalAllowance; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public double getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(double remainingBalance) { this.remainingBalance = remainingBalance; }
    public void setSafeDailyLimit(double safeDailyLimit) { this.safeDailyLimit = safeDailyLimit; }
}
