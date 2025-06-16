/*
    Адаптер для работы с расписанием мероприятий на главном экране
 */

package com.example.assistant.ui.timetable.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.assistant.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.MyViewTimetableHolder> {
    
    private final int TYPE_ITEM1 = 0;
    private final int TYPE_ITEM2 = 1;

    Context context;

    List<ArrayList<String>> eventData;
    String token;

    public TimetableAdapter(Context context, List<ArrayList<String>> eventData, String token) {
        this.context = context;
        this.eventData = eventData;
        this.token = token;
    }

    @NonNull
    @Override
    public TimetableAdapter.MyViewTimetableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case TYPE_ITEM1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_text_view, parent, false);
                break;
            case TYPE_ITEM2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_item_view, parent, false);
        }
        return new TimetableAdapter.MyViewTimetableHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewTimetableHolder holder, @SuppressLint("RecyclerView") int position) {
        int type = getItemViewType(position);

        switch (type) {

            case TYPE_ITEM1:
                holder.dayOfWeekTextOutput.setText(eventData.get(position).get(0)); //День недели
                break;

            case TYPE_ITEM2:
                holder.taskEventTextOutput.setText(eventData.get(position).get(1));
                holder.startTimeEventTextOutput.setText(eventData.get(position).get(2));
                holder.stopTimeEventTextOutput.setText(eventData.get(position).get(3));
                holder.placeEventTextOutput.setText(eventData.get(position).get(4));
                holder.formatEventTextOutput.setText(eventData.get(position).get(5));
                break;
        }
        if (type == 1) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    PopupMenu menu = new PopupMenu(context, v);
                    menu.getMenu().add("Удалить");
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().equals("Удалить")) {
                                // удаление мероприятия
                                Resources res = context.getResources();
                                String urlString = res.getString(R.string.urlTuna) + "events/" + eventData.get(position).get(0);

                                // Тут удаление по ссылке через Tuna
                                // Запускаем удаление в отдельном потоке
                                new Thread(() -> {
                                    try {
                                        String result = deleteFromUrl(urlString, token);

                                        if (result != null && result.equals("SUCCESS")) {
                                            Toast.makeText(context, "Мероприятие удалено", Toast.LENGTH_SHORT).show();
                                            //update page
                                            Intent intent = new Intent(context, TimetableActivity.class);
                                            context.startActivity(intent);
                                        } else {
                                            Toast.makeText(context, "Ошибка удаления объекта", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Log.e("THREAD_ERROR", "Ошибка в потоке:", e);
                                    }
                                }).start();

                                Toast.makeText(context, "Мероприятие удалено", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    menu.show();
                    return true;
                }
            });

        }

    }


    public static String deleteFromUrl(String urlString, String token) {
        String result = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");

            // Заголовок для обхода tuna browser warning
            urlConnection.setRequestProperty("tuna-skip-browser-warning", "true");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);

            urlConnection.setDoOutput(true);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                result = "SUCCESS";
            }

        } catch (IOException e) {
            Log.e("DEBUG", "IOException при удалении", e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return result;
    }


    @Override
    public int getItemCount() {
        return eventData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (eventData.get(position).size() == 1) {
            return TYPE_ITEM1;
        }
        else {
            return TYPE_ITEM2;
        }

    }
    public class MyViewTimetableHolder extends RecyclerView.ViewHolder {
        TextView dayOfWeekTextOutput;
        TextView taskEventTextOutput;
        TextView startTimeEventTextOutput;
        TextView stopTimeEventTextOutput;
        TextView placeEventTextOutput;
        TextView formatEventTextOutput;

        public MyViewTimetableHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeekTextOutput = itemView.findViewById(R.id.dayOfWeekTextOut);
            taskEventTextOutput = itemView.findViewById(R.id.nameEventTextOut);
            startTimeEventTextOutput = itemView.findViewById(R.id.startTimeEventTextOut);
            stopTimeEventTextOutput = itemView.findViewById(R.id.stopTimeEventTextOut);
            placeEventTextOutput = itemView.findViewById(R.id.placeEventTextOut);
            formatEventTextOutput = itemView.findViewById(R.id.formatEventTextOut);
        }

    }

}
