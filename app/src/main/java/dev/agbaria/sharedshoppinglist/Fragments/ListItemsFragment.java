package dev.agbaria.sharedshoppinglist.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Adapters.ListItemsAdapter;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListItemsFragment extends Fragment {

    private static final String LIST_ID = "listID";
    private static final String LIST = "list";

    private String listID;
    private ShoppingList list;
    private ArrayList<DataSnapshot> snapshots;
    private View view;
    private ListItemsAdapter adapter;

    public static Fragment getInstance(String listID, ShoppingList list) {
        Fragment fragment = new ListItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LIST_ID, listID);
        bundle.putSerializable(LIST, list);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ListItemsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments() != null) {
            this.listID = getArguments().getString(LIST_ID);
            this.list = (ShoppingList) getArguments().getSerializable(LIST);
        }
        snapshots = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_items, container, false);
        this.view = view;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(list.getListName());
        init();
    }

    private void init() {
        initRecycler();
        updateContent();
    }

    private void initRecycler() {
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recyclerListItems);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = new ListItemsAdapter(snapshots, getActivity());
        recycler.setAdapter(adapter);
    }

    private void updateContent() {
        snapshots.clear();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("ListItems").child(listID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                snapshots.add(dataSnapshot);
                adapter.notifyItemInserted(snapshots.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int position = getItemPosition(dataSnapshot.getKey());
                snapshots.set(position, dataSnapshot);
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int position = getItemPosition(dataSnapshot.getKey());
                snapshots.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getItemPosition(String key) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshots.get(i).getKey().equals(key))
                return i;
        }
        throw new IllegalArgumentException("List key was not found");
    }
}
