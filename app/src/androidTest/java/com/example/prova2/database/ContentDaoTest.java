package com.example.prova2.database;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ContentDaoTest {
    private NotebookRoomDatabase mDatabase;
    private ContentDao mContent;
    private NotebookDao mNotebook;

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(),
                NotebookRoomDatabase.class)
                .allowMainThreadQueries()
                .build();
        mContent = mDatabase.contentDao();
        mNotebook = mDatabase.notebookDao();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void testInsertFile() {
        ArrayList<NotebookContent> contentList = new ArrayList<>();
        Notebook nb = new Notebook("testNotebook");
        int id = (int)mNotebook.insertNotebook(nb);
        for (int i=0; i<5; i++) {
            contentList.add(new NotebookContent(id, "fp"+i));
        }

        int fileId[] = new int[5];
        int j = -1;
        for (NotebookContent nc : contentList) {
            fileId[++j] = (int) mContent.insertFile(nc);
            nc.setNum(fileId[j]);
        }

        for (int i=0; i<5; i++)
            assertEquals(contentList.get(0).toString(), mContent.getFile(fileId[0]).toString());
    }

    @Test
    public void testDeleteFile() {
        ArrayList<NotebookContent> contentList = new ArrayList<>();
        Notebook nb = new Notebook("testNotebook");
        int id = (int)mNotebook.insertNotebook(nb);
        for (int i=0; i<5; i++) {
            contentList.add(new NotebookContent(id, "fp"+i));
        }

        int fileId[] = new int[5];
        int j = -1;
        for (NotebookContent nc : contentList)
            fileId[++j] = (int)mContent.insertFile(nc);

        for (int i=0; i<5; i++) {
            mContent.deleteFile(mContent.getFile(fileId[i]));
            assertNull(mContent.getFile(fileId[i]));
        }

    }


    @Test
    public void testGetFile() {
        Notebook nb = new Notebook("testNotebook");
        int nbId = (int)mNotebook.insertNotebook(nb);

        NotebookContent nc = new NotebookContent(nbId, "fp");
        int id = (int)mContent.insertFile(nc);

        NotebookContent fileFromDb = mContent.getFile(id);
        assertEquals(id+", "+nbId+", "+"fp", fileFromDb.getNum()+", "+fileFromDb.getNotebook()+", "+fileFromDb.getFilePath());
    }

    @Test
    public void testGetAllFiles() {
        ArrayList<NotebookContent> contentList = new ArrayList<>();
        Notebook nb1 = new Notebook("testNotebook1");
        Notebook nb2 = new Notebook("testNotebook2");
        int id1 = (int)mNotebook.insertNotebook(nb1);
        int id2 = (int)mNotebook.insertNotebook(nb2);

        for (int i=0; i<5; i++) {
            contentList.add(new NotebookContent(id1, "fp"+i));
        }

        contentList.add(new NotebookContent(id2, "fp"));

        int fileId[] = new int[contentList.size()];
        int j = -1;
        for (NotebookContent nc : contentList) {
            fileId[++j] = (int) mContent.insertFile(nc);
            nc.setNum(fileId[j]);
        }

        List<NotebookContent> fileListFromDb = mContent.getAllFiles();

        if (contentList.size() != fileListFromDb.size())
            fail();

        for (int i=0; i<contentList.size(); i++) {
            assertEquals(contentList.get(i).toString(), fileListFromDb.get(i).toString());
        }
    }

    @Test
    public void testGetAllFilesFromNotebook() {
        ArrayList<NotebookContent> contentList = new ArrayList<>();
        Notebook nb1 = new Notebook("testNotebook1");
        Notebook nb2 = new Notebook("testNotebook2");
        int id1 = (int)mNotebook.insertNotebook(nb1);
        int id2 = (int)mNotebook.insertNotebook(nb2);

        for (int i=0; i<5; i++) {
            contentList.add(new NotebookContent(id1, "fp"+i));
        }

        contentList.add(new NotebookContent(id2, "fp"));

        int fileId[] = new int[contentList.size()];
        int j = -1;
        for (NotebookContent nc : contentList) {
            fileId[++j] = (int) mContent.insertFile(nc);
            nc.setNum(fileId[j]);
        }

        List<NotebookContent> fileListFromDb = mContent.getAllFiles(id1);

        if (fileListFromDb.size() != 5)
            fail();

        for (int i=0; i<fileListFromDb.size(); i++) {
            assertEquals(contentList.get(i).toString(), fileListFromDb.get(i).toString());
        }
    }
}