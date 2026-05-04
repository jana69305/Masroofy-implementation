package com.view;

import com.model.CategoryTotal;
import com.controller.DashboardController;
import com.controller.HistoryController;

import java.util.List;
import java.util.Scanner;

/**
 * StatsScreen — View layer for spending statistics and pie chart.
 *
 * Responsibilities (thin-view):
 *   • Display total amount spent
 *   • Render a text-based pie chart (proportional bar) per category
 *   • Provide a button to navigate to HistoryScreen
 *
 * All aggregation is delegated to DashboardController.aggregateTotals().
 * The "pie chart" is a console-friendly horizontal bar chart where
 * each category's bar length is proportional to its spending share.
 */
public class StatsScreen {

    // ── UI state ──────────────────────────────────────────────────────────
    private double totalSpentDisplay;

    // ── controller dependencies (injected) ────────────────────────────────
    private final HistoryController historyController;
    private final DashboardController dashboardController;

    // ── active cycle id ───────────────────────────────────────────────────
    private final int activeCycleId;

    // ── scanner for console input ─────────────────────────────────────────
    private final Scanner scanner;

    // ── chart width in characters ─────────────────────────────────────────
    private static final int BAR_WIDTH = 30;

    public StatsScreen(HistoryController historyController,
                       DashboardController dashboardController,
                       int activeCycleId) {
        this.historyController = historyController;
        this.dashboardController = dashboardController;
        this.activeCycleId = activeCycleId;
        this.scanner = new Scanner(System.in);
    }

    // ── show() : void ─────────────────────────────────────────────────────
    /**
     * Main entry point.  Fetches transactions, delegates aggregation
     * to DashboardController, renders chart, and offers navigation
     * to HistoryScreen.
     */
    public void show() {
        System.out.println("========================================");
        System.out.println("        MASROOFY — Spending Stats       ");
        System.out.println("========================================");

        // fetch transactions and delegate aggregation to controller
        List<CategoryTotal> data = dashboardController.aggregateTotals(
                historyController.getAll(activeCycleId));

        // sum the already-aggregated display values for rendering
        double grandTotal = 0;
        for (CategoryTotal ct : data) {
            grandTotal += ct.getTotal();
        }

        displayTotalSpent(grandTotal);
        renderChart(data, grandTotal);

        // navigation option
        System.out.println("\n[H] View full history   [Q] Back");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim().toUpperCase();

        if ("H".equals(choice)) {
            onViewHistory();
        }
    }

    // ── displayTotalSpent(total) : void ───────────────────────────────────
    /**
     * Shows the total spending figure for the current cycle.
     */
    public void displayTotalSpent(double total) {
        this.totalSpentDisplay = total;
        System.out.printf("%n  Total Spent:  %.2f EGP%n", total);
    }

    // ── renderChart(data) : void ──────────────────────────────────────────
    /**
     * Renders a console pie chart (horizontal bar chart) from the
     * given list of CategoryTotal objects.
     *
     * Each bar's length is proportional to its share of total spending.
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

    // ── onViewHistory() : void ────────────────────────────────────────────
    /**
     * Navigates to the HistoryScreen.
     * Creates a new HistoryScreen and calls show().
     */
    public void onViewHistory() {
        System.out.println("\nNavigating to History...\n");
        HistoryScreen historyScreen = new HistoryScreen(historyController, activeCycleId);
        historyScreen.show();
    }
}
