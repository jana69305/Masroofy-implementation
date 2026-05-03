package com.view;
 
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
 
import com.model.BudgetCycle;
import com.model.CategoryTotal;
import com.model.Transaction;
 

public class DashboardScreen {
 
    private double limitDisplay;
 
    public void displayDailyLimit(double limit) {
        this.limitDisplay = limit;
        System.out.println("Today's Safe Daily Limit: " + limit + " EGP");
    }
 
    public void renderChart(List<CategoryTotal> data) {
        if (data == null || data.isEmpty()) {
            System.out.println("No data available. Log an expense to see your insights.");
            return;
        }
        System.out.println("--- Spending Breakdown ---");
        double totalSpent = 0;
        for (CategoryTotal ct : data) {
            totalSpent += ct.getTotal();
        }
        for (CategoryTotal ct : data) {
            double percentage = totalSpent > 0 ? (ct.getTotal() / totalSpent) * 100 : 0;
            System.out.printf("%s: %.1f%% (%.2f EGP)%n",
                ct.getCategoryName(), percentage, ct.getTotal());
        }
    }
 
    public void onLogExpense() {
        System.out.println("Navigating to Expense Entry Screen...");
    }
 
    public void showAlert(String msg) {
        System.out.println("[ALERT] " + msg);
    }
 
    public void loadDashboard(BudgetCycle cycle, List<Transaction> transactions) {
        if (cycle == null) {
            System.out.println("No active cycle. Please set up a budget cycle first.");
            return;
        }
 
        int remainingDays = cycle.getRemainingDays();
        double limit = remainingDays > 0 ? cycle.getRemainingBalance() / remainingDays : 0.0;
        displayDailyLimit(limit);
 
        Map<Integer, Double> totalsMap = new HashMap<>();
        for (Transaction t : transactions) {
            int catId = t.getCategoryId();
            totalsMap.put(catId, totalsMap.getOrDefault(catId, 0.0) + t.getAmount());
        }
        List<CategoryTotal> totals = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : totalsMap.entrySet()) {
            totals.add(new CategoryTotal("Category " + entry.getKey(), entry.getValue()));
        }
        renderChart(totals);
    }
 
    public double getLimitDisplay() {
        return limitDisplay;
    }
}
 