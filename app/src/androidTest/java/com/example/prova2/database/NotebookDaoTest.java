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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class NotebookDaoTest {
    private NotebookRoomDatabase mDatabase;
    private NotebookDao mNotebook;

    @Before
    public void initDb() throws Exception {
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(),
                NotebookRoomDatabase.class)
                .allowMainThreadQueries()
                .build();
        mNotebook = mDatabase.notebookDao();
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    @Test
    public void testTableIsEmpty() throws InterruptedException {
        List<Notebook> notebookList = LiveDataTestUtil.getValue(mNotebook.getAllNotebooks());
        assertTrue(notebookList.isEmpty());
    }

    @Test
    public void testInsertNotebook() throws InterruptedException {
        ArrayList<Notebook> notebookList = new ArrayList<>();
        for (int i=0; i<5; i++) {
            notebookList.add(new Notebook("Notebook"+ i));
        }
        int id[] = new int[5];
        int j = -1;
        for (Notebook nb : notebookList) {
            id[++j] = (int) mNotebook.insertNotebook(nb);
            nb.setId(id[j]);
        }

        assertEquals(notebookList.get(4).toString(), LiveDataTestUtil.getValue(mNotebook.getNotebook(id[4])).toString());
    }

    @Test
    public void testDeleteNotebook() throws InterruptedException {
        ArrayList<Notebook> notebookList = new ArrayList<>();
        for (int i=0; i<5; i++) {
            notebookList.add(new Notebook("Notebook"+ i));
        }
        int id[] = new int[5];
        int j = -1;
        for (Notebook nb : notebookList)
            id[++j] = (int)mNotebook.insertNotebook(nb);

        mNotebook.deleteNotebook(LiveDataTestUtil.getValue(mNotebook.getNotebook(id[3])));
        assertNull(LiveDataTestUtil.getValue(mNotebook.getNotebook(id[3])));
    }

    @Test
    public void testUpdateNotebook() throws InterruptedException {
        ArrayList<Notebook> notebookList = new ArrayList<>();
        for (int i=0; i<5; i++) {
            notebookList.add(new Notebook("Notebook"+ i));
        }
        int id[] = new int[5];
        int j = -1;
        for (Notebook nb : notebookList)
            id[++j] = (int)mNotebook.insertNotebook(nb);


        mNotebook.updateNotebook(id[2], "N"+2);
        assertEquals("N"+2, LiveDataTestUtil.getValue(mNotebook.getNotebook(id[2])).getNbName());
    }

    @Test
    public void testGetAllNotebooks() throws InterruptedException {
        ArrayList<Notebook> notebookList = new ArrayList<>();
        for (int i=0; i<5; i++) {
            notebookList.add(new Notebook("Notebook"+ i));
        }
        int id[] = new int[5];
        int j = -1;
        for (Notebook nb : notebookList) {
            id[++j] = (int) mNotebook.insertNotebook(nb);
            nb.setId(id[j]);
        }

        List<Notebook> notebooksFromDb = LiveDataTestUtil.getValue(mNotebook.getAllNotebooks());

        if (notebookList.size() != notebooksFromDb.size()) {
            fail();
        }
        for (int i=0; i<notebookList.size(); i++) {
            assertEquals(notebookList.get(i).toString(), notebooksFromDb.get(i).toString());
        }
    }
}

