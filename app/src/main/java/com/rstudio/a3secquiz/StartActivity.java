package com.rstudio.a3secquiz;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.android.publish.adsCommon.StartAppSDK;

public class StartActivity extends AppCompatActivity {


    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("player");
    private Button bt_start, bt_redeem;
    private static final String TAG = "StartActivity";
    private TextView tv_coin,tv_life;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "211618215", true);
        setContentView(R.layout.activity_start);

        tv_coin = findViewById(R.id.tv_coin_startActivity);
        tv_life = findViewById(R.id.tv_life_startactivity);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);
                tv_coin.setText(String.valueOf(player.getCoins()));
                tv_life.setText(String.valueOf(player.getLifes()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
