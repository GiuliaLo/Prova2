package com.example.prova2.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class NotebookRepository {
    private NotebookDao mNotebookDao;
    private ContentDao mContentDao;
    private LiveData<List<Notebook>> mAllNotebooks;
    private LiveData<List<NotebookContent>> mAllContents;

    NotebookRepository(Application application) {
        NotebookRoomDatabase db = NotebookRoomDatabase.getDatabase(application);
        mNotebookDao = db.notebookDao();
        mAllNotebooks = mNotebookDao.getAllNotebooks();
        mContentDao = db.contentDao();
        mAllContents = mContentDao.getAllFiles();
    }

    LiveData<List<Notebook>> getAllNotebooks() {
        return mAllNotebooks;
    }

    LiveData<List<NotebookContent>> getAllFiles() {
        return mAllContents;
    }

    LiveData<List<NotebookContent>> getAllFiles(String nb) {
        return mContentDao.getAllFiles(nb); //?
    }

    LiveData<NotebookContent> getFile(String nb, int num) {
        return mContentDao.getFile(nb, num); //?
    }

    LiveData<Integer> getLastFileNumber(String nb) {
        return mContentDao.getLastFileNumber(nb);
    }


    //INSERT NOTEBOOK

    public void insertNotebook (Notebook nb) {
        new insertNotebookAsyncTask(mNotebookDao).execute(nb);
    }

    private static class insertNotebookAsyncTask extends AsyncTask<Notebook, Void, Void> {

        private NotebookDao mAsyncTaskDao;

        insertNotebookAsyncTask(NotebookDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Notebook... params) {
            mAsyncTaskDao.insertNotebook(params[0]);
            return null;
        }
    }


    //INSERT FILE

    public void insertFile (NotebookContent nc) {
        new insertContentAsyncTask(mContentDao).execute(nc);
    }

    private static class insertContentAsyncTask extends AsyncTask<NotebookContent, Void, Void> {

        private ContentDao mAsyncTaskDao;

        insertContentAsyncTask(ContentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final NotebookContent... params) {
            mAsyncTaskDao.insertFile(params[0]);
            return null;
        }
    }


    //DELETE NOTEBOOK

    public void deleteNotebook (Notebook nb) {
        new deleteNotebookAsyncTask(mNotebookDao).execute(nb);
    }

    private static class deleteNotebookAsyncTask extends AsyncTask<Notebook, Void, Void> {
        private NotebookDao mAsyncTaskDao;

        deleteNotebookAsyncTask(NotebookDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Notebook... params) {
            mAsyncTaskDao.deleteNotebook(params[0]);
            return null;
        }
    }



    //DELETE FILE

    public void deleteFile (String nb, int num) {
        new deleteContentAsyncTask(mContentDao).execute(new MyParamsDeleteFile(nb, num));
    }

    private static class deleteContentAsyncTask extends AsyncTask<MyParamsDeleteFile, Void, Void> {
        private ContentDao mAsyncTaskDao;

        deleteContentAsyncTask(ContentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MyParamsDeleteFile... params) {
            mAsyncTaskDao.deleteFile(params[0].nbId, params[0].fileNum);
            return null;
        }
    }

    private static class MyParamsDeleteFile {
        int nbId;
        int fileNum;

        MyParamsDeleteFile(String nbName, int fileNum) {this.nbId = nbId; this.fileNum = fileNum;}
    }


    //UPDATE NOTEBOOK

    public void updateNotebook (final String oldName, final String newName) {
        new updateNotebookAsyncTask(mNotebookDao).execute(new MyParamsUpdateNotebook(oldName, newName));
    }

    private static class updateNotebookAsyncTask extends AsyncTask<MyParamsUpdateNotebook, Void, Void> {
        private NotebookDao mAsyncTaskDao;

        updateNotebookAsyncTask(NotebookDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MyParamsUpdateNotebook... params) {
            mAsyncTaskDao.updateNotebook(params[0].oldName, params[0].newName);
            return null;
        }
    }

    private static class MyParamsUpdateNotebook {
        String oldName;
        String newName;

        MyParamsUpdateNotebook(String oldName, String newName) {this.oldName = oldName; this.newName = newName;}
    }

}
