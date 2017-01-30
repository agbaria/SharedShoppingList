package dev.agbaria.sharedshoppinglist.DialogFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dev.agbaria.sharedshoppinglist.Models.ListItem;
import dev.agbaria.sharedshoppinglist.R;
import dev.agbaria.sharedshoppinglist.Transactions.IncreaseTransaction;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class AddItemFragment extends DialogFragment implements View.OnClickListener {


    private static final String LIST_ID = "listID";

    private EditText etItemName;
    private DatabaseReference listItemsRef;
    private DatabaseReference listRef;

    public AddItemFragment() {
        // Required empty public constructor
    }

    public static AddItemFragment getInstance(String listID) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LIST_ID, listID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            String listID = getArguments().getString(LIST_ID);
            assert listID != null;
            listItemsRef = FirebaseDatabase.getInstance().getReference().child("ListItems").child(listID);
            listRef = FirebaseDatabase.getInstance().getReference().child("Lists").child(listID);
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
        if (view.getId() == R.id.btnClose) {
            dismiss();
        }
        else {
            String itemName = etItemName.getText().toString();
            if(itemName.length() == 0)
                return;
            ListItem item = new ListItem(itemName, false, null);
            listItemsRef.child(itemName).setValue(item);
            etItemName.setText("");
            listRef.child("items").runTransaction(new IncreaseTransaction());
        }
    }
}
