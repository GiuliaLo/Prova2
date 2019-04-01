package com.example.prova2.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class NotebooksViewModel extends AndroidViewModel {
    private NotebookRepository mRepository;

    private LiveData<List<Notebook>> mAllNotebooks;
    private LiveData<List<NotebookContent>> mAllContents;

    public NotebooksViewModel (Application application) {
        super(application);
        mRepository = new NotebookRepository(application);
        mAllNotebooks = mRepository.getAllNotebooks();
        mAllContents = mRepository.getAllFiles();
    }

    public LiveData<List<Notebook>> getAllNotebooks() {
        return mAllNotebooks;
    }

    public LiveData<List<NotebookContent>> getAllFiles() {
        return mAllContents;
    }

    public LiveData<List<NotebookContent>> getAllFiles(String nb) {
        return mRepository.getAllFiles(nb);
    }

    public LiveData<NotebookContent> getFile(String nb, int num) {
        return mRepository.getFile(nb, num);
    }

    public LiveData<Integer> getLastFileNumber(String nb) {
        return mRepository.getLastFileNumber(nb);
    }

    public void insertNotebook(Notebook nb) {mRepository.insertNotebook(nb);}

    public void insertFile(NotebookContent nc) {mRepository.insertFile(nc);}

    public void updateNotebook(String oldName, String newName) {mRepository.updateNotebook(oldName, newName);}

    public void deleteNotebook(Notebook nb) {mRepository.deleteNotebook(nb);}

    public void deleteFile(String nb, int num) {mRepository.deleteFile(nb, num);}
}
