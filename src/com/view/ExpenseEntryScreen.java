package com.view;

import com.controller.HistoryController;
import com.model.Category;

import java.util.List;
import java.util.Scanner;


public class ExpenseEntryScreen {

    // ── UI state ──────────────────────────────────────────────────────────
    private double amountInput;
    private Category selectedCategory;

    // ── controller dependency (injected) ──────────────────────────────────
    private final HistoryController historyController;

    // ── active cycle id (passed in from the session) ──────────────────────
    private final int activeCycleId;

    // ── scanner for console input ─────────────────────────────────────────
    private final Scanner scanner;

    public ExpenseEntryScreen(HistoryController historyController, int activeCycleId) {
        this.historyController = historyController;
        this.activeCycleId = activeCycleId;
        this.scanner = new Scanner(System.in);
    }

 
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


    public void onSave() {
        System.out.println("========================================");
        System.out.println("         MASROOFY — New Expense         ");
        System.out.println("========================================");

        // ── 1. show categories ───────────────────────────────────────
        displayCategoryGrid();
        List<Category> categories = Category.fetchAll();

        // ── 2. pick a category (speed-optimised: just type a number) ─
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

        // ── 3. enter amount ──────────────────────────────────────────
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

        // ── 4. optional note ─────────────────────────────────────────
        System.out.print("Note (optional, press Enter to skip): ");
        String note = scanner.nextLine().trim();

        // ── 5. delegate to controller ────────────────────────────────
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

 
    public void showError(String msg) {
        System.out.println("  ✗ " + msg);
    }
}
