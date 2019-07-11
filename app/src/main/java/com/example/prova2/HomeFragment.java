package com.example.prova2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prova2.database.NbListAdapter;
import com.example.prova2.database.Notebook;
import com.example.prova2.database.NotebookContent;
import com.example.prova2.database.NotebooksViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


/*
Fragment containing the home view of the PagerAdapter with the notebook list, updated using NbListAdapter
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private String sectionNumber;
    private View view;

    private TextView mOCR;

    private OnFragmentInteractionListener mListener;

    private NotebooksViewModel mNotebooksViewModel;


    //Storage
    private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;

    public final int PICK_IMAGE_REQUEST = 71;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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

    /*
     defines what the fragment displays and the onClick actions on the elements of the UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //connect with the database
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        //get list items from NbListAdapter and inflate the layout
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


        //Defines action for floating action button + to add new notebook
        //open dialog to prompt insertion of notebook name
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

            }
        });

        return view;
    }


    /**
     * Calls insert query for a new notebook with name
     * @param name The name of the notebook
     */
    public void addAProject (String name) {
        // insert new project into db
        if (!name.isEmpty())
            mNotebooksViewModel.insertNotebook(new Notebook(name));
        else {
            Snackbar.make(view, "Title can't be empty", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }


    private int mNb_id = -1;

    /**
     * Makes the user pick an image from device storage
     * @param nb_id Id of the notebook that the file has to be linked to
     */
    public void chooseImage(int nb_id) {
        mNb_id = nb_id;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Uploads the chosen image to Firebase Storage
     * @param nb_id Id of the notebook that the file has to be linked to
     */
    private void uploadImage(final int nb_id) {

        if(filePath != null)
        {


            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    //insert into db
                                    NotebookContent nc = new NotebookContent(nb_id, downloadUrl.toString());
                                    long id = mNotebooksViewModel.insertFile(nc);

                                    Log.d(TAG, "Insert " + downloadUrl.toString() + " in notebook " + nb_id
                                            + " with number " + id);


                                    //Dialog that tells the user the id of the file they uploaded
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Write:");
                                    View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.number_dialog,
                                            (ViewGroup) getView(), false);
                                    final TextView input = viewInflated.findViewById(R.id.textView);
                                    //final TextView mLastNumber = getView().findViewById(R.id.textViewNumber);
                                    NbListAdapter adapter;
                                    if (id < 10) {
                                        input.setText("0" + id);
                                        //adapter = new NbListAdapter(getContext(), "0" + id);
                                    }
                                    else {
                                        input.setText("" + id);
                                        //adapter = new NbListAdapter(getContext(), "" + id);
                                    }
                                    builder.setView(viewInflated);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.show();
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });




        }
    }

    /*
    Receives the result of the activity launched in method chooseImage, so the image picked by the user
    Creates dialog to ask for confirmation to upload to Firebase Storage
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Do you want to store it?");
                View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.image_dialog,
                        (ViewGroup) this.getView(), false);


                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                final ImageView image = viewInflated.findViewById(R.id.imgView);
                image.setImageBitmap(bitmap);
                builder.setView(viewInflated);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        uploadImage(mNb_id);

                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
//                imageView.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    // not used
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
