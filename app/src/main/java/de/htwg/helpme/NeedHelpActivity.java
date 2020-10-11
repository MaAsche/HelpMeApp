package de.htwg.helpme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;

import de.htwg.helpme.helpers.ClickListener;

public class NeedHelpActivity extends AppCompatActivity {

    private Spinner category;
    private TextInputLayout description;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_help);
        category = findViewById(R.id.categorySpinner);
        description = findViewById(R.id.descriptionInput);
        next = findViewById(R.id.continueButton);

        findViewById(R.id.homeImage).setOnClickListener(new ClickListener(getApplicationContext(), this));
        findViewById(R.id.messageImage).setOnClickListener(new ClickListener(getApplicationContext(), this));
        findViewById(R.id.profileImage).setOnClickListener(new ClickListener(getApplicationContext(), this));
        nextPage();


    }

    private void nextPage(){
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NeedHelp2Activity.class);
                intent.putExtra("category", category.getSelectedItem().toString());
                intent.putExtra("description", description.getEditText().getText().toString());
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0){
            finish();
        }
    }
}
