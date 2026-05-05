package com.view;

import com.controller.HistoryController;
import com.model.Category;
import com.model.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;


/**
 * HistoryScreen — View layer for browsing and managing past expenses.
 *
 * Responsibilities (thin-view):
 *   • Render a formatted list of transactions
 *   • Provide filter UI for category and date
 *   • Trigger onEdit / onDelete via HistoryController
 *
 * All data retrieval, persistence, and balance recalculation
 * is delegated to HistoryController.
 */
public class HistoryScreen {

    // ── UI state ──────────────────────────────────────────────────────────
    private List<Transaction> displayedTransactions;

    // ── controller dependency (injected) ──────────────────────────────────
    private final HistoryController historyController;

    // ── active cycle id (passed in from the session) ──────────────────────
    private final int activeCycleId;

    // ── scanner for console input ─────────────────────────────────────────
    private final Scanner scanner;

    // ── formatter for pretty timestamps ───────────────────────────────────
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm");

    public HistoryScreen(HistoryController historyController, int activeCycleId) {
        this.historyController = historyController;
        this.activeCycleId = activeCycleId;
        this.scanner = new Scanner(System.in);
    }

    // ── displayHistory(list) : void ───────────────────────────────────────
    /**
     * Renders the given transaction list as a formatted table.
     */
    public void displayHistory(List<Transaction> list) {
        this.displayedTransactions = list;

        if (list.isEmpty()) {
            System.out.println("\n  (no transactions to display)\n");
            return;
        }

        // resolve category names once
        List<Category> categories = Category.fetchAll();

        System.out.println("\n┌──────┬────────────┬────────────────┬──────────────────┬────────────┐");
        System.out.println("│  ID  │   Amount   │   Category     │   Date & Time    │   Note     │");
        System.out.println("├──────┼────────────┼────────────────┼──────────────────┼────────────┤");

        for (Transaction t : list) {
            String catName = categories.stream()
                    .filter(c -> c.getCategoryId() == t.getCategoryId())
                    .map(Category::getName)
                    .findFirst()
                    .orElse("Unknown");

            System.out.printf("│ %4d │ %8.2f   │ %-14s │ %-16s │ %-10s │%n",
                    t.getTransactionId(),
                    t.getAmount(),
                    catName,
                    t.getTimestamp() != null ? t.getTimestamp().format(FMT) : "—",
                    t.getNote() != null ? t.getNote() : "—");
        }

        System.out.println("└──────┴────────────┴────────────────┴──────────────────┴────────────┘");
    }

    // ── show() : void ─────────────────────────────────────────────────────
    /**
     * Main entry point.  Loads all transactions for the active cycle,
     * displays them, and presents an action menu.
     */
    public void show() {
        System.out.println("========================================");
        System.out.println("        MASROOFY — Expense History      ");
        System.out.println("========================================");

        // load full history from controller
        displayedTransactions = historyController.getAll(activeCycleId);
        displayHistory(displayedTransactions);

        // action loop
        boolean running = true;
        while (running) {
            System.out.println("\nActions:  [F] Filter by category  [D] Filter by date"
                             + "  [E] Edit  [X] Delete  [R] Refresh  [Q] Quit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "F":
                    promptFilterByCategory();
                    break;
                case "D":
                    promptFilterByDate();
                    break;
                case "E":
                    promptEdit();
                    break;
                case "X":
                    promptDelete();
                    break;
                case "R":
                    displayedTransactions = historyController.getAll(activeCycleId);
                    displayHistory(displayedTransactions);
                    break;
                case "Q":
                    running = false;
                    break;
                default:
                    System.out.println("  ✗ Unknown option.");
            }
        }
    }

    // ── onFilterByCategory(catId) : void ──────────────────────────────────
    /**
     * Delegates category filtering to HistoryController.filterByCategory().
     */
    public void onFilterByCategory(int catId) {
        List<Transaction> filtered = historyController.filterByCategory(catId);

        System.out.println("\n— Showing category id " + catId + " —");
        displayHistory(filtered);
    }

    // ── onEdit(txId) : void ───────────────────────────────────────────────
    /**
     * Captures new values from the user and delegates editing
     * to HistoryController.editTransaction().
     */
    public void onEdit(int txId) {
        System.out.println("\nEditing transaction #" + txId);

        System.out.print("  New amount (EGP): ");
        double newAmount;
        try {
            newAmount = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  ✗ Invalid amount.");
            return;
        }

        System.out.print("  New category id: ");
        int newCatId;
        try {
            newCatId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  ✗ Invalid category id.");
            return;
        }

        // delegate to controller
        historyController.editTransaction(txId, newAmount, newCatId);

        System.out.println("  ✓ Transaction #" + txId + " updated.");

        // refresh the list
        displayedTransactions = historyController.getAll(activeCycleId);
        displayHistory(displayedTransactions);
    }

    // ── onDelete(txId) : void ─────────────────────────────────────────────
    /**
     * Asks for confirmation, then delegates deletion to the controller.
     */
    public void onDelete(int txId) {
        System.out.print("\nDelete transaction #" + txId + "? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (!"Y".equals(confirm)) {
            System.out.println("  Cancelled.");
            return;
        }

        // delegate to controller
        historyController.deleteTransaction(txId);
        System.out.println("  ✓ Transaction #" + txId + " deleted.");

        // refresh the list
        displayedTransactions = historyController.getAll(activeCycleId);
        displayHistory(displayedTransactions);
    }

    // ══════════════════════════════════════════════════════════════════════
    //   Private prompt helpers — keep the action-loop readable
    // ══════════════════════════════════════════════════════════════════════

    private void promptFilterByCategory() {
        List<Category> cats = Category.fetchAll();
        System.out.println("\nAvailable categories:");
        for (Category c : cats) {
            System.out.println("  " + c.getCategoryId() + ". " + c.getIcon() + " " + c.getName());
        }
        System.out.print("Enter category id to filter: ");
        try {
            int catId = Integer.parseInt(scanner.nextLine().trim());
            onFilterByCategory(catId);
        } catch (NumberFormatException e) {
            System.out.println("  ✗ Invalid category id.");
        }
    }

    private void promptFilterByDate() {
        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine().trim();

        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);

            // delegate to controller
            List<Transaction> filtered = historyController.filterByDate(date);

            System.out.println("\n— Showing transactions on " + dateStr + " —");
            displayHistory(filtered);
        } catch (Exception e) {
            System.out.println("  ✗ Invalid date format. Use yyyy-MM-dd.");
        }
    }

    private void promptEdit() {
        System.out.print("Enter transaction id to edit: ");
        try {
            int txId = Integer.parseInt(scanner.nextLine().trim());
            onEdit(txId);
        } catch (NumberFormatException e) {
            System.out.println("  ✗ Invalid transaction id.");
        }
    }

    private void promptDelete() {
        System.out.print("Enter transaction id to delete: ");
        try {
            int txId = Integer.parseInt(scanner.nextLine().trim());
            onDelete(txId);
        } catch (NumberFormatException e) {
            System.out.println("  ✗ Invalid transaction id.");
        }
    }
}
