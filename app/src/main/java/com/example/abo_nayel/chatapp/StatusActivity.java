package com.example.abo_nayel.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    TextInputLayout statusTXT;
    Button saveBTN;
    DatabaseReference database;
    Toolbar toolbar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        toolbar =(Toolbar)findViewById(R.id.status_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        saveBTN = (Button)findViewById(R.id.status_save_btn);
        statusTXT = (TextInputLayout)findViewById(R.id.status_txt);

        String prev_status = getIntent().getStringExtra("status");
        statusTXT.getEditText().setText(prev_status);

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle("Saving Changes");
                progressDialog.setMessage("your app is updating your status..");
                progressDialog.show();

                String status = statusTXT.getEditText().getText().toString();

                database.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                        }
                        else {
                            progressDialog.hide();
                            Toast.makeText(getApplicationContext(),"can't Update your status, please try again later.",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
