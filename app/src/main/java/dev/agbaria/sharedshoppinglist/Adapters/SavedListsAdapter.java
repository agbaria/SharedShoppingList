package dev.agbaria.sharedshoppinglist.Adapters;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Fragments.SavedListsFragment;
import dev.agbaria.sharedshoppinglist.Models.SavedList;
import dev.agbaria.sharedshoppinglist.R;

/**
 * Created by agbaria on 24/01/2017.
 *
 */

public class SavedListsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FIRST = 1;
    private ArrayList<DataSnapshot> snapshots;
    private LayoutInflater inflater;
    private boolean toAdd;
    private SavedListsFragment listener;

    public SavedListsAdapter(ArrayList<DataSnapshot> snapshots, Activity activity,
                             boolean toAdd, @Nullable SavedListsFragment listener) {
        this.snapshots = snapshots;
        this.inflater = LayoutInflater.from(activity);
        this.toAdd = toAdd;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (toAdd && viewType == FIRST) {
            View view = inflater.inflate(R.layout.saved_lists_header, parent, false);
            return new HeaderViewHolder(view);
        }
        else {
            View view = inflater.inflate(R.layout.saved_list_item, parent, false);
            return new SavedListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (toAdd && viewType == FIRST) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.btnCreateList.setOnClickListener(listener);
        }
        else {
            DataSnapshot snapshot;
            if (!toAdd) {
                snapshot = snapshots.get(position);
            } else {
                snapshot = snapshots.get(position - 1);
            }
            SavedListViewHolder viewHolder = (SavedListViewHolder) holder;
            SavedList savedList = snapshot.getValue(SavedList.class);
            viewHolder.tvListName.setText(savedList.getListName());
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (toAdd)
                        listener.clicked(holder.getAdapterPosition() - 1, view);
                    else {
                        //TODO add positionClickedListener
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int size = snapshots.size();
        if (toAdd)
            size++;
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (toAdd && position == 0)
            return FIRST;
        return super.getItemViewType(position);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        Button btnCreateList;

        HeaderViewHolder(View v) {
            super(v);
            btnCreateList = (Button) v.findViewById(R.id.btnCreateList);
        }
    }

    private class SavedListViewHolder extends RecyclerView.ViewHolder {

        TextView tvListName;
        RelativeLayout layout;

        SavedListViewHolder(View v) {
            super(v);

            tvListName = (TextView) v.findViewById(R.id.tvSavedListName);
            layout = (RelativeLayout) v.findViewById(R.id.rlSavedListItem);
        }
    }
}
