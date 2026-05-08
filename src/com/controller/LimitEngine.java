package com.controller;

import com.model.BudgetCycle;

/**
 * Calculates and manages the safe daily spending limit for the Masroofy application.
 * Uses the remaining balance and remaining days in a budget cycle to determine
 * how much the user can safely spend per day.
 */

public class LimitEngine {

    /**
     * Calculates and updates the safe daily spending limit for the given budget cycle.
     * The limit is computed by dividing the remaining balance by the number of remaining days.
     * Does nothing if there are no remaining days in the cycle.
     *
     * @param cycle the {@link BudgetCycle} whose daily limit will be recalculated
     */

public void calcDailyLimit(BudgetCycle cycle) {

   int remainingDays = cycle.getRemainingDays();

 if (remainingDays > 0) {

        double newLimit = cycle.getRemainingBalance() / remainingDays;

        
        cycle.setSafeDailyLimit(newLimit);
    }
}
 /**
     * Processes a day rollover by recalculating the daily spending limit
     * and printing the updated limit to the console.
     *
     * @param cycle the {@link BudgetCycle} to process the rollover for
     */
    
public void processRollover(BudgetCycle cycle) {

  
    calcDailyLimit(cycle);
    System.out.println("New day! Daily limit updated to: "
            + String.format("%.2f", cycle.getSafeDailyLimit()) + " EGP");
}
}