package de.htwg.helpme.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.widget.TextView;



import androidx.appcompat.app.AlertDialog;
import androidx.room.Room;

import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.util.ArrayList;



import de.htwg.helpme.R;

import de.htwg.helpme.room.AppDatabase;
import de.htwg.helpme.datatypes.TaskRoom;
import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CustomAdapter extends ArrayAdapter<TaskRoom> {
    private ArrayList<TaskRoom> arrayList;
    private final Activity activity;
    private final int screen;
    private static AppDatabase db;
    private CustomAdapter adapter;


    public CustomAdapter(Activity activity, int screen, ArrayList<TaskRoom> arrayList) {
        super(activity, screen, arrayList);
        this.arrayList = arrayList;
        this.screen = screen;
        this.activity = activity;
        this.adapter = this;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TaskRoom task = arrayList.get(position);
        db = Room.databaseBuilder(activity, AppDatabase.class, AppDatabase.DB_NAME).fallbackToDestructiveMigration().build();
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            convertView = layoutInflater.inflate(R.layout.tasklist, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.description = convertView.findViewById(R.id.descriptionTextView);
            viewHolder.date = convertView.findViewById(R.id.date);
            viewHolder.category = convertView.findViewById(R.id.category);
            viewHolder.button = convertView.findViewById(R.id.closeImageView);
            convertView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        if (task.getStatus() == 1 || task.getStatus() == 3) {
            convertView.findViewById(R.id.closeImageView).setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.task_popup);
                TextView desc = dialog.findViewById(R.id.descriptionTextView);
                desc.setText(task.getDescription());
                dialog.show();

            }
        });
        if (screen == 0) {
            final View finalConvertView = convertView;
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.LogoutAlert);
                    builder.setMessage(R.string.close_task_question);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new updateId().execute(task.getId());
                            finalConvertView.setBackgroundResource(R.drawable.tasklistviewgreen);
                            finalConvertView.findViewById(R.id.closeImageView).setVisibility(View.GONE);
                            changeStatus(1, task);
                        }

                    });
                    builder.setNegativeButton(R.string.no, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            switch (task.getStatus()) {
                case 0:
                    convertView.setBackgroundResource(R.drawable.tasklistviewyellow);
                    break;
                case 1:
                    convertView.setBackgroundResource(R.drawable.tasklistviewgreen);
                    break;
                case 2:
                    convertView.setBackgroundResource(R.drawable.tasklisthelp);
                    break;
                case 3:
                    convertView.setBackgroundResource(R.drawable.tasklistviewred);
                    break;
            }


        } else if (screen == 1) {
            convertView.setBackgroundResource(R.drawable.tasklisthelp);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.LogoutAlert);
                    builder.setMessage(R.string.dismiss_task);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            changeStatus(0, task);
                            new removeTask().execute(task.getId());
                            Log.v("arrayList", arrayList.size() + " - " + position);
                            adapter.remove(task);
                        }
                    });
                    builder.setNegativeButton(R.string.no, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

        }

        holder.category.setText(task.getCategory());
        Log.v("date", task.getDuedate());
        holder.date.setText(task.getDuedate().substring(0, 10));


        return convertView;
    }


    public String changeStatus(int status, final TaskRoom task) {


        new updateId().execute(task.getId());
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("status", String.valueOf(status))
                .addFormDataPart("id", String.valueOf(task.getId()))
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:1337/api/task/changestatus")
                .post(body)
                .build();

        final String[] res = new String[1];


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    res[0] = response.body().string();
                }
            }
        });
        return res[0];

    }

    ;

    static class updateId extends AsyncTask<Integer, Context, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            db.taskDao().updateStatus(1, integers[0]);
            return null;
        }
    }

    public void remove(int position) {
        arrayList.remove(position);
        this.notifyDataSetChanged();
    }

    static class removeTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            db.taskDao().deleteId(params[0]);
            return null;
        }
    }
}

class ViewHolder {
    TextView category;
    TextView date;
    TextView description;
    ImageView button;

}
