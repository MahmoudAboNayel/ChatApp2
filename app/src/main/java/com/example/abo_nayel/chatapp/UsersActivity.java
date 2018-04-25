package com.example.abo_nayel.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    Query query;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        toolbar = (Toolbar)findViewById(R.id.users_appbar);
        recyclerView = (RecyclerView)findViewById(R.id.users_list);
        database = FirebaseDatabase.getInstance().getReference().child("users");
        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("users").limitToLast(20);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                User.class,
                R.layout.single_user_layout,
                UsersViewHolder.class,
                database) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, User model, int position) {
                viewHolder.setDisplayName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setUserImage(model.getThumb_image(),getApplicationContext());
                final String userid = getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
                        i.putExtra("id",userid);
                        startActivity(i);
                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDisplayName(String name){
            TextView uName = (TextView)mView.findViewById(R.id.u_name);
            uName.setText(name);
        }
        public void setStatus(String status){
            TextView uStatus = (TextView)mView.findViewById(R.id.u_status);
            uStatus.setText(status);
        }
        public void setUserImage(String thumb_image,Context ctx){
            CircleImageView userImage = (CircleImageView)mView.findViewById(R.id.u_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.ic_account_circle).into(userImage);

        }
    }
}
