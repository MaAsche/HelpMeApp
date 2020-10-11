package de.htwg.helpme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class MainEmptyActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        setContentView(R.layout.activity_main_empty);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String token = pref.getString("token", null);

        if (token != null){
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();


    }
}
