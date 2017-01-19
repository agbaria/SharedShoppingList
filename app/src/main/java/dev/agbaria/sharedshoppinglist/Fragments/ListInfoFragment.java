package dev.agbaria.sharedshoppinglist.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

import dev.agbaria.sharedshoppinglist.Adapters.FriendsAdapter;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListInfoFragment extends Fragment {

    private static final String LIST_ID = "listID";
    private static final String LIST = "list";

    private String listID;
    private ShoppingList list;
    private ArrayList<DataSnapshot> snapshots;
    private View view;
    private FriendsAdapter adapter;
    //private ArrayAdapter adapter;

    private TextView listOwner;
    private TextView creationDate;

    public static Fragment getInstance(String listID, ShoppingList list) {
        Fragment fragment = new ListInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LIST_ID, listID);
        bundle.putSerializable(LIST, list);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ListInfoFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_list_info, container, false);
        this.view = view;
        findViews();
        setViewsValues();
        return view;
    }

    private void findViews() {
        listOwner = (TextView) view.findViewById(R.id.tvListOwner);
        creationDate = (TextView) view.findViewById(R.id.tvCreationDate);
    }

    private void setViewsValues() {
        listOwner.setText(list.getListOwner());
        Date date = new Date(list.getCreationTimeStamp() * 1000L);
        creationDate.setText(DateFormat.format("dd-MM-yyyy", date));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        initRecycler();
        updateContent();
    }

    private void initRecycler() {
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recyclerParticipants);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendsAdapter(snapshots, getActivity());
        recycler.setAdapter(adapter);
        /*
        ListView listView = (ListView) view.findViewById(R.id.listParticipants);
        adapter = new ArrayAdapter(getContext(), R.layout.friends_item, snapshots);
        listView.setAdapter(adapter);
        */
    }

    private void updateContent() {
        snapshots.clear();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("SharedWith").child(listID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                snapshots.add(dataSnapshot);
                adapter.notifyItemInserted(snapshots.size() - 1);
//                adapter.add(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int position = getListPosition(dataSnapshot.getKey());
                snapshots.set(position, dataSnapshot);
                adapter.notifyItemChanged(position);
//                adapter.remove(snapshots.get(position));
//                adapter.insert(dataSnapshot, position);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int position = getListPosition(dataSnapshot.getKey());
                snapshots.remove(position);
                adapter.notifyItemRemoved(position);
//                adapter.remove(snapshots.get(position));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getListPosition(String key) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshots.get(i).getKey().equals(key))
                return i;
        }
        throw new IllegalArgumentException("List key was not found");
    }
}
