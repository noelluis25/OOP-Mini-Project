package com.example.data.repository;

import com.example.data.db.GoalDao;
import com.example.data.db.TransactionDao;
import com.example.data.db.UserDao;
import com.example.data.db.UserPreferencesDao;
import com.example.data.model.Goal;
import com.example.data.model.Transaction;
import com.example.data.model.UserPreferences;
import com.example.data.model.UserProfile;
import com.example.data.storage.JsonStorageManager;
import java.util.List;

public class PocketPalRepository {
    private final UserDao userDao;
    private final TransactionDao transactionDao;
    private final GoalDao goalDao;
    private final UserPreferencesDao preferencesDao;
    private final JsonStorageManager jsonStorageManager;

    public PocketPalRepository(
            UserDao userDao,
            TransactionDao transactionDao,
            GoalDao goalDao,
            UserPreferencesDao preferencesDao,
            JsonStorageManager jsonStorageManager
    ) {
        this.userDao = userDao;
        this.transactionDao = transactionDao;
        this.goalDao = goalDao;
        this.preferencesDao = preferencesDao;
        this.jsonStorageManager = jsonStorageManager;
    }

    public List<UserProfile> getAllUsersSync() {
        return userDao.getAllUsersSync();
    }

    public UserPreferences getPreferencesSync() {
        return preferencesDao.getPreferencesSync();
    }

    public List<Transaction> getTransactionsForUserSync(long userId) {
        return transactionDao.getTransactionsForUserSync(userId);
    }

    public List<Goal> getGoalsForUserSync(long userId) {
        return goalDao.getGoalsForUserSync(userId);
    }

    private void syncToJsonFile() {
        if (jsonStorageManager == null) return;
        List<UserProfile> users = userDao.getAllUsersSync();
        List<Transaction> transactions = transactionDao.getAllTransactionsSync();
        List<Goal> goals = goalDao.getAllGoalsSync();
        UserPreferences preferences = preferencesDao.getPreferencesSync();
        jsonStorageManager.saveData(users, transactions, goals, preferences);
    }

    public void ensureInitialSetup() {
        UserPreferences existingPrefs = preferencesDao.getPreferencesSync();
        int userCount = userDao.getUserCount();

        if (userCount == 0 && jsonStorageManager != null) {
            JsonStorageManager.PocketPalJsonData jsonBackup = jsonStorageManager.loadData();
            if (jsonBackup != null && !jsonBackup.users.isEmpty()) {
                userDao.insertAllUsers(jsonBackup.users);
                transactionDao.insertAllTransactions(jsonBackup.transactions);
                goalDao.insertAllGoals(jsonBackup.goals);
                if (jsonBackup.preferences != null) {
                    preferencesDao.insertPreferences(jsonBackup.preferences);
                } else {
                    preferencesDao.insertPreferences(
                            new UserPreferences(1, true, jsonBackup.users.get(0).id, false, true)
                    );
                }
                return;
            }
        }

        if (existingPrefs == null) {
            preferencesDao.insertPreferences(
                    new UserPreferences(1, userCount > 0, 1L, false, true)
            );
        }
        syncToJsonFile();
    }

    public long createNewUser(String name, String roleTag, String emoji, long colorHex, double monthlyBudget) {
        long userId = userDao.insertUser(
                new UserProfile(name, roleTag, emoji, colorHex, monthlyBudget, "₹")
        );
        UserPreferences prefs = preferencesDao.getPreferencesSync();
        if (prefs == null) {
            prefs = new UserPreferences();
        }
        preferencesDao.insertPreferences(prefs.copyWithActiveUser(userId, true));
        syncToJsonFile();
        return userId;
    }

    public void switchActiveUser(long userId) {
        UserPreferences prefs = preferencesDao.getPreferencesSync();
        if (prefs == null) {
            prefs = new UserPreferences();
        }
        preferencesDao.insertPreferences(prefs.copyWithActiveUser(userId, prefs.hasCompletedOnboarding));
        syncToJsonFile();
    }

    public void completeOnboarding() {
        UserPreferences prefs = preferencesDao.getPreferencesSync();
        if (prefs == null) {
            prefs = new UserPreferences();
        }
        preferencesDao.insertPreferences(prefs.copyWithOnboarding(true));
        syncToJsonFile();
    }

    public void toggleDarkMode(boolean enabled) {
        UserPreferences prefs = preferencesDao.getPreferencesSync();
        if (prefs == null) {
            prefs = new UserPreferences();
        }
        preferencesDao.insertPreferences(prefs.copyWithDarkMode(enabled));
        syncToJsonFile();
    }

    public void toggleNotifications(boolean enabled) {
        UserPreferences prefs = preferencesDao.getPreferencesSync();
        if (prefs == null) {
            prefs = new UserPreferences();
        }
        preferencesDao.insertPreferences(prefs.copyWithNotifications(enabled));
        syncToJsonFile();
    }

    public void updateUserBudget(UserProfile user, double newBudget) {
        userDao.updateUser(user.copyWithBudget(newBudget));
        syncToJsonFile();
    }

    public void updateUserProfile(UserProfile user) {
        userDao.updateUser(user);
        syncToJsonFile();
    }

    public void deleteUserAndData(UserProfile user) {
        transactionDao.deleteAllForUser(user.id);
        goalDao.deleteAllForUser(user.id);
        userDao.deleteUser(user);
        syncToJsonFile();
    }

    public void resetUserDataOnly(long userId) {
        transactionDao.deleteAllForUser(userId);
        goalDao.deleteAllForUser(userId);
        syncToJsonFile();
    }

    public long addTransaction(Transaction transaction) {
        long id = transactionDao.insertTransaction(transaction);
        syncToJsonFile();
        return id;
    }

    public void updateTransaction(Transaction transaction) {
        transactionDao.updateTransaction(transaction);
        syncToJsonFile();
    }

    public void deleteTransaction(Transaction transaction) {
        transactionDao.deleteTransaction(transaction);
        syncToJsonFile();
    }

    public long addGoal(Goal goal) {
        long id = goalDao.insertGoal(goal);
        syncToJsonFile();
        return id;
    }

    public void updateGoal(Goal goal) {
        goalDao.updateGoal(goal);
        syncToJsonFile();
    }

    public void deleteGoal(Goal goal) {
        goalDao.deleteGoal(goal);
        syncToJsonFile();
    }
}
