package dev.agbaria.sharedshoppinglist.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dev.agbaria.sharedshoppinglist.Adapters.FriendsAdapter;
import dev.agbaria.sharedshoppinglist.Listeners.PositionClickedListener;
import dev.agbaria.sharedshoppinglist.Models.MySharedList;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.Models.User;
import dev.agbaria.sharedshoppinglist.R;
import dev.agbaria.sharedshoppinglist.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment
        implements View.OnClickListener, PositionClickedListener {

    private static final String LIST_ID = "listID";
    private static final String TO_ADD = "toAdd";
    private static final String LIST = "list";

    private String userID;
    private boolean toAdd;
    private String listID;
    private ShoppingList list;
    private ArrayList<Integer> friendsToAdd;
    private View view;
    private ArrayList<DataSnapshot> snapshots;
    private FriendsAdapter adapter;

    private EditText etAddFriend;
    private ImageButton ibAddFriend;
    private ChildEventListener friendsListener;

    public static Fragment getInstance(boolean toAdd,
                                       @Nullable String listID, @Nullable ShoppingList list) {
        Fragment fragment = new FriendsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(TO_ADD, toAdd);
        if (toAdd) {
            bundle.putString(LIST_ID, listID);
            bundle.putSerializable(LIST, list);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    public FriendsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if(arguments != null) {
            this.toAdd = arguments.getBoolean(TO_ADD);
            if (toAdd) {
                this.listID = arguments.getString(LIST_ID);
                this.list = (ShoppingList) arguments.getSerializable(LIST);
                this.friendsToAdd = new ArrayList<>();
            }
        }
        userID = Utils.getUserID();
        snapshots = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        etAddFriend = (EditText) view.findViewById(R.id.etAddFriend);
        ibAddFriend = (ImageButton) view.findViewById(R.id.ibAddFriend);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (toAdd)
            inflater.inflate(R.menu.friends_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_friends) {
            addSelectedFriends();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        ibAddFriend.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ibAddFriend.setOnClickListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (toAdd) getActivity().setTitle("Select friends");
        else getActivity().setTitle("Friends");
        initRecycler();
        updateContent();
    }

    private void initRecycler() {
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recyclerFriends);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        if (toAdd)
            adapter = new FriendsAdapter(snapshots, getActivity(), toAdd, this);
        else
            adapter = new FriendsAdapter(snapshots, getActivity(), toAdd, null);
        recycler.setAdapter(adapter);
        friendsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                snapshots.add(dataSnapshot);
                if (toAdd)
                    friendsToAdd.add(0);
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
        };
    }

    private void updateContent() {
        snapshots.clear();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("UserFriends").child(userID).addChildEventListener(friendsListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("UserFriends").child(userID).removeEventListener(friendsListener);
    }

    private int getListPosition(String key) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshots.get(i).getKey().equals(key))
                return i;
        }
        throw new IllegalArgumentException("List key was not found");
    }

    @Override
    public void onClick(View v) {
        String email = etAddFriend.getText().toString();
        if(!Utils.validate(email, etAddFriend))
            return;

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final String encodedEmail = email.replaceAll("\\.", ",");
        rootRef.child("Users").child(encodedEmail).addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists())
                        Toast.makeText(getContext(), "Email doesn't exist", Toast.LENGTH_LONG).show();
                    else {
                        User friend = dataSnapshot.getValue(User.class);
                        rootRef.child("UserFriends").child(userID).child(encodedEmail).setValue(friend);
                        etAddFriend.setText("");
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //no implementation
                }
            });
    }

    @Override
    public boolean clicked(int position, View v) {
        if (friendsToAdd.get(position).equals(0)) {
            friendsToAdd.set(position, 1);
            v.setBackgroundColor(Color.LTGRAY);

        }
        else {
            friendsToAdd.set(position, 0);
            v.setBackgroundColor(Color.TRANSPARENT);
        }
        return true;
    }

    private void addSelectedFriends() {
        new addSelectedFriendsTask().execute();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private class addSelectedFriendsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            Map<String, Object> childUpdates = new HashMap<>();
            for (int i = 0; i < friendsToAdd.size(); i++) {
                if (friendsToAdd.get(i).equals(1)) {
                    DataSnapshot snapshot = snapshots.get(i);
                    User friend = snapshot.getValue(User.class);
                    String friendID = friend.getEmail();
                    MySharedList mySharedList = new MySharedList(list.getListName(), new Date().getTime());

                    childUpdates.put("/SharedWith/" + listID + "/" + friendID, friend);
                    childUpdates.put("/UserLists/" + friendID + "/" + listID, mySharedList);
                }
            }
            rootRef.updateChildren(childUpdates);
            return null;
        }
    }
}
