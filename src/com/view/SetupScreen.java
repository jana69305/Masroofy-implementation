package com.view;
 
import java.util.List;
import java.util.Map;
import com.controller.SetupController;

import java.util.Scanner;
import java.time.LocalDate;
import com.model.BudgetCycle;

public class SetupScreen {

    private double budgetInput;
 
    private LocalDate startDateInput;
 
    private LocalDate endDateInput;

    public void displayForm() {
        System.out.println("=== Initialize Budget ===");
        System.out.println("Enter your total budget (EGP), start date, and end date.");
    }
 

    public BudgetCycle onContinue() {
        if (budgetInput <= 0) {
            showError("Allowance must be a positive number.");
            return null;
        }
        if (startDateInput == null || endDateInput == null) {
            showError("Please select valid start and end dates.");
            return null;
        }
        if (!endDateInput.isAfter(startDateInput)) {
            showError("End date must be after start date.");
            return null;
        }
 
        BudgetCycle cycle = new BudgetCycle();
        cycle.setTotalAllowance(budgetInput);
        cycle.setStartDate(startDateInput);
        cycle.setEndDate(endDateInput);
        cycle.initializeCycle();
 
        System.out.println("Cycle started! Safe Daily Limit: " + cycle.getSafeDailyLimit() + " EGP");
        return cycle;
    }
 
    public void onChange(double amount) {
        this.budgetInput = amount;
    }
 

    public void showError(String msg) {
        System.out.println("[ERROR] " + msg);
    }
 

    public void setBudgetInput(double budgetInput) {
        this.budgetInput = budgetInput;
    }
 

}
