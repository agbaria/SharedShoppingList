package dev.agbaria.sharedshoppinglist.Fragments;


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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Adapters.ListInfoAdapter;
import dev.agbaria.sharedshoppinglist.Models.MySharedList;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.Listeners.MyChildEventListener;
import dev.agbaria.sharedshoppinglist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListInfoFragment extends Fragment {

    private static final String LIST_ID = "listID";
    private static final String LIST = "list";
    private static final String MY_LIST = "myList";

    private String listID;
    private ShoppingList list;
    private MySharedList myList;
    private ArrayList<DataSnapshot> snapshots;
    private View view;
    private MyChildEventListener myListener;

    public static Fragment getInstance(String listID, ShoppingList list, MySharedList myList) {
        Fragment fragment = new ListInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LIST_ID, listID);
        bundle.putSerializable(LIST, list);
        bundle.putSerializable(MY_LIST, myList);
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
            this.myList = (MySharedList) getArguments().getSerializable(MY_LIST);
        }
        snapshots = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_list_info, container, false);
        initRecycler();
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
        updateContent();
    }

    private void initRecycler() {
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recyclerParticipants);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ListInfoAdapter adapter = new ListInfoAdapter(snapshots, list, myList, getActivity());
        recycler.setAdapter(adapter);
        myListener = new MyChildEventListener(snapshots, adapter);
    }

    private void updateContent() {
        snapshots.clear();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("SharedWith").child(listID).addChildEventListener(myListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("SharedWith").child(listID).removeEventListener(myListener);
    }
}
