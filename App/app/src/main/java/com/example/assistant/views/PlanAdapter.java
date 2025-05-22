/*
    Адаптер для работы с планами
 */

package com.example.assistant.views;
import static com.example.assistant.viewmodel.TimetableAdapter.deleteFromUrl;
import static com.example.assistant.tasks.TaskAdapter.sendUpdateObjectStatusToServer;


import static java.lang.Integer.parseInt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assistant.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.MyViewPlanHolder> {

    int flag = 3;
    private final int TYPE_ITEM1 = 0;
    private final int TYPE_ITEM2 = 1;

    Context context;

    List<ArrayList<String>> planData;
    String activityName;

    public PlanAdapter(Context context, List<ArrayList<String>> planData, String activityName) {
        this.context = context;
        this.planData = planData;
        this.activityName = activityName;
    }


    // Добавляем интерфейс для обработки изменений
    public interface OnPlanStatusChangeListener {
        void onStatusChanged(int position, boolean isChecked);
    }

    private PlanAdapter.OnPlanStatusChangeListener listener;

    public void setOnPlanStatusChangeListener(PlanAdapter.OnPlanStatusChangeListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public PlanAdapter.MyViewPlanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case TYPE_ITEM1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plans_text_view, parent, false);
                break;
            case TYPE_ITEM2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plans_item_view, parent, false);
        }
        return new PlanAdapter.MyViewPlanHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewPlanHolder holder, @SuppressLint("RecyclerView") int position) {
        int type = getItemViewType(position);

        switch (type) {

            case TYPE_ITEM1:
                holder.planGroupTextOutput.setText(planData.get(position).get(0)); //День недели
                break;

            case TYPE_ITEM2:
                    int idPlan = parseInt(planData.get(position).get(0));
                    String namePlan = planData.get(position).get(1);

                    Resources res = context.getResources();
                    String urlPlanId = res.getString(R.string.urlTuna) + "plans/" + idPlan;
                    System.out.println("url Plan  =  " + urlPlanId);

                    if (planData.get(position).get(2).equals("1")) {
                        holder.planStatus.setChecked(true);
                    }
                    holder.planNameTextOutput.setText(namePlan);

                    // Добавляем обработчик для CheckBox
                    holder.planStatus.setOnClickListener(v -> {
                        if (listener != null) {
                            boolean isChecked = holder.planStatus.isChecked();
                            listener.onStatusChanged(position, isChecked);
                        }
                    });

                /*
                    Смена статуса у плана (отображение чекбокс),
                    отправка на сервер нового статуса
                 */
                    flag = 3;  // Для статуса
                    holder.planStatus.setOnClickListener(v -> {
                        if (planData.get(position).get(2).equals("0") && holder.planStatus.isChecked()) {
                            flag = 1;
                        }
                        if (planData.get(position).get(2).equals("1") && !holder.planStatus.isChecked()) {
                            flag = 0;
                        }

                        if (flag == 0 || flag == 1) {

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

                            sendUpdateObjectStatusToServer(context, urlPlanId, jsonString, activityName);
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
                                                String result = deleteFromUrl(urlPlanId);

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
                    Переход на экран редактирвоания и просмотра всего плана
                */
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, EditPlanActivity.class);
                        intent.putExtra("id", idPlan);  // Отправка id плана в активити редактирования и просмотра содержимого плана
                        context.startActivity(intent);
                    }
                });
                break;
        }

    }



    @Override
    public int getItemCount() {
        return planData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (planData.get(position).size() == 1) {
            return TYPE_ITEM1;
        }
        else {
            return TYPE_ITEM2;
        }

    }
    public class MyViewPlanHolder extends RecyclerView.ViewHolder {
        TextView planGroupTextOutput;
        TextView planNameTextOutput;
        CheckBox planStatus;

        public MyViewPlanHolder(@NonNull View itemView) {
            super(itemView);
            planGroupTextOutput = itemView.findViewById(R.id.planGroupTextOut);
            planNameTextOutput = itemView.findViewById(R.id.planNameText);
            planStatus = itemView.findViewById(R.id.checkBoxPlan);
        }

    }

}
