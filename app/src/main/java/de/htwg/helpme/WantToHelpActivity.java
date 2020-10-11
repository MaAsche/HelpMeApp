package de.htwg.helpme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.htwg.helpme.datatypes.Point;
import de.htwg.helpme.helpers.ClickListener;
import de.htwg.helpme.helpers.LocationHandler;
import de.htwg.helpme.room.AppDatabase;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WantToHelpActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView distanceView;
    private int distance;
    private List<Integer> categories;
    private List<CheckBox> selected;
    private Button start;
    private ImageView profile;
    private ImageView message;
    private ImageView home;
    private OkHttpClient client;
    private static Point point;
    private static LocationHandler locationHandler;
    private final int PERMISSION_REQUEST_CODE = 1;
    private static Context context;
    private SharedPreferences prefs;
    private static AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_want_to_help);

        locationHandler = new LocationHandler(getApplicationContext());
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        profile = findViewById(R.id.profileImage);
        message = findViewById(R.id.messageImage);
        home = findViewById(R.id.homeImage);
        distance = 100;

        profile.setOnClickListener(new ClickListener(getApplicationContext(), this));
        message.setOnClickListener(new ClickListener(getApplicationContext(), this));
        home.setOnClickListener(new ClickListener(getApplicationContext(), this));


        context = getApplicationContext();


        selected = new ArrayList<CheckBox>();
        categories = new ArrayList<Integer>();
        selected.add((CheckBox) findViewById(R.id.shoppingCheckBox));
        selected.add((CheckBox) findViewById(R.id.houseCheckBox));
        selected.add((CheckBox) findViewById(R.id.gardenCheckBox));
        selected.add((CheckBox) findViewById(R.id.otherCheckBox));
        start = findViewById(R.id.startButton);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTasks();


            }

        });
        client = new OkHttpClient();

        setupSeekBar();

    }

    @SuppressLint("StaticFieldLeak")
    private void getTasks() {
        if (distance == 100) {
            Toast.makeText(getApplicationContext(), getString(R.string.select_dist), Toast.LENGTH_SHORT).show();
        } else if (handleCheckBoxes().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.select_category), Toast.LENGTH_SHORT).show();
        } else {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                    PackageManager.PERMISSION_GRANTED) {
                        locationHandler = new LocationHandler(getApplicationContext());
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                            }
                        });
                    }

                    findViewById(R.id.loadingPanel2).setVisibility(View.VISIBLE);
                    findViewById(R.id.wanttohelp).setBackgroundColor(getColor(R.color.dim));
                    findViewById(R.id.textView11).setVisibility(View.INVISIBLE);
                    findViewById(R.id.textView12).setVisibility(View.INVISIBLE);
                    findViewById(R.id.textView13).setVisibility(View.INVISIBLE);
                    findViewById(R.id.textView14).setVisibility(View.INVISIBLE);
                    findViewById(R.id.distanceSeekBar).setVisibility(View.INVISIBLE);
                    findViewById(R.id.startButton).setVisibility(View.INVISIBLE);
                    findViewById(R.id.gardenCheckBox).setVisibility(View.INVISIBLE);
                    findViewById(R.id.shoppingCheckBox).setVisibility(View.INVISIBLE);
                    findViewById(R.id.otherCheckBox).setVisibility(View.INVISIBLE);
                    findViewById(R.id.houseCheckBox).setVisibility(View.INVISIBLE);
                    findViewById(R.id.distance).setVisibility(View.INVISIBLE);


                }


                @SuppressLint("StaticFieldLeak")
                @Override
                protected Void doInBackground(Void... voids) {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    point = locationHandler.getLocation();


                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Log.v("done", point.toString());

                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("distance", String.valueOf(distance))
                            .addFormDataPart("token", prefs.getString("token", null))
                            .addFormDataPart("category", handleCheckBoxes().toString())
                            .build();

                    Request request = new Request.Builder()
                            .url(getString(R.string.server_url) + "api/task/getforhelp")
                            .post(body)
                            .build();


                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.code() == 200) {
                                String res = response.body().string();

                                Intent intent = new Intent(getApplicationContext(), ShowHelpTasks.class);
                                intent.putExtra("tasks", res);
                                Log.v("json", res);
                                startActivityForResult(intent, 0);
                            }

                        }
                    });
                    finish();
                }
            }.execute();


        }

    }

    private List handleCheckBoxes() {
        categories.clear();

        for (CheckBox v : selected) {
            if (v.isChecked()) {
                categories.add(selected.indexOf(v));
            }
        }
        return categories;


    }

    private void setupSeekBar() {
        seekBar = findViewById(R.id.distanceSeekBar);
        distanceView = findViewById(R.id.distance);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                distance = i;
                String tmp = distance + " km";
                distanceView.setText(tmp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


}


