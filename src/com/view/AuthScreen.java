package com.view;

import com.controller.AuthController;
import java.util.Scanner;

/**
 * AuthScreen — View layer for PIN authentication.
 *
 * Responsibilities (thin-view):
 *   • Display a PIN prompt to the user
 *   • Delegate validation to AuthController.validatePIN()
 *   • Show a lockout countdown when AuthController.isLockedOut() is true
 */
public class AuthScreen {

    // ── UI state ──────────────────────────────────────────────────────────
    private String pinInput;

    // ── controller dependency (injected) ──────────────────────────────────
    private final AuthController authController;

    // ── scanner for console input ─────────────────────────────────────────
    private final Scanner scanner;

    public AuthScreen(AuthController authController) {
        this.authController = authController;
        this.scanner = new Scanner(System.in);
    }

    // ── displayPINPrompt() : void ─────────────────────────────────────────
    /**
     * Main entry point.  Shows the PIN prompt in a loop until the user
     * successfully authenticates or is locked out (in which case the
     * lockout timer is shown first, then the prompt reappears).
     */
public void displayPINPrompt() {
    System.out.println("========================================");
    System.out.println("         MASROOFY-Authentication      ");
    System.out.println("========================================");

    // ── FIRST TIME: no PIN set yet, ask user to create one ───────────────
    if (!authController.isPinSet()) {
        System.out.println("\nNo PIN found. Please create a new PIN.");

        while (true) {
            System.out.print("Enter new PIN     : ");
            String newPin = scanner.nextLine().trim();

            if (newPin.isEmpty()) {
                System.out.println("PIN cannot be empty. Try again.");
                continue;
            }

            System.out.print("Confirm new PIN   : ");
            String confirmPin = scanner.nextLine().trim();

            if (!newPin.equals(confirmPin)) {
                System.out.println("PINs do not match. Try again.");
                continue;
            }

            authController.updatePIN(newPin);
            System.out.println("\n✓ PIN created successfully!");
            return; // go straight to dashboard on first setup
        }
    }

    // ── RETURNING USER: validate existing PIN ────────────────────────────
    while (true) {
        if (authController.isLockedOut()) {
            showLockout(30);
            continue;
        }

        System.out.print("\nEnter your PIN: ");
        pinInput = scanner.nextLine().trim();

        if (pinInput.isEmpty()) {
            System.out.println("PIN cannot be empty. Please try again.");
            continue;
        }

        boolean success = authController.validatePIN(pinInput);

        if (success) {
            onUnlock();
            return;
        } else {
            System.out.println("Incorrect PIN.");
        }
    }
}

    // ── onUnlock() : void ─────────────────────────────────────────────────
    /**
     * Called when the PIN is validated successfully.
     * Prints a confirmation message. Navigation to DashboardScreen
     * is handled by the caller (e.g. Main) which owns the cycle context.
     */
    public void onUnlock() {
        System.out.println("\n PIN accepted-welcome back!");
        System.out.println("Navigating to dashboard...\n");
    }

    // ── showLockout(seconds) : void ───────────────────────────────────────
    /**
     * Displays a live countdown while the user is locked out.
     * The View only handles the UI countdown — the actual lockout
     * state is managed by AuthController / SecurityConfig.
     */
    public void showLockout(int seconds) {
        System.out.println("\n⚠  Too many failed attempts!");
        System.out.println("   Please wait before trying again.\n");

        for (int remaining = seconds; remaining > 0; remaining--) {
            System.out.print("\r   Locked out — retry in " + remaining + "s ...  ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("\r   Lockout expired. You may try again.       ");
    }
}
