package dev.agbaria.sharedshoppinglist.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dev.agbaria.sharedshoppinglist.Models.ListItem;
import dev.agbaria.sharedshoppinglist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends DialogFragment implements View.OnClickListener {


    private static final String LIST_NAME = "listName";

    private EditText etItemName;
    private DatabaseReference ref;

    public AddItemFragment() {
        // Required empty public constructor
    }

    public static AddItemFragment getInstance(String listName) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LIST_NAME, listName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            String listName = getArguments().getString(LIST_NAME);
            assert listName != null;
            ref = FirebaseDatabase.getInstance().getReference().child("ListItems").child(listName);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        etItemName = (EditText) rootView.findViewById(R.id.etItemName);

        rootView.findViewById(R.id.btnClose).setOnClickListener(this);
        rootView.findViewById(R.id.btnAdd).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnClose)
            dismiss();
        else {
            String itemName = etItemName.getText().toString();
            ListItem item = new ListItem(itemName, false);
            ref.child(itemName).setValue(item);
            etItemName.setText("");
        }
    }
}
