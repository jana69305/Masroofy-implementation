package com.view;

import com.controller.HistoryController;
import com.model.Category;

import java.util.List;
import java.util.Scanner;
/**
 * Handles the expense entry screen for the Masroofy application.
 * Allows the user to select a category, enter an amount and optional note,
 * and save a new expense transaction to the current budget cycle.
 */
public class ExpenseEntryScreen {

 /** The amount entered by the user for the expense. */
    private double amountInput;

    /** The category selected by the user for the expense. */
    private Category selectedCategory;

    /** The controller responsible for logging and managing transactions. */
    private final HistoryController historyController;

    /** The ID of the currently active budget cycle. */
    private final int activeCycleId;

    /** Scanner used to read user input from the console. */
    private final Scanner scanner;

    /**
     * Constructs an ExpenseEntryScreen with the given history controller and active cycle ID.
     *
     * @param historyController the {@link HistoryController} used to log expenses
     * @param activeCycleId     the ID of the currently active budget cycle
     */
    public ExpenseEntryScreen(HistoryController historyController, int activeCycleId) {
        this.historyController = historyController;
        this.activeCycleId = activeCycleId;
        this.scanner = new Scanner(System.in);
    }
/**
     * Displays all available spending categories in a formatted grid.
     * Each category is shown with its index number, icon, and name.
     */
    public void displayCategoryGrid() {
        List<Category> categories = Category.fetchAll();

        System.out.println("\n┌──────────── Categories ────────────┐");

        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            System.out.printf("│  %d. %s  %-15s            │%n",
                    i + 1, cat.getIcon(), cat.getName());
        }

        System.out.println("└────────────────────────────────────┘");
    }
    /**
     * Handles the full expense entry flow.
     * Prompts the user to select a category, enter an amount, and optionally add a note.
     * Validates all inputs and logs the expense using {@link HistoryController#logExpense}.
     * Displays a confirmation message upon successful save.
     */
    public void onSave() {
        System.out.println("========================================");
        System.out.println("         MASROOFY — New Expense         ");
        System.out.println("========================================");

        displayCategoryGrid();
        List<Category> categories = Category.fetchAll();

        int catChoice = -1;
        while (catChoice < 1 || catChoice > categories.size()) {
            System.out.print("Select category (number): ");
            String input = scanner.nextLine().trim();
            try {
                catChoice = Integer.parseInt(input);
                if (catChoice < 1 || catChoice > categories.size()) {
                    showError("Please choose a number between 1 and " + categories.size() + ".");
                }
            } catch (NumberFormatException e) {
                showError("Invalid input — enter a number.");
            }
        }
        selectedCategory = categories.get(catChoice - 1);

        amountInput = -1;
        while (amountInput <= 0) {
            System.out.print("Amount (EGP): ");
            String input = scanner.nextLine().trim();
            try {
                amountInput = Double.parseDouble(input);
                if (amountInput <= 0) {
                    showError("Amount must be greater than zero.");
                }
            } catch (NumberFormatException e) {
                showError("Invalid amount — enter a number.");
            }
        }

        System.out.print("Note (optional, press Enter to skip): ");
        String note = scanner.nextLine().trim();

        historyController.logExpense(
                amountInput,
                selectedCategory.getCategoryId(),
                note.isEmpty() ? "-" : note,
                activeCycleId
        );

        System.out.println("\n✓ Expense saved!  "
                + selectedCategory.getIcon() + " "
                + selectedCategory.getName()
                + " — " + String.format("%.2f", amountInput) + " EGP\n");
    }
/**
     * Displays a formatted error message to the user.
     *
     * @param msg the error message to display
     */
    public void showError(String msg) {
        System.out.println("  ✗ " + msg);
    }
}
