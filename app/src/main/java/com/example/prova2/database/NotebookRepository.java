package com.example.prova2.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.support.constraint.Constraints.TAG;

/*
Implementation of database query methods defined in DAOs.
Use an AsyncTask to perform db queries on a non-UI thread, or the app will crash.
Room ensures that you don't do any long-running operations on the main thread, which would block the UI.
 */
public class NotebookRepository {
    private NotebookDao mNotebookDao;
    private ContentDao mContentDao;
    private LiveData<List<Notebook>> mAllNotebooks;
    private List<NotebookContent> mAllContents;

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

    LiveData<Notebook> getNotebook(int id) { return mNotebookDao.getNotebook(id); }

    List<NotebookContent> getAllFiles() {
        return mAllContents;
    }

    List<NotebookContent> getAllFiles(int nb) {
        return mContentDao.getAllFiles(nb); //?
    }

    NotebookContent getFile(int num) {
        return mContentDao.getFile(num); //?
    }


    //INSERT NOTEBOOK

    public int insertNotebook (Notebook nb) {
        int id = 0;
        try {
            id = new insertNotebookAsyncTask(mNotebookDao).execute(nb).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    private static class insertNotebookAsyncTask extends AsyncTask<Notebook, Void, Integer> {

        private NotebookDao mAsyncTaskDao;

        insertNotebookAsyncTask(NotebookDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(final Notebook... params) {
            int id = (int)mAsyncTaskDao.insertNotebook(params[0]);
            return id;
        }

        @Override
        protected void onPostExecute(Integer id) {
            super.onPostExecute(id);
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
        //delete all files
        List<NotebookContent> files = getAllFiles(nb.getId());
        for (NotebookContent f : files) {
            deleteFile(f);
        }
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
    public void deleteFile (NotebookContent nc) {
        //remove file from storage before deleting reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fileRef = storage.getReferenceFromUrl(nc.getFilePath());

        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete file");
            }
        });

        new deleteContentAsyncTask(mContentDao).execute(nc);
    }

    private static class deleteContentAsyncTask extends AsyncTask<NotebookContent, Void, Void> {
        private ContentDao mAsyncTaskDao;

        deleteContentAsyncTask(ContentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final NotebookContent... params) {
            //mAsyncTaskDao.deleteFile(params[0].nbId, params[0].fileNum);
            mAsyncTaskDao.deleteFile(params[0]);
            return null;
        }
    }

    //UPDATE NOTEBOOK

    public void updateNotebook (final int id, final String newName) {
        new updateNotebookAsyncTask(mNotebookDao).execute(new MyParamsUpdateNotebook(id, newName));
    }

    private static class updateNotebookAsyncTask extends AsyncTask<MyParamsUpdateNotebook, Void, Void> {
        private NotebookDao mAsyncTaskDao;

        updateNotebookAsyncTask(NotebookDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MyParamsUpdateNotebook... params) {
            mAsyncTaskDao.updateNotebook(params[0].id, params[0].newName);
            return null;
        }
    }

    //helper class for use as AsyncTask parameters
    private static class MyParamsUpdateNotebook {
        int id;
        String newName;

        MyParamsUpdateNotebook(int id, String newName) {this.id = id; this.newName = newName;}
    }

}
