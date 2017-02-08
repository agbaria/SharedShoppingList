package dev.agbaria.sharedshoppinglist.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dev.agbaria.sharedshoppinglist.Models.User;
import dev.agbaria.sharedshoppinglist.R;
import dev.agbaria.sharedshoppinglist.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView ivUserImage;
    private TextView tvUserName;
    private TextView tvUserEmail;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ivUserImage = (ImageView) view.findViewById(R.id.ivUserImage);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvUserEmail = (TextView) view.findViewById(R.id.tvUserEmail);
        initViews();

        ivUserImage.setOnClickListener(this);
        view.findViewById(R.id.btnEditName).setOnClickListener(this);
        return view;
    }

    private void initViews() {
        User currentUser = Utils.getCurrentUser();

        tvUserName.setText(currentUser.getName());
        tvUserEmail.setText(currentUser.getEmail().replace(",", "."));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEditName:
                //TODO complete from here ...
                return;
            default:
                //TODO take a picture
        }
    }
}
