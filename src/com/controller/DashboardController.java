package com.controller;
 
import com.model.BudgetCycle;
import com.model.CategoryTotal;
import com.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class responsible for managing dashboard operations.
 * Handles loading cycle data, updating the daily limit display,
 * and aggregating transaction totals by category.
 */

public class DashboardController {

    /**
 * Loads and returns the current active budget cycle data.
 * @param cycle the current BudgetCycle object
 * @return the same BudgetCycle object passed in
 */

    public BudgetCycle loadCycleData(BudgetCycle cycle) {
        return cycle;
    }
 /**
 * Calculates and returns the current safe daily limit.
 * Divides remaining balance by remaining days in the cycle.
 * @param cycle the current active BudgetCycle
 * @return the calculated daily limit in EGP, or 0.0 if no days remain
 */

    public double updateLimitDisplay(BudgetCycle cycle) {
        if (cycle == null) return 0.0;
        int remainingDays = cycle.getRemainingDays();
        if (remainingDays <= 0) return 0.0;
        return cycle.getRemainingBalance() / remainingDays;
    }
 
    /**
 * Aggregates a list of transactions into category totals.
 * Groups all transactions by category and sums their amounts.
 * @param transactions the list of transactions to aggregate
 * @return a list of CategoryTotal objects each containing a category name and total amount
 */

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
