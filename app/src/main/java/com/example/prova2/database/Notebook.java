package com.example.prova2.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "notebooks_table"
        //, indices = {@Index(value = "name", unique = true)}
        )
public class Notebook {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "name")
    private String nbName;

    public int getId() { return id;  }

    public void setId(int id) { this.id = id;  }

    public Notebook(String nbName) {this.nbName = nbName;}

    public String getNbName() {return this.nbName;}

    public void setNbName(String nbName) {this.nbName = nbName;}
}
