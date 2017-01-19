package dev.agbaria.sharedshoppinglist.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Models.User;
import dev.agbaria.sharedshoppinglist.R;

/**
 * Created by 3la2 on 19/01/2017.
 */
//TODO probably remove this class
public class ArrayAdapter extends android.widget.ArrayAdapter<DataSnapshot> {

    private LayoutInflater inflater;

    public ArrayAdapter(Context context, int resource, ArrayList<DataSnapshot> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataSnapshot snapshot = getItem(position);
        User user = snapshot.getValue(User.class);

        ArrayAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ArrayAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.friends_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tvFriendName);
            viewHolder.email = (TextView) convertView.findViewById(R.id.tvFriendEmail);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ArrayAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(user.getName());
        viewHolder.email.setText(user.getEmail());

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView email;
    }
}
