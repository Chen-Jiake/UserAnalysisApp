package com.example.useranalysisapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.useranalysisapp.R;
import com.example.useranalysisapp.activity.BindingManageActivity;


public class FragmentMine extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container,false);
        view.findViewById(R.id.binding_user_management).setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.binding_user_management:
                Intent intent = new Intent(getActivity(), BindingManageActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                break;
        }
    }
}
