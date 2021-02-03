package com.example.useranalysisapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.useranalysisapp.MainActivity;
import com.example.useranalysisapp.R;
import com.example.useranalysisapp.model.LoginUser;
import com.example.useranalysisapp.utils.ResultListener;
import com.example.useranalysisapp.utils.SendUtils;

public class BindingActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edUsername;
    private EditText edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
        edUsername = findViewById(R.id.ed_username);
        edPassword = findViewById(R.id.ed_password);
        findViewById(R.id.btn_binding).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_binding:
                String username = edUsername.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                if (username.length() <= 0 || password.length() <= 0) {
                    Toast.makeText(BindingActivity.this, "请正确输入", Toast.LENGTH_SHORT).show();
                } else {
                    if(username.equals(LoginUser.getLoginUser().getUser().getUsername())) {
                        Toast.makeText(BindingActivity.this, "不需要绑定自己！", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    SendUtils.bind(username, password, new ResultListener<Void>() {
                        @Override
                        public void onSuccess(String message, Void data) {
                            Looper.prepare();
                            Toast.makeText(BindingActivity.this, message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(BindingActivity.this, MainActivity.class));
                            finish();
                            Looper.loop();
                        }

                        @Override
                        public void onFailure(String message, Void data) {
                            Looper.prepare();
                            Toast.makeText(BindingActivity.this, message, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    });
                }
                break;
        }
    }
}
