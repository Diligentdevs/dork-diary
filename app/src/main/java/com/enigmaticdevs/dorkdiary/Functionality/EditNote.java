package com.enigmaticdevs.dorkdiary.Functionality;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.enigmaticdevs.dorkdiary.MainActivity;
import com.enigmaticdevs.dorkdiary.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditNote extends AppCompatActivity {
    TextInputEditText title,diary,mood;
    TextInputLayout title_layout,diary_layout,mood_layout;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ConstraintLayout progressBar;
    ImageView user_uploaded_image;
    String uid,key;
    Uri imageUri;
    Bitmap bitmap;
    int t,d,m,user_image=0,GALLERY_REQUEST_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Intent intent = getIntent();
        key = intent.getExtras().getString("Key");
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        title = findViewById(R.id.edit_note_title);
        title_layout= findViewById(R.id.edit_note_title_layout);
        diary = findViewById(R.id.edit_note_body);
        diary_layout = findViewById(R.id.edit_note_body_layout);
        mood = findViewById(R.id.edit_note_mood);
        mood_layout = findViewById(R.id.edit_note_mood_layout);
        user_uploaded_image = findViewById(R.id.edit_user_uploaded_image);
        progressBar = findViewById(R.id.edit_note_loading);
        progressBar.setVisibility(View.GONE);
        readdata();
    }

    private void readdata() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child(uid).child(key);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   title.setText(dataSnapshot.child("Title").getValue(String.class));
                   mood.setText(dataSnapshot.child("Mood").getValue(String.class));
                   diary.setText(dataSnapshot.child("Note").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
         FirebaseStorage.getInstance().getReference().child(uid).child(key).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(user_uploaded_image);
                user_uploaded_image.setVisibility(View.VISIBLE);
                user_image = 1;
            }
        });
    }

    public void update(View view) {
        if(title.getText().toString().isEmpty())
            title_layout.setError("come on,dont do this");
        else
            t=1;
        if(mood.getText().toString().isEmpty())
            mood_layout.setError("you know the drill");
        else
            m=1;
        if(diary.getText().toString().isEmpty())
            diary_layout.setError("this aint happening");
        else
            d=1;
        if(t==1 && m==1 && d==1) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child(uid).child(key);
            databaseReference.child("Title").setValue(title.getText().toString());
            databaseReference.child("Note").setValue(diary.getText().toString());
            databaseReference.child("Mood").setValue(mood.getText().toString());
            if(user_image ==3)
            {
                progressBar.setVisibility(View.VISIBLE);
                ByteArrayOutputStream BAOs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, BAOs);
                byte[] data = BAOs.toByteArray();
                 FirebaseStorage.getInstance().getReference().child(uid).child(key).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(EditNote.this, "Image Upload Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditNote.this, MainActivity.class);
                        progressBar.setVisibility(View.GONE);
                        startActivity(intent);
                        finishAffinity();
                          }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(EditNote.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
            if(user_image ==0)
            {
                FirebaseStorage.getInstance().getReference().child(uid).child(key).delete();
            }
        }
    }

    public void addImage(View view) {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Sellect Picture"), GALLERY_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            user_uploaded_image.setImageURI(imageUri);
            user_uploaded_image.setVisibility(View.VISIBLE);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            user_image = 3;
        }
    }

    public void fullscreen(View view) {
        final ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(user_uploaded_image.getDrawable());
        final AlertDialog dialog=new AlertDialog.Builder(this)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user_uploaded_image.setImageDrawable(null);
                        user_uploaded_image.setVisibility(View.GONE);
                        user_image = 0;
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setTitle("Your Uploaded Image")
                .setView(imageView)
                .create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.TRANSPARENT);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.TRANSPARENT);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#4169E1"));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4169E1"));
                FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(800, 800);
                layoutParams.gravity= Gravity.CENTER;
                imageView.setLayoutParams(layoutParams);
            }
        });
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimaryDark);
    }

}
