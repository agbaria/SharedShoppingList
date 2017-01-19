package dev.agbaria.sharedshoppinglist.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import dev.agbaria.sharedshoppinglist.Models.User;
import dev.agbaria.sharedshoppinglist.R;

/**
 * Created by 3la2 on 09/01/2017.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private ArrayList<DataSnapshot> friends;
    private LayoutInflater inflater;
    private FragmentActivity activity;

    public FriendsAdapter(ArrayList<DataSnapshot> snapshots, FragmentActivity activity) {
        this.friends = snapshots;
        this.inflater = LayoutInflater.from(activity);
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.friends_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataSnapshot snapshot = friends.get(position);
        User user = snapshot.getValue(User.class);
        holder.friendName.setText(user.getName());
        holder.friendEmail.setText(user.getEmail());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add Listener idf needed
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView friendName;
        public TextView friendEmail;
        public RelativeLayout layout;

        public ViewHolder(View v) {
            super(v);

            friendName = (TextView) v.findViewById(R.id.tvFriendName);
            friendEmail = (TextView) v.findViewById(R.id.tvFriendEmail);
            layout = (RelativeLayout) v.findViewById(R.id.rlFriend);
        }
    }
}
