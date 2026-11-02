package com.example.ui.viewmodel;

public class BarChartPoint {
    public final String label;
    public final double expense;
    public final double income;

    public BarChartPoint(String label, double expense, double income) {
        this.label = label;
        this.expense = expense;
        this.income = income;
    }
}
