package com.view;

import com.model.Category;
import com.model.CategoryTotal;
import com.model.Transaction;
import com.controller.HistoryController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * StatsScreen — View layer for spending statistics and pie chart.
 *
 * Responsibilities (thin-view):
 *   • Display total amount spent
 *   • Render a text-based pie chart (proportional bar) per category
 *   • Provide a button to navigate to HistoryScreen
 *
 * All aggregation data comes from the controller / pre-computed list.
 * The "pie chart" is a console-friendly horizontal bar chart where
 * each category's bar length is proportional to its spending share.
 */
public class StatsScreen {

    // ── UI state ──────────────────────────────────────────────────────────
    private double totalSpentDisplay;

    // ── controller dependency (injected) ──────────────────────────────────
    private final HistoryController historyController;

    // ── active cycle id ───────────────────────────────────────────────────
    private final int activeCycleId;

    // ── scanner for console input ─────────────────────────────────────────
    private final Scanner scanner;

    // ── chart width in characters ─────────────────────────────────────────
    private static final int BAR_WIDTH = 30;

    public StatsScreen(HistoryController historyController, int activeCycleId) {
        this.historyController = historyController;
        this.activeCycleId = activeCycleId;
        this.scanner = new Scanner(System.in);
    }

    // ── show() : void ─────────────────────────────────────────────────────
    /**
     * Main entry point.  Aggregates data, renders chart, and offers
     * navigation to HistoryScreen.
     */
    public void show() {
        System.out.println("========================================");
        System.out.println("        MASROOFY — Spending Stats       ");
        System.out.println("========================================");

        // build category totals from the current cycle's transactions
        List<CategoryTotal> data = buildCategoryTotals();

        // calculate grand total
        double grandTotal = data.stream()
                .mapToDouble(CategoryTotal::getTotal)
                .sum();

        displayTotalSpent(grandTotal);
        renderChart(data);

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
    public void renderChart(List<CategoryTotal> data) {
        if (data.isEmpty()) {
            System.out.println("\n  (no spending data to chart)\n");
            return;
        }

        double grandTotal = data.stream()
                .mapToDouble(CategoryTotal::getTotal)
                .sum();

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

    // ══════════════════════════════════════════════════════════════════════
    //   Private helpers
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Aggregates transactions by category to build the CategoryTotal list.
     * This is a thin helper — in a richer app, DashboardController.aggregateTotals()
     * would provide this data.
     */
    private List<CategoryTotal> buildCategoryTotals() {
        List<Transaction> transactions = historyController.getAll(activeCycleId);
        List<Category> categories = Category.fetchAll();

        // sum amounts per categoryId
        Map<Integer, Double> totals = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategoryId,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        // map to CategoryTotal using category names
        List<CategoryTotal> result = new ArrayList<>();
        for (Category cat : categories) {
            double sum = totals.getOrDefault(cat.getCategoryId(), 0.0);
            if (sum > 0) {
                result.add(new CategoryTotal(cat.getName(), sum));
            }
        }

        return result;
    }
}
