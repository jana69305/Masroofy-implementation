package com.view;
 
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
 
import com.model.BudgetCycle;
import com.model.CategoryTotal;
import com.model.Transaction;
import com.model.Category;
import java.util.stream.Collectors;

/**
 * View class responsible for displaying the main dashboard screen.
 * Shows the safe daily limit, spending breakdown by category,
 * and handles navigation to the expense entry screen.
 */

public class DashboardScreen {
    /** The current safe daily limit value displayed on the dashboard. */
    private double limitDisplay;
    
    /**
 * Displays the current safe daily limit on the dashboard.
 * @param limit the calculated daily limit in EGP to display
 */

    public void displayDailyLimit(double limit) {
        this.limitDisplay = limit;
        System.out.println("Today's Safe Daily Limit: " + limit + " EGP");
    }
 /**
 * Renders a text-based spending breakdown chart by category.
 * Shows each category with its percentage and total amount spent.
 * Displays a placeholder message if no data is available.
 * @param data list of CategoryTotal objects to display in the chart
 */

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
 /**
 * Handles navigation to the expense entry screen.
 * Called when the user wants to log a new expense from the dashboard.
 */

    public void onLogExpense() {
        System.out.println("Navigating to Expense Entry Screen...");
    }
 /**
 * Displays an alert message on the dashboard.
 * @param msg the alert message to display
 */
    public void showAlert(String msg) {
        System.out.println("[ALERT] " + msg);
    }
 /**
 * Loads and displays the full dashboard for the active budget cycle.
 * Shows the daily limit and spending breakdown by category.
 * @param cycle the current active BudgetCycle
 * @param transactions the list of transactions for the current cycle
 */
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
        List<Category> categories = Category.fetchAll();
        for (Map.Entry<Integer, Double> entry : totalsMap.entrySet()) {
            String catName = categories.stream()
            .filter(c -> c.getCategoryId() == entry.getKey())
            .map(Category::getName)
            .findFirst()
            .orElse("Category " + entry.getKey());
            totals.add(new CategoryTotal(catName, entry.getValue()));
        }
        renderChart(totals);
    }
    
 /**
 * Returns the current limit display value.
 * @return the safe daily limit currently displayed on the dashboard
 */

    public double getLimitDisplay() {
        return limitDisplay;
    }
}
 