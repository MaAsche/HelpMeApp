package de.htwg.helpme.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import de.htwg.helpme.MainActivity;
import de.htwg.helpme.MessageActivity;
import de.htwg.helpme.ProfileActivity;
import de.htwg.helpme.R;

public class ClickListener implements View.OnClickListener {
    private Context context;
    private Activity activity;


    public ClickListener(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //Navbar
            case R.id.homeImage:
                activity.startActivity(new Intent(context, MainActivity.class));
                break;

            case R.id.messageImage:
                activity.startActivity(new Intent(context, MessageActivity.class));
                break;

            case R.id.profileImage:
                activity.startActivity(new Intent(context, ProfileActivity.class));
                break;
        }
    }
}
