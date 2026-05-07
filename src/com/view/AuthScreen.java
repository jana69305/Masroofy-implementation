package com.view;

import com.controller.AuthController;
import java.util.Scanner;


public class AuthScreen {

    private String pinInput;

    private final AuthController authController;

    private final Scanner scanner;

    public AuthScreen(AuthController authController) {
        this.authController = authController;
        this.scanner = new Scanner(System.in);
    }


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

  
    public void onUnlock() {
        System.out.println("\n PIN accepted-welcome back!");
        System.out.println("Navigating to dashboard...\n");
    }

 
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
