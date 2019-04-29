package com.example.prova2.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

/*
The ViewModel is a class whose role is to provide data to the UI and survive configuration changes.
A ViewModel acts as a communication center between the Repository and the UI.
 */
public class NotebooksViewModel extends AndroidViewModel {
    private NotebookRepository mRepository;

    private LiveData<List<Notebook>> mAllNotebooks;
    private List<NotebookContent> mAllContents;

    public NotebooksViewModel (Application application) {
        super(application);
        mRepository = new NotebookRepository(application);
        mAllNotebooks = mRepository.getAllNotebooks();
        mAllContents = mRepository.getAllFiles();
    }

    public LiveData<List<Notebook>> getAllNotebooks() {
        return mAllNotebooks;
    }

    public LiveData<Notebook> getNotebook(int id) { return mRepository.getNotebook(id); }

    public List<NotebookContent> getAllFiles() {
        return mAllContents;
    }

    public List<NotebookContent> getAllFiles(int nb) {
        return mRepository.getAllFiles(nb);
    }

    public NotebookContent getFile(int num) {
        return mRepository.getFile(num);
    }

    public int insertNotebook(Notebook nb) {return mRepository.insertNotebook(nb);}

    public long insertFile(NotebookContent nc) {return mRepository.insertFile(nc);}

    public void updateNotebook(int id, String newName) {mRepository.updateNotebook(id, newName);}

    public void deleteNotebook(Notebook nb) {mRepository.deleteNotebook(nb);}

    public void deleteContent(NotebookContent nc) {mRepository.deleteFile(nc);}
}
