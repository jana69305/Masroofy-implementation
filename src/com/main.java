package com;
import com.controller.*;
import com.view.AuthScreen;
import com.view.SetupScreen;
import com.model.BudgetCycle;
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

        // safe AlertNotifier (prevents null crash)
        AlertNotifier alertNotifier = new AlertNotifier() {
            @Override
            public void check80Percent(BudgetCycle cycle) {
                // no action (safe stub)
            }
        };

        HistoryController historyController =
                new HistoryController(limitEngine, alertNotifier);

        Settingcontroller settingController =
                new Settingcontroller(authController, historyController);

        // ─────────────────────────────
        // Views
        // ─────────────────────────────
        AuthScreen authScreen =
                new AuthScreen(authController);

        SetupScreen setupScreen =
                new SetupScreen(setupController, scanner);

        // ─────────────────────────────
        // 🔐 AUTH FIRST
        // ─────────────────────────────
        

        authScreen.displayPINPrompt();

        

        // ─────────────────────────────
        // 💰 US1: SET INITIAL BUDGET
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
        // END
        // ─────────────────────────────
        System.out.println("\nWelcome to Dashboard 🎉");

        scanner.close();
    }
}

