package com.view;

import com.controller.SetupController;
import com.model.BudgetCycle;

import java.time.LocalDate;
import java.util.Scanner;

public class SetupScreen {

    private double budgetInput;
    private LocalDate startDateInput;
    private LocalDate endDateInput;
    private BudgetCycle createdCycle;

    private SetupController controller;
    private Scanner scanner;

    public SetupScreen(SetupController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void displayForm() {
        System.out.println("=== Initialize Budget ===");
        System.out.print("Total Budget (EGP): ");
        budgetInput = Double.parseDouble(scanner.nextLine());

        System.out.print("Start Date (YYYY-MM-DD): ");
        startDateInput = LocalDate.parse(scanner.nextLine());

        System.out.print("End Date (YYYY-MM-DD): ");
        endDateInput = LocalDate.parse(scanner.nextLine());
    }

    public void onContinue() {
        createdCycle = controller.startNewCycle(
                budgetInput,
                startDateInput,
                endDateInput
        );

        if (createdCycle != null) {
            System.out.println("\n=== Dashboard ===");
            System.out.printf("Safe Daily Limit : %.2f EGP%n", createdCycle.getSafeDailyLimit());
            System.out.printf("Remaining Balance: %.2f EGP%n", createdCycle.getRemainingBalance());
            System.out.printf("Days Remaining   : %d%n", createdCycle.getRemainingDays());
        } else {
            showError("Invalid input.");
        }
    }

    public BudgetCycle getCreatedCycle() {
        return createdCycle;
    }

    public void showError(String msg) {
        System.out.println("[ERROR] " + msg);
    }
}
