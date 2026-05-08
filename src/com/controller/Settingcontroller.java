package com.controller;
/**
 * Handles user settings and app-level configuration for the Masroofy application.
 * Delegates to {@link AuthController}, {@link HistoryController}, and {@link SetupController}
 * to manage privacy, data reset, and PIN change operations.
 */
public class Settingcontroller {
    /** Manages PIN authentication and privacy lock state. */
    private AuthController    authController;
        /** Manages transaction and cycle data, including deletion. */
    private HistoryController historyController;
    /** Handles budget cycle setup and detection. */
    private SetupController   setupController;
    /**
     * Constructs a SettingController with the required controller dependencies.
     *
     * @param authController    the controller handling authentication and privacy lock
     * @param historyController the controller handling transaction and cycle data
     * @param setupController   the controller handling budget cycle setup and detection
     */
    public Settingcontroller(AuthController authController,
                             HistoryController historyController,
                             SetupController setupController) {
        this.authController    = authController;
        this.historyController = historyController;
        this.setupController   = setupController;
    }
    /**
     * Enables or disables the privacy lock feature.
     * Delegates to {@link AuthController#togglePrivacyLock(boolean)}.
     *
     * @param enabled {@code true} to enable the privacy lock, {@code false} to disable it
     */
    // ── US #12: toggle privacy lock ──────────────────────────────────────
    public void togglePrivacyLock(boolean enabled) {
        authController.togglePrivacyLock(enabled);
    }

/**
     * Resets the application to its uninitialized state by clearing all
     * transaction records and the active budget cycle.
     * Confirms the reset was successful by checking for any remaining active cycle.
     *
     * @return {@code true} if the app was fully reset, {@code false} if cycle data
     *         could not be fully cleared
     */
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

/**
     * Updates the user's PIN to a new value.
     * Delegates to {@link AuthController#updatePIN(String)}.
     *
     * @param newPIN the new plain-text PIN to set
     */
    
    public void changePIN(String newPIN) {
        authController.updatePIN(newPIN);
    }
}