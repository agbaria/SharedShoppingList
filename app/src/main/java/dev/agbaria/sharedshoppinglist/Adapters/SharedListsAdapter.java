package dev.agbaria.sharedshoppinglist.Adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.R;

/**
 * Created by ANDROID on 28/12/2016.
 */

public class SharedListsAdapter extends RecyclerView.Adapter<SharedListsAdapter.ViewHolder> {

    private ArrayList<DataSnapshot> shoppingLists;
    private LayoutInflater inflater;
    private FragmentActivity activity;

    public SharedListsAdapter(ArrayList<DataSnapshot> shoppingLists, FragmentActivity activity) {
        this.shoppingLists = shoppingLists;
        this.inflater = LayoutInflater.from(activity);
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataSnapshot snapshot = shoppingLists.get(position);
        ShoppingList shoppingList = snapshot.getValue(ShoppingList.class);
        //TODO to be continue
        //https://github.com/appswebdev/Fire/blob/master/app/src/main/java/college/minhal/fire/fragments/ShoppingListFragment.java
        //https://github.com/appswebdev/Fire/blob/master/app/src/main/java/college/minhal/fire/adapters/ShoppingListsAdapter.java
        holder.listName.setText(shoppingList.getListName());
        holder.listOwner.setText(shoppingList.getListOwner());
        holder.listId = shoppingList.getListID();
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView listName;
        public TextView listOwner;
        public String listId;

        public ViewHolder(View v) {
            super(v);
            listName = (TextView) v.findViewById(R.id.tvListName);
            listOwner = (TextView) v.findViewById(R.id.tvListOwner);
        }
    }
}
