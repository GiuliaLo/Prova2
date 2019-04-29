package com.example.prova2.database;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.prova2.HomeFragment;
import com.example.prova2.R;

import java.util.List;

/*
Class that manages single recyclerview_item elements, that is single notebooks and their actions
 */
public class NbListAdapter extends RecyclerView.Adapter<NbListAdapter.NbViewHolder> {

    class NbViewHolder extends RecyclerView.ViewHolder {
        private final TextView nbItemView;
        private final ImageButton mDeleteNotebook;
        private final ImageButton mUpdateNotebook;
        private final ImageButton mInsertFile;
        //private final TextView mLastNumber;

        private NbViewHolder(View itemView) {
            super(itemView);
            nbItemView = itemView.findViewById(R.id.textView);
            mDeleteNotebook = itemView.findViewById(R.id.btn_delete);
            mUpdateNotebook = itemView.findViewById(R.id.btn_update);
            mInsertFile = itemView.findViewById(R.id.btn_insert);
            //mLastNumber = itemView.findViewById(R.id.textViewNumber);
        }
    }

    private final LayoutInflater mInflater;
    private List<Notebook> mNotebooks; // Cached copy of notebooks
    private NotebooksViewModel mModel;
    private Context mContext;
    private String lastId;

    public NbListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    //Constructor with last file number (not used)
    public NbListAdapter(Context context, String id) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        lastId = id;
    }

    @Override
    public NbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new NbViewHolder(itemView);
    }

    //Loads notebook name of current notebook and manages onClick actions on update and delete notebook and upload file
    @Override
    public void onBindViewHolder(NbViewHolder holder, final int position) {
        if (mNotebooks != null) {
            Notebook current = mNotebooks.get(position);
            holder.nbItemView.setText(current.getNbName());
            //holder.mLastNumber.setText(lastId);
        } else {
            // Covers the case of data not being ready yet.
            holder.nbItemView.setText("No Notebooks");
        }

        holder.mDeleteNotebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("DELETE NOTEBOOK");
                builder.setMessage("Do you really want to delete the notebook?\nYou will lose all of the files connected to it.");
                View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.empty_dialog,
                        (ViewGroup) v.getParent(), false);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mModel.deleteNotebook(mNotebooks.get(position));
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

        holder.mUpdateNotebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("New Title");
                View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.input_dialog,
                        (ViewGroup) v.getParent(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        mModel.updateNotebook(mNotebooks.get(position).getId(), input.getText().toString() );
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


        holder.mInsertFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Do you want upload a new file? Choose it!");
                View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.empty_dialog,
                        (ViewGroup) v.getParent(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                builder.setView(viewInflated);
                builder.setPositiveButton("choose", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        ((HomeFragment)((FragmentActivity)mContext).getSupportFragmentManager().
                                getFragments().get(0)).chooseImage(mNotebooks.get(position).getId());

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

    }

    //called by HomeFragment to get complete list of notebooks
    public void setNotebooks(List<Notebook> notebooks, NotebooksViewModel model){
        mNotebooks = notebooks;
        mModel = model;
        notifyDataSetChanged();
    }

    //returns number of notebooks
    @Override
    public int getItemCount() {
        if (mNotebooks != null)
            return mNotebooks.size();
        else return 0;
    }
}
