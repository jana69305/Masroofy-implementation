package com;

import com.controller.*;
import com.view.*;
import com.model.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("START APP");

        Scanner scanner = new Scanner(System.in);

        // ─────────────────────────────
        // Controllers
        // ─────────────────────────────
        AuthController authController = new AuthController();
        SetupController setupController = new SetupController();
        LimitEngine limitEngine = new LimitEngine();

        AlertNotifier alertNotifier = new AlertNotifier() {
            @Override
            public void check80Percent(BudgetCycle cycle) {
                // stub
            }
        };

        HistoryController historyController =
                new HistoryController(limitEngine, alertNotifier);

        Settingcontroller settingController =
                new Settingcontroller(authController, historyController);

        // ─────────────────────────────
        // Views
        // ─────────────────────────────
        AuthScreen authScreen = new AuthScreen(authController);
        SetupScreen setupScreen = new SetupScreen(setupController, scanner);
        DashboardScreen dashboard = new DashboardScreen();

        // ─────────────────────────────
        // AUTH
        // ─────────────────────────────
        authScreen.displayPINPrompt();

        // ─────────────────────────────
        // SETUP
        // ─────────────────────────────
        if (!setupController.detectActiveCycle()) {

            System.out.println("\nNo active budget found.");
            System.out.println("Starting setup...\n");

            setupScreen.displayForm();
            setupScreen.onContinue();

        } else {
            System.out.println("\nActive cycle loaded successfully.");
        }

        // ─────────────────────────────
        // GET CURRENT CYCLE
        // ─────────────────────────────
        BudgetCycle cycle = setupController.getCurrentCycle();

        if (cycle == null) {
            System.out.println("Error: No cycle found. Exiting...");
            return;
        }

        // ─────────────────────────────
        // INIT SCREENS 
        // ─────────────────────────────
        HistoryScreen historyScreen =
                new HistoryScreen(historyController, cycle.getCycleId());

        ExpenseEntryScreen expenseScreen =
                new ExpenseEntryScreen(historyController, cycle.getCycleId());

        // ─────────────────────────────
        // DASHBOARD
        // ─────────────────────────────
        System.out.println("\n--- DASHBOARD ---");

        dashboard.loadDashboard(
                cycle,
                historyController.getAll(cycle.getCycleId())
        );

        // ─────────────────────────────
        // ADD EXPENSE
        // ─────────────────────────────
        System.out.println("\nAdd Expense?");
        System.out.println("1- Yes | 2- No");

        int choice = scanner.nextInt();
        scanner.nextLine(); // مهم عشان newline

        if (choice == 1) {
            expenseScreen.onSave();
        }

        // ─────────────────────────────
        // HISTORY
        // ─────────────────────────────
        System.out.println("\n--- HISTORY ---");
        historyScreen.show();

        // ─────────────────────────────
        // END
        // ─────────────────────────────
        System.out.println("\nApp Finished ✅");

        scanner.close();
    }
}