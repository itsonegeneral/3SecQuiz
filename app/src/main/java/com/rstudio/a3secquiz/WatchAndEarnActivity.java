package com.rstudio.a3secquiz;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyReward;
import com.adcolony.sdk.AdColonyRewardListener;
import com.adcolony.sdk.AdColonyZone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.UUID;

public class WatchAndEarnActivity extends AppCompatActivity {

    private Button bt_viewAd;
    private StartAppAd startAppAd = new StartAppAd(this);
    private ProgressBar pgBar;
    private String userID;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USERS");
    private TextView tv_coin, tv_life;
    private int coins;
    private static final String TAG = "WatchAndEarnActivity";
    private AdColonyInterstitialListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_and_earn);
        setValues();
        pgBar.setVisibility(View.VISIBLE);

        //   AdColony.configure(this, APP_ID, ZONE_ID);
        bt_viewAd.setEnabled(false);


        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                Log.d(TAG, "onReceiveAd: Ad Loaded");
                bt_viewAd.setEnabled(true);
                pgBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                pgBar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onFailedToReceiveAd: " + ad.getErrorMessage());
            }
        });

        startAppAd.setVideoListener(new VideoListener() {
            @Override
            public void onVideoCompleted() {
                coins = coins + 20;
                reference.child(userID).child("coins").setValue(coins).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(WatchAndEarnActivity.this, "20 Coins Credited", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WatchAndEarnActivity.this, "Failed ! 445", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    }

    private void setValues() {
        bt_viewAd = findViewById(R.id.bt_viewRewardedVideo);
        userID = getIntent().getExtras().getString("UUID");
        tv_coin = findViewById(R.id.tv_coin_startActivity);
        tv_life = findViewById(R.id.tv_life_startactivity);
        pgBar = findViewById(R.id.pgBar_watch_and_earn);

    }
}

           /*
            private AdColonyAdOptions adOptions;
            private AdColonyInterstitial ad;
            private static final String APP_ID = "appaee1c999acb0489b96";
            public static final String ZONE_ID = "vz97cb81ad62d3489e8a";


     AdColony.setRewardListener(new AdColonyRewardListener() {
            @Override
            public void onReward(AdColonyReward reward) {
                // Query reward object for info here
                bt_viewAd.setEnabled(true);
                coins = coins + 20;
                pgBar.setVisibility(View.VISIBLE);
                reference.child(userID).child("coins").setValue(coins).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(WatchAndEarnActivity.this, "20 Coins Credited", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WatchAndEarnActivity.this, "Credit Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AdColony.requestInterstitial(ZONE_ID, listener, adOptions);
                Log.d(TAG, "onReward");
            }
        });

        // Set up listener for interstitial ad callbacks. You only need to implement the callbacks
        // that you care about. The only required callback is onRequestFilled, as this is the only
        // way to get an ad object.

        listener = new AdColonyInterstitialListener() {
            @Override
            public void onRequestFilled(AdColonyInterstitial ad) {
                // Ad passed back in request filled callback, ad can now be shown
                WatchAndEarnActivity.this.ad = ad;
                bt_viewAd.setEnabled(true);
                pgBar.setVisibility(View.INVISIBLE);
                //progress.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onRequestFilled");
            }

            @Override
            public void onRequestNotFilled(AdColonyZone zone) {
                // Ad request was not filled
                pgBar.setVisibility(View.INVISIBLE);
                bt_viewAd.setEnabled(false);
                Log.d(TAG, "onRequestNotFilled");
            }

            @Override
            public void onOpened(AdColonyInterstitial ad) {
                // Ad opened, reset UI to reflect state change
                bt_viewAd.setEnabled(false);
                //progress.setVisibility(View.VISIBLE);
                Log.d(TAG, "onOpened");
                pgBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onExpiring(AdColonyInterstitial ad) {
                // Request a new ad if ad is expiring
                bt_viewAd.setEnabled(false);
                //progress.setVisibility(View.VISIBLE);
                AdColony.requestInterstitial(ZONE_ID, this, adOptions);
                Log.d(TAG, "onExpiring");
                pgBar.setVisibility(View.INVISIBLE);
            }
        };
        AdColony.requestInterstitial(ZONE_ID, listener, adOptions);


    */