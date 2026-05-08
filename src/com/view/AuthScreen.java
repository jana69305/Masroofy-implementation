package com.view;

import com.controller.AuthController;
import java.util.Scanner;
/**
 * Handles the authentication screen for the Masroofy application.
 * Displays the PIN prompt, manages PIN creation for first-time users,
 * and handles lockout behavior after repeated failed attempts.
 */
public class AuthScreen {

    /** The PIN input entered by the user. */
    private String pinInput;

    /** The controller responsible for PIN validation and authentication logic. */
    private final AuthController authController;

    /** Scanner used to read user input from the console. */
    private final Scanner scanner;

     /**
     * Constructs an AuthScreen with the given authentication controller.
     *
     * @param authController the {@link AuthController} handling PIN logic
     */
    public AuthScreen(AuthController authController) {
        this.authController = authController;
        this.scanner = new Scanner(System.in);
    }

/**
     * Displays the authentication screen to the user.
     * If no PIN has been set, prompts the user to create and confirm a new PIN.
     * Otherwise, prompts for the existing PIN and handles validation,
     * including lockout if the maximum number of failed attempts is exceeded.
     */
public void displayPINPrompt() {
    System.out.println("========================================");
    System.out.println("         MASROOFY-Authentication      ");
    System.out.println("========================================");

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
            return; 
        }
    }

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

        String status = authController.validatePIN(pinInput);

        switch (status) {
            case "SUCCESS":
                onUnlock();
                return;
            case "LOCKED":
                System.out.println("\n⚠  Too many failed attempts!");
                showLockout(30);
                break;
            case "INVALID":
            default:
                System.out.println("Wrong PIN. Try again.");
                break;
        }
    }
}

  /**
     * Called when the user successfully authenticates.
     * Prints a welcome message and navigates to the dashboard.
     */
    public void onUnlock() {
        System.out.println("\n PIN accepted-welcome back!");
        System.out.println("Navigating to dashboard...\n");
    }

 /**
     * Displays a countdown lockout timer to the user.
     * Blocks the thread for the given number of seconds, updating the console each second.
     *
     * @param seconds the number of seconds to lock the user out for
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
