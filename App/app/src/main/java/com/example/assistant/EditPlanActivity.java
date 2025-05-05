package com.example.assistant;

import static com.example.assistant.NewTaskActivity.changeDateFormat;
import static com.example.assistant.TimetableActivity.getJsonFromUrl;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EditPlanActivity extends AppCompatActivity {
    String urlPlan, namePlan, dateFrom, dateTo, description, goalPoints, donePoints;
    int idPlan;
    EditText namePlanET, dateFromET, dateToET,scorePlanET, descriptionET;
    List<ArrayList<String>> categories = new ArrayList<>();
    List<ArrayList<String>> tasks = new ArrayList<>();
    TaskAdapter tasksAdapter;
    PlanCtgAdapter categoriesAdapter;
    RecyclerView tasksRecyclerView, categoriesRecyclerView;
    LinearLayoutManager linearLayoutManagerTask, linearLayoutManagerCtg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plan);

        Bundle bundle = getIntent().getExtras();
        idPlan = (int) bundle.getSerializable("id");
        System.out.println("----- edit plan's id  = " + idPlan);

        //ссылка
        Resources res = getApplicationContext().getResources();
        urlPlan = res.getString(R.string.urlTuna) + "plans/" + idPlan;

        namePlanET = findViewById(R.id.planName);               // Название плана
        dateFromET = findViewById(R.id.dateFromEditText);       // Дата от
        dateToET = findViewById(R.id.dateToEditText);           // Дата до
        scorePlanET = findViewById(R.id.scoreEditText);         // Общая оценка плана
        descriptionET = findViewById(R.id.decrPlanEditText);    // Описание

        backToOption();
        loadPlanData();


    }



    /*
        Загрузка данных редактируемого/просматриваемого плана по его id
     */
    protected void loadPlanData() {
        new Thread(() -> {
            try {
                // ссылка
                String json = getJsonFromUrl(urlPlan);

                if (json != null) {
                    runOnUiThread(() -> {
                        getPlanFromJSON(json);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Ошибка загрузки данных",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e("THREAD_ERROR", "Ошибка в потоке:", e);
            }
        }).start();
    }



    protected void getPlanFromJSON(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);

            namePlan = jsonObject.getString("name");
            dateFrom = jsonObject.getString("start_date");
            dateTo = jsonObject.getString("stop_date");
            description = jsonObject.getString("details");

            goalPoints = jsonObject.getString("goal_points");
            donePoints = jsonObject.getString("done_points");

            //categories
            JSONArray jsonArrayCtg = jsonObject.getJSONArray("categories");
            categories = new ArrayList<>();
            for (int i = 0; i < jsonArrayCtg.length(); i++) {
                JSONObject ctgData = jsonArrayCtg.getJSONObject(i);
                ArrayList<String> oneCtg = new ArrayList<>();
                oneCtg.add(ctgData.getString("title"));
                oneCtg.add(ctgData.getString("color"));
                categories.add(oneCtg);

            }

            //tasks
            JSONArray jsonArrayTasks = jsonObject.getJSONArray("tasks");
            tasks = new ArrayList<>();
            for (int i = 0; i < jsonArrayTasks.length(); i++) {
                JSONObject taskData = jsonArrayTasks.getJSONObject(i);
                ArrayList<String> oneTask = new ArrayList<>();
                oneTask.add(taskData.getString("id"));
                oneTask.add(taskData.getString("name"));
                oneTask.add(taskData.getString("status"));
                tasks.add(oneTask);

            }

            outputPlanDataInForm();  // Отрисовка

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    /*
        Функция заполняющая поля данными выбранного плана
    */
    protected void outputPlanDataInForm() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

        namePlanET.setText(namePlan);
        if (!dateFrom.equals("null")) dateFromET.setText(changeDateFormat(dateFrom, inputFormat, outputFormat));
        if (!dateTo.equals("null")) dateToET.setText(changeDateFormat(dateTo, inputFormat, outputFormat));
        descriptionET.setText(description);
        String score = donePoints + " / " + goalPoints;
        scorePlanET.setText(score);

        //categoriesRecyclerView
        linearLayoutManagerCtg = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRecyclerView = findViewById(R.id.ctgInPlanRV);
        categoriesRecyclerView.setLayoutManager(linearLayoutManagerCtg);
        categoriesAdapter = new PlanCtgAdapter(EditPlanActivity.this, categories);
        categoriesRecyclerView.setAdapter(categoriesAdapter);


        //tasksRecyclerView
        tasksRecyclerView = findViewById(R.id.allTaskInPlanRV);
        linearLayoutManagerTask = new LinearLayoutManager(getApplicationContext());
        tasksRecyclerView.setLayoutManager(linearLayoutManagerTask);
        tasksAdapter = new TaskAdapter(EditPlanActivity.this, tasks, "com.example.assistant.PlansActivity");
        tasksRecyclerView.setAdapter(tasksAdapter);


    }





    /*
        Возвращение назад на экран планов
     */
    protected void backToOption() {
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditPlanActivity.this, PlansActivity.class));
            }
        });
    }
}
