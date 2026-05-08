package com.model;

/**
 * Simple data holder that pairs a Category name with its total spending.
 * Used by StatsScreen to render the pie-chart summary.
 */
public class CategoryTotal {
    /** The name of the spending category. */
    private String categoryName;
    /** The total amount spent in this category. */
    private double total;
    /** Default no-argument constructor. */
    public CategoryTotal() {}
/**
     * Constructs a CategoryTotal with the specified category name and total spending.
     *
     * @param categoryName the name of the category
     * @param total        the total amount spent in this category
     */
    public CategoryTotal(String categoryName, double total) {
        this.categoryName = categoryName;
        this.total = total;
    }
/** @return the category name */
    public String getCategoryName() { return categoryName; }
       /** @param categoryName the category name to set */
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    /** @return the total amount spent in this category */
    public double getTotal() { return total; }
    /** @param total the total spending to set */
    public void setTotal(double total) { this.total = total; }
}
