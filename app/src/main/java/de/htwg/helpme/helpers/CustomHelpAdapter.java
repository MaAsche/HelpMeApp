package de.htwg.helpme.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.room.Room;

import com.google.gson.Gson;

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

public class CustomHelpAdapter implements ListAdapter {
    ArrayList<TaskRoom> arrayList;
    Context context;
    Activity activity;
    private static AppDatabase db;

    public CustomHelpAdapter(ArrayList<TaskRoom> arrayList, Context context, Activity activity) {
        this.arrayList = arrayList;
        this.context = context;
        this.activity = activity;
        db = Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DB_NAME).fallbackToDestructiveMigration().build();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final TaskRoom task = arrayList.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.taskhelplist, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.category = convertView.findViewById(R.id.categoryHelp);
            viewHolder.date = convertView.findViewById(R.id.dateHelp);
            viewHolder.button = convertView.findViewById(R.id.acceptTaskImageView);
            viewHolder.description = convertView.findViewById(R.id.descriptionTextView);
            convertView.setTag(viewHolder);

            ViewHolder holder = (ViewHolder) convertView.getTag();
            convertView.setBackgroundResource(R.drawable.tasklisthelp);


            final View finalConvertView1 = convertView;
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.LogoutAlert);

                    builder.setMessage(R.string.accept_task_question);

                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            OkHttpClient client = new OkHttpClient();
                            RequestBody body = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("status", "2")
                                    .addFormDataPart("id", String.valueOf(task.getId()))
                                    .build();
                            Request request = new Request.Builder()
                                    .url("http://10.0.2.2:1337/api/task/changestatus")
                                    .post(body)
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    if (Looper.myLooper() == null) {
                                        Looper.prepare();
                                    }
                                    if (response.code() == 200) {
                                        //Log.v("task", response.body().string());
                                        Gson gson = new Gson();
                                        TaskRoom task = gson.fromJson(response.body().charStream(), TaskRoom.class);
                                        new myTask().execute(task);


                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, context.getString(R.string.accepted_task), Toast.LENGTH_SHORT).show();
                                                finalConvertView1.setBackgroundResource(R.drawable.tasklistviewgreen);
                                            }
                                        });
                                    }
                                }
                            });
                        }

                    });

                    builder.setNegativeButton(R.string.no, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });

            holder.category.setText(task.getCategory());
            holder.date.setText(task.getDuedate().substring(0, 10));


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.task_popup);
                    TextView desc = dialog.findViewById(R.id.descriptionTextView);
                    desc.setText(task.getDescription());
                    dialog.show();

                }
            });
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return Math.max(arrayList.size(), 1);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private static class myTask extends AsyncTask<TaskRoom, Void, Void> {
        @Override
        protected Void doInBackground(TaskRoom... tasks) {
            TaskRoom task = tasks[0];
            db.taskDao().insertTask(new TaskRoom(task.getId(), task.getDescription(), task.getCategoryNumber(), task.getDuedate(), 2, 0));
            return null;
        }
    }
}


