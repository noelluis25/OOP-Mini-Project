package com.example.ui.viewmodel;

import com.example.data.model.ExpenseCategory;

public class CategoryBreakdownItem {
    public final ExpenseCategory category;
    public final double amount;
    public final float percentage;
    public final int transactionCount;

    public CategoryBreakdownItem(ExpenseCategory category, double amount, float percentage, int transactionCount) {
        this.category = category;
        this.amount = amount;
        this.percentage = percentage;
        this.transactionCount = transactionCount;
    }
}
