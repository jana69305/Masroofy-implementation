package com.view;

import com.controller.HistoryController;
import com.model.Category;

import java.util.List;
import java.util.Scanner;


public class ExpenseEntryScreen {

    private double amountInput;
    private Category selectedCategory;

    private final HistoryController historyController;

    private final int activeCycleId;

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

 
    public void showError(String msg) {
        System.out.println("  ✗ " + msg);
    }
}
