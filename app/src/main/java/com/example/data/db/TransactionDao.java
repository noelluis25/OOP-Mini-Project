package com.example.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.data.model.Transaction;
import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    List<Transaction> getTransactionsForUserSync(long userId);

    @Query("SELECT * FROM transactions")
    List<Transaction> getAllTransactionsSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTransaction(Transaction transaction);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTransactions(List<Transaction> transactions);

    @Update
    void updateTransaction(Transaction transaction);

    @Delete
    void deleteTransaction(Transaction transaction);

    @Query("DELETE FROM transactions WHERE userId = :userId")
    void deleteAllForUser(long userId);

    @Query("DELETE FROM transactions")
    void deleteAllTransactions();
}
