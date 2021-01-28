package com.example.useranalysisapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.useranalysisapp.MainActivity;
import com.example.useranalysisapp.R;
import com.example.useranalysisapp.model.User;
import com.example.useranalysisapp.utils.ResultListener;
import com.example.useranalysisapp.utils.SendUtils;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edUsername;
    private EditText edPassword;
    private TextView tvLose;
    private TextView tvNew;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edUsername = findViewById(R.id.ed_username);
        edPassword = findViewById(R.id.ed_password);
        findViewById(R.id.tv_lose).setOnClickListener(this);
        findViewById(R.id.tv_new).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_new:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                String username = edUsername.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                if (username.length()<=0 ||password.length()<=0) {
                    Toast.makeText(LoginActivity.this, "请正确输入", Toast.LENGTH_SHORT).show();
                } else {
                    SendUtils.login(username, password, new ResultListener<Void>() {
                        @Override
                        public void onSuccess(String message, Void data) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(String message, Void data) {
                            Looper.prepare();
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    });
                }
                break;
        }

    }
}
