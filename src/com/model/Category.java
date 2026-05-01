package com.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private int categoryId;
    private String name;
    private String iconId;
    private boolean isCustom;

    public Category() {}

    public Category(int categoryId, String name, String iconId, boolean isCustom) {
        this.categoryId = categoryId;
        this.name = name;
        this.iconId = iconId;
        this.isCustom = isCustom;
    }

    public String getIcon() {
        return iconId;
    }

    public static List<Category> fetchAll() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Food",          "[F]", false));
        categories.add(new Category(2, "Transport",     "[T]", false));
        categories.add(new Category(3, "Entertainment", "[E]", false));
        categories.add(new Category(4, "Other",         "[O]", false));
        return categories;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIconId() { return iconId; }
    public void setIconId(String iconId) { this.iconId = iconId; }
    public boolean isCustom() { return isCustom; }
    public void setCustom(boolean custom) { isCustom = custom; }
}