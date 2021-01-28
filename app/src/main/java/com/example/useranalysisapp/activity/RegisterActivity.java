package com.example.useranalysisapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.useranalysisapp.R;
import com.example.useranalysisapp.utils.ResultListener;
import com.example.useranalysisapp.utils.SendUtils;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edUsername;
    private EditText edPassword;
    private EditText edConfirmPassword;
    private Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edUsername = findViewById(R.id.ed_username);
        edPassword = findViewById(R.id.ed_password);
        edConfirmPassword = findViewById(R.id.ed_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();
                String confirmPassword = edConfirmPassword.getText().toString();
                //判断一下字符字符串是否符合
                if (username.length()<=0 ||password.length()<=0)
                    Toast.makeText(RegisterActivity.this,"请正确输入",Toast.LENGTH_SHORT).show();
                else if (!confirmPassword.equals(password)){
                    Toast.makeText(RegisterActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                } else {
                    SendUtils.register(username, password, new ResultListener<Void>() {
                        @Override
                        public void onSuccess(String message, Void data) {
                            Looper.prepare();
                            Toast.makeText(RegisterActivity.this, message,Toast.LENGTH_SHORT).show();
                            finish();
                            Looper.loop();
                        }

                        @Override
                        public void onFailure(String message, Void data) {
                            Looper.prepare();
                            Toast.makeText(RegisterActivity.this, message,Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    });

                }
                break;
        }
    }
}
