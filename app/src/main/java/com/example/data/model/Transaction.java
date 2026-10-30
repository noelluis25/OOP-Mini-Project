package com.example.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    public String title;
    public double amount;
    public String type; // "EXPENSE" or "INCOME"
    public String category; // ExpenseCategory.name()
    public long timestamp;
    public String note;
    public String paymentMethod; // "UPI", "Cash", "Card"

    public Transaction(long id, long userId, String title, double amount, String type, String category, long timestamp, String note, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.timestamp = timestamp;
        this.note = note != null ? note : "";
        this.paymentMethod = paymentMethod != null ? paymentMethod : "UPI";
    }

    @Ignore
    public Transaction(long userId, String title, double amount, String type, String category, long timestamp, String note, String paymentMethod) {
        this(0L, userId, title, amount, type, category, timestamp, note, paymentMethod);
    }

    public boolean isExpense() {
        return TransactionType.EXPENSE.name().equals(type);
    }

    public boolean isIncome() {
        return TransactionType.INCOME.name().equals(type);
    }

    public boolean isSavings() {
        return TransactionType.SAVINGS.name().equals(type);
    }

    public ExpenseCategory getExpenseCategory() {
        return ExpenseCategory.fromName(category);
    }

    public CategoryItem getCategoryItem() {
        if (isSavings()) {
            return new CategoryItem("SAVINGS", "Goal Savings", "🎯", 0xFF7C3AED, 0xFFF3E8FF);
        }
        return CategoryItem.fromName(category);
    }
}
