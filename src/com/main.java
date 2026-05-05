package com;

import com.controller.*;
import com.view.AuthScreen;
import com.view.SetupScreen;
import com.model.BudgetCycle;
import com.model.Transaction;

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

     
        AuthController authController = new AuthController();
        SetupController setupController = new SetupController();
        LimitEngine limitEngine = new LimitEngine();

        AlertNotifier alertNotifier = new AlertNotifier() {
            @Override
            public void check80Percent(BudgetCycle cycle) {
               
            }
        };

        HistoryController historyController =
                new HistoryController(limitEngine, alertNotifier);

        Settingcontroller settingController =
                new Settingcontroller(authController, historyController);

      
        AuthScreen authScreen = new AuthScreen(authController);
        SetupScreen setupScreen = new SetupScreen(setupController, scanner);

      
        authScreen.displayPINPrompt();

      
        while (true) {

            System.out.println("\n========== MASROOFY MENU ==========");
            System.out.println("1. Initialize New Budget");
            System.out.println("2. Open Dashboard");
            System.out.println("3. Apply Daily Rollover");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());

           
            if (choice == 1) {

                System.out.println("\nStarting Budget Setup...\n");
                setupScreen.displayForm();
                setupScreen.onContinue();
            }

          
            else if (choice == 2) {

                if (setupController.detectActiveCycle()) {

                    BudgetCycle cycle = setupController.getSavedCycle();

                    System.out.println("\n===== DASHBOARD =====");
                    System.out.println("Remaining Balance: " +
                            String.format("%.2f", cycle.getRemainingBalance()) + " EGP");

                    System.out.println("Safe Daily Limit: " +
                            String.format("%.2f", cycle.getSafeDailyLimit()) + " EGP");

                    System.out.println("Remaining Days: " + cycle.getRemainingDays());

                  
                    check80PercentAlert(cycle);

                } else {
                    System.out.println("No active cycle found. Please initialize budget first.");
                }
            }

         
            else if (choice == 3) {

                if (setupController.detectActiveCycle()) {

                    BudgetCycle cycle = setupController.getSavedCycle();

                    LocalDate today = LocalDate.now();

                    if (lastRolloverDate == null || !lastRolloverDate.equals(today)) {

                        System.out.println("\n--- Daily Rollover Management ---");

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

                        System.out.println("Yesterday spent: " +
                                String.format("%.2f", yesterdaySpent) + " EGP");

                        System.out.println("Yesterday limit: " +
                                String.format("%.2f", oldLimit) + " EGP");

                        if (leftover >= 0) {
                            System.out.println("Unspent rollover added: " +
                                    String.format("%.2f", leftover) + " EGP");
                        } else {
                            System.out.println("Overspending deficit: " +
                                    String.format("%.2f", Math.abs(leftover)) + " EGP");
                        }

                        System.out.println("New Safe Daily Limit: " +
                                String.format("%.2f", cycle.getSafeDailyLimit()) + " EGP");

                     
                        check80PercentAlert(cycle);

                        lastRolloverDate = today;

                    } else {
                        System.out.println("Rollover already applied today. Skipping...");
                    }

                } else {
                    System.out.println("No active cycle found. Initialize budget first.");
                }
            }

        
            else if (choice == 4) {
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

