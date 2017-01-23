package dev.agbaria.sharedshoppinglist.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dev.agbaria.sharedshoppinglist.Adapters.SharedListsAdapter;
import dev.agbaria.sharedshoppinglist.MyChildEventListener;
import dev.agbaria.sharedshoppinglist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SharedListsFragment extends Fragment {

    private static final String USER_ID = "userID";

    private String userID;
    private View view;
    private ArrayList<DataSnapshot> snapshots;
    private MyChildEventListener myListener;

    public static Fragment getInstance(String userID) {
        Fragment fragment = new SharedListsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_ID, userID);
        fragment.setArguments(bundle);
        return fragment;
    }

    public SharedListsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            this.userID = getArguments().getString(USER_ID).replaceAll("\\.", ",");
        }
        snapshots = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shopping_lists, container, false);
        initRecycler();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Shared lists");
        updateContent();
    }

    private void initRecycler() {
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recyclerSharedLists);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        SharedListsAdapter adapter = new SharedListsAdapter(snapshots, getActivity());
        recycler.setAdapter(adapter);
        myListener = new MyChildEventListener(snapshots, adapter);
    }

    private void updateContent() {
        snapshots.clear();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("UserLists").child(userID).addChildEventListener(myListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("UserLists").child(userID).removeEventListener(myListener);
    }
}












