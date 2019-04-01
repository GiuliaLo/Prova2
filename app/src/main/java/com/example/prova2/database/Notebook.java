package com.example.prova2.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "notebooks_table")
public class Notebook {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String nbName;

    public Notebook(String nbName) {this.nbName = nbName;}

    public String getNbName() {return this.nbName;}

    public void setNbName(String nbName) {this.nbName = nbName;}
}
