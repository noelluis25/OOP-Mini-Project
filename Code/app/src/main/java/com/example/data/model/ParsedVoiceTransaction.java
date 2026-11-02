package com.example.data.model;

public class ParsedVoiceTransaction {
    public double amount;
    public TransactionType type;
    public CategoryItem category;
    public String title;
    public String originalText;
    public String paymentMethod;
    public long timestamp;

    public ParsedVoiceTransaction(
            double amount,
            TransactionType type,
            CategoryItem category,
            String title,
            String originalText,
            String paymentMethod,
            long timestamp
    ) {
        this.amount = amount;
        this.type = type != null ? type : TransactionType.EXPENSE;
        this.category = category != null ? category : ExpenseCategory.OTHERS.toItem();
        this.title = (title != null && !title.trim().isEmpty()) ? title.trim() : this.category.label;
        this.originalText = originalText != null ? originalText : "";
        this.paymentMethod = (paymentMethod != null && !paymentMethod.trim().isEmpty()) ? paymentMethod : "UPI";
        this.timestamp = timestamp > 0 ? timestamp : System.currentTimeMillis();
    }
}
