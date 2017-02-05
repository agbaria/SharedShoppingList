package dev.agbaria.sharedshoppinglist.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dev.agbaria.sharedshoppinglist.Adapters.ListItemsAdapter;
import dev.agbaria.sharedshoppinglist.DialogFragment.AddItemFragment;
import dev.agbaria.sharedshoppinglist.DialogFragment.LeaveListFragment;
import dev.agbaria.sharedshoppinglist.Listeners.PositionClickedListener;
import dev.agbaria.sharedshoppinglist.Models.ListItem;
import dev.agbaria.sharedshoppinglist.Models.MySharedList;
import dev.agbaria.sharedshoppinglist.Models.ShoppingList;
import dev.agbaria.sharedshoppinglist.R;
import dev.agbaria.sharedshoppinglist.Transactions.DecreaseTransaction;
import dev.agbaria.sharedshoppinglist.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListItemsFragment extends Fragment implements PositionClickedListener {

    private static final String LIST_ID = "listID";
    private static final String LIST = "list";
    private static final String MY_LIST = "myList";
    //request codes:
    private static final int LEAVE_LIST = 1;

    private String userID;
    private String listID;
    private ShoppingList list;
    private MySharedList myList;
    private ArrayList<DataSnapshot> snapshots;
    private View view;
    private ChildEventListener myListener;
    private DatabaseReference rootRef;

    private ProgressBar progressBar;

    //select mode
    private MenuItem deleteIcon;
    private int selectMode; //0-false, 1-true, 2-deleted
    private ArrayList<Integer> selected;
    private int selectCount;

    public static Fragment getInstance(String listID, ShoppingList list, MySharedList myList) {
        Fragment fragment = new ListItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LIST_ID, listID);
        bundle.putSerializable(LIST, list);
        bundle.putSerializable(MY_LIST, myList);
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
            this.myList = (MySharedList) arguments.getSerializable(MY_LIST);
        }
        userID = Utils.getUserID();
        rootRef = FirebaseDatabase.getInstance().getReference();
        snapshots = new ArrayList<>();
        selectMode = 0;
        selected = new ArrayList<>();
        selectCount = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_list_items, container, false);
        view.findViewById(R.id.fab).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AddItemFragment fragment = AddItemFragment.getInstance(listID);
                        fragment.show(getFragmentManager(), "DialogFragment");
                    }
                }
        );
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        showProgress();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.list_menu, menu);
        deleteIcon = menu.findItem(R.id.action_delete);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (selectMode == 1) deleteIcon.setVisible(true);
        else deleteIcon.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteSelectedItems();
                return true;
            case R.id.action_favorite:
                return true;
            case R.id.action_add_friend:
                fragment = FriendsFragment.getInstance(true, listID, list);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, fragment).addToBackStack(null).commit();
                return true;
            case R.id.action_listInfo:
                fragment = ListInfoFragment.getInstance(listID, list, myList);
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

    private void deleteSelectedItems() {
        showProgress();
        new deleteItemsTask().execute();
        selectMode = 2;
        selectCount = 0;
        onPrepareOptionsMenu(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case LEAVE_LIST:
                if (resultCode == 1)
                    leaveList();
                break;
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

        new leaveListTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(list.getListName());
        initRecycler();
        updateContent();
    }

    private void initRecycler() {
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recyclerListItems);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        final ListItemsAdapter adapter = new ListItemsAdapter(snapshots, listID, getActivity(), this);
        recycler.setAdapter(adapter);
        myListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                snapshots.add(dataSnapshot);
                selected.add(0);
                adapter.notifyItemInserted(snapshots.size() - 1);
                hideProgress();
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
                selected.remove(position);
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
        rootRef.child("ListItems").child(listID).addChildEventListener(myListener);
        rootRef.child("ListItems").child(listID).addListenerForSingleValueEvent(
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
        rootRef.child("ListItems").child(listID).removeEventListener(myListener);
    }

    @Override
    public boolean clicked(int position, View v) {
        switch (selectMode) {
            case 2:
                selectMode = 0;
                return false;
            case 1:
                if (selected.get(position).equals(1)) {
                    toggleSelect(v, position, false);
                    if (selectCount == 0) {
                        selectMode = 0;
                        onPrepareOptionsMenu(null);
                        return false;
                    }
                }
                else toggleSelect(v, position, true);
                return true;
            default:
                selectMode = 1;
                toggleSelect(v, position, true);
                onPrepareOptionsMenu(null);
                return true;
        }
    }

    private void toggleSelect(View v, int position, boolean select) {
        if (select) {
            v.setBackgroundColor(Color.rgb(200, 200, 200));
            selected.set(position, 1);
            selectCount++;
        }
        else {
            v.setBackgroundColor(Color.TRANSPARENT);
            selected.set(position, 0);
            selectCount--;
        }
    }

    private class leaveListTask extends AsyncTask<Void, Void, Void> {

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
                                            if (!dataSnapshot.exists()) {
                                                rootRef.child("ListItems").child(listID).removeValue();
                                                rootRef.child("Lists").child(listID).removeValue();
                                            }
                                            else {
                                                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                                Iterator<DataSnapshot> iterator = children.iterator();
                                                String key = iterator.next().getKey();
                                                rootRef.child("Lists").child(listID).child("listOwner")
                                                        .setValue(key);
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

    private class deleteItemsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Map<String, Object> childUpdates = new HashMap<>();
            String prefix = "/ListItems/" + listID + "/";
            int checked = 0;
            int items = 0;
            for (int i = 0; i < selected.size(); i++) {
                if (selected.get(i) == 1) {
                    DataSnapshot snapshot = snapshots.get(i);
                    ListItem item = snapshot.getValue(ListItem.class);

                    childUpdates.put(prefix + item.getItemName(), null);

                    if (item.isChecked()) checked++;
                    items++;
                }
            }
            final int finalChecked = checked;
            final int finalItems = items;
            reference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (finalChecked > 0)
                        reference.child("Lists").child(listID).child("checkedItems")
                                .runTransaction(new DecreaseTransaction(finalChecked));

                    reference.child("Lists").child(listID).child("items")
                            .runTransaction(new DecreaseTransaction(finalItems));
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideProgress();
        }
    }

    private int getListPosition(String key) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshots.get(i).getKey().equals(key))
                return i;
        }
        throw new IllegalArgumentException("List key was not found");
    }

    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
