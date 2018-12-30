package com.rstudio.a3secquiz;

import android.content.Intent;
import android.content.SharedPreferences;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "211618215", true);
        StartAppAd.disableSplash();
        setContentView(R.layout.activity_start);

        setValues();
        setListners();
        loadUserDetails();

    }

    private void loadUserDetails() {
        String UserID = preferences.getString("USERID", null);
        if (UserID == null) {
            createNewUser();
            Log.d(TAG, "loadUserDetails: NULL Value");
        }else{
            userID = UserID;
            reference.child(UserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        try{
                            Player player = dataSnapshot.getValue(Player.class);
                            tv_coin.setText(String.valueOf(player.getCoins()));
                            tv_life.setText(String.valueOf(player.getLifes()));
                        }catch(NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Log.d(TAG, "loadUserDetails: User ID got : "+UserID);
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
                                editor.putString("USERID",userID);
                                editor.apply();
                                Log.d(TAG, "onComplete: User Created");
                                loadUserDetails();
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
                Toast.makeText(getApplicationContext(), "Failed , " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setValues() {
        preferences =getSharedPreferences(prefKEY, MODE_PRIVATE);
        bt_redeem = findViewById(R.id.bt_redeemCoin);
        editor = preferences.edit();
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
                intent.putExtra("UUID",userID);
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
}
