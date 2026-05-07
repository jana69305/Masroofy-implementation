package com.controller;

public class Settingcontroller {

    private AuthController    authController;
    private HistoryController historyController;
    private SetupController   setupController;

    public Settingcontroller(AuthController authController,
                             HistoryController historyController,
                             SetupController setupController) {
        this.authController    = authController;
        this.historyController = historyController;
        this.setupController   = setupController;
    }

    // ── US #12: toggle privacy lock ──────────────────────────────────────
    public void togglePrivacyLock(boolean enabled) {
        authController.togglePrivacyLock(enabled);
    }


    public boolean requestReset() {
        // 1. clear transaction records
        historyController.deleteTransactions();

        // 2. clear cycle record
        historyController.deleteCycle();

        // 3. confirm app is back to uninitialized state
        boolean noActiveCycle = !setupController.detectActiveCycle();

        if (noActiveCycle) {
            System.out.println("All data cleared. App is in uninitialized state.");
        } else {
            System.out.println("Warning: cycle data may not have been fully cleared.");
        }

        return noActiveCycle;
    }


    public void changePIN(String newPIN) {
        authController.updatePIN(newPIN);
    }
}