package com.example.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_preferences")
public class UserPreferences {
    @PrimaryKey
    public int id;
    public boolean hasCompletedOnboarding;
    public long activeUserId;
    public boolean isDarkMode;
    public boolean notificationsEnabled;

    public UserPreferences(int id, boolean hasCompletedOnboarding, long activeUserId, boolean isDarkMode, boolean notificationsEnabled) {
        this.id = id;
        this.hasCompletedOnboarding = hasCompletedOnboarding;
        this.activeUserId = activeUserId;
        this.isDarkMode = isDarkMode;
        this.notificationsEnabled = notificationsEnabled;
    }

    @Ignore
    public UserPreferences() {
        this(1, false, 1L, false, true);
    }

    public UserPreferences copyWithActiveUser(long newActiveUserId, boolean onboardingDone) {
        return new UserPreferences(this.id, onboardingDone, newActiveUserId, this.isDarkMode, this.notificationsEnabled);
    }

    public UserPreferences copyWithDarkMode(boolean dark) {
        return new UserPreferences(this.id, this.hasCompletedOnboarding, this.activeUserId, dark, this.notificationsEnabled);
    }

    public UserPreferences copyWithNotifications(boolean enabled) {
        return new UserPreferences(this.id, this.hasCompletedOnboarding, this.activeUserId, this.isDarkMode, enabled);
    }

    public UserPreferences copyWithOnboarding(boolean completed) {
        return new UserPreferences(this.id, completed, this.activeUserId, this.isDarkMode, this.notificationsEnabled);
    }
}
