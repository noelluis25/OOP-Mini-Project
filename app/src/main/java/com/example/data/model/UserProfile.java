package com.example.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserProfile {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public String roleTag; // e.g., "College", "School", "Student", "Teenager"
    public String avatarEmoji;
    public long avatarColorHex;
    public double monthlyBudget;
    public String currencySymbol;
    public long createdAt;

    public UserProfile(long id, String name, String roleTag, String avatarEmoji, long avatarColorHex, double monthlyBudget, String currencySymbol, long createdAt) {
        this.id = id;
        this.name = name;
        this.roleTag = roleTag != null ? roleTag : "Student";
        this.avatarEmoji = avatarEmoji != null ? avatarEmoji : "🎓";
        this.avatarColorHex = avatarColorHex;
        this.monthlyBudget = monthlyBudget;
        this.currencySymbol = currencySymbol != null ? currencySymbol : "₹";
        this.createdAt = createdAt;
    }

    @Ignore
    public UserProfile(String name, String roleTag, String avatarEmoji, long avatarColorHex, double monthlyBudget, String currencySymbol) {
        this(0L, name, roleTag, avatarEmoji, avatarColorHex, monthlyBudget, currencySymbol, System.currentTimeMillis());
    }

    public UserProfile copyWithBudget(double newBudget) {
        return new UserProfile(this.id, this.name, this.roleTag, this.avatarEmoji, this.avatarColorHex, newBudget, this.currencySymbol, this.createdAt);
    }
}
