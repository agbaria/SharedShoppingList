package dev.agbaria.sharedshoppinglist.Listeners;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

/**
 * Created by 3la2 on 22/01/2017.
 */

public class MyChildEventListener implements ChildEventListener {

    private ArrayList<DataSnapshot> snapshots;
    private RecyclerView.Adapter adapter;
    private View progressBar;

    public MyChildEventListener(ArrayList<DataSnapshot> snapshots, RecyclerView.Adapter adapter, View progressBar) {
        this.snapshots = snapshots;
        this.adapter = adapter;
        this.progressBar = progressBar;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        hideProgress();
        snapshots.add(dataSnapshot);
        adapter.notifyItemInserted(snapshots.size() - 1);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        int position = getListPosition(dataSnapshot.getKey());
        snapshots.set(position, dataSnapshot);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        int position = getListPosition(dataSnapshot.getKey());
        snapshots.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private int getListPosition(String key) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshots.get(i).getKey().equals(key))
                return i;
        }
        throw new IllegalArgumentException("List key was not found");
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
