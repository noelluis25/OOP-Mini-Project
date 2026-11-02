package com.example.ui.viewmodel;

public enum TransactionFilter {
    ALL("All"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    FOOD("🍔 Food"),
    TRANSPORT("🚌 Transport"),
    STUDY("📚 Study");

    public final String label;

    TransactionFilter(String label) {
        this.label = label;
    }
}
