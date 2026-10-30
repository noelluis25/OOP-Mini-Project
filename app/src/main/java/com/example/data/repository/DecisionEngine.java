package com.example.data.repository;

import com.example.data.model.DecisionResult;
import com.example.data.model.Goal;
import com.example.data.model.Transaction;
import com.example.ui.viewmodel.DashboardData;
import java.util.Calendar;

public class DecisionEngine {

    public static DecisionResult evaluatePurchase(
            String itemName,
            double itemPrice,
            DashboardData dashboardData
    ) {
        String finalName = (itemName == null || itemName.trim().isEmpty()) ? "Planned Purchase" : itemName.trim();
        double price = Math.max(0, itemPrice);

        double currentBalance = dashboardData.currentBalance;
        double balanceAfter = currentBalance - price;
        double monthlyBudget = dashboardData.activeUser != null && dashboardData.activeUser.monthlyBudget > 0
                ? Math.abs(dashboardData.activeUser.monthlyBudget)
                : 5000.0;

        Calendar cal = Calendar.getInstance();
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        int daysRemaining = Math.max(1, daysInMonth - currentDay + 1);

        double activeGoalsRemaining = 0.0;
        for (Goal g : dashboardData.goals) {
            if (g.savedAmount < g.targetAmount) {
                activeGoalsRemaining += (g.targetAmount - g.savedAmount);
            }
        }

        double essentialSpentThisMonth = 0.0;
        for (Transaction tx : dashboardData.allTransactions) {
            if (tx.isExpense()) {
                String cat = tx.category != null ? tx.category.toUpperCase() : "";
                if (cat.contains("FOOD") || cat.contains("TRANSPORT") || cat.contains("STUDY")
                        || cat.contains("BILLS") || cat.contains("HEALTH")) {
                    essentialSpentThisMonth += tx.amount;
                }
            }
        }
        int daysPassed = Math.max(1, currentDay);
        double dailyEssentialRate = essentialSpentThisMonth / daysPassed;
        if (dailyEssentialRate < 80.0) {
            dailyEssentialRate = 80.0;
        }
        double estimatedEssentialsNeeded = dailyEssentialRate * daysRemaining;

        DecisionResult.Status status;
        String explanation;
        String suggestion;

        if (balanceAfter < 0) {
            status = DecisionResult.Status.NOT_RIGHT_NOW;
            double deficit = Math.abs(balanceAfter);
            explanation = "This item costs ₹" + Math.round(price) + " but you only have ₹" + Math.round(currentBalance)
                    + " available. Buying it creates a ₹" + Math.round(deficit) + " deficit.";
            suggestion = "Hold off for now! Create a Goal Jar instead of overspending your available balance.";

        } else if (balanceAfter < estimatedEssentialsNeeded) {
            status = DecisionResult.Status.NOT_RIGHT_NOW;
            explanation = "Buying this leaves you with ₹" + Math.round(balanceAfter) + ", which is less than your estimated ₹"
                    + Math.round(estimatedEssentialsNeeded) + " needed for essentials (food, transport, bills) for the remaining "
                    + daysRemaining + " days of the month.";
            suggestion = "Protect your pocket money for daily food and travel first before non-essential purchases.";

        } else if (balanceAfter < estimatedEssentialsNeeded + (activeGoalsRemaining * 0.25)
                || price > monthlyBudget * 0.30
                || balanceAfter < currentBalance * 0.20) {
            status = DecisionResult.Status.THINK_TWICE;
            if (price > monthlyBudget * 0.30) {
                explanation = "You can afford this item, but it takes " + Math.round((price / monthlyBudget) * 100)
                        + "% of your entire monthly pocket money. You'll have ₹" + Math.round(balanceAfter) + " left.";
            } else {
                explanation = "You'll have ₹" + Math.round(balanceAfter) + " left after purchase. It covers essentials (₹"
                        + Math.round(estimatedEssentialsNeeded) + "), but leaves a thin buffer for your savings goals.";
            }
            suggestion = "Pause for 24 hours to decide if you really need it, or save up using a Goal Jar.";

        } else {
            status = DecisionResult.Status.GO_FOR_IT;
            explanation = "You'll have ₹" + Math.round(balanceAfter) + " remaining, which easily covers your estimated essentials (₹"
                    + Math.round(estimatedEssentialsNeeded) + " for " + daysRemaining + " days) and keeps your budget healthy!";
            suggestion = "It fits your budget and savings plan comfortably. Enjoy your purchase responsibly!";
        }

        return new DecisionResult(
                status,
                finalName,
                price,
                currentBalance,
                balanceAfter,
                estimatedEssentialsNeeded,
                daysRemaining,
                activeGoalsRemaining,
                explanation,
                suggestion
        );
    }
}
