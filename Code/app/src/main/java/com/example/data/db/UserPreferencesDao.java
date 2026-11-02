package com.example.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.data.model.UserPreferences;

@Dao
public interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    UserPreferences getPreferencesSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPreferences(UserPreferences preferences);

    @Update
    void updatePreferences(UserPreferences preferences);
}
