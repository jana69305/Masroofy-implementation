package com.model;

/**
 * Simple data holder that pairs a Category name with its total spending.
 * Used by StatsScreen to render the pie-chart summary.
 */
public class CategoryTotal {
    private String categoryName;
    private double total;

    public CategoryTotal() {}

    public CategoryTotal(String categoryName, double total) {
        this.categoryName = categoryName;
        this.total = total;
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
