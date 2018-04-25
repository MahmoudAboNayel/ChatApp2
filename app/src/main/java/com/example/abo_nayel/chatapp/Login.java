package com.example.abo_nayel.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private Toolbar mToolbar;
    TextInputLayout password , mail;
    Button login;
    ProgressDialog mLoginDialog;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar= (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        password = (TextInputLayout)findViewById(R.id.login_password);
        mail = (TextInputLayout)findViewById(R.id.login_mail);
        login = (Button)findViewById(R.id.login_button);
        mLoginDialog = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getEditText().getText().toString();
                String pass = password.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                    mLoginDialog.setTitle("Logging to your Account");
                    mLoginDialog.setMessage("Please wait, your account is logging in!");
                    mLoginDialog.setCanceledOnTouchOutside(false);
                    mLoginDialog.show();
                    login_user(email, pass);
                }

            }
        });
    }

    private void login_user(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mLoginDialog.dismiss();
                    startActivity(new Intent(Login.this ,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();

                }else {
                    mLoginDialog.hide();
                    Toast.makeText(getApplicationContext(),"some errors occur during logging in!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
