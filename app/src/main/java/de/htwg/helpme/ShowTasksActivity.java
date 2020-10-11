package de.htwg.helpme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.htwg.helpme.helpers.ClickListener;
import de.htwg.helpme.helpers.CustomAdapter;
import de.htwg.helpme.room.AppDatabase;
import de.htwg.helpme.datatypes.TaskRoom;
import okhttp3.OkHttpClient;

public class ShowTasksActivity extends AppCompatActivity {
    private OkHttpClient client;
    private SharedPreferences prefs;
    private ImageView home;
    private ImageView profile;
    private ImageView message;
    private Gson gson;
    private ListView listView;
    private AppDatabase db;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tasks);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        client = new OkHttpClient();
        gson = new Gson();

        listView = findViewById(R.id.tasksListView);

        findViewById(R.id.homeImage).setOnClickListener(new ClickListener(getApplicationContext(), this));
        findViewById(R.id.messageImage).setOnClickListener(new ClickListener(getApplicationContext(), this));
        findViewById(R.id.profileImage).setOnClickListener(new ClickListener(getApplicationContext(), this));


        db = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME).fallbackToDestructiveMigration().build();

        new AsyncTask<Void, Void, List<TaskRoom>>() {

            @SuppressLint("StaticFieldLeak")
            @Override
            protected List<TaskRoom> doInBackground(Void... voids) {
                return db.taskDao().getTaskList();

            }

            @Override
            protected void onPostExecute(List<TaskRoom> taskRooms) {
                super.onPostExecute(taskRooms);
                ArrayList<TaskRoom> arrayList = new ArrayList<>(taskRooms);

                final CustomAdapter customAdapter = new CustomAdapter(ShowTasksActivity.this, 0, arrayList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(customAdapter);
                    }
                });

            }
        }.execute();

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View empty = findViewById(R.id.listEmpty);
        ListView tmp = findViewById(R.id.tasksListView);
        tmp.setEmptyView(empty);
    }
}
