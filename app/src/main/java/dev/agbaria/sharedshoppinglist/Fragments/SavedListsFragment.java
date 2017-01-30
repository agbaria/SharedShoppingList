package dev.agbaria.sharedshoppinglist.Fragments;


import android.content.Intent;
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

import dev.agbaria.sharedshoppinglist.Adapters.SavedListsAdapter;
import dev.agbaria.sharedshoppinglist.DialogFragment.EnterListNameFragment;
import dev.agbaria.sharedshoppinglist.Listeners.PositionClickedListener;
import dev.agbaria.sharedshoppinglist.Models.SavedList;
import dev.agbaria.sharedshoppinglist.R;
import dev.agbaria.sharedshoppinglist.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedListsFragment extends Fragment implements PositionClickedListener, View.OnClickListener {

    private static final String TO_ADD = "toAdd";
    //request codes:
    private static final int LIST_NAME = 1;
    //received result codes:
    private static final int OK = 1;
    //to send result codes:
    private static final int NEW_LIST = 1;
    private static final int SAVED_LIST = 2;

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
        return view;
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
        initRecycler();
        updateContent();
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
    public boolean clicked(int position, View v) {
        v.setBackgroundColor(Color.LTGRAY);
        String listKey = snapshots.get(position).getKey();
        SavedList list = snapshots.get(position).getValue(SavedList.class);
        Intent data = new Intent();
        data.putExtra("listName", list.getListName());
        data.putExtra("listKey", listKey);
        sendResult(SAVED_LIST, data);
        return true;
    }

    @Override
    public void onClick(View view) {
        EnterListNameFragment fragment = new EnterListNameFragment();
        fragment.setTargetFragment(this, LIST_NAME);
        fragment.show(getActivity().getSupportFragmentManager(), "EnterListNameFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LIST_NAME:
                if (resultCode == OK)
                    sendResult(NEW_LIST, data);
                break;
        }
    }

    public void sendResult(int resultCode, Intent data) {
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
