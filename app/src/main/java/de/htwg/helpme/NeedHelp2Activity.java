package de.htwg.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.htwg.helpme.datatypes.ReturnTask;
import de.htwg.helpme.helpers.ClickListener;
import de.htwg.helpme.helpers.LocationHandler;
import de.htwg.helpme.datatypes.Point;
import de.htwg.helpme.room.AppDatabase;
import de.htwg.helpme.datatypes.TaskRoom;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NeedHelp2Activity extends AppCompatActivity {

    private CalendarView date;
    private static Date dueDate = new Date();
    private Button next;
    private ImageView home;
    private ImageView profile;
    private ImageView message;
    private OkHttpClient client;
    private static String description;
    private static String category;
    private static int categoryNumber;
    private SharedPreferences prefs;
    private final int PERMISSION_REQUEST_CODE = 1;
    private static AppDatabase db;
    LocationHandler locationHandler;
    Point point;
    private static int id;
    static SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_help2);

        home = findViewById(R.id.homeImage);
        message = findViewById(R.id.messageImage);
        profile = findViewById(R.id.profileImage);
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

        home.setOnClickListener(new ClickListener(getApplicationContext(), this));
        message.setOnClickListener(new ClickListener(getApplicationContext(), this));
        profile.setOnClickListener(new ClickListener(getApplicationContext(), this));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        db = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME).fallbackToDestructiveMigration().build();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
        description = getIntent().getStringExtra("description");
        category = getIntent().getStringExtra("category");
        switch (Objects.requireNonNull(category)) {
            case "Einkaufen":
                categoryNumber = 0;
                break;
            case "Haushalt":
                categoryNumber = 1;
                break;
            case "Garten":
                categoryNumber = 2;
                break;
            case "Sonstiges":
                categoryNumber = 3;
                break;
        }
        date = findViewById(R.id.calendarView);
        date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String tmpmonth;
                int correctMonth = month + 1;
                if (String.valueOf(day).length() == 1) {
                    tmpmonth = "0" + day;
                } else {
                    tmpmonth = String.valueOf(correctMonth);
                }
                String tmpday;
                if (String.valueOf(day).length() == 1) {
                    tmpday = "0" + day;
                } else {
                    tmpday = String.valueOf(day);
                }
                try {
                    dueDate = sdf.parse(year + "-" + tmpmonth + "-" + tmpday);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.v("date", dueDate.toString());
            }
        });
        next = findViewById(R.id.postButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        setResult(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(0);
    }

    @SuppressLint("StaticFieldLeak")
    private void next() {

        if (dueDate.before(new Date(System.currentTimeMillis() - 3600 * 1000))) {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_date), Toast.LENGTH_SHORT).show();
            return;
        }
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
                if (dueDate == null) {
                    dueDate = new Date(date.getDate());
                }
                date.setVisibility(View.INVISIBLE);
                findViewById(R.id.textViewCalendar).setVisibility(View.INVISIBLE);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                findViewById(R.id.needHelp).setBackgroundColor(getColor(R.color.dim));
                next.setVisibility(View.INVISIBLE);
            }

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


            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("description", description)
                        .addFormDataPart("category", String.valueOf(categoryNumber))
                        .addFormDataPart("duedate", sdf.format(dueDate))
                        .addFormDataPart("owner", prefs.getString("token", null))
                        .addFormDataPart("location", point.toString())
                        .build();

                @SuppressLint("StaticFieldLeak") Request request = new Request.Builder()
                        .url(getString(R.string.server_url) + "api/task/create")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.code() == 200) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.send_task_ok), Toast.LENGTH_SHORT).show();
                                    Gson gson = new Gson();
                                    id = gson.fromJson(response.body().charStream(), ReturnTask.class).getId();
                                    new myTask().execute();
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                        });
                    }
                });
            }
        }.execute();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationHandler = new LocationHandler(getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.need_gps), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class myTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            db.taskDao().insertTask(new TaskRoom(id, description, categoryNumber, sdf.format(dueDate), 0, 1));
            return null;
        }
    }

}
