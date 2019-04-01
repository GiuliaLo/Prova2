package com.example.prova2.database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.prova2.R;

import java.util.List;

public class NbListAdapter extends RecyclerView.Adapter<NbListAdapter.NbViewHolder> {

class NbViewHolder extends RecyclerView.ViewHolder {
    private final TextView nbItemView;

    private NbViewHolder(View itemView) {
        super(itemView);
        nbItemView = itemView.findViewById(R.id.textView);
    }
}

    private final LayoutInflater mInflater;
    private List<Notebook> mNotebooks; // Cached copy of words

    public NbListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public NbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new NbViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NbViewHolder holder, int position) {
        if (mNotebooks != null) {
            Notebook current = mNotebooks.get(position);
            //TODO add last file number
            holder.nbItemView.setText(current.getNbName());
        } else {
            // Covers the case of data not being ready yet.
            holder.nbItemView.setText("No Notebooks");
        }
    }

    public void setNotebooks(List<Notebook> notebooks){
        mNotebooks = notebooks;
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
