package com.example.data.model;

public class DecisionResult {

    public enum Status {
        GO_FOR_IT("🟢 GO FOR IT", 0xFF10B981, 0xFFE8F7EE),
        THINK_TWICE("🟡 THINK TWICE", 0xFFF59E0B, 0xFFFEF3C7),
        NOT_RIGHT_NOW("🔴 NOT RIGHT NOW", 0xFFEF4444, 0xFFFEE2E2);

        public final String label;
        public final int color;
        public final int bgColor;

        Status(String label, int color, int bgColor) {
            this.label = label;
            this.color = color;
            this.bgColor = bgColor;
        }
    }

    public final Status status;
    public final String itemName;
    public final double itemPrice;
    public final double currentBalance;
    public final double balanceAfterPurchase;
    public final double estimatedEssentialsNeeded;
    public final int daysRemainingInMonth;
    public final double activeGoalsRemaining;
    public final String explanation;
    public final String suggestion;

    public DecisionResult(
            Status status,
            String itemName,
            double itemPrice,
            double currentBalance,
            double balanceAfterPurchase,
            double estimatedEssentialsNeeded,
            int daysRemainingInMonth,
            double activeGoalsRemaining,
            String explanation,
            String suggestion
    ) {
        this.status = status;
        this.itemName = itemName != null ? itemName.trim() : "Item";
        this.itemPrice = itemPrice;
        this.currentBalance = currentBalance;
        this.balanceAfterPurchase = balanceAfterPurchase;
        this.estimatedEssentialsNeeded = estimatedEssentialsNeeded;
        this.daysRemainingInMonth = daysRemainingInMonth;
        this.activeGoalsRemaining = activeGoalsRemaining;
        this.explanation = explanation;
        this.suggestion = suggestion;
    }
}
