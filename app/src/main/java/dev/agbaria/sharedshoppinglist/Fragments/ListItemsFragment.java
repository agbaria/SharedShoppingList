package dev.agbaria.sharedshoppinglist.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Adapters.ListItemsAdapter;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.R;
import dev.agbaria.sharedshoppinglist.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListItemsFragment extends Fragment {

    private static final String LIST_ID = "listID";
    private static final String LIST = "list";

    private String userID;
    private String listID;
    private ShoppingList list;
    private ArrayList<DataSnapshot> snapshots;
    private View view;
    private ListItemsAdapter adapter;
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
        View view = inflater.inflate(R.layout.fragment_list_items, container, false);
        this.view = view;
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
                leaveList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void leaveList() {
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
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }
                        );

                        rootRef.child("UserLists").child(userID).child(listID).removeValue(
                                new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }
                                }
                        );
                    }
                }
        );
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
