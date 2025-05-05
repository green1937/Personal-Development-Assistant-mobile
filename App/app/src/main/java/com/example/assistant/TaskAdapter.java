package com.example.assistant;

import static com.example.assistant.TimetableAdapter.deleteFromUrl;
import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    int flag = 3;
    private final int TYPE_ITEM1 = 0;
    private final int TYPE_ITEM2 = 1;

    Context context;
    List<ArrayList<String>> taskData;
    String activityName;

    public TaskAdapter(Context context, List<ArrayList<String>> taskData, String activityName) {
        this.context = context;
        this.taskData = taskData;
        this.activityName = activityName;

    }

    // Добавляем интерфейс для обработки изменений
    public interface OnTaskStatusChangeListener {
        void onStatusChanged(int position, boolean isChecked);
    }

    private OnTaskStatusChangeListener listener;

    public void setOnTaskStatusChangeListener(OnTaskStatusChangeListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case TYPE_ITEM1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_text_view, parent, false);
                break;
            case TYPE_ITEM2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_view, parent, false);
        }
        return new TaskAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int type = getItemViewType(position);

        switch (type) {

            case TYPE_ITEM1:
                holder.tasksGroupTextOutput.setText(taskData.get(position).get(0)); //День недели
                break;

            case TYPE_ITEM2:
                System.out.println(taskData.get(position).get(0) + " " + taskData.get(position).get(1) + "  " + taskData.get(position).get(2));
                int idTask = parseInt(taskData.get(position).get(0));
                String nameTask = taskData.get(position).get(1);

                Resources res = context.getResources();
                String urlTasksId = res.getString(R.string.urlTuna) + "tasks/" + idTask;


                if (taskData.get(position).get(2).equals("1")) {
                    holder.taskStatus.setChecked(true);
                }
                holder.taskNameTextOutput.setText(nameTask);

                // Добавляем обработчик для CheckBox
                holder.taskStatus.setOnClickListener(v -> {
                    if (listener != null) {
                        boolean isChecked = holder.taskStatus.isChecked();
                        listener.onStatusChanged(position, isChecked);
                    }
                });

                /*
                    Смена статуса у задачи (отображение чекбокс),
                    отправка на сервер нового статуса
                 */
                flag = 3;  // Для статуса
                holder.taskStatus.setOnClickListener(v -> {
                    if (taskData.get(position).get(2).equals("0") && holder.taskStatus.isChecked()) {
                        flag = 1;
                    }
                    if (taskData.get(position).get(2).equals("1") && !holder.taskStatus.isChecked()) {
                        flag = 0;
                    }

                    if (flag==0 || flag==1) {

                        ObjectMapper mapper = new ObjectMapper();
                        ArrayNode array = mapper.createArrayNode();
                        ObjectNode json = mapper.createObjectNode()
                                .put("op", "replace")
                                .put("path", "/status")
                                .put("value", flag);
                        array.add(json);

                        String jsonString = null;
                        try {
                            jsonString = mapper.writeValueAsString(array);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println(jsonString); // Выводим сформированный JSON.

                        sendUpdateObjectStatusToServer(context, urlTasksId, jsonString, activityName);

                        //update page
                        /*
                        try {
                            Class<?> activityClass = Class.forName(activityName);
                            Intent intent = new Intent(context, activityClass);
                            context.startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                         */

                    }
                });


                /*
                    Удаление задачи по id
                 */
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        PopupMenu menu = new PopupMenu(context, v);
                        menu.getMenu().add("Удалить");
                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().equals("Удалить")) {

                                    // Запускаем удаление в отдельном потоке
                                    new Thread(() -> {
                                        try {
                                            String result = deleteFromUrl(urlTasksId);

                                            if (result != null && result.equals("SUCCESS")) {
                                                //update page
                                                Intent intent2 = new Intent(context, Class.forName(activityName));
                                                context.startActivity(intent2);
                                            } else {
                                                Toast.makeText(context, "Ошибка удаления!", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Log.e("THREAD_ERROR", "Ошибка в потоке:", e);
                                        }
                                    }).start();
                                }
                                return true;
                            }
                        });
                        menu.show();
                        return true;
                    }
                });

                /*
                    Переход на экран редактирвоания и просмотра всей задачи с полями:название, категория, дата, время, описание и прочее.
                */
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, EditTaskActivity.class);
                        intent.putExtra("id", idTask);  // Отправка id задачи в активити редактирования и просмотра содержимого задачи
                        context.startActivity(intent);
                    }
                });
                break;
        }



    }



    public void updateAdapter(List<ArrayList<String>> data){
        this.taskData.clear();
        this.taskData.addAll(data);
        notifyDataSetChanged();
    }



    static void sendUpdateObjectStatusToServer(Context context, String url, String jsonData, String activityName) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // Создаем URL и соединение
                URL serverUrl = new URL(url);
                connection = (HttpURLConnection) serverUrl.openConnection();

                // Настраиваем запрос
                connection.setRequestMethod("PATCH");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("tuna-skip-browser-warning", "true");

                // Отправляем JSON данные

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }


                // Получаем ответ
                int responseCode = connection.getResponseCode();
                System.out.println("response CODE update status  " + responseCode);
                if (responseCode == 200) {
                    Intent intent2 = new Intent(context, Class.forName(activityName));
                    context.startActivity(intent2);
                }

            } catch (IOException | ClassNotFoundException e) {
                Log.e("SEND_ERROR", "Ошибка при отправке данных", e);
                Toast.makeText(context, "Произошла ошибка при отправке данных", Toast.LENGTH_SHORT).show();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return taskData.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (taskData.get(position).size() == 1) {
            return TYPE_ITEM1;
        }
        else {
            return TYPE_ITEM2;
        }

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tasksGroupTextOutput;
        TextView taskNameTextOutput;

        CheckBox taskStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tasksGroupTextOutput = itemView.findViewById(R.id.tasksGroupTextOut);
            taskNameTextOutput = itemView.findViewById(R.id.taskNameTextOut);
            taskStatus = itemView.findViewById(R.id.checkBoxTask);
        }

    }

}
