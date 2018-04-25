package com.example.abo_nayel.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    TextView nametxt, statustxt, freindstxt;
    ImageView prof_image;
    Button req_btn, decline_btn;
    DatabaseReference databaseReference;
    DatabaseReference friendReqDatabase;
    DatabaseReference friendsDatabase;
    ProgressDialog progressDialog;
    int currentStatus;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nametxt = (TextView) findViewById(R.id.profile_name);
        statustxt = (TextView) findViewById(R.id.profile_status);
        freindstxt = (TextView) findViewById(R.id.profile_freinds);
        prof_image = (ImageView) findViewById(R.id.profile_image);
        req_btn = (Button) findViewById(R.id.prof_send_request2);
        decline_btn = (Button) findViewById(R.id.prof_dec_request);
        decline_btn.setVisibility(View.INVISIBLE);
        decline_btn.setEnabled(false);
        final String profID = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(profID);
        friendReqDatabase = FirebaseDatabase.getInstance().getReference().child("friend_req");
        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentStatus = 0; //currentStatus=0: not friends|| currentStatus=1: sent
        // currentStatus=2:received || currentStatus=3:friends


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading user data...");
        progressDialog.setMessage("Please wait, profile is loading its data!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                nametxt.setText(name);
                statustxt.setText(status);
                Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.ic_person_black_24dp).into(prof_image);
                progressDialog.dismiss();
                //_______________ Freinds list features _____________
                friendReqDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(profID)) {
                            if (dataSnapshot.child(profID).child("req_type").getValue().toString().equals("1")) {
                                currentStatus = 1;
                                req_btn.setText("Cancel Friend Request");
                                decline_btn.setVisibility(View.INVISIBLE);
                                decline_btn.setEnabled(false);
                            } else if (dataSnapshot.child(profID).child("req_type").getValue().toString().equals("2")) {
                                currentStatus = 2;
                                req_btn.setText("Accept Friend Request");
                                progressDialog.dismiss();
                                decline_btn.setVisibility(View.VISIBLE);
                                decline_btn.setEnabled(true);

                            }
                        }  else {
                            friendsDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(profID)) {
                                        currentStatus = 3;
                                        req_btn.setText("Unfriend this person");
                                        decline_btn.setVisibility(View.INVISIBLE);
                                        decline_btn.setEnabled(false);

                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        decline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendReqDatabase.child(currentUser.getUid()).child(profID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendReqDatabase.child(profID).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    currentStatus = 0;
                                    req_btn.setText("Send Friend Request");
                                    decline_btn.setEnabled(false);
                                    decline_btn.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Friend request declined", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "error declining friend request ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        req_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStatus == 0) {
                    friendReqDatabase.child(currentUser.getUid()).child(profID).child("req_type").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendReqDatabase.child(profID).child(currentUser.getUid()).child("req_type").setValue(2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        currentStatus = 1;
                                        req_btn.setText("Cancel Friend Request");
                                        Toast.makeText(getApplicationContext(), "Friend request sent", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "failed to send request, try again later", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                if (currentStatus == 1) {
                    friendReqDatabase.child(currentUser.getUid()).child(profID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendReqDatabase.child(profID).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        currentStatus = 0;
                                        req_btn.setText("Send Friend Request");
                                        Toast.makeText(getApplicationContext(), "Friend request canceled", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "error canceling friend request ", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                if (currentStatus == 2) {
                    final String current_date = DateFormat.getDateTimeInstance().format(new Date()).toString();
                    friendsDatabase.child(currentUser.getUid()).child(profID).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendsDatabase.child(profID).child(currentUser.getUid()).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendReqDatabase.child(currentUser.getUid()).child(profID).removeValue().
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        friendReqDatabase.child(profID).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                currentStatus = 4;
                                                                req_btn.setText("Unfriend this person");
                                                                decline_btn.setVisibility(View.INVISIBLE);
                                                                decline_btn.setEnabled(false);
                                                                Toast.makeText(getApplicationContext(), "you are now friends", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "error canceling friend request ", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    });
                }
                if (currentStatus == 3) {
                    friendsDatabase.child(currentUser.getUid()).child(profID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendsDatabase.child(profID).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        currentStatus = 0;
                                        req_btn.setText("Send Friend Request");
                                        Toast.makeText(getApplicationContext(), "you are not friends any more.. ", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "error complete unfriend request ", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });
    }
}
