package com.controller;
 
import com.model.BudgetCycle;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupController {

     public boolean detectActiveCycle(BudgetCycle cycle) {
        return cycle != null && !cycle.isExpired();
    }
 

    public BudgetCycle startNewCycle(double allowance, LocalDate start, LocalDate end) {
        BudgetCycle cycle = new BudgetCycle();
        cycle.setTotalAllowance(allowance);
        cycle.setStartDate(start);
        cycle.setEndDate(end);
        cycle.initializeCycle();
        return cycle;
    }
 
    public void resetCycle(BudgetCycle cycle) {
        if (cycle != null) {
            cycle.initializeCycle();
        }
    }
 
    public boolean isPositive(double allowance) {
        return allowance > 0;
    }
 
    public boolean isValidate(double allowance, LocalDate start, LocalDate end) {
        if (!isPositive(allowance)) return false;
        if (start == null || end == null) return false;
        if (!end.isAfter(start)) return false;
        return true;
    }
}
