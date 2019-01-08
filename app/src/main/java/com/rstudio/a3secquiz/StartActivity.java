package com.rstudio.a3secquiz;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adcolony.sdk.AdColony;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.Random;
import java.util.UUID;

public class StartActivity extends AppCompatActivity {


    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USERS");
    private Button bt_start, bt_redeem, bt_watchAndEarn;
    private static final String TAG = "StartActivity";
    private TextView tv_coin, tv_life;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static final String prefKEY = "USERDATA";
    private String userID;
    private StartAppAd startAppAd;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressBar pgBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "211618215", true);
        StartAppAd.disableSplash();
        setContentView(R.layout.activity_start);
        AdColony.configure(this, "appaee1c999acb0489b96", "vz97cb81ad62d3489e8a");
        setValues();
        setListners();
        loadUserDetails();

    }

    private void loadUserDetails() {
        String UserID = preferences.getString("USERID", null);
        if (UserID == null) {
            createNewUser();
            Log.d(TAG, "loadUserDetails: NULL Value");
        } else {
            userID = UserID;
            pgBar.setVisibility(View.VISIBLE);
            reference.child(UserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try {
                            Player player = dataSnapshot.getValue(Player.class);
                            tv_coin.setText(String.valueOf(player.getCoins()));
                            tv_life.setText(String.valueOf(player.getLifes()));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        pgBar.setVisibility(View.INVISIBLE);
                    }else{
                        pgBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    pgBar.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });
            Log.d(TAG, "loadUserDetails: User ID got : " + UserID);
        }

    }

    private void createNewUser() {
        userID = UUID.randomUUID().toString();
        Log.d(TAG, "createNewUser: User ID = " + userID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userID)) {
                    createNewUser();
                    Log.d(TAG, "onDataChange: Database Already Exists");
                } else {
                    Player player = new Player("Not Set", 0, 0);
                    reference.child(userID).setValue(player).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                editor.putString("USERID", userID);
                                editor.apply();
                                Log.d(TAG, "onComplete: User Created");
                                loadUserDetails();
                                showNewUserDialog();
                                Toast.makeText(getApplicationContext(), "Welcome !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed, 034", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed , " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showNewUserDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.free_100_coin_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setView(deleteDialogView);
        Button b = deleteDialogView.findViewById(R.id.bt_claimFreeCoin);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                reference.child(userID).child("coins").setValue(100).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StartActivity.this, "100 Coins Credited", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StartActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        deleteDialog.show();
    }

    private void setValues() {
        preferences = getSharedPreferences(prefKEY, MODE_PRIVATE);
        bt_redeem = findViewById(R.id.bt_redeemCoin);
        editor = preferences.edit();
        pgBar = findViewById(R.id.pgBar_watch_coin_life);
        bt_start = findViewById(R.id.bt_startGame);
        startAppAd = new StartAppAd(this);
        bt_watchAndEarn = findViewById(R.id.bt_watchAndEarn);
        tv_coin = findViewById(R.id.tv_coin_startActivity);
        tv_life = findViewById(R.id.tv_life_startactivity);
    }

    private void setListners() {
        bt_watchAndEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch watch and earn activity here
                Intent intent = new Intent(StartActivity.this, WatchAndEarnActivity.class);
                intent.putExtra("UUID", userID);
                startActivity(intent);
            }
        });

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start quiz game activity here
                Toast.makeText(getApplicationContext(), "Under Development", Toast.LENGTH_SHORT).show();
            }
        });

        bt_redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Redeem coin page activity
                startActivity(new Intent(StartActivity.this, RedeemCoinActivity.class));
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
