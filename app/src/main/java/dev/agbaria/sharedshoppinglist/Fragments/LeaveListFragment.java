package dev.agbaria.sharedshoppinglist.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import dev.agbaria.sharedshoppinglist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaveListFragment extends DialogFragment implements View.OnClickListener {

    private static final int LEAVE_LIST = 1;
    private static final int CANCEL = 0;
    private static final String LIST_NAME = "listName";

    private String listName;

    public LeaveListFragment() {
        // Required empty public constructor
    }

    public static LeaveListFragment getInstance(String listName) {
        LeaveListFragment fragment = new LeaveListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LIST_NAME, listName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            listName = getArguments().getString(LIST_NAME);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leave_list, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ((TextView) rootView.findViewById(R.id.tvLeaveListName)).setText(listName);

        rootView.findViewById(R.id.btnCancel).setOnClickListener(this);
        rootView.findViewById(R.id.btnLeave).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int res;
        if (view.getId() == R.id.btnLeave)
            res = LEAVE_LIST;
        else
            res = CANCEL;
        getTargetFragment().onActivityResult(getTargetRequestCode(), res, null);
        dismiss();
    }
}
