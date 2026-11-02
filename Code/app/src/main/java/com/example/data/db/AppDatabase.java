package com.example.data.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.data.model.Goal;
import com.example.data.model.Transaction;
import com.example.data.model.UserPreferences;
import com.example.data.model.UserProfile;

@Database(
    entities = {
        UserProfile.class,
        Transaction.class,
        Goal.class,
        UserPreferences.class
    },
    version = 3,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract TransactionDao transactionDao();
    public abstract GoalDao goalDao();
    public abstract UserPreferencesDao userPreferencesDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "pocketpal_db"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
