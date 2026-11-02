package com.example.ui.viewmodel;

import com.example.data.model.Goal;
import com.example.data.model.Transaction;
import com.example.data.model.UserPreferences;
import com.example.data.model.UserProfile;
import java.util.Collections;
import java.util.List;

public class DashboardData {
    public final UserProfile activeUser;
    public final List<UserProfile> allUsers;
    public final UserPreferences preferences;
    public final List<Transaction> allTransactions;
    public final List<Transaction> filteredTransactions;
    public final List<Goal> goals;
    public final double totalIncomeThisMonth;
    public final double totalSpentThisMonth;
    public final double totalMoney;
    public final double totalSavedInGoals;
    public final double currentBalance; // Available to Spend
    public final float budgetUsedFraction;
    public final List<CategoryBreakdownItem> categoryBreakdown;
    public final List<BarChartPoint> barChartPoints;
    public final String searchQuery;
    public final TransactionFilter selectedFilter;
    public final BreakdownPeriod selectedBreakdownPeriod;

    public DashboardData(
            UserProfile activeUser,
            List<UserProfile> allUsers,
            UserPreferences preferences,
            List<Transaction> allTransactions,
            List<Transaction> filteredTransactions,
            List<Goal> goals,
            double totalIncomeThisMonth,
            double totalSpentThisMonth,
            double totalMoney,
            double totalSavedInGoals,
            double currentBalance,
            float budgetUsedFraction,
            List<CategoryBreakdownItem> categoryBreakdown,
            List<BarChartPoint> barChartPoints,
            String searchQuery,
            TransactionFilter selectedFilter,
            BreakdownPeriod selectedBreakdownPeriod
    ) {
        this.activeUser = activeUser;
        this.allUsers = allUsers;
        this.preferences = preferences;
        this.allTransactions = allTransactions;
        this.filteredTransactions = filteredTransactions;
        this.goals = goals;
        this.totalIncomeThisMonth = totalIncomeThisMonth;
        this.totalSpentThisMonth = totalSpentThisMonth;
        this.totalMoney = totalMoney;
        this.totalSavedInGoals = totalSavedInGoals;
        this.currentBalance = currentBalance;
        this.budgetUsedFraction = budgetUsedFraction;
        this.categoryBreakdown = categoryBreakdown;
        this.barChartPoints = barChartPoints;
        this.searchQuery = searchQuery;
        this.selectedFilter = selectedFilter;
        this.selectedBreakdownPeriod = selectedBreakdownPeriod;
    }

    public static DashboardData empty() {
        return new DashboardData(
                null,
                Collections.emptyList(),
                new UserPreferences(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0f,
                Collections.emptyList(),
                Collections.emptyList(),
                "",
                TransactionFilter.ALL,
                BreakdownPeriod.THIS_MONTH
        );
    }
}
