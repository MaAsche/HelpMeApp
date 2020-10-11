package de.htwg.helpme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.htwg.helpme.helpers.ClickListener;
import de.htwg.helpme.datatypes.GlobalTaskCount;
import de.htwg.helpme.helpers.CustomAdapter;
import de.htwg.helpme.room.AppDatabase;
import de.htwg.helpme.datatypes.TaskRoom;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity{

    private ImageView profile;
    private ImageView message;
    private Button needHelp;
    private Button wantToHelp;
    private OkHttpClient client;
    private Gson gson;
    private TextView done;
    private TextView open;
    private AppDatabase db;
    private ListView listView;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile = findViewById(R.id.profileImage);
        message = findViewById(R.id.messageImage);
        needHelp = findViewById(R.id.needHelpButton);
        wantToHelp = findViewById(R.id.wantToHelpButton);
        done = findViewById(R.id.doneTaskTextView);
        open = findViewById(R.id.openTaskTextView);
        listView = findViewById(R.id.mainListView);

        client = new OkHttpClient();
        gson = new Gson();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        profile.setOnClickListener(new ClickListener(getApplicationContext(), this));
        message.setOnClickListener(new ClickListener(getApplicationContext(), this));

        db = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME).fallbackToDestructiveMigration().build();

        needHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NeedHelpActivity.class);
                startActivity(intent);

            }
        });
        wantToHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WantToHelpActivity.class);
                startActivity(intent);
            }
        });

        new getData().execute();
        gettotal();

    }


    //realtime update would be nice
    public void gettotal() {
        Request request = new Request.Builder()
                .url(getString(R.string.server_url) + "api/task/gettotal")
                .get()
                .build();

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {


                    final GlobalTaskCount count = gson.fromJson(response.body().charStream(), GlobalTaskCount.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (!count.getOpen().equals("0")) {
                                open.setText(count.getOpen());
                            } else {
                                open.setText("0");
                            }
                            if (!count.getDone().equals("0")) {
                                done.setText(count.getDone());
                            } else {
                                done.setText("0");
                            }

                        }
                    });


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public class getData extends AsyncTask<Void, Void, List<TaskRoom>> {


        @Override
        protected List<TaskRoom> doInBackground(Void... voids) {
            return db.taskDao().getStrangerList(0);
        }

        @Override
        protected void onPostExecute(List<TaskRoom> tasks) {
            super.onPostExecute(tasks);
            ArrayList<TaskRoom> arrayList = new ArrayList<>(tasks);
            customAdapter = new CustomAdapter(MainActivity.this, 1, arrayList);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(customAdapter);

                }
            });

        }

    }

}
