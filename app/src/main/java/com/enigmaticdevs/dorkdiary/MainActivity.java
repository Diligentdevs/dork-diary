package com.enigmaticdevs.dorkdiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.enigmaticdevs.dorkdiary.Account.PasscodeActivity;
import com.enigmaticdevs.dorkdiary.Functionality.NewNote;
import com.enigmaticdevs.dorkdiary.ItemAdapters.ItemAdapter;
import com.enigmaticdevs.dorkdiary.NavItemAdapter.NavItemAdapter;
import com.enigmaticdevs.dorkdiary.NotificationReceiver.NotificationReceiver;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> menu,Title,Date,Time,Note,Key;
    List<Integer> icons;
    NavItemAdapter itemAdapter;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String uid;
    AdView mAdView;
    SkeletonScreen skeletonScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.menu_left_drawer)
                .withToolbarMenuToggle(toolbar)
                .inject();
        ImageView imageView = findViewById(R.id.lock_icon);
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        setAlarmManager();
        initializerecycler();
        sharedPreferences = this.getSharedPreferences("com.enigmaticdevs.dorkdiary",MODE_PRIVATE);
        if(!sharedPreferences.getString("passcode","").isEmpty()){

            imageView.setImageResource(R.drawable.locked);
        }
        else
            imageView.setImageResource(R.drawable.not_locked);

         readdata();
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
               mAdView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setAlarmManager() {
        createNotificationChannel();
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        int curHr = calendar.get(Calendar.HOUR_OF_DAY);
        if(curHr>=20)
           calendar.add(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Daily Reminder";
            String description = "Message";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channelId", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void readdata() {
        Title = new ArrayList<>();
        Date = new ArrayList<>();
        Time = new ArrayList<>();
        Note = new ArrayList<>();
        Key = new ArrayList<>();
        final ShimmerRecyclerView recyclerView;
        final ItemAdapter itemAdapter;
        recyclerView= findViewById(R.id.recyclerView);
        itemAdapter = new ItemAdapter(Title,Date,Note,Key,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemAdapter);
        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(itemAdapter)
                .load(R.layout.skelton_row_item_shimmer)
                .show();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.getValue()!=null){
                 Log.d("else","ifchaleya");
                 for(DataSnapshot keys : dataSnapshot.getChildren()){
                             String key = keys.getKey();
                             String title = keys.child("Title").getValue(String.class);
                             String note = keys.child("Note").getValue(String.class);
                             String date = keys.child("Date").getValue(String.class);
                             String time = keys.child("Time").getValue(String.class);
                             Title.add(title);
                             Note.add(note);
                             Date.add(date);
                             Time.add(time);
                             Key.add(key);
                             itemAdapter.notifyDataSetChanged();
                             skeletonScreen.hide();
                     }
                 }
             else {
                 Log.d("else","elsechaleya");
                 skeletonScreen.hide();
             }
             }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    protected  void initializerecycler(){
        additems();
        recyclerView = findViewById(R.id.list);
        itemAdapter = new NavItemAdapter(menu,icons,this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(itemAdapter);
    }

    private void additems() {
        menu = new ArrayList<>();
        icons = new ArrayList<>();
        menu.add("My Entries");
        menu.add("Rate App");
        menu.add("Share");
       // menu.add("Remove Ads");
        menu.add("Logout");
        icons.add(R.drawable.notes);
        icons.add(R.drawable.rate_app);
        icons.add(R.drawable.share);
     //   icons.add(R.drawable.remove_ads);
        icons.add(R.drawable.logout);
    }

    public void Add_note(View view) {
        Intent intent = new Intent(this,NewNote.class);
        startActivity(intent);

    }

    public void passcode(View view) {
            Intent intent = new Intent(this, PasscodeActivity.class);
            startActivity(intent);
    }
}