package com.example;

import android.content.Context;
import com.example.data.db.AppDatabase;
import com.example.data.model.CategoryItem;
import com.example.data.model.ExpenseCategory;
import com.example.data.model.Goal;
import com.example.data.model.IncomeCategory;
import com.example.data.model.TransactionType;
import com.example.data.repository.PocketPalRepository;
import com.example.data.storage.JsonStorageManager;
import com.example.ui.components.UiUtils;
import com.example.ui.viewmodel.AppScreen;
import com.example.ui.viewmodel.PocketPalViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ExampleRobolectricTest {

    @Test
    public void categories_areCorrectForExpenseAndIncome() {
        List<CategoryItem> expenseCats = CategoryItem.getCategoriesForType(TransactionType.EXPENSE);
        List<CategoryItem> incomeCats = CategoryItem.getCategoriesForType(TransactionType.INCOME);

        assertFalse(expenseCats.isEmpty());
        assertFalse(incomeCats.isEmpty());
        assertEquals(ExpenseCategory.FOOD.label, expenseCats.get(0).label);
        assertEquals(IncomeCategory.ALLOWANCE.label, incomeCats.get(0).label);
    }

    @Test
    public void budgetColor_transitionsFromGreenToYellowToRed() {
        int color0 = UiUtils.interpolateBudgetColor(0.0f);
        int color50 = UiUtils.interpolateBudgetColor(0.5f);
        int color100 = UiUtils.interpolateBudgetColor(1.0f);

        assertEquals(0xFF10B981, color0);
        assertEquals(0xFFFACC15, color50);
        assertEquals(0xFFEF4444, color100);
        assertNotEquals(color0, color50);
        assertNotEquals(color50, color100);
    }

    @Test
    public void viewModel_onboardingAndProfileFlow() {
        Context context = RuntimeEnvironment.getApplication();
        AppDatabase database = AppDatabase.getInstance(context);
        JsonStorageManager jsonManager = new JsonStorageManager(context);
        PocketPalRepository repository = new PocketPalRepository(
                database.userDao(),
                database.transactionDao(),
                database.goalDao(),
                database.userPreferencesDao(),
                jsonManager
        );

        PocketPalViewModel viewModel = new PocketPalViewModel(repository);

        // Initially onboarding
        assertEquals(AppScreen.ONBOARDING, viewModel.getCurrentScreen());

        // Complete onboarding -> proceeds to CREATE_PROFILE since no users exist
        viewModel.completeOnboardingAndProceed();
        assertEquals(AppScreen.CREATE_PROFILE, viewModel.getCurrentScreen());

        // Create profile
        viewModel.createNewProfile("Alex", "Student", "🎓", 0xFF0F8A74L, 5000.0);
        assertEquals(AppScreen.HOME, viewModel.getCurrentScreen());
        assertEquals("Alex", viewModel.getDashboardData().activeUser.name);

        // Add expense
        viewModel.saveTransaction("Lunch", 150.0, TransactionType.EXPENSE, ExpenseCategory.FOOD.toItem(), "", "UPI", System.currentTimeMillis());
        assertEquals(1, viewModel.getDashboardData().allTransactions.size());
        assertEquals(150.0, viewModel.getDashboardData().totalSpentThisMonth, 0.01);

        // Add goal & contribute
        viewModel.addGoal("Earbuds", 1000.0, 0.0, "🎧", 0xFF0F8A74L, 30);
        assertEquals(1, viewModel.getDashboardData().goals.size());
        Goal goal = viewModel.getDashboardData().goals.get(0);
        viewModel.contributeToGoal(goal, 200.0);
        assertEquals(200.0, viewModel.getDashboardData().goals.get(0).savedAmount, 0.01);
    }

    @Test
    public void savingsGoal_customContributionAndValidation() {
        Context context = RuntimeEnvironment.getApplication();
        AppDatabase database = AppDatabase.getInstance(context);
        JsonStorageManager jsonManager = new JsonStorageManager(context);
        PocketPalRepository repository = new PocketPalRepository(
                database.userDao(),
                database.transactionDao(),
                database.goalDao(),
                database.userPreferencesDao(),
                jsonManager
        );

        PocketPalViewModel viewModel = new PocketPalViewModel(repository);
        viewModel.completeOnboardingAndProceed();
        viewModel.createNewProfile("Riya", "College", "🎓", 0xFF0F8A74L, 2000.0);

        // Budget is 2000. Current available balance is 2000.
        assertEquals(2000.0, viewModel.getDashboardData().currentBalance, 0.01);
        assertEquals(0.0, viewModel.getDashboardData().totalSpentThisMonth, 0.01);

        // Add a goal
        viewModel.addGoal("Trip Fund", 5000.0, 0.0, "🚌", 0xFF0F8A74L, 60);
        Goal goal = viewModel.getDashboardData().goals.get(0);

        // Attempt contribution exceeding available balance (2500 > 2000) -> should be rejected!
        boolean overContributeResult = viewModel.contributeToGoal(goal, 2500.0);
        assertFalse(overContributeResult);
        assertEquals(2000.0, viewModel.getDashboardData().currentBalance, 0.01);

        // Valid custom contribution of 500
        boolean validContributeResult = viewModel.contributeToGoal(goal, 500.0);
        assertTrue(validContributeResult);

        // Goal progress updated
        assertEquals(500.0, viewModel.getDashboardData().goals.get(0).savedAmount, 0.01);

        // Available balance decreases by 500 (2000 - 500 = 1500)
        assertEquals(1500.0, viewModel.getDashboardData().currentBalance, 0.01);

        // Crucial requirement 4: Virtual savings allocation MUST NOT create fake transactions in history!
        assertEquals(0, viewModel.getDashboardData().allTransactions.size());

        // Test withdrawing money from goal back to available balance
        Goal updatedGoal = viewModel.getDashboardData().goals.get(0);
        boolean withdrawResult = viewModel.withdrawFromGoal(updatedGoal, 200.0);
        assertTrue(withdrawResult);
        assertEquals(300.0, viewModel.getDashboardData().goals.get(0).savedAmount, 0.01);

        // Available balance increases back by 200 (1500 + 200 = 1700)
        assertEquals(1700.0, viewModel.getDashboardData().currentBalance, 0.01);
        assertEquals(0, viewModel.getDashboardData().allTransactions.size());
    }
}
