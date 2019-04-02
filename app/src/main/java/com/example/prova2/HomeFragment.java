package com.example.prova2;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prova2.database.NbListAdapter;
import com.example.prova2.database.Notebook;
import com.example.prova2.database.NotebookRoomDatabase;
import com.example.prova2.database.NotebooksViewModel;

import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    */
    private static final String ARG_SECTION_NUMBER = "section_number";

    // TODO: Rename and change types of parameters
    /*
    private String mParam1;
    private String mParam2;
    */
    private String sectionNumber;
    private View view;


    //private ViewGroup mContainer;

    private TextView mOCR;

    private OnFragmentInteractionListener mListener;

    private NotebooksViewModel mNotebooksViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * param param1 Parameter 1.
     * param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    //public static HomeFragment newInstance(String param1, String param2) {
    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            */

            FragmentManager mng = getFragmentManager();
            mng.addOnBackStackChangedListener(
                    new FragmentManager.OnBackStackChangedListener() {
                        public void onBackStackChanged() {
                            // TODO Update your UI here.
                        }
                    });
            sectionNumber = getArguments().getString(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mContainer = container;
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        final NbListAdapter adapter = new NbListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mNotebooksViewModel = ViewModelProviders.of(this).get(NotebooksViewModel.class);


        mNotebooksViewModel.getAllNotebooks().observe(this, new Observer<List<Notebook>>() {
            @Override
            public void onChanged(@Nullable final List<Notebook> notebooks) {
                // Update the cached copy of the words in the adapter.
                adapter.setNotebooks(notebooks, mNotebooksViewModel);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Title");
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.input_dialog, (ViewGroup) getView(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        addAProject(input.getText().toString());
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                /*
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment addNotebook = AddNotebook.newInstance();
                transaction.replace(R.id.container,addNotebook); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
                */
                /*
                Snackbar.make(view, "Replace with add notebook action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
            }
        });


        return view;
    }

    public void setOcrText(String text) {

        mOCR = getView().findViewById(R.id.ocr_text);

        mOCR.setText(text);

    }

    public void addAProject (String name) {
        // insert new project into db
        if (!name.isEmpty())
            mNotebooksViewModel.insertNotebook(new Notebook(name));
        else {
            Snackbar.make(view, "Title can't be empty", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        // show text.. only for test
        /*
        mOCR = getView().findViewById(R.id.ocr_text);
        mOCR.setText(name);
        */
    }

    /*
        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    Fragment addNotebook = AddNotebook.newInstance();
                    transaction.replace(mContainer.getId(),addNotebook); // give your fragment container id in first parameter
                    transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                    transaction.commit();
                    //
                    //Snackbar.make(view, "Replace with add notebook action", Snackbar.LENGTH_LONG)
                    //.setAction("Action", null).show();
                }
            });
        }
    */
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
