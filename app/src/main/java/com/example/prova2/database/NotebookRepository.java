package com.example.prova2.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

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

    LiveData<List<NotebookContent>> getAllFiles(int nb) {
        return mContentDao.getAllFiles(nb); //?
    }

    NotebookContent getFile(int num) {
        return mContentDao.getFile(num); //?
    }

    LiveData<Integer> getLastFileNumber(int nb) {
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

    public long insertFile (NotebookContent nc) {
        try {
            return new insertContentAsyncTask(mContentDao).execute(nc).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static class insertContentAsyncTask extends AsyncTask<NotebookContent, Void, Long> {

        private ContentDao mAsyncTaskDao;

        insertContentAsyncTask(ContentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Long doInBackground(final NotebookContent... params) {
            return mAsyncTaskDao.insertFile(params[0]);
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

    public void deleteFile (int num) {
        new deleteContentAsyncTask(mContentDao).execute(num);
    }

    private static class deleteContentAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ContentDao mAsyncTaskDao;

        deleteContentAsyncTask(ContentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Integer... params) {
            //mAsyncTaskDao.deleteFile(params[0].nbId, params[0].fileNum);
            mAsyncTaskDao.deleteFile(params[0]);
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
