package com.example.prova2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ContentDao {
    @Insert
    long insertFile(NotebookContent nc);

    @Query("DELETE FROM content_table WHERE file_number = :num")
    void deleteFile(int num);

    @Query("SELECT MAX(file_number) FROM content_table WHERE notebook = :nb")
    LiveData<Integer> getLastFileNumber(int nb);

    @Query("SELECT * FROM content_table WHERE file_number = :num")
    NotebookContent getFile(long num);

    @Query("SELECT * FROM content_table WHERE notebook = :nb ORDER BY file_number ASC")
    LiveData<List<NotebookContent>> getAllFiles(int nb);

    @Query("SELECT * FROM content_table ORDER BY notebook, file_number ASC")
    LiveData<List<NotebookContent>> getAllFiles();
}
