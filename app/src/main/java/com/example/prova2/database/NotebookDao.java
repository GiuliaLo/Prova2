package com.example.prova2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/*
Data access object for communication with the Database (Notebooks table notebooks_table)
Maps method calls to db queries
The methods are implemented in NotebookRepository
 */
@Dao
public interface NotebookDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertNotebook(Notebook nb);

    @Delete
    void deleteNotebook(Notebook nb);

    @Query("UPDATE notebooks_table SET name = :newName WHERE id = :id")
    void updateNotebook(int id, String newName);

    @Query("SELECT * FROM notebooks_table ORDER BY name ASC")
    LiveData<List<Notebook>> getAllNotebooks();

    @Query("SELECT * FROM notebooks_table WHERE id = :id")
    LiveData<Notebook> getNotebook(int id);
}
