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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    TextInputLayout dName, mail,pass;
    DatabaseReference firebaseDatabase;
    Button register;
    private ProgressDialog mRegDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mToolbar= (Toolbar) findViewById(R.id.reg_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegDialog = new ProgressDialog(this);
        mAuth =FirebaseAuth.getInstance();
        dName =(TextInputLayout)findViewById(R.id.textInputLayout_name);
        mail =(TextInputLayout)findViewById(R.id.TextInputLayout_mail);
        pass =(TextInputLayout)findViewById(R.id.textInputLayout_pass);
        register =(Button) findViewById(R.id.reg_button);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = dName.getEditText().getText().toString();
                String email = mail.getEditText().getText().toString();
                String password = pass.getEditText().getText().toString();
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    mRegDialog.setTitle("Registering User");
                    mRegDialog.setMessage("Please a while we create your account!");
                    mRegDialog.setCanceledOnTouchOutside(false);
                    mRegDialog.show();
                    register_user(name,email,password);
                }
            }
        });

    }

    private void register_user(final String dName, String mail, String pass) {

        mAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",dName);
                    userMap.put("status","Hi there, I'm using com.example.abo_nayel.chatapp.ChatApp");
                    userMap.put("image", "default");
                    userMap.put("thumb image","default");
                    firebaseDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mRegDialog.dismiss();
                            startActivity(new Intent(RegisterActivity.this , MainActivity.class));
                            finish();
                        }
                    });


                }else {
                    mRegDialog.hide();
                    Toast.makeText(getApplicationContext(),"you got some error",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
