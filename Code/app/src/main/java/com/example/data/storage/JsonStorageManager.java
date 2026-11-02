package com.example.data.storage;

import android.content.Context;
import com.example.data.model.Goal;
import com.example.data.model.Transaction;
import com.example.data.model.UserPreferences;
import com.example.data.model.UserProfile;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonStorageManager {
    private static final String FILE_NAME = "pocketpal_data.json";
    private final Context context;

    public static class PocketPalJsonData {
        public final List<UserProfile> users;
        public final List<Transaction> transactions;
        public final List<Goal> goals;
        public final UserPreferences preferences;

        public PocketPalJsonData(List<UserProfile> users, List<Transaction> transactions, List<Goal> goals, UserPreferences preferences) {
            this.users = users;
            this.transactions = transactions;
            this.goals = goals;
            this.preferences = preferences;
        }
    }

    public JsonStorageManager(Context context) {
        this.context = context.getApplicationContext();
    }

    private File getJsonFile() {
        return new File(context.getFilesDir(), FILE_NAME);
    }

    public synchronized void saveData(
            List<UserProfile> users,
            List<Transaction> transactions,
            List<Goal> goals,
            UserPreferences preferences
    ) {
        try {
            JSONObject root = new JSONObject();

            JSONArray usersArray = new JSONArray();
            for (UserProfile u : users) {
                JSONObject obj = new JSONObject();
                obj.put("id", u.id);
                obj.put("name", u.name);
                obj.put("roleTag", u.roleTag);
                obj.put("avatarEmoji", u.avatarEmoji);
                obj.put("avatarColorHex", u.avatarColorHex);
                obj.put("monthlyBudget", u.monthlyBudget);
                obj.put("currencySymbol", u.currencySymbol);
                obj.put("createdAt", u.createdAt);
                usersArray.put(obj);
            }
            root.put("users", usersArray);

            JSONArray txArray = new JSONArray();
            for (Transaction t : transactions) {
                JSONObject obj = new JSONObject();
                obj.put("id", t.id);
                obj.put("userId", t.userId);
                obj.put("title", t.title);
                obj.put("amount", t.amount);
                obj.put("type", t.type);
                obj.put("category", t.category);
                obj.put("timestamp", t.timestamp);
                obj.put("note", t.note);
                obj.put("paymentMethod", t.paymentMethod);
                txArray.put(obj);
            }
            root.put("transactions", txArray);

            JSONArray goalsArray = new JSONArray();
            for (Goal g : goals) {
                JSONObject obj = new JSONObject();
                obj.put("id", g.id);
                obj.put("userId", g.userId);
                obj.put("title", g.title);
                obj.put("targetAmount", g.targetAmount);
                obj.put("savedAmount", g.savedAmount);
                obj.put("emoji", g.emoji);
                obj.put("colorHex", g.colorHex);
                obj.put("deadlineMillis", g.deadlineMillis);
                goalsArray.put(obj);
            }
            root.put("goals", goalsArray);

            if (preferences != null) {
                JSONObject prefObj = new JSONObject();
                prefObj.put("id", preferences.id);
                prefObj.put("hasCompletedOnboarding", preferences.hasCompletedOnboarding);
                prefObj.put("activeUserId", preferences.activeUserId);
                prefObj.put("isDarkMode", preferences.isDarkMode);
                prefObj.put("notificationsEnabled", preferences.notificationsEnabled);
                root.put("preferences", prefObj);
            }

            File file = getJsonFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(root.toString(2).getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception ignored) {
        }
    }

    public synchronized PocketPalJsonData loadData() {
        try {
            File file = getJsonFile();
            if (!file.exists()) return null;

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            String content = sb.toString().trim();
            if (content.isEmpty()) return null;

            JSONObject root = new JSONObject(content);

            List<UserProfile> users = new ArrayList<>();
            JSONArray usersArray = root.optJSONArray("users");
            if (usersArray != null) {
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject obj = usersArray.getJSONObject(i);
                    users.add(new UserProfile(
                            obj.optLong("id", 0L),
                            obj.optString("name", "Student"),
                            obj.optString("roleTag", "College"),
                            obj.optString("avatarEmoji", "🎓"),
                            obj.optLong("avatarColorHex", 0xFF0F8A74L),
                            obj.optDouble("monthlyBudget", 5000.0),
                            obj.optString("currencySymbol", "₹"),
                            obj.optLong("createdAt", System.currentTimeMillis())
                    ));
                }
            }

            List<Transaction> transactions = new ArrayList<>();
            JSONArray txArray = root.optJSONArray("transactions");
            if (txArray != null) {
                for (int i = 0; i < txArray.length(); i++) {
                    JSONObject obj = txArray.getJSONObject(i);
                    transactions.add(new Transaction(
                            obj.optLong("id", 0L),
                            obj.optLong("userId", 1L),
                            obj.optString("title", ""),
                            obj.optDouble("amount", 0.0),
                            obj.optString("type", "EXPENSE"),
                            obj.optString("category", "OTHERS"),
                            obj.optLong("timestamp", System.currentTimeMillis()),
                            obj.optString("note", ""),
                            obj.optString("paymentMethod", "UPI")
                    ));
                }
            }

            List<Goal> goals = new ArrayList<>();
            JSONArray goalsArray = root.optJSONArray("goals");
            if (goalsArray != null) {
                for (int i = 0; i < goalsArray.length(); i++) {
                    JSONObject obj = goalsArray.getJSONObject(i);
                    goals.add(new Goal(
                            obj.optLong("id", 0L),
                            obj.optLong("userId", 1L),
                            obj.optString("title", ""),
                            obj.optDouble("targetAmount", 1000.0),
                            obj.optDouble("savedAmount", 0.0),
                            obj.optString("emoji", "🎯"),
                            obj.optLong("colorHex", 0xFF3B82F6L),
                            obj.optLong("deadlineMillis", System.currentTimeMillis())
                    ));
                }
            }

            JSONObject prefObj = root.optJSONObject("preferences");
            UserPreferences preferences = null;
            if (prefObj != null) {
                preferences = new UserPreferences(
                        prefObj.optInt("id", 1),
                        prefObj.optBoolean("hasCompletedOnboarding", false),
                        prefObj.optLong("activeUserId", 1L),
                        prefObj.optBoolean("isDarkMode", false),
                        prefObj.optBoolean("notificationsEnabled", true)
                );
            }

            return new PocketPalJsonData(users, transactions, goals, preferences);
        } catch (Exception e) {
            return null;
        }
    }
}
