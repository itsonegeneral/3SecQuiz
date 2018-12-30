package com.rstudio.a3secquiz;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.VideoListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

public class WatchAndEarnActivity extends AppCompatActivity {

    private Button bt_viewAd;
    private StartAppAd startAppAd = new StartAppAd(this);
    private ProgressDialog pgDialog;
    private String userID;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USERS");
    private TextView tv_coin, tv_life;
    private int coins;
    private static final String TAG = "WatchAndEarnActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_and_earn);
        setValues();
        Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();
        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                Log.d(TAG, "onReceiveAd: Ad Loaded");
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                Log.d(TAG, "onFailedToReceiveAd: "+ ad.getErrorMessage());
            }
        });
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        Player player = dataSnapshot.getValue(Player.class);
                        coins = player.getCoins();
                        tv_life.setText(String.valueOf(player.getLifes()));
                        tv_coin.setText(String.valueOf(player.getCoins()));

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WatchAndEarnActivity.this, "Failed to Load", Toast.LENGTH_SHORT).show();
            }
        });
        bt_viewAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAppAd.showAd();
            }
        });
        startAppAd.setVideoListener(new VideoListener() {
            @Override
            public void onVideoCompleted() {
                coins = coins+20;
                reference.child(userID).child("coins").setValue(coins).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(WatchAndEarnActivity.this,"20 Coins Credited",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(WatchAndEarnActivity.this,"Failed ! 445",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void setValues() {
        bt_viewAd = findViewById(R.id.bt_viewRewardedVideo);
        userID = getIntent().getExtras().getString("UUID");
        tv_coin = findViewById(R.id.tv_coin_startActivity);
        tv_life = findViewById(R.id.tv_life_startactivity);
        pgDialog = new ProgressDialog(this);

    }
}
