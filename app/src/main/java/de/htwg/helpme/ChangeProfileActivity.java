package de.htwg.helpme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;

import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.htwg.helpme.helpers.ClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeProfileActivity extends AppCompatActivity {

    private ImageView profile;
    private ImageView message;
    private ImageView home;
    private Button cancel;
    private Button save;
    private EditText name;
    private EditText phone;
    private EditText email;
    private ImageView image;
    private SharedPreferences prefs;
    private OkHttpClient client;
    public static final int PICK_IMAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    String imageBase64;


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    boolean isPhoneValid(CharSequence phone) {
        return (Patterns.PHONE.matcher(phone).matches() && phone.length() < 16);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new OkHttpClient();
        home = findViewById(R.id.homeImage);
        message = findViewById(R.id.messageImage);
        profile = findViewById(R.id.profileImage);

        home.setOnClickListener(new ClickListener(getApplicationContext(), this));
        profile.setOnClickListener(new ClickListener(getApplicationContext(), this));
        message.setOnClickListener(new ClickListener(getApplicationContext(), this));

        cancel = findViewById(R.id.cancleButton);
        save = findViewById(R.id.saveButton);

        name = findViewById(R.id.nameEditText);
        phone = findViewById(R.id.phoneEditText);
        email = findViewById(R.id.mailEditText);
        image = findViewById(R.id.profileImageView);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        name.setText(prefs.getString("name", ""));
        phone.setText(prefs.getString("phone", ""));
        email.setText(prefs.getString("email", ""));
        imageBase64 = prefs.getString("profileImage", null);
        if (imageBase64 != null) {
            if (imageBase64.equals("")) {
                image.setImageResource(R.drawable.ic_default_profile);
            } else {
                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                image.setImageBitmap(decodedByte);
            }
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (saveProfile()) {
                    finish();
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.LogoutAlert);

        builder.setTitle(R.string.profile_title);
        builder.setMessage(R.string.profile_question);

        builder.setPositiveButton(R.string.chooseImage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                imageFromGallery();
            }
        });
        builder.setNegativeButton(R.string.makeImage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openCamera();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }

    }

    private void imageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_pic)), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                image.setImageURI(data.getData());
            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);

        }
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


    private boolean saveProfile() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ((BitmapDrawable) image.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] bArray = bos.toByteArray();

        SharedPreferences.Editor editor = prefs.edit();
        if (!isEmailValid(email.getText().toString())) {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_mail), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isPhoneValid(phone.getText().toString()) && !phone.getText().toString().equals("") && !phone.getText().toString().equals("0")) {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_phone), Toast.LENGTH_SHORT).show();
            return false;
        }
        editor.putString("name", capitalizeFirstLetter(name.getText().toString())).apply();
        editor.putString("phone", phone.getText().toString()).apply();
        editor.putString("email", email.getText().toString()).apply();
        String tmp = Base64.encodeToString(bArray, Base64.DEFAULT);
        editor.putString("profileImage", tmp).apply();


        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", capitalizeFirstLetter(name.getText().toString()))
                .addFormDataPart("phone", phone.getText().toString())
                .addFormDataPart("email", email.getText().toString())
                .addFormDataPart("token", Objects.requireNonNull(prefs.getString("token", null)))
                .addFormDataPart("image", Base64.encodeToString(bArray, Base64.DEFAULT))
                .build();

        final Request request = new Request.Builder()
                .url(getString(R.string.server_url) + "api/user/changeprofile")
                .post(body)
                .build();

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.code() == 200) {
                                Toast.makeText(getApplicationContext(), getString(R.string.change_profile_ok), Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 400) {
                                Toast.makeText(getApplicationContext(), getString(R.string.change_profile_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;


    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
