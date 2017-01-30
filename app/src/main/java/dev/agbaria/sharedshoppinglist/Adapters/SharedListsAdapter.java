package dev.agbaria.sharedshoppinglist.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Fragments.ListItemsFragment;
import dev.agbaria.sharedshoppinglist.Models.MySharedList;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.R;

/**
 * Created by agbaria on 28/12/2016.
 *
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
        View view = inflater.inflate(R.layout.shared_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final DataSnapshot snapshot = shoppingLists.get(position);
        final MySharedList myList = snapshot.getValue(MySharedList.class);
        final String listKey = snapshot.getKey();
        FirebaseDatabase.getInstance().getReference().child("Lists").child(listKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ShoppingList shoppingList = dataSnapshot.getValue(ShoppingList.class);
                        holder.listName.setText(shoppingList.getListName());
                        holder.listOwner.setText(shoppingList.getListOwner());
                        //TODO display list itemsCount & checkedItemsCount
                        holder.layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Fragment fragment = ListItemsFragment.getInstance(listKey, shoppingList, myList);
                                activity.getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_main, fragment, "ListItemsFragment").addToBackStack(null).commit();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView listName;
        public TextView listOwner;
        public RelativeLayout layout;

        public ViewHolder(View v) {
            super(v);
            listName = (TextView) v.findViewById(R.id.tvListName);
            listOwner = (TextView) v.findViewById(R.id.tvListOwner);
            layout = (RelativeLayout) v.findViewById(R.id.rlListItem);
        }
    }
}
