package com.example.prova2.database;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.prova2.R;

import java.util.List;

public class NbListAdapter extends RecyclerView.Adapter<NbListAdapter.NbViewHolder> {

class NbViewHolder extends RecyclerView.ViewHolder {
    private final TextView nbItemView;
    private final Button mDeleteNotebook;
    private final Button mUpdateNotebook;
    private final Button mInsertFile;

    private NbViewHolder(View itemView) {
        super(itemView);
        nbItemView = itemView.findViewById(R.id.textView);
        mDeleteNotebook = itemView.findViewById(R.id.btn_delete);
        mUpdateNotebook = itemView.findViewById(R.id.btn_update);
        mInsertFile = itemView.findViewById(R.id.btn_insert);
    }
}

    private final LayoutInflater mInflater;
    private List<Notebook> mNotebooks; // Cached copy of words
    private NotebooksViewModel mModel;
    private Context mContext;

    public NbListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public NbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new NbViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NbViewHolder holder, final int position) {
        if (mNotebooks != null) {
            Notebook current = mNotebooks.get(position);
            //TODO add last file number
            holder.nbItemView.setText(current.getNbName());
        } else {
            // Covers the case of data not being ready yet.
            holder.nbItemView.setText("No Notebooks");
        }

        holder.mDeleteNotebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModel.deleteNotebook(mNotebooks.get(position));
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

                        mModel.updateNotebook(mNotebooks.get(position).getNbName(), input.getText().toString() );
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
                builder.setTitle("Content");
                View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.input_dialog,
                        (ViewGroup) v.getParent(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        NotebookContent nc = new NotebookContent((mNotebooks.get(position).getId()),
                                input.getText().toString());

                        mModel.insertFile(nc);
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

    public void setNotebooks(List<Notebook> notebooks, NotebooksViewModel model){
        mNotebooks = notebooks;
        mModel = model;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mNotebooks != null)
            return mNotebooks.size();
        else return 0;
    }
}
