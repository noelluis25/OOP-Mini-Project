package com.example.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.data.model.CategoryItem;
import com.example.data.model.ExpenseCategory;
import com.example.data.model.Goal;
import com.example.data.model.Transaction;
import com.example.data.model.TransactionType;
import com.example.data.model.UserPreferences;
import com.example.data.model.UserProfile;
import com.example.data.repository.PocketPalRepository;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PocketPalViewModel extends ViewModel {

    public interface StateListener {
        void onStateChanged();
    }

    private final PocketPalRepository repository;
    private final List<StateListener> listeners = new ArrayList<>();

    private AppScreen currentScreen = AppScreen.ONBOARDING;
    private final List<AppScreen> screenBackStack = new ArrayList<>();
    private boolean isInitialized = false;
    private boolean showUserSwitcherDialog = false;
    private Transaction editingTransaction = null;
    private TransactionType initialTransactionType = TransactionType.EXPENSE;

    private String searchQuery = "";
    private TransactionFilter selectedFilter = TransactionFilter.ALL;
    private BreakdownPeriod breakdownPeriod = BreakdownPeriod.THIS_MONTH;

    private DashboardData dashboardData = DashboardData.empty();

    public PocketPalViewModel(PocketPalRepository repository) {
        this.repository = repository;
        repository.ensureInitialSetup();
        refreshState(true);
    }

    public void addListener(StateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(StateListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (StateListener l : new ArrayList<>(listeners)) {
            l.onStateChanged();
        }
    }

    public AppScreen getCurrentScreen() {
        return currentScreen;
    }

    public boolean isShowUserSwitcherDialog() {
        return showUserSwitcherDialog;
    }

    public Transaction getEditingTransaction() {
        return editingTransaction;
    }

    public DashboardData getDashboardData() {
        return dashboardData;
    }

    public void refreshState(boolean isFirstCheck) {
        List<UserProfile> users = repository.getAllUsersSync();
        UserPreferences prefs = repository.getPreferencesSync();
        if (prefs == null) {
            prefs = new UserPreferences();
        }

        if (!isInitialized && isFirstCheck) {
            currentScreen = AppScreen.ONBOARDING;
            isInitialized = true;
        } else if (users.isEmpty() && currentScreen != AppScreen.ONBOARDING && currentScreen != AppScreen.CREATE_PROFILE) {
            currentScreen = AppScreen.CREATE_PROFILE;
        }

        UserProfile activeUser = null;
        for (UserProfile u : users) {
            if (u.id == prefs.activeUserId) {
                activeUser = u;
                break;
            }
        }
        if (activeUser == null && !users.isEmpty()) {
            activeUser = users.get(0);
        }

        List<Transaction> transactions = activeUser != null
                ? repository.getTransactionsForUserSync(activeUser.id)
                : Collections.emptyList();
        List<Goal> goals = activeUser != null
                ? repository.getGoalsForUserSync(activeUser.id)
                : Collections.emptyList();

        dashboardData = computeDashboardData(
                users,
                activeUser,
                prefs,
                transactions,
                goals,
                searchQuery,
                selectedFilter,
                breakdownPeriod
        );
        notifyListeners();
    }

    private DashboardData computeDashboardData(
            List<UserProfile> users,
            UserProfile activeUser,
            UserPreferences prefs,
            List<Transaction> transactions,
            List<Goal> goals,
            String query,
            TransactionFilter filter,
            BreakdownPeriod bPeriod
    ) {
        long now = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfMonth = cal.getTimeInMillis();

        Calendar lastMonthCal = (Calendar) cal.clone();
        lastMonthCal.add(Calendar.MONTH, -1);
        long startOfLastMonth = lastMonthCal.getTimeInMillis();

        long sevenDaysAgo = now - 7L * 24 * 60 * 60 * 1000L;
        long fourteenDaysAgo = now - 14L * 24 * 60 * 60 * 1000L;

        List<Transaction> thisMonthTx = new ArrayList<>();
        for (Transaction tx : transactions) {
            if (tx.timestamp >= startOfMonth) {
                thisMonthTx.add(tx);
            }
        }

        double totalIncome = 0.0;
        double totalSpent = 0.0;
        for (Transaction tx : thisMonthTx) {
            if (TransactionType.INCOME.name().equals(tx.type)) {
                totalIncome += tx.amount;
            } else if (TransactionType.EXPENSE.name().equals(tx.type)) {
                totalSpent += tx.amount;
            }
        }

        double totalSavedInGoals = 0.0;
        for (Goal g : goals) {
            if (g.savedAmount > 0) {
                totalSavedInGoals += g.savedAmount;
            }
        }

        double budget = activeUser != null && activeUser.monthlyBudget > 0 ? Math.abs(activeUser.monthlyBudget) : 5000.0;
        double effectiveLimit = budget + totalIncome;
        double totalMoney = effectiveLimit - totalSpent;
        double availableBalance = totalMoney - totalSavedInGoals;

        float budgetFraction = effectiveLimit > 0 ? (float) (totalSpent / effectiveLimit) : 0f;
        if (budgetFraction < 0f) budgetFraction = 0f;
        if (budgetFraction > 1.0f) budgetFraction = 1.0f;

        List<Transaction> filtered = new ArrayList<>();
        String lowerQ = query != null ? query.trim().toLowerCase(Locale.getDefault()) : "";
        for (Transaction tx : transactions) {
            boolean matchesQuery = lowerQ.isEmpty()
                    || tx.title.toLowerCase(Locale.getDefault()).contains(lowerQ)
                    || tx.category.toLowerCase(Locale.getDefault()).contains(lowerQ)
                    || tx.note.toLowerCase(Locale.getDefault()).contains(lowerQ);

            boolean matchesFilter;
            switch (filter) {
                case ALL:
                    matchesFilter = true;
                    break;
                case THIS_WEEK:
                    matchesFilter = tx.timestamp >= sevenDaysAgo;
                    break;
                case THIS_MONTH:
                    matchesFilter = tx.timestamp >= startOfMonth;
                    break;
                case FOOD:
                    matchesFilter = ExpenseCategory.FOOD.name().equalsIgnoreCase(tx.category) || ExpenseCategory.FOOD.label.equalsIgnoreCase(tx.category);
                    break;
                case TRANSPORT:
                    matchesFilter = ExpenseCategory.TRANSPORT.name().equalsIgnoreCase(tx.category) || ExpenseCategory.TRANSPORT.label.equalsIgnoreCase(tx.category);
                    break;
                case STUDY:
                    matchesFilter = ExpenseCategory.STUDY.name().equalsIgnoreCase(tx.category) || ExpenseCategory.STUDY.label.equalsIgnoreCase(tx.category);
                    break;
                default:
                    matchesFilter = true;
                    break;
            }
            if (matchesQuery && matchesFilter) {
                filtered.add(tx);
            }
        }

        List<Transaction> periodExpenses = new ArrayList<>();
        for (Transaction tx : transactions) {
            if (!TransactionType.EXPENSE.name().equals(tx.type)) continue;
            if (bPeriod == BreakdownPeriod.THIS_MONTH && tx.timestamp >= startOfMonth) {
                periodExpenses.add(tx);
            } else if (bPeriod == BreakdownPeriod.LAST_MONTH && tx.timestamp >= startOfLastMonth && tx.timestamp < startOfMonth) {
                periodExpenses.add(tx);
            } else if (bPeriod == BreakdownPeriod.CUSTOM && tx.timestamp >= fourteenDaysAgo) {
                periodExpenses.add(tx);
            }
        }

        double totalPeriodExpense = 0.0;
        for (Transaction tx : periodExpenses) {
            totalPeriodExpense += tx.amount;
        }

        List<CategoryBreakdownItem> breakdown = new ArrayList<>();
        if (totalPeriodExpense > 0.0) {
            for (ExpenseCategory cat : ExpenseCategory.values()) {
                double amt = 0.0;
                int count = 0;
                for (Transaction tx : periodExpenses) {
                    if (cat.name().equalsIgnoreCase(tx.category) || cat.label.equalsIgnoreCase(tx.category)) {
                        amt += tx.amount;
                        count++;
                    }
                }
                if (amt > 0) {
                    float pct = (float) ((amt / totalPeriodExpense) * 100.0);
                    breakdown.add(new CategoryBreakdownItem(cat, amt, pct, count));
                }
            }
            Collections.sort(breakdown, new Comparator<CategoryBreakdownItem>() {
                @Override
                public int compare(CategoryBreakdownItem a, CategoryBreakdownItem b) {
                    return Double.compare(b.amount, a.amount);
                }
            });
        }

        List<BarChartPoint> barPoints = buildBarChartPoints(transactions, bPeriod, now);

        return new DashboardData(
                activeUser,
                users,
                prefs,
                transactions,
                filtered,
                goals,
                totalIncome,
                totalSpent,
                totalMoney,
                totalSavedInGoals,
                availableBalance,
                budgetFraction,
                breakdown,
                barPoints,
                query,
                filter,
                bPeriod
        );
    }

    private List<BarChartPoint> buildBarChartPoints(List<Transaction> transactions, BreakdownPeriod period, long now) {
        List<BarChartPoint> list = new ArrayList<>();

        if (period == BreakdownPeriod.LAST_MONTH) {
            String[] labels = {"Wk 1", "Wk 2", "Wk 3", "Wk 4"};
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(now);
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long monthStart = cal.getTimeInMillis();
            long weekMs = 7L * 24 * 60 * 60 * 1000L;

            for (int index = 0; index < 4; index++) {
                long wkStart = monthStart + index * weekMs;
                long wkEnd = (index == 3) ? (monthStart + 31L * 24 * 60 * 60 * 1000L) : (wkStart + weekMs - 1);
                double exp = 0.0;
                double inc = 0.0;
                for (Transaction tx : transactions) {
                    if (tx.timestamp >= wkStart && tx.timestamp <= wkEnd) {
                        if (tx.isExpense()) exp += tx.amount;
                        else inc += tx.amount;
                    }
                }
                list.add(new BarChartPoint(labels[index], exp, inc));
            }
        } else {
            String[] labels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(now);
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long monStart = cal.getTimeInMillis();
            long dayMs = 24L * 60 * 60 * 1000L;

            for (int index = 0; index < 7; index++) {
                long dayStart = monStart + index * dayMs;
                long dayEnd = dayStart + dayMs - 1;
                double exp = 0.0;
                double inc = 0.0;
                for (Transaction tx : transactions) {
                    if (tx.timestamp >= dayStart && tx.timestamp <= dayEnd) {
                        if (tx.isExpense()) exp += tx.amount;
                        else inc += tx.amount;
                    }
                }
                list.add(new BarChartPoint(labels[index], exp, inc));
            }
        }
        return list;
    }

    public void navigateTo(AppScreen screen) {
        if (screen == currentScreen) return;
        if (screen == AppScreen.HOME) {
            screenBackStack.clear();
        } else {
            screenBackStack.add(currentScreen);
        }
        if (screen != AppScreen.ADD_TRANSACTION) {
            editingTransaction = null;
        }
        currentScreen = screen;
        notifyListeners();
    }

    public boolean navigateBack() {
        editingTransaction = null;
        if (!screenBackStack.isEmpty()) {
            currentScreen = screenBackStack.remove(screenBackStack.size() - 1);
            notifyListeners();
            return true;
        } else if (currentScreen != AppScreen.HOME && currentScreen != AppScreen.ONBOARDING && currentScreen != AppScreen.CREATE_PROFILE) {
            currentScreen = AppScreen.HOME;
            notifyListeners();
            return true;
        }
        return false;
    }

    public void completeOnboardingAndProceed() {
        repository.completeOnboarding();
        screenBackStack.clear();
        List<UserProfile> users = repository.getAllUsersSync();
        currentScreen = users.isEmpty() ? AppScreen.CREATE_PROFILE : AppScreen.HOME;
        refreshState(false);
    }

    public void setShowUserSwitcher(boolean show) {
        showUserSwitcherDialog = show;
        notifyListeners();
    }

    public void switchUser(long userId) {
        repository.switchActiveUser(userId);
        showUserSwitcherDialog = false;
        refreshState(false);
    }

    public void createNewProfile(String name, String roleTag, String emoji, long colorHex, double monthlyBudget) {
        if (name == null || name.trim().isEmpty()) return;
        repository.createNewUser(name.trim(), roleTag, emoji, colorHex, monthlyBudget);
        showUserSwitcherDialog = false;
        screenBackStack.clear();
        currentScreen = AppScreen.HOME;
        refreshState(false);
    }

    public void updateSearchQuery(String query) {
        this.searchQuery = query != null ? query : "";
        refreshState(false);
    }

    public void updateTransactionFilter(TransactionFilter filter) {
        this.selectedFilter = filter;
        refreshState(false);
    }

    public void updateBreakdownPeriod(BreakdownPeriod period) {
        this.breakdownPeriod = period;
        refreshState(false);
    }

    public TransactionType getInitialTransactionType() {
        return initialTransactionType;
    }

    public void startAddTransaction() {
        editingTransaction = null;
        initialTransactionType = TransactionType.EXPENSE;
        navigateTo(AppScreen.ADD_TRANSACTION);
    }

    public void startAddTransactionWithType(TransactionType type) {
        editingTransaction = null;
        initialTransactionType = type != null ? type : TransactionType.EXPENSE;
        navigateTo(AppScreen.ADD_TRANSACTION);
    }

    public void startEditTransaction(Transaction transaction) {
        editingTransaction = transaction;
        navigateTo(AppScreen.ADD_TRANSACTION);
    }

    public void saveTransaction(
            String title,
            double amount,
            TransactionType type,
            CategoryItem category,
            String note,
            String paymentMethod,
            long timestamp
    ) {
        UserProfile activeUser = dashboardData.activeUser;
        if (activeUser == null) return;
        Transaction existing = editingTransaction;
        String finalTitle = (title == null || title.trim().isEmpty()) ? category.label : title.trim();
        String finalNote = note != null ? note.trim() : "";

        if (existing != null) {
            repository.updateTransaction(new Transaction(
                    existing.id,
                    activeUser.id,
                    finalTitle,
                    amount,
                    type.name(),
                    category.name,
                    timestamp,
                    finalNote,
                    paymentMethod
            ));
        } else {
            repository.addTransaction(new Transaction(
                    activeUser.id,
                    finalTitle,
                    amount,
                    type.name(),
                    category.name,
                    timestamp,
                    finalNote,
                    paymentMethod
            ));
        }
        editingTransaction = null;
        navigateBack();
        refreshState(false);
    }

    public void deleteTransaction(Transaction transaction) {
        repository.deleteTransaction(transaction);
        refreshState(false);
    }

    public void addGoal(String title, double targetAmount, double initialSaved, String emoji, long colorHex, long daysFromNow) {
        UserProfile activeUser = dashboardData.activeUser;
        if (activeUser == null) return;
        long deadline = System.currentTimeMillis() + daysFromNow * 24L * 60 * 60 * 1000L;
        repository.addGoal(new Goal(
                activeUser.id,
                title.trim(),
                targetAmount,
                initialSaved,
                emoji,
                colorHex,
                deadline
        ));
        refreshState(false);
    }

    public boolean contributeToGoal(Goal goal, double extraAmount) {
        if (extraAmount <= 0) return false;
        double availableBalance = dashboardData.currentBalance;
        if (extraAmount > availableBalance) {
            return false;
        }
        UserProfile activeUser = dashboardData.activeUser;
        if (activeUser == null) return false;

        double updatedSaved = Math.min(goal.savedAmount + extraAmount, goal.targetAmount);
        repository.updateGoal(goal.copyWithSavedAmount(updatedSaved));

        refreshState(false);
        return true;
    }

    public boolean withdrawFromGoal(Goal goal, double withdrawAmount) {
        if (withdrawAmount <= 0) return false;
        if (withdrawAmount > goal.savedAmount) return false;
        UserProfile activeUser = dashboardData.activeUser;
        if (activeUser == null) return false;

        double updatedSaved = Math.max(0, goal.savedAmount - withdrawAmount);
        repository.updateGoal(goal.copyWithSavedAmount(updatedSaved));

        refreshState(false);
        return true;
    }

    public void updateGoalDetails(Goal goal, String newTitle, double newTargetAmount, String newEmoji) {
        if (newTitle == null || newTitle.trim().isEmpty() || newTargetAmount <= 0) return;
        Goal updated = new Goal(
                goal.id,
                goal.userId,
                newTitle.trim(),
                newTargetAmount,
                Math.min(goal.savedAmount, newTargetAmount),
                newEmoji != null ? newEmoji : goal.emoji,
                goal.colorHex,
                goal.deadlineMillis
        );
        repository.updateGoal(updated);
        refreshState(false);
    }

    public void deleteGoal(Goal goal) {
        repository.deleteGoal(goal);
        refreshState(false);
    }

    public void updateMonthlyBudget(double newBudget) {
        UserProfile activeUser = dashboardData.activeUser;
        if (activeUser == null || newBudget <= 0) return;
        repository.updateUserBudget(activeUser, newBudget);
        refreshState(false);
    }

    public void updateActiveUserDetails(String name, String roleTag, String emoji, double budget) {
        UserProfile activeUser = dashboardData.activeUser;
        if (activeUser == null || name == null || name.trim().isEmpty()) return;
        repository.updateUserProfile(new UserProfile(
                activeUser.id,
                name.trim(),
                roleTag,
                emoji,
                activeUser.avatarColorHex,
                budget > 0 ? budget : activeUser.monthlyBudget,
                activeUser.currencySymbol,
                activeUser.createdAt
        ));
        refreshState(false);
    }

    public void toggleDarkMode(boolean enabled) {
        repository.toggleDarkMode(enabled);
        refreshState(false);
    }

    public void toggleNotifications(boolean enabled) {
        repository.toggleNotifications(enabled);
        refreshState(false);
    }

    public void resetCurrentUserData() {
        UserProfile activeUser = dashboardData.activeUser;
        if (activeUser == null) return;
        repository.resetUserDataOnly(activeUser.id);
        refreshState(false);
    }

    public void deleteCurrentUserProfile() {
        UserProfile activeUser = dashboardData.activeUser;
        if (activeUser == null) return;
        repository.deleteUserAndData(activeUser);
        List<UserProfile> remaining = repository.getAllUsersSync();
        if (remaining.isEmpty()) {
            screenBackStack.clear();
            currentScreen = AppScreen.CREATE_PROFILE;
        } else {
            repository.switchActiveUser(remaining.get(0).id);
            screenBackStack.clear();
            currentScreen = AppScreen.HOME;
        }
        refreshState(false);
    }

    public String buildExportSummaryText() {
        DashboardData d = dashboardData;
        UserProfile user = d.activeUser;
        if (user == null) return "PocketPal Report";
        StringBuilder sb = new StringBuilder();
        sb.append("📊 PocketPal Monthly Report — ").append(user.name)
                .append(" (").append(user.roleTag).append(")\n");
        sb.append("====================================\n");
        sb.append("💰 Current Balance: ₹").append((int) d.currentBalance).append("\n");
        sb.append("📥 Income This Month: ₹").append((int) d.totalIncomeThisMonth).append("\n");
        sb.append("📤 Spent This Month: ₹").append((int) d.totalSpentThisMonth)
                .append(" / ₹").append((int) user.monthlyBudget).append("\n\n");
        sb.append("🍩 Category Breakdown:\n");
        for (CategoryBreakdownItem item : d.categoryBreakdown) {
            sb.append("  • ").append(item.category.emoji).append(" ")
                    .append(item.category.label).append(": ₹").append((int) item.amount)
                    .append(" (").append((int) item.percentage).append("%)\n");
        }
        sb.append("\n🎯 Active Goals:\n");
        for (Goal goal : d.goals) {
            sb.append("  • ").append(goal.emoji).append(" ").append(goal.title)
                    .append(": ₹").append((int) goal.savedAmount).append(" / ₹")
                    .append((int) goal.targetAmount).append(" (")
                    .append(goal.getProgressPercentage()).append("%)\n");
        }
        return sb.toString();
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final PocketPalRepository repository;

        public Factory(PocketPalRepository repository) {
            this.repository = repository;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(PocketPalViewModel.class)) {
                return (T) new PocketPalViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
