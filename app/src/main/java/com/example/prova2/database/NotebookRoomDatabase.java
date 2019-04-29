package com.example.prova2.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

/*
Database layer on top of SQLite database
 */
@Database(entities = {Notebook.class, NotebookContent.class}, version = 1)
public abstract class NotebookRoomDatabase extends RoomDatabase {
    public abstract NotebookDao notebookDao();
    public abstract ContentDao contentDao();

    private static volatile NotebookRoomDatabase INSTANCE;

    static NotebookRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NotebookRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NotebookRoomDatabase.class, "notebook_database")
                            .addCallback(sRoomDatabaseCallback)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    //test
                    //new PopulateDbAsync(INSTANCE).execute();
                }
            };

    //test
    /*
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final NotebookDao mNbDao;

        PopulateDbAsync(NotebookRoomDatabase db) {
            mNbDao = db.notebookDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            Notebook nb = new Notebook("Notebook1");
            mNbDao.insertNotebook(nb);
            nb = new Notebook("Notebook2");
            mNbDao.insertNotebook(nb);
            return null;
        }
    }
    */
}
