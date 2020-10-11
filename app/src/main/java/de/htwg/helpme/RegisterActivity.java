package de.htwg.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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

import de.htwg.helpme.datatypes.Token;
import de.htwg.helpme.datatypes.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button register;
    private EditText name;
    private EditText email;
    private EditText password;
    private Gson gson;
    private SharedPreferences prefs;


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        register = findViewById(R.id.register_button);
        gson = new Gson();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        register();

    }

    private void register() {
        final OkHttpClient client = new OkHttpClient();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = findViewById(R.id.nameEditText);
                email = findViewById(R.id.emailEditText);
                password = findViewById(R.id.passwordEditText);

                if (!isEmailValid(email.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_mail), Toast.LENGTH_LONG).show();
                } else if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), getString(R.string.short_pw), Toast.LENGTH_LONG).show();
                } else {
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("username", capitalizeFirstLetter(name.getText().toString()))
                            .addFormDataPart("email", email.getText().toString())
                            .addFormDataPart("password", password.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url(getString(R.string.server_url) + "api/user/register")
                            .post(body)
                            .build();


                    try {
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                if (Looper.myLooper() == null)
                                {
                                    Looper.prepare();
                                }
                                if (response.code() == 409) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.duplicate_mail), Toast.LENGTH_LONG).show();
                                } else if (response.code() == 400) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_LONG).show();
                                } else if (response.code() == 200) {
                                    Token token = gson.fromJson(response.body().charStream(), Token.class);
                                    Log.v("token", token.toString());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("token", token.toString()).apply();
                                    editor.putString("name", capitalizeFirstLetter(name.getText().toString()));
                                    editor.putString("email", email.getText().toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.register_ok, Toast.LENGTH_SHORT).show();
                                        }
                                    });
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
    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
