package com.view;

import com.controller.SetupController;
import com.model.BudgetCycle;

import java.time.LocalDate;
import java.util.Scanner;
/**
 * Handles the budget setup screen for the Masroofy application.
 * Collects the user's budget amount and cycle dates, then initializes
 * a new budget cycle via {@link SetupController}.
 */
public class SetupScreen {

/** The total budget amount entered by the user. */
    private double budgetInput;

    /** The start date of the budget cycle entered by the user. */
    private LocalDate startDateInput;

    /** The end date of the budget cycle entered by the user. */
    private LocalDate endDateInput;

    /** The budget cycle created after the user submits the setup form. */
    private BudgetCycle createdCycle;

    /** The controller responsible for creating and managing budget cycles. */
    private SetupController controller;

    /** Scanner used to read user input from the console. */
    private Scanner scanner;

    /**
     * Constructs a SetupScreen with the given setup controller and scanner.
     *
     * @param controller the {@link SetupController} used to create the budget cycle
     * @param scanner    the {@link Scanner} used to read user input
     */
    public SetupScreen(SetupController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }
/**
     * Displays the budget setup form and collects the user's input.
     * Prompts for the total budget amount, start date, and end date.
     */
    public void displayForm() {
        System.out.println("=== Initialize Budget ===");
        System.out.print("Total Budget (EGP): ");
        budgetInput = Double.parseDouble(scanner.nextLine());

        System.out.print("Start Date (YYYY-MM-DD): ");
        startDateInput = LocalDate.parse(scanner.nextLine());

        System.out.print("End Date (YYYY-MM-DD): ");
        endDateInput = LocalDate.parse(scanner.nextLine());
    }
/**
     * Processes the submitted form by creating a new budget cycle.
     * If successful, displays a summary of the cycle's daily limit,
     * remaining balance, and remaining days. Shows an error if creation fails.
     */
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
/**
     * Returns the budget cycle created after the setup form was submitted.
     *
     * @return the newly created {@link BudgetCycle}, or {@code null} if setup failed
     */
    public BudgetCycle getCreatedCycle() {
        return createdCycle;
    }
/**
     * Displays a formatted error message to the user.
     *
     * @param msg the error message to display
     */
    public void showError(String msg) {
        System.out.println("[ERROR] " + msg);
    }
}
