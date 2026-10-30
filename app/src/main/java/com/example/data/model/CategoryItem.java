package com.example.data.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryItem {
    public final String name;
    public final String label;
    public final String emoji;
    public final int colorValue;
    public final int bgLightColor;

    public CategoryItem(String name, String label, String emoji, int colorValue, int bgLightColor) {
        this.name = name;
        this.label = label;
        this.emoji = emoji;
        this.colorValue = colorValue;
        this.bgLightColor = bgLightColor;
    }

    public static CategoryItem fromName(String name) {
        if (name == null) {
            return ExpenseCategory.OTHERS.toItem();
        }
        for (ExpenseCategory exp : ExpenseCategory.values()) {
            if (exp.name().equalsIgnoreCase(name) || exp.label.equalsIgnoreCase(name)) {
                return exp.toItem();
            }
        }
        for (IncomeCategory inc : IncomeCategory.values()) {
            if (inc.name().equalsIgnoreCase(name) || inc.label.equalsIgnoreCase(name)) {
                return inc.toItem();
            }
        }
        return ExpenseCategory.OTHERS.toItem();
    }

    public static List<CategoryItem> getCategoriesForType(TransactionType type) {
        List<CategoryItem> list = new ArrayList<>();
        if (type == TransactionType.EXPENSE) {
            for (ExpenseCategory exp : ExpenseCategory.values()) {
                list.add(exp.toItem());
            }
        } else {
            for (IncomeCategory inc : IncomeCategory.values()) {
                list.add(inc.toItem());
            }
        }
        return list;
    }
}
