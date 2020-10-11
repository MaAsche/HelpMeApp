package de.htwg.helpme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import de.htwg.helpme.datatypes.TaskList;
import de.htwg.helpme.datatypes.User;
import de.htwg.helpme.room.AppDatabase;
import de.htwg.helpme.datatypes.TaskRoom;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button loginButton;
    private OkHttpClient client;
    private Gson gson;
    private SharedPreferences prefs;
    private AppDatabase db;


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        loginButton = findViewById(R.id.loginButton);
        client = new OkHttpClient();
        gson = new Gson();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        db = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME).fallbackToDestructiveMigration().build();


        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

            }
        });


        login();

    }

    private void login() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = findViewById(R.id.emailText);
                password = findViewById(R.id.passwordText);
                if (!isEmailValid(email.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_mail), Toast.LENGTH_LONG).show();
                } else if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), getString(R.string.short_pw), Toast.LENGTH_LONG).show();
                } else {
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("email", email.getText().toString())
                            .addFormDataPart("password", password.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url(getString(R.string.server_url) + "api/user/login")
                            .post(body)
                            .build();

                    try {
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }

                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                if (Looper.myLooper() == null)
                                {
                                    Looper.prepare();
                                }
                                if (response.code() == 401) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.mail_or_pw_error), Toast.LENGTH_LONG).show();
                                } else if (response.code() == 400) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
                                } else if (response.code() == 200) {
                                    User user = gson.fromJson(response.body().charStream(), User.class);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("token", user.getToken()).apply();
                                    editor.putString("name", user.getName()).apply();
                                    editor.putString("email", email.getText().toString());
                                    editor.putString("profileImage", user.getImage());
                                    editor.putString("phone", user.getPhone()).apply();


                                    new AsyncTask<Void, Void, Void>() {

                                        @SuppressLint("StaticFieldLeak")
                                        @Override
                                        protected Void doInBackground(Void... voids) {
                                            db.taskDao().nukeTable();
                                            HttpUrl.Builder urlBuilder = HttpUrl.parse((getString(R.string.server_url)) + "api/task/getall").newBuilder();
                                            urlBuilder.addQueryParameter("token", prefs.getString("token", null));
                                            String url = urlBuilder.build().toString();

                                            Request request = new Request.Builder()
                                                    .get()
                                                    .url(url)
                                                    .build();

                                            client.newCall(request).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                    Toast.makeText(getApplicationContext(),getString(R.string.login_fail), Toast.LENGTH_SHORT).show();

                                                }

                                                @SuppressLint("StaticFieldLeak")
                                                @Override
                                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                    if (response.code() == 200) {
                                                        String res = response.body().string();
                                                        Log.v("json", res);
                                                        TaskList tasks = gson.fromJson(res, TaskList.class);

                                                        ArrayList<TaskRoom> arrayList = new ArrayList<>();
                                                        if (tasks.getTasks() != null) {
                                                            arrayList.addAll(tasks.getTasks());
                                                        }
                                                        db.taskDao().insertAllTasks(arrayList);
                                                    }
                                                }
                                            });
                                            return null;
                                        }
                                    }.execute();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }


                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
