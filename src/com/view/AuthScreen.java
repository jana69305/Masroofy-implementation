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
        System.out.println("         MASROOFY — Authentication      ");
        System.out.println("========================================");

        while (true) {
            // ── check lockout before every attempt ───────────────────
            if (authController.isLockedOut()) {
                showLockout(30);          // LOCKOUT_SECS from AuthController
                continue;                 // re-check after countdown
            }

            System.out.print("\nEnter your PIN: ");
            pinInput = scanner.nextLine().trim();

            if (pinInput.isEmpty()) {
                System.out.println("PIN cannot be empty. Please try again.");
                continue;
            }

            // ── delegate validation to the controller ────────────────
            boolean success = authController.validatePIN(pinInput);

            if (success) {
                onUnlock();
                return;                   // exit the authentication loop
            } else {
                // controller already recorded the failed attempt
                System.out.println("Incorrect PIN.");
            }
        }
    }

    // ── onUnlock() : void ─────────────────────────────────────────────────
    /**
     * Called when the PIN is validated successfully.
     * Prints a confirmation message; in a full app this would navigate
     * to the DashboardScreen.
     */
    public void onUnlock() {
        System.out.println("\n✓ PIN accepted — welcome back!");
        System.out.println("Navigating to dashboard...\n");
        // TODO: navigate to DashboardScreen when it is implemented
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
