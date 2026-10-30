package com.example.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    public String title;
    public double targetAmount;
    public double savedAmount;
    public String emoji;
    public long colorHex;
    public long deadlineMillis;

    public Goal(long id, long userId, String title, double targetAmount, double savedAmount, String emoji, long colorHex, long deadlineMillis) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.emoji = emoji;
        this.colorHex = colorHex;
        this.deadlineMillis = deadlineMillis;
    }

    @Ignore
    public Goal(long userId, String title, double targetAmount, double savedAmount, String emoji, long colorHex, long deadlineMillis) {
        this(0L, userId, title, targetAmount, savedAmount, emoji, colorHex, deadlineMillis);
    }

    public float getProgressFraction() {
        if (targetAmount <= 0) return 0f;
        float ratio = (float) (savedAmount / targetAmount);
        if (ratio < 0f) return 0f;
        if (ratio > 1f) return 1f;
        return ratio;
    }

    public int getProgressPercentage() {
        return (int) (getProgressFraction() * 100);
    }

    public Goal copyWithSavedAmount(double newSavedAmount) {
        return new Goal(this.id, this.userId, this.title, this.targetAmount, newSavedAmount, this.emoji, this.colorHex, this.deadlineMillis);
    }
}
