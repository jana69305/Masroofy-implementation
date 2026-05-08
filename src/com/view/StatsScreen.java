package com.view;

import com.model.CategoryTotal;
import com.controller.DashboardController;
import com.controller.HistoryController;

import java.util.List;
import java.util.Scanner;

/**
 * Handles the spending statistics screen for the Masroofy application.
 * Displays a breakdown of spending by category using a visual bar chart,
 * and shows the total amount spent in the active budget cycle.
 */
public class StatsScreen {

   /** The total amount spent, used for display purposes. */
    private double totalSpentDisplay;

    /** The controller responsible for retrieving transaction data. */
    private final HistoryController historyController;

    /** The controller responsible for aggregating spending totals by category. */
    private final DashboardController dashboardController;

    /** The ID of the currently active budget cycle. */
    private final int activeCycleId;

    /** Scanner used to read user input from the console. */
    private final Scanner scanner;

    /** The fixed width of the bar chart rendered in the console. */
    private static final int BAR_WIDTH = 30;

    /**
     * Constructs a StatsScreen with the required controllers and active cycle ID.
     *
     * @param historyController   the {@link HistoryController} used to retrieve transactions
     * @param dashboardController the {@link DashboardController} used to aggregate category totals
     * @param activeCycleId       the ID of the currently active budget cycle
     */
    public StatsScreen(HistoryController historyController,
                       DashboardController dashboardController,
                       int activeCycleId) {
        this.historyController = historyController;
        this.dashboardController = dashboardController;
        this.activeCycleId = activeCycleId;
        this.scanner = new Scanner(System.in);
    }

  /**
     * Displays the spending statistics screen.
     * Aggregates category totals, displays the total spent, and renders the bar chart.
     * Allows the user to navigate to the full history screen or go back.
     */
    public void show() {
        System.out.println("========================================");
        System.out.println("        MASROOFY — Spending Stats       ");
        System.out.println("========================================");

        List<CategoryTotal> data = dashboardController.aggregateTotals(
                historyController.getAll(activeCycleId));

        double grandTotal = 0;
        for (CategoryTotal ct : data) {
            grandTotal += ct.getTotal();
        }

        displayTotalSpent(grandTotal);
        renderChart(data, grandTotal);

        System.out.println("\n[H] View full history   [Q] Back");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim().toUpperCase();

        if ("H".equals(choice)) {
            onViewHistory();
        }
    }

 /**
     * Displays the total amount spent in the active budget cycle.
     *
     * @param total the grand total spending amount to display
     */ 
    public void displayTotalSpent(double total) {
        this.totalSpentDisplay = total;
        System.out.printf("%n  Total Spent:  %.2f EGP%n", total);
    }

 /**
     * Renders a console-based horizontal bar chart showing spending by category.
     * Each bar's length is proportional to that category's share of the grand total.
     * Displays a message if there is no spending data available.
     *
     * @param data       the list of {@link CategoryTotal} objects to chart
     * @param grandTotal the total spending across all categories, used to calculate percentages
     */
    public void renderChart(List<CategoryTotal> data, double grandTotal) {
        if (data.isEmpty()) {
            System.out.println("\n  (no spending data to chart)\n");
            return;
        }

        System.out.println("\n  ┌─────────── Spending Breakdown ───────────┐");

        for (CategoryTotal ct : data) {
            double pct = (grandTotal > 0) ? (ct.getTotal() / grandTotal) * 100 : 0;
            int barLen  = (grandTotal > 0) ? (int) Math.round((ct.getTotal() / grandTotal) * BAR_WIDTH) : 0;

            String bar = "█".repeat(barLen) + "░".repeat(BAR_WIDTH - barLen);

            System.out.printf("  │ %-14s %s %5.1f%% │%n",
                    ct.getCategoryName(), bar, pct);
        }

        System.out.println("  └────────────────────────────────────────────┘");
    }

 /**
     * Navigates to the full transaction history screen.
     * Creates and displays a {@link HistoryScreen} for the active cycle.
     */ 
    public void onViewHistory() {
        System.out.println("\nNavigating to History...\n");
        HistoryScreen historyScreen = new HistoryScreen(historyController, activeCycleId);
        historyScreen.show();
    }
}
