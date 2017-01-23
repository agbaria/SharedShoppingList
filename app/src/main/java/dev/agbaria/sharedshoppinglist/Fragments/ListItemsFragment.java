package dev.agbaria.sharedshoppinglist.Fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Adapters.ListItemsAdapter;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.MyChildEventListener;
import dev.agbaria.sharedshoppinglist.R;
import dev.agbaria.sharedshoppinglist.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListItemsFragment extends Fragment {

    private static final String LIST_ID = "listID";
    private static final String LIST = "list";
    private static final int LEAVE_LIST = 10;

    private String userID;
    private String listID;
    private ShoppingList list;
    private ArrayList<DataSnapshot> snapshots;
    private View view;
    private ListItemsAdapter adapter;
    private MyChildEventListener myListner;
    private DatabaseReference rootRef;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments() != null) {
            Bundle arguments = getArguments();
            this.listID = arguments.getString(LIST_ID);
            this.list = (ShoppingList) arguments.getSerializable(LIST);
        }
        this.userID = Utils.getUserID();
        rootRef = FirebaseDatabase.getInstance().getReference();
        snapshots = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_list_items, container, false);
        initRecycler();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.action_favorite:
                return true;
            case R.id.action_add_friend:
                fragment = FriendsFragment.getInstance(userID, true, listID, list);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, fragment).addToBackStack(null).commit();
                return true;
            case R.id.action_listInfo:
                fragment = ListInfoFragment.getInstance(listID, list);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, fragment).addToBackStack(null).commit();
                return true;
            case R.id.action_leaveList:
                fragment = LeaveListFragment.getInstance(list.getListName());
                fragment.setTargetFragment(this, LEAVE_LIST);
                ((DialogFragment) fragment).show(getFragmentManager(), "DialogFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LEAVE_LIST) {
            if (resultCode == 1)
                leaveList();
        }
    }

    private void leaveList() {
        rootRef.child("UserLists").child(userID).child(listID).removeValue(
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
        );

        Task completeLeavingInBG = new Task();
        completeLeavingInBG.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(list.getListName());
        updateContent();
    }

    private void initRecycler() {
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recyclerListItems);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = new ListItemsAdapter(snapshots, getActivity());
        recycler.setAdapter(adapter);
        myListner = new MyChildEventListener(snapshots, adapter);
    }

    private void updateContent() {
        snapshots.clear();
        rootRef.child("ListItems").child(listID).addChildEventListener(myListner);
    }

    @Override
    public void onPause() {
        super.onPause();
        rootRef.child("ListItems").child(listID).removeEventListener(myListner);
    }

    private class Task extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            rootRef.child("SharedWith").child(listID).child(userID).removeValue(
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            rootRef.child("SharedWith").child(listID).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.exists())
                                                rootRef.child("ListItems").child(listID).removeValue();
                                            else {
                                                //TODO change list owner
                                                //probably need to change the database structure
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    }
                            );
                        }
                    }
            );
            return null;
        }
    }
}
