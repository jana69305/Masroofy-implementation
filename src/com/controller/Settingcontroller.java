package com.controller;

import com.model.Transaction;
import java.util.List;

public class Settingcontroller {

  

    private AuthController    authController;
    private HistoryController historyController;

    public Settingcontroller(AuthController authController,
                             HistoryController historyController) {
        this.authController    = authController;
        this.historyController = historyController;
    }

  public void togglePrivacyLock(boolean enabled) {
    authController.togglePrivacyLock(enabled);
}

   public void requestReset(int cycleId) {

    List<Transaction> all = historyController.getAll(cycleId);

    if (all == null || all.isEmpty()) {
        System.out.println("No transactions to reset.");
        return;
    }

    // delete safely (copy loop)
    for (int i = 0; i < all.size(); i++) {
        historyController.deleteTransaction(all.get(i).getTransactionId());
    }

    System.out.println("All transactions deleted. Cycle reset.");
}

   
    public void changePIN(String newPIN) {
        authController.updatePIN(newPIN);
    }
}