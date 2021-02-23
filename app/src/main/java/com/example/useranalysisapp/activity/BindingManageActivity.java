package com.example.useranalysisapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.useranalysisapp.R;

public class BindingManageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_manage);
        TextView title = findViewById(R.id.tv_title);
        title.setText("关联账号管理");
    }

    private void refresh() {

    }
}
