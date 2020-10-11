package de.htwg.helpme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import de.htwg.helpme.helpers.ClickListener;

public class ProfileActivity extends AppCompatActivity {

    private Button changeProfile;
    private Button showAll;
    private Button logout;
    private ImageView home;
    private ImageView message;
    private SharedPreferences prefs;
    private ImageView image;
    private TextView name;
    private TextView phone;
    private TextView email;
    private Configuration configuration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        changeProfile = findViewById(R.id.changeProfile);
        showAll = findViewById(R.id.showAll);
        logout = findViewById(R.id.logoutButton);
        home = findViewById(R.id.homeImage);
        message = findViewById(R.id.messageImage);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        configuration = getResources().getConfiguration();

        name = findViewById(R.id.nameTextView);
        phone = findViewById(R.id.phoneTextView);
        email = findViewById(R.id.mailTextView);
        image = findViewById(R.id.profilePictureImageView);

        name.setText(prefs.getString("name", null));
        phone.setText(prefs.getString("phone", null));
        email.setText(prefs.getString("email", null));
        String imageBase64 = prefs.getString("profileImage", null);
        if (imageBase64 != null) {
            if (imageBase64.equals("")) {
                image.setImageResource(R.drawable.ic_default_profile);

            } else {
                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                image.setImageBitmap(decodedByte);
            }
        }


        findViewById(R.id.messageImage).setOnClickListener(new ClickListener(getApplicationContext(), this));
        findViewById(R.id.homeImage).setOnClickListener(new ClickListener(getApplicationContext(), this));


        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChangeProfileActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowTasksActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showLogoutMessage();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            name.setText(prefs.getString("name", null));
            phone.setText(prefs.getString("phone", null));
            email.setText(prefs.getString("email", null));
            String imageBase64 = prefs.getString("profileImage", null);
            if (imageBase64 != null) {
                if (imageBase64.equals("")) {
                    image.setImageResource(R.drawable.ic_default_profile);
                } else {
                    byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image.setImageBitmap(decodedByte);
                }
            }
        }
    }

    private void showLogoutMessage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.LogoutAlert);

        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logoutQuestion);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                finishAffinity();
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.no, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

