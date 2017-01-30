package dev.agbaria.sharedshoppinglist.Adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Date;

import dev.agbaria.sharedshoppinglist.Models.MySharedList;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.Models.User;
import dev.agbaria.sharedshoppinglist.R;

/**
 * Created by agbaria on 22/01/2017.
 *
 */

public class ListInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FIRST = 1;
    private ArrayList<DataSnapshot> friends;
    private ShoppingList list;
    private MySharedList myList;
    private LayoutInflater inflater;

    public ListInfoAdapter(ArrayList<DataSnapshot> snapshots, ShoppingList list, MySharedList myList
            , FragmentActivity activity) {
        this.friends = snapshots;
        this.list = list;
        this.myList = myList;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FIRST){
            View view = inflater.inflate(R.layout.list_info_header, parent, false);
            return new HeaderViewHolder(view);
        }
        else {
            View view = inflater.inflate(R.layout.friends_item, parent, false);
            return new FriendViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (viewType == FIRST){
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.listOwner.setText(list.getListOwner());
            Date creationDate = new Date(list.getCreationTimeStamp());
            viewHolder.creationDate.setText(DateFormat.format("dd/MM/yyyy", creationDate));
            Date joinDate = new Date(myList.getJoinDate());
            viewHolder.joinDate.setText(DateFormat.format("dd/MM/yyyy", joinDate));
        }
        else{
            FriendViewHolder viewHolder = (FriendViewHolder) holder;
            DataSnapshot snapshot = friends.get(position - 1);
            User user = snapshot.getValue(User.class);

            viewHolder.friendName.setText(user.getName());
            viewHolder.friendEmail.setText(user.getEmail());
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO add Listener if needed
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return friends.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return FIRST;
        }
        return super.getItemViewType(position);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView listOwner;
        TextView creationDate;
        TextView joinDate;

        HeaderViewHolder(View v) {
            super(v);
            listOwner = (TextView) v.findViewById(R.id.tvListOwner);
            creationDate = (TextView) v.findViewById(R.id.tvCreationDate);
            joinDate = (TextView) v.findViewById(R.id.tvJoinDate);
        }
    }

    private class FriendViewHolder extends RecyclerView.ViewHolder {

        TextView friendName;
        TextView friendEmail;
        public RelativeLayout layout;

        FriendViewHolder(View v) {
            super(v);
            friendName = (TextView) v.findViewById(R.id.tvFriendName);
            friendEmail = (TextView) v.findViewById(R.id.tvFriendEmail);
            layout = (RelativeLayout) v.findViewById(R.id.rlFriend);
        }
    }
}
