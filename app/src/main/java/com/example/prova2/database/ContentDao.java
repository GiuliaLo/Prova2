package com.example.prova2.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/*
Data access object for communication with the Database (Notebook content table content_table)
Maps method calls to db queries
The methods are implemented in NotebookRepository
 */
@Dao
public interface ContentDao {
    @Insert
    long insertFile(NotebookContent nc);

    @Delete
    void deleteFile(NotebookContent nc);

    @Query("SELECT * FROM content_table WHERE file_number = :num")
    NotebookContent getFile(long num);

    @Query("SELECT * FROM content_table WHERE notebook = :nb ORDER BY file_number ASC")
    List<NotebookContent> getAllFiles(int nb);

    @Query("SELECT * FROM content_table ORDER BY notebook, file_number ASC")
    List<NotebookContent> getAllFiles();
}
