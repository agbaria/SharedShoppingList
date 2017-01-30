package dev.agbaria.sharedshoppinglist.Adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Listeners.PositionClickedListener;
import dev.agbaria.sharedshoppinglist.Models.ListItem;
import dev.agbaria.sharedshoppinglist.R;
import dev.agbaria.sharedshoppinglist.Transactions.DecreaseTransaction;
import dev.agbaria.sharedshoppinglist.Transactions.IncreaseTransaction;
import dev.agbaria.sharedshoppinglist.Utils;

/**
 * Created by agbaria on 04/01/2017.
 *
 */

public class ListItemsAdapter extends RecyclerView.Adapter<ListItemsAdapter.ViewHolder> {

    private ArrayList<DataSnapshot> listItems;
    private String listID;
    private LayoutInflater inflater;
    private final FragmentActivity activity;
    private String userID;
    private PositionClickedListener positionClickedListener;
    private boolean selectMode;


    public ListItemsAdapter(ArrayList<DataSnapshot> shoppingLists, String listID,
                            FragmentActivity activity, PositionClickedListener listener) {
        this.listItems = shoppingLists;
        this.listID = listID;
        this.inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.userID = Utils.getUserID();
        this.positionClickedListener = listener;
        selectMode = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_item, parent, false);
        return new ListItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DataSnapshot snapshot = listItems.get(position);
        final ListItem item = snapshot.getValue(ListItem.class);
        holder.tvItemName.setText(item.getItemName());
        if (!item.isChecked()) {
            holder.tv1.setText(R.string.unchecked);
            holder.tvUserName.setVisibility(View.INVISIBLE);
            holder.rbCheck.setSelected(false);
            holder.rbCheck.setChecked(false);
            holder.rbCheck.setEnabled(true);
        }
        else {
            if (item.getUserID().equals(userID)) {
                holder.tvUserName.setText(R.string.you);
                holder.rbCheck.setEnabled(true);
            }
            else {
                holder.rbCheck.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(item.getUserID()).child("name").addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String name = dataSnapshot.getValue(String.class);
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.tvUserName.setText(name);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }
                        );
                    }
                }).start();
            }
            holder.rbCheck.setSelected(true);
            holder.rbCheck.setChecked(true);
            holder.tv1.setText(R.string.checked_by);
            holder.tvUserName.setVisibility(View.VISIBLE);
        }
        holder.rbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.rbCheck.setEnabled(false);
                if (holder.rbCheck.isSelected()) {
                    item.setChecked(false);
                    item.setUserID("");
                    FirebaseDatabase.getInstance().getReference().child("ListItems").child(listID)
                            .child(item.getItemName()).setValue(item);
                    FirebaseDatabase.getInstance().getReference().child("Lists").child(listID)
                            .child("checkedItems").runTransaction(new DecreaseTransaction(1));
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("ListItems").child(listID)
                            .child(item.getItemName()).runTransaction(
                            new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    ListItem value = mutableData.getValue(ListItem.class);
                                    if (value.isChecked())
                                        return Transaction.success(mutableData);
                                    value.setChecked(true);
                                    value.setUserID(userID);
                                    mutableData.setValue(value);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                    ListItem item = dataSnapshot.getValue(ListItem.class);
                                    if (!item.getUserID().equals(userID)) return;

                                    FirebaseDatabase.getInstance().getReference().child("Lists").child(listID)
                                            .child("checkedItems").runTransaction(new IncreaseTransaction());
                                }
                            }
                    );
                }
            }
        });
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                selectMode = positionClickedListener.clicked(holder.getAdapterPosition(), holder.layout);
                return true;
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectMode)
                    selectMode = positionClickedListener.clicked(holder.getAdapterPosition(), holder.layout);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemName;
        TextView tv1;
        TextView tvUserName;
        RadioButton rbCheck;
        public RelativeLayout layout;

        ViewHolder(View v) {
            super(v);
            tvItemName = (TextView) v.findViewById(R.id.tvItemName);
            tv1 = (TextView) v.findViewById(R.id.tv1);
            tvUserName = (TextView) v.findViewById(R.id.tvUserName);
            rbCheck = (RadioButton) v.findViewById(R.id.rbCheck);
            layout = (RelativeLayout) v.findViewById(R.id.rlItemItem);
        }
    }
}
