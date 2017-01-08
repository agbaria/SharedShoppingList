package dev.agbaria.sharedshoppinglist.Adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Models.ListItem;
import dev.agbaria.sharedshoppinglist.R;

/**
 * Created by ANDROID on 04/01/2017.
 */

public class ListItemsAdapter extends RecyclerView.Adapter<ListItemsAdapter.ViewHolder> {

    private ArrayList<DataSnapshot> listItems;
    private LayoutInflater inflater;
    private FragmentActivity activity;

    public ListItemsAdapter(ArrayList<DataSnapshot> shoppingLists, FragmentActivity activity) {
        this.listItems = shoppingLists;
        this.inflater = LayoutInflater.from(activity);
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_item, parent, false);
        return new ListItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataSnapshot snapshot = listItems.get(position);
        ListItem item = snapshot.getValue(ListItem.class);
        Log.d("agbaria", item.toString());
        holder.itemName.setText(item.getItemName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add on click listener to item
                //item id: snapshot.getKey()
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemName;
        public RelativeLayout layout;

        public ViewHolder(View v) {
            super(v);
            itemName = (TextView) v.findViewById(R.id.tvItemName);
            layout = (RelativeLayout) v.findViewById(R.id.rlItemItem);
        }
    }
}
