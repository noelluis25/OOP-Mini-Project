package com.example.ui.viewmodel;

public enum BreakdownPeriod {
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    CUSTOM("Custom");

    public final String label;

    BreakdownPeriod(String label) {
        this.label = label;
    }
}
