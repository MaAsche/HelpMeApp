package de.htwg.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

import de.htwg.helpme.datatypes.TaskList;
import de.htwg.helpme.helpers.ClickListener;
import de.htwg.helpme.helpers.CustomHelpAdapter;
import de.htwg.helpme.datatypes.TaskRoom;

public class ShowHelpTasks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_help_tasks);


        findViewById(R.id.homeImage).setOnClickListener(new ClickListener(getApplicationContext(), this));
        findViewById(R.id.messageImage).setOnClickListener(new ClickListener(getApplicationContext(), this));
        findViewById(R.id.profileImage).setOnClickListener(new ClickListener(getApplicationContext(), this));

        final ListView listView = findViewById(R.id.tasksHelpListView);
        String tmp = getIntent().getStringExtra("tasks");

        Gson gson = new Gson();
        TaskList tasks = gson.fromJson(tmp, TaskList.class);

        ArrayList<TaskRoom> arrayList = new ArrayList<>();
        if (tasks.getTasks() != null) {
            arrayList.addAll(tasks.getTasks());
        }


        final CustomHelpAdapter customhelpAdapter = new CustomHelpAdapter(arrayList, ShowHelpTasks.this, this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(customhelpAdapter);
            }
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View empty = findViewById(R.id.listEmpty);
        ListView tmp = findViewById(R.id.tasksHelpListView);
        tmp.setEmptyView(empty);
    }
}