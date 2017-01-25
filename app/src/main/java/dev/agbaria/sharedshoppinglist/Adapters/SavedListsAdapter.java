package dev.agbaria.sharedshoppinglist.Adapters;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Listeners.PositionClickedListener;
import dev.agbaria.sharedshoppinglist.Models.SavedList;
import dev.agbaria.sharedshoppinglist.R;

/**
 * Created by 3la2 on 24/01/2017.
 */

public class SavedListsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FIRST = 1;
    private ArrayList<DataSnapshot> snapshots;
    private LayoutInflater inflater;
    private boolean toAdd;
    private PositionClickedListener listener;

    public SavedListsAdapter(ArrayList<DataSnapshot> snapshots, Activity activity,
                             boolean toAdd, @Nullable PositionClickedListener listener) {
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
        if (toAdd && viewType == FIRST)
            return;

        DataSnapshot snapshot = null;
        if (!toAdd) {
            snapshot = snapshots.get(position);
            Log.d("agbaria", "P: " + position);
        }
        else if (viewType != FIRST) {
            snapshot = snapshots.get(position - 1);
        }
        Log.d("agbaria", snapshot.toString());
        SavedListViewHolder viewHolder = (SavedListViewHolder) holder;
        SavedList savedList = snapshot.getValue(SavedList.class);
        viewHolder.tvListName.setText(savedList.getListName());
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toAdd)
                    listener.clicked(holder.getAdapterPosition(), view);
                else {
                    //TODO add listener
                }
            }
        });
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

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View v) {
            super(v);
        }
    }

    public class SavedListViewHolder extends RecyclerView.ViewHolder {

        public TextView tvListName;
        public RelativeLayout layout;

        public SavedListViewHolder(View v) {
            super(v);

            tvListName = (TextView) v.findViewById(R.id.tvSavedListName);
            layout = (RelativeLayout) v.findViewById(R.id.rlSavedListItem);
        }
    }
}
