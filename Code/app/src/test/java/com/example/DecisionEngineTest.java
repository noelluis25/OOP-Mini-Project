package com.example;

import com.example.data.model.DecisionResult;
import com.example.data.model.Transaction;
import com.example.data.model.TransactionType;
import com.example.data.model.UserPreferences;
import com.example.data.model.UserProfile;
import com.example.data.repository.DecisionEngine;
import com.example.ui.viewmodel.BreakdownPeriod;
import com.example.ui.viewmodel.DashboardData;
import com.example.ui.viewmodel.TransactionFilter;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DecisionEngineTest {

    @Test
    public void evaluatePurchase_NotRightNow_WhenExceedsBalance() {
        UserProfile user = new UserProfile(1L, "Alex", "Student", "🎓", 0xFF0F8A74L, 5000.0, "₹", System.currentTimeMillis());
        DashboardData data = new DashboardData(
                user,
                List.of(user),
                new UserPreferences(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                0.0,
                3000.0,
                2000.0, // totalMoney
                1500.0, // totalSavedInGoals
                500.0,  // Available balance is 500
                0.6f,
                new ArrayList<>(),
                new ArrayList<>(),
                "",
                TransactionFilter.ALL,
                BreakdownPeriod.THIS_MONTH
        );

        DecisionResult result = DecisionEngine.evaluatePurchase("Headphones", 1200.0, data);
        assertNotNull(result);
        assertEquals(DecisionResult.Status.NOT_RIGHT_NOW, result.status);
        assertTrue(result.explanation.contains("deficit"));
    }

    @Test
    public void evaluatePurchase_GoForIt_WhenBudgetSufficientAndBufferHealthy() {
        UserProfile user = new UserProfile(1L, "Alex", "Student", "🎓", 0xFF0F8A74L, 10000.0, "₹", System.currentTimeMillis());
        DashboardData data = new DashboardData(
                user,
                List.of(user),
                new UserPreferences(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                0.0,
                1000.0,
                9000.0, // totalMoney
                1000.0, // totalSavedInGoals
                8000.0, // Available balance 8000
                0.1f,
                new ArrayList<>(),
                new ArrayList<>(),
                "",
                TransactionFilter.ALL,
                BreakdownPeriod.THIS_MONTH
        );

        DecisionResult result = DecisionEngine.evaluatePurchase("Snacks", 100.0, data);
        assertNotNull(result);
        assertEquals(DecisionResult.Status.GO_FOR_IT, result.status);
    }

    @Test
    public void evaluatePurchase_ThinkTwice_WhenLargePercentageOfMonthlyBudget() {
        UserProfile user = new UserProfile(1L, "Alex", "Student", "🎓", 0xFF0F8A74L, 5000.0, "₹", System.currentTimeMillis());
        DashboardData data = new DashboardData(
                user,
                List.of(user),
                new UserPreferences(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                0.0,
                1000.0,
                4000.0, // totalMoney
                0.0,    // totalSavedInGoals
                4000.0, // Available balance 4000
                0.2f,
                new ArrayList<>(),
                new ArrayList<>(),
                "",
                TransactionFilter.ALL,
                BreakdownPeriod.THIS_MONTH
        );

        // Price 2000 is 40% of 5000 budget
        DecisionResult result = DecisionEngine.evaluatePurchase("Jacket", 2000.0, data);
        assertNotNull(result);
        assertEquals(DecisionResult.Status.THINK_TWICE, result.status);
    }
}
