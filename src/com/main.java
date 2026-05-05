package com;

import com.controller.*;
import com.view.*;
import com.model.*;

import java.util.Scanner;
import java.util.List;
import java.time.LocalDate;

public class Main {

    private static void check80PercentAlert(BudgetCycle cycle) {

        if (cycle == null) return;

        double total = cycle.getTotalAllowance();
        double spent = total - cycle.getRemainingBalance();

        if (total == 0) return;

        double percent = (spent / total) * 100;

        if (percent >= 100) {
            System.out.println("\n🚨 Budget Exhausted!");
            System.out.println("You have used 100% or more of your allowance.");
        }
        else if (percent >= 80) {
            System.out.println("\n⚠️ Warning: You have used 80% of your allowance.");
        }
    }

    public static void main(String[] args) {

        System.out.println("START APP");

        Scanner scanner = new Scanner(System.in);
        LocalDate lastRolloverDate = null;

        // Controllers
        AuthController authController = new AuthController();
        SetupController setupController = new SetupController();
        LimitEngine limitEngine = new LimitEngine();

        AlertNotifier alertNotifier = new AlertNotifier() {
            @Override
            public void check80Percent(BudgetCycle cycle) {
                check80PercentAlert(cycle);
            }
        };

        HistoryController historyController =
                new HistoryController(limitEngine, alertNotifier);

        Settingcontroller settingController =
                new Settingcontroller(authController, historyController);

        // Views
        AuthScreen authScreen = new AuthScreen(authController);
        SetupScreen setupScreen = new SetupScreen(setupController, scanner);

        // AUTH
        authScreen.displayPINPrompt();

        // MAIN MENU LOOP
        while (true) {

            System.out.println("\n========== MASROOFY MENU ==========");
            System.out.println("1. Initialize New Budget");
            System.out.println("2. Open Dashboard");
            System.out.println("3. Add Expense");
            System.out.println("4. Show History");
            System.out.println("5. Apply Daily Rollover");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            // 1 - Setup
            if (choice == 1) {

                System.out.println("\nStarting Budget Setup...\n");
                setupScreen.displayForm();
                setupScreen.onContinue();
            }

            // 2 - Dashboard
            else if (choice == 2) {

                if (setupController.detectActiveCycle()) {

                    BudgetCycle cycle = setupController.getSavedCycle();

                    DashboardScreen dashboard = new DashboardScreen();

                    dashboard.loadDashboard(
                            cycle,
                            historyController.getAll(cycle.getCycleId())
                    );

                    check80PercentAlert(cycle);

                } else {
                    System.out.println("No active cycle found.");
                }
            }

            // 3 - Add Expense
            else if (choice == 3) {

                if (setupController.detectActiveCycle()) {

                    BudgetCycle cycle = setupController.getSavedCycle();

                    ExpenseEntryScreen expenseScreen =
                            new ExpenseEntryScreen(historyController, cycle.getCycleId());

                    expenseScreen.onSave();

                } else {
                    System.out.println("Initialize budget first.");
                }
            }

            // 4 - History
            else if (choice == 4) {

                if (setupController.detectActiveCycle()) {

                    BudgetCycle cycle = setupController.getSavedCycle();

                    HistoryScreen historyScreen =
                            new HistoryScreen(historyController, cycle.getCycleId());

                    historyScreen.show();

                } else {
                    System.out.println("No history available.");
                }
            }

            // 5 - Rollover
            else if (choice == 5) {

                if (setupController.detectActiveCycle()) {

                    BudgetCycle cycle = setupController.getSavedCycle();
                    LocalDate today = LocalDate.now();

                    if (lastRolloverDate == null || !lastRolloverDate.equals(today)) {

                        System.out.println("\n--- Daily Rollover ---");

                        LocalDate yesterday = today.minusDays(1);

                        List<Transaction> yesterdayTransactions =
                                historyController.filterByDate(yesterday);

                        double yesterdaySpent = 0;
                        for (Transaction t : yesterdayTransactions) {
                            yesterdaySpent += t.getAmount();
                        }

                        double oldLimit = cycle.getSafeDailyLimit();
                        double leftover = oldLimit - yesterdaySpent;

                        cycle.setRemainingBalance(cycle.getRemainingBalance() + leftover);
                        cycle.initializeCycle();

                        setupController.saveUpdatedCycle(cycle);

                        System.out.println("Yesterday spent: " + yesterdaySpent);
                        System.out.println("Old limit: " + oldLimit);

                        if (leftover >= 0) {
                            System.out.println("Saved: " + leftover);
                        } else {
                            System.out.println("Overspent: " + Math.abs(leftover));
                        }

                        System.out.println("New Daily Limit: " +
                                cycle.getSafeDailyLimit());

                        check80PercentAlert(cycle);

                        lastRolloverDate = today;

                    } else {
                        System.out.println("Already applied today.");
                    }

                } else {
                    System.out.println("Initialize budget first.");
                }
            }

            // 6 - Exit
            else if (choice == 6) {
                System.out.println("Closing Masroofy...");
                break;
            }

            else {
                System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }
}