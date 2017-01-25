package dev.agbaria.sharedshoppinglist.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import dev.agbaria.sharedshoppinglist.R;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class AddListFragment extends DialogFragment implements View.OnClickListener {

    private EditText etListName;

    public AddListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_list, container, false);
        view.findViewById(R.id.btnCancel).setOnClickListener(this);
        view.findViewById(R.id.btnCreateListName).setOnClickListener(this);
        etListName = (EditText) view.findViewById(R.id.etAddListName);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnCreateListName) {
            if(!validate()) {
                showError();
                return;
            }
            else getTargetFragment().onActivityResult(getTargetRequestCode(), 1, new Intent().putExtra("listName", getListName()));
        }
        else getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
        dismiss();
    }

    private void showError() {
        etListName.setError("List name too short");
    }

    private boolean validate() {
        if (getListName().length() < 3)
            return false;
        return true;
    }

    private String getListName() {
        return etListName.getText().toString();
    }
}
