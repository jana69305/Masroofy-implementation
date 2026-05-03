package com.controller;
 
import com.model.BudgetCycle;
import com.model.CategoryTotal;
import com.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
public class DashboardController {

    public BudgetCycle loadCycleData(BudgetCycle cycle) {
        return cycle;
    }
 
    public double updateLimitDisplay(BudgetCycle cycle) {
        if (cycle == null) return 0.0;
        int remainingDays = cycle.getRemainingDays();
        if (remainingDays <= 0) return 0.0;
        return cycle.getRemainingBalance() / remainingDays;
    }
 
    public List<CategoryTotal> aggregateTotals(List<Transaction> transactions) {
        Map<Integer,Double> totalsMap = new HashMap<>();
 
        for (Transaction t : transactions) {
            int catId = t.getCategoryId();
            totalsMap.put(catId, totalsMap.getOrDefault(catId, 0.0) + t.getAmount());
        }
 
        List<CategoryTotal> result = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : totalsMap.entrySet()) {
            CategoryTotal ct = new CategoryTotal(
                "Category " + entry.getKey(),
                entry.getValue()
            );
            result.add(ct);
        }
        return result;
    }
}
