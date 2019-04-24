package com.example.prova2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface NotebookDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertNotebook(Notebook nb);

    @Delete
    void deleteNotebook(Notebook nb);

    @Query("UPDATE notebooks_table SET name = :newName WHERE name = :oldName")
    void updateNotebook(String oldName, String newName);

    @Query("SELECT * FROM notebooks_table ORDER BY name ASC")
    LiveData<List<Notebook>> getAllNotebooks();
}
