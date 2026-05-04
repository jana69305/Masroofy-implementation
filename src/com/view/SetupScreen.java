package com.view;

import com.controller.SetupController;
import com.model.BudgetCycle;

import java.time.LocalDate;

public class SetupScreen {

    private double budgetInput;
    private LocalDate startDateInput;
    private LocalDate endDateInput;

    private SetupController controller = new SetupController();

    public void displayForm() {
        System.out.println("=== Initialize Budget ===");
    }

    public void onContinue() {

        BudgetCycle cycle = controller.startNewCycle(
                budgetInput,
                startDateInput,
                endDateInput
        );

        if (cycle != null) {
            System.out.println("Cycle started! Safe Daily Limit: "
                    + cycle.getSafeDailyLimit());
        } else {
            showError("Invalid input.");
        }
    }

    public void showError(String msg) {
        System.out.println("[ERROR] " + msg);
    }
}
