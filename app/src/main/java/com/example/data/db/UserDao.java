package com.example.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.data.model.UserProfile;
import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users ORDER BY createdAt ASC")
    List<UserProfile> getAllUsersSync();

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    UserProfile getUserById(long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserProfile user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllUsers(List<UserProfile> users);

    @Update
    void updateUser(UserProfile user);

    @Delete
    void deleteUser(UserProfile user);

    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();

    @Query("DELETE FROM users")
    void deleteAllUsers();
}
