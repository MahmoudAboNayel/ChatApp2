package com.example.abo_nayel.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AcountSettingsActivity extends AppCompatActivity {

    DatabaseReference database;
    TextView nametxt, statustxt;
    CircleImageView acc_image;
    Button imagebtn, statusbtn;
    private StorageReference storageReference;
    private FirebaseUser current_user;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount_settings);
        nametxt=(TextView)findViewById(R.id.acc_name);
        statustxt = (TextView)findViewById(R.id.acc_status);
        statusbtn = (Button)findViewById(R.id.acc_status_btn) ;
        imagebtn = (Button)findViewById(R.id.acc_image_btn) ;
        acc_image= (CircleImageView)findViewById(R.id.acc_image);

        storageReference = FirebaseStorage.getInstance().getReference();
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        database = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        database.keepSynced(true);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String image = dataSnapshot.child("image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                nametxt.setText(name);
                statustxt.setText(status);

                Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                        placeholder(R.drawable.ic_account_circle).into(acc_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.ic_account_circle).into(acc_image);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        statusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),
                        StatusActivity.class).putExtra("status",statustxt.getText().toString()));

            }
        });
        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                        .start(AcountSettingsActivity.this);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(AcountSettingsActivity.this);
                progressDialog.setTitle("Uplouding image");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();


                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());

                final Bitmap thumb_image = new Compressor(this).setMaxHeight(200).setMaxWidth(200).setQuality(70).compressToBitmap(thumb_file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference image_path = storageReference.child("profile_images").child(current_user.getUid()+".jpg");
                final StorageReference thumb_image_path = storageReference.child("profile_images").child("thumbs").child(current_user.getUid()+".jpg");


                image_path.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        UploadTask uploadTask = thumb_image_path.putBytes(thumb_byte);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                String thumb_download_url = thumb_task.getResult().getDownloadUrl().toString();


                                if(thumb_task.isSuccessful()){
                                    Map update_map = new HashMap();
                                    update_map.put("image" , downloadUri);
                                    update_map.put("thumb_image", thumb_download_url);
                                    database.updateChildren(update_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            progressDialog.dismiss();
                                            Toast.makeText(AcountSettingsActivity.this,"image uploaded",Toast.LENGTH_LONG).show();

                                        }
                                    });
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AcountSettingsActivity.this,"image uploading failed",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
       }
    }
}
