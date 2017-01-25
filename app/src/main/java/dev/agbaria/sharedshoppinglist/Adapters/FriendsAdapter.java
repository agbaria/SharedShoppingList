package dev.agbaria.sharedshoppinglist.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Listeners.PositionClickedListener;
import dev.agbaria.sharedshoppinglist.Models.User;
import dev.agbaria.sharedshoppinglist.R;

/**
 * Created by agbaria on 09/01/2017.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private ArrayList<DataSnapshot> friends;
    private LayoutInflater inflater;
    private boolean toAdd;
    private PositionClickedListener listener;

    public FriendsAdapter(ArrayList<DataSnapshot> snapshots, FragmentActivity activity,
                          boolean toAdd, @Nullable PositionClickedListener listener) {
        this.friends = snapshots;
        this.inflater = LayoutInflater.from(activity);
        this.toAdd = toAdd;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.friends_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DataSnapshot snapshot = friends.get(position);
        User user = snapshot.getValue(User.class);
        holder.friendName.setText(user.getName());
        holder.friendEmail.setText(user.getEmail());
        holder.layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toAdd)
                    listener.clicked(holder.getAdapterPosition(), view);
                else {
                    //TODO add Listener idf needed
                }
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
