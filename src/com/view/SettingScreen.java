package com.view;

import com.controller.Settingcontroller;

import java.util.Scanner;

/**
 * SettingScreen — View layer for app-wide preferences and data management.
 *
 * Responsibilities (thin-view):
 *   • Display a settings menu
 *   • Capture user input for each option
 *   • Delegate all logic to Settingcontroller methods:
 *       - togglePrivacyLock(boolean)
 *       - changePIN(String)
 *       - requestReset()
 *
 * No business logic lives here — toggle state, PIN hashing,
 * and data deletion are all handled by the controller layer.
 */
public class SettingScreen {

    // ── controller dependency (injected) ──────────────────────────────────
    private final Settingcontroller settingcontroller;

    // ── scanner for console input ─────────────────────────────────────────
    private final Scanner scanner;

    public SettingScreen(Settingcontroller settingcontroller) {
        this.settingcontroller = settingcontroller;
        this.scanner = new Scanner(System.in);
    }

    // ── show() : void ─────────────────────────────────────────────────────
    /**
     * Main entry point.  Displays the settings menu in a loop
     * until the user chooses to go back.
     */
    public void show() {
        boolean running = true;

        while (running) {
            System.out.println("========================================");
            System.out.println("          MASROOFY — Settings           ");
            System.out.println("========================================");
            System.out.println();
            System.out.println("  [1]  Toggle Privacy Lock");
            System.out.println("  [2]  Change PIN");
            System.out.println("  [3]  Reset Current Cycle");
            System.out.println("  [4]  Reset Entire Database");
            System.out.println("  [Q]  Back");
            System.out.println();
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "1":
                    onToggleLock();
                    break;
                case "2":
                    onChangePIN();
                    break;
                case "3":
                    onResetCycle();
                    break;
                case "4":
                    onResetDatabase();
                    break;
                case "Q":
                    running = false;
                    break;
                default:
                    System.out.println("  ✗ Unknown option.");
            }
        }
    }

    // ── onToggleLock(enabled) : void ──────────────────────────────────────
    /**
     * Asks the user whether to enable or disable the privacy lock,
     * then delegates to Settingcontroller.togglePrivacyLock().
     */
    public void onToggleLock() {
        System.out.println("\n── Privacy Lock ─────────────────────────");
        System.out.print("  Enable privacy lock? (Y/N): ");
        String input = scanner.nextLine().trim().toUpperCase();

        if ("Y".equals(input)) {
            settingcontroller.togglePrivacyLock(true);
            System.out.println("  ✓ Privacy lock ENABLED.");
        } else if ("N".equals(input)) {
            settingcontroller.togglePrivacyLock(false);
            System.out.println("  ✓ Privacy lock DISABLED.");
        } else {
            System.out.println("  ✗ Invalid input — expected Y or N.");
        }
    }

    // ── onChangePIN() : void ──────────────────────────────────────────────
    /**
     * Prompts for a new PIN (with confirmation), then delegates
     * to Settingcontroller.changePIN().
     */
    public void onChangePIN() {
        System.out.println("\n── Change PIN ───────────────────────────");

        System.out.print("  Enter new PIN: ");
        String newPIN = scanner.nextLine().trim();

        if (newPIN.isEmpty()) {
            System.out.println("  ✗ PIN cannot be empty.");
            return;
        }

        System.out.print("  Confirm new PIN: ");
        String confirm = scanner.nextLine().trim();

        if (!newPIN.equals(confirm)) {
            System.out.println("  ✗ PINs do not match. Change cancelled.");
            return;
        }

        // delegate to controller
        settingcontroller.changePIN(newPIN);
        System.out.println("  ✓ PIN changed successfully.");
    }

    // ── onResetCycle() : void ─────────────────────────────────────────────
    /**
     * Resets the current budget cycle.  Asks for confirmation
     * ("Permanently delete logs?") then delegates to
     * Settingcontroller.requestReset().
     */
    public void onResetCycle() {
        System.out.println("\n── Reset Current Cycle ──────────────────");
        System.out.println("  ⚠  Permanently delete all transaction logs?");
        System.out.print("  Are you sure? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (!"Y".equals(confirm)) {
            System.out.println("  Cancelled.");
            return;
        }

        // delegate to controller
        boolean success = settingcontroller.requestReset();

        if (success) {
            System.out.println("  ✓ Cycle reset complete. Please initialize a new budget.");
        } else {
            System.out.println("  ✗ Reset may not have completed fully.");
        }
    }

    // ── onResetDatabase() : void ──────────────────────────────────────────
    /**
     * Factory-resets the entire database (transactions + cycle data).
     * Requires double confirmation because this is destructive.
     * Delegates to Settingcontroller.requestReset() for the data wipe.
     */
    public void onResetDatabase() {
        System.out.println("\n── Reset Entire Database ────────────────");
        System.out.println("  ⚠  WARNING: This will permanently erase ALL data!");
        System.out.print("  Type 'RESET' to confirm: ");
        String confirm = scanner.nextLine().trim();

        if (!"RESET".equals(confirm)) {
            System.out.println("  Cancelled.");
            return;
        }

        // delegate to controller
        boolean success = settingcontroller.requestReset();

        if (success) {
            System.out.println("  ✓ Database has been fully reset.");
        } else {
            System.out.println("  ✗ Reset may not have completed fully.");
        }
    }
}
