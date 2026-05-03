package com.controller;

import com.model.Transaction;
import java.util.List;

public class Settingscontroller {

  

    private AuthController    authController;
    private HistoryController historyController;

    public Settingscontroller(AuthController authController,
                               HistoryController historyController) {
        this.authController    = authController;
        this.historyController = historyController;
    }

  public void togglePrivacyLock(boolean enabled) {
    authController.togglePrivacyLock(enabled);
}

    public void requestReset() {
    
        List<Transaction> all = historyController.getAll(1);

       
        for (Transaction t : all) {
            historyController.deleteTransaction(t.getTransactionId());
        }

        System.out.println("All transactions deleted. Cycle reset.");
    }

   
    public void changePIN(String newPIN) {
        authController.updatePIN(newPIN);
    }
}