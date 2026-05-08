package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an expense category in the Masroofy budgeting system.
 * Each category has a name, icon, and a flag indicating if it is custom or default.
 */

public class Category {
    private int categoryId;
    private String name;
    private String iconId;
    private boolean isCustom;

    /**
 * Default constructor for Category.
 */
    public Category() {}
/**
 * Creates a new Category with all fields specified.
 * @param categoryId unique identifier for the category
 * @param name the display name of the category (e.g. Food, Transport)
 * @param iconId the icon identifier for this category
 * @param isCustom true if this is a user-created category, false if default
 */
    public Category(int categoryId, String name, String iconId, boolean isCustom) {
        this.categoryId = categoryId;
        this.name = name;
        this.iconId = iconId;
        this.isCustom = isCustom;
    }

/**
 * Returns the icon identifier for this category.
 * @return the icon ID string
 */

    public String getIcon() {
        return iconId;
    }

/**
 * Returns the full list of all available default categories.
 * @return list of all default Category objects
 */

    public static List<Category> fetchAll() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Food",          "[F]", false));
        categories.add(new Category(2, "Transport",     "[T]", false));
        categories.add(new Category(3, "Entertainment", "[E]", false));
        categories.add(new Category(4, "Other",         "[O]", false));
        return categories;
    }

    /** @return the category ID */
    public int getCategoryId() { return categoryId; }
    /** @param categoryId the category ID to set */
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    /** @return the category name */
    public String getName() { return name; }
    /** @param name the category name to set */
    public void setName(String name) { this.name = name; }
    /** @return the icon ID string */
    public String getIconId() { return iconId; }
    /** @param iconId the icon ID to set */
    public void setIconId(String iconId) { this.iconId = iconId; }
    /** @return true if this is a custom category*/
    public boolean isCustom() { return isCustom; }
    /** @param custom true to mark this category as custom */
    public void setCustom(boolean custom) { isCustom = custom; }
}