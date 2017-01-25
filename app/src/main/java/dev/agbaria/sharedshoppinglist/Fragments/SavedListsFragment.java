package dev.agbaria.sharedshoppinglist.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dev.agbaria.sharedshoppinglist.Adapters.SavedListsAdapter;
import dev.agbaria.sharedshoppinglist.Listeners.ListNameListener;
import dev.agbaria.sharedshoppinglist.Listeners.PositionClickedListener;
import dev.agbaria.sharedshoppinglist.Models.MySharedList;
import dev.agbaria.sharedshoppinglist.Models.SavedList;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.Models.User;
import dev.agbaria.sharedshoppinglist.R;
import dev.agbaria.sharedshoppinglist.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedListsFragment extends Fragment implements PositionClickedListener, View.OnClickListener, ListNameListener {

    private static final String TO_ADD = "toAdd";
    private static final int LIST_NAME = 1;
    private boolean toAdd;
    private String userID;
    private ArrayList<DataSnapshot> snapshots;
    private View view;
    private SavedListsAdapter adapter;
    private ValueEventListener savedListsListener;

    public SavedListsFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    public static SavedListsFragment getInstance(boolean toAdd) {
        SavedListsFragment fragment = new SavedListsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(TO_ADD, toAdd);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null)
            toAdd = getArguments().getBoolean(TO_ADD);
        snapshots = new ArrayList<>();
        userID = Utils.getUserID();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_saved_lists, container, false);
        initRecycler();
        return view;
    }

    private void initRecycler() {
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recyclerSavedLists);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (toAdd) adapter = new SavedListsAdapter(snapshots, getActivity(), toAdd, this);
        else adapter = new SavedListsAdapter(snapshots, getActivity(), toAdd, null);
        recycler.setAdapter(adapter);
        savedListsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot aChildren : children) snapshots.add(aChildren);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (toAdd) getActivity().setTitle("Create new list");
        else getActivity().setTitle("Saved lists");
        updateContent();
    }

    private void updateContent() {
        snapshots.clear();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("SavedLists").child(userID).addListenerForSingleValueEvent(savedListsListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("SavedLists").child(userID).removeEventListener(savedListsListener);
    }

    @Override
    public void clicked(int position, View v) {
        v.setBackgroundColor(Color.LTGRAY);
        SavedList list = snapshots.get(position).getValue(SavedList.class);

    }

    @Override
    public void onClick(View view) {
        AddListFragment fragment = new AddListFragment();
        fragment.setTargetFragment(this, LIST_NAME);
        fragment.show(getActivity().getSupportFragmentManager(), "");
        //TODO complete from here ...
    }

    @Override
    public void createList(String listName) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String listKey = mDatabase.child("Lists").push().getKey();

        ShoppingList shoppingList = new ShoppingList(listName, userID, new Date().getTime(), 0, 0);
        MySharedList mySharedList = new MySharedList(listName, new Date().getTime());
        User user = new User(Utils.getUserName(), userID);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Lists/" + listKey, shoppingList);
        childUpdates.put("/UserLists/" + userID + "/" + listKey, mySharedList);
        childUpdates.put("/SharedWith/" + listKey + "/" + userID, user);

        mDatabase.updateChildren(childUpdates);

        getActivity().getSupportFragmentManager().popBackStack();
    }
}
