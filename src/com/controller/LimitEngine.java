package com.controller;

import com.model.BudgetCycle;

public class LimitEngine {

public void calcDailyLimit(BudgetCycle cycle) {

   int remainingDays = cycle.getRemainingDays();

 if (remainingDays > 0) {

        double newLimit = cycle.getRemainingBalance() / remainingDays;

        
        cycle.setSafeDailyLimit(newLimit);
    }
}

public void processRollover(BudgetCycle cycle) {

  
    calcDailyLimit(cycle);
    System.out.println("New day! Daily limit updated to: "
            + String.format("%.2f", cycle.getSafeDailyLimit()) + " EGP");
}
}