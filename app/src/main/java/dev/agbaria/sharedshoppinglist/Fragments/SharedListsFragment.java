package dev.agbaria.sharedshoppinglist.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dev.agbaria.sharedshoppinglist.Adapters.SharedListsAdapter;
import dev.agbaria.sharedshoppinglist.Listeners.MyChildEventListener;
import dev.agbaria.sharedshoppinglist.Models.MySharedList;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.Models.User;
import dev.agbaria.sharedshoppinglist.R;
import dev.agbaria.sharedshoppinglist.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SharedListsFragment extends Fragment {

    //request code
    private static final int LIST_NAME = 1;
    //received result codes:
    private static final int SAVED_LIST = 2;

    private String userID;
    private View view;
    private ArrayList<DataSnapshot> snapshots;
    private MyChildEventListener myListener;
    private ProgressBar progressBar;

    public SharedListsFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userID = Utils.getUserID();
        snapshots = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shopping_lists, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        showProgress();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_addList:
                SavedListsFragment fragment = SavedListsFragment.getInstance(true);
                fragment.setTargetFragment(this, LIST_NAME);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment)
                        .addToBackStack(null).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Shared lists");
        initRecycler();
        updateContent();
    }

    private void initRecycler() {
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recyclerSharedLists);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        SharedListsAdapter adapter = new SharedListsAdapter(snapshots, getActivity());
        recycler.setAdapter(adapter);
        myListener = new MyChildEventListener(snapshots, adapter, progressBar);
    }

    private void updateContent() {
        snapshots.clear();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("UserLists").child(userID).addChildEventListener(myListener);
        rootRef.child("UserLists").child(userID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0)
                            hideProgress();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("UserLists").child(userID).removeEventListener(myListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LIST_NAME:
                String listName = data.getStringExtra("listName");
                String newListKey = createList(listName);
                if (resultCode == SAVED_LIST) {
                    String savedListKey = data.getStringExtra("listKey");
                    addSavedItems(savedListKey, newListKey);
                }
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    private void addSavedItems(String savedListKey, final String newListKey) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("ListItems").child(savedListKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/ListItems/" + newListKey, dataSnapshot.getValue());
                        mDatabase.updateChildren(childUpdates);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public String createList(String listName) {
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
        return listKey;
    }

    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}












