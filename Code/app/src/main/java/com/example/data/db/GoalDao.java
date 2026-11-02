package com.example.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.data.model.Goal;
import java.util.List;

@Dao
public interface GoalDao {
    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY deadlineMillis ASC")
    List<Goal> getGoalsForUserSync(long userId);

    @Query("SELECT * FROM goals")
    List<Goal> getAllGoalsSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertGoal(Goal goal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllGoals(List<Goal> goals);

    @Update
    void updateGoal(Goal goal);

    @Delete
    void deleteGoal(Goal goal);

    @Query("DELETE FROM goals WHERE userId = :userId")
    void deleteAllForUser(long userId);

    @Query("DELETE FROM goals")
    void deleteAllGoals();
}
