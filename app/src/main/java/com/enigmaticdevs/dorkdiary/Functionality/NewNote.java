package com.enigmaticdevs.dorkdiary.Functionality;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.enigmaticdevs.dorkdiary.Account.StartupActivity;
import com.enigmaticdevs.dorkdiary.MainActivity;
import com.enigmaticdevs.dorkdiary.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewNote extends AppCompatActivity {
    TextView date, time;
    TextInputEditText title, diary, mood;
    TextInputLayout title_layout, diary_layout, mood_layout;
    Calendar calendar;
    DatabaseReference databaseReference;
    UploadTask mStorageRef;
    FirebaseAuth firebaseAuth;
    String uid, mytime, mydate;
    ConstraintLayout progressBar;
    Bitmap bitmap;
    ImageView user_uploaded_image;
    int t, d, m, GALLERY_REQUEST_CODE,imageUploaded=0;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        date = findViewById(R.id.new_date);
        time = findViewById(R.id.new_time);
        title = findViewById(R.id.new_note_title);
        title_layout = findViewById(R.id.new_note_title_layout);
        diary = findViewById(R.id.new_note_body);
        diary_layout = findViewById(R.id.new_note_body_layout);
        mood = findViewById(R.id.new_note_mood);
        mood_layout = findViewById(R.id.new_note_mood_layout);
        user_uploaded_image = findViewById(R.id.user_uploaded_image);
        progressBar = findViewById(R.id.new_note_loading);
        progressBar.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        mydate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        date.setText(mydate);
        if (DateFormat.is24HourFormat(this)) {
            mytime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        } else
            mytime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        time.setText(mytime);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title_layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        diary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                diary_layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mood_layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public void save(View view) {
        if (title.getText().toString().isEmpty()) {
            title_layout.setError("Come on,fill it");
        } else
            t = 1;
        if (diary.getText().toString().isEmpty()) {
            diary_layout.setError("this cant be empty either");
        } else
            d = 1;
        if (mood.getText().toString().isEmpty())
            mood_layout.setError("You must feel somethin");
        else
            m = 1;
        if (d == 1 && t == 1 && m == 1) {
            uid = firebaseAuth.getCurrentUser().getUid();
            String currentTime = String.valueOf(System.currentTimeMillis());
            databaseReference = FirebaseDatabase.getInstance().getReference().child(uid).child(currentTime);
            databaseReference.child("Title").setValue(title.getText().toString());
            databaseReference.child("Note").setValue(diary.getText().toString());
            databaseReference.child("Date").setValue(date.getText().toString());
            databaseReference.child("Time").setValue(time.getText().toString());
            databaseReference.child("Mood").setValue(mood.getText().toString());
            if(imageUploaded==1)
            {   progressBar.setVisibility(View.VISIBLE);
                ByteArrayOutputStream BAOs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, BAOs);
                byte[] data = BAOs.toByteArray();
                mStorageRef = FirebaseStorage.getInstance().getReference().child(uid).child(currentTime).putBytes(data);
                mStorageRef.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(NewNote.this, "Image Upload Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(NewNote.this, MainActivity.class);
                                progressBar.setVisibility(View.GONE);
                                startActivity(intent);
                                finishAffinity();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(NewNote.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
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
            imageUploaded=1;
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
                        imageUploaded=0;
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
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}