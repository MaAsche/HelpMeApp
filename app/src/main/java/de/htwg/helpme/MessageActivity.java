package de.htwg.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import de.htwg.helpme.helpers.ClickListener;

public class MessageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        findViewById(R.id.homeImage).setOnClickListener(new ClickListener(getApplicationContext(), this));
        findViewById(R.id.profileImage).setOnClickListener(new ClickListener(getApplicationContext(), this));

        //To implement this activity the server has to send data to the client
    }
}
