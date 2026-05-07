package com.view;

import com.model.CategoryTotal;
import com.controller.DashboardController;
import com.controller.HistoryController;

import java.util.List;
import java.util.Scanner;


public class StatsScreen {

    private double totalSpentDisplay;

    private final HistoryController historyController;
    private final DashboardController dashboardController;

    private final int activeCycleId;

    private final Scanner scanner;
    private static final int BAR_WIDTH = 30;

    public StatsScreen(HistoryController historyController,
                       DashboardController dashboardController,
                       int activeCycleId) {
        this.historyController = historyController;
        this.dashboardController = dashboardController;
        this.activeCycleId = activeCycleId;
        this.scanner = new Scanner(System.in);
    }

 
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

  
    public void displayTotalSpent(double total) {
        this.totalSpentDisplay = total;
        System.out.printf("%n  Total Spent:  %.2f EGP%n", total);
    }

 
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

  
    public void onViewHistory() {
        System.out.println("\nNavigating to History...\n");
        HistoryScreen historyScreen = new HistoryScreen(historyController, activeCycleId);
        historyScreen.show();
    }
}
