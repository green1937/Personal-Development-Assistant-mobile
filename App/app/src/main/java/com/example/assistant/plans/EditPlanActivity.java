package com.example.assistant.plans;

import static com.example.assistant.plans.PlansActivity.checkDateFormat;
import static com.example.assistant.tasks.NewTaskActivity.changeDateFormat;
import static com.example.assistant.timetable.TimetableActivity.getJsonFromUrl;

import static java.lang.Integer.parseInt;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assistant.R;
import com.example.assistant.tasks.TaskAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditPlanActivity extends AppCompatActivity {
    String urlPlan, urlAllPlans, namePlan, dateFrom, dateTo, description, goalPoints, donePoints;
    int idPlan, status;
    EditText namePlanET, dateFromET, dateToET,scorePlanET, descriptionET;
    List<ArrayList<String>> categories = new ArrayList<>();
    List<ArrayList<String>> tasks = new ArrayList<>();
    TaskAdapter tasksAdapter;
    PlanCtgAdapter categoriesAdapter;
    RecyclerView tasksRecyclerView, categoriesRecyclerView;
    LinearLayoutManager linearLayoutManagerTask, linearLayoutManagerCtg;
    ImageButton savePlanBtn;
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();


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
        urlAllPlans = res.getString(R.string.urlTuna) + "plans";

        namePlanET = findViewById(R.id.planName);               // Название плана
        dateFromET = findViewById(R.id.dateFromEditText);       // Дата от
        dateToET = findViewById(R.id.dateToEditText);           // Дата до
        scorePlanET = findViewById(R.id.scoreEditText);         // Общая оценка плана
        descriptionET = findViewById(R.id.decrPlanEditText);    // Описание

        savePlanBtn = findViewById(R.id.tickBtn);

        backToOption();
        loadPlanData();

        saveEditPlan();


    }

    protected void saveEditPlan() {
        savePlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namePlan = namePlanET.getText().toString();
                description = descriptionET.getText().toString();
                dateFrom = dateFromET.getText().toString();
                dateTo = dateToET.getText().toString();


                if ( namePlan.equals("") || dateFrom.equals("") || dateTo.equals("") ) {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения изменений! Поля не могут быть пустыми!", Toast.LENGTH_SHORT).show();
                }


                else {
                    if ( checkDateFormat(dateFrom) == 1 || checkDateFormat(dateTo) == 1 ){
                        Toast.makeText(getApplicationContext(), "Ошибка сохранения!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if ((dateFrom.length() != 10 &&  checkDateFormat(dateFrom) == 0)
                                || (dateTo.length() != 10 && checkDateFormat(dateTo) == 0)) {
                            Toast.makeText(getApplicationContext(),
                                    "Ошибка! Формат даты должен быть ДД.ММ.ГГГГ",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Если введены были обе даты
                            if (checkDateFormat(dateFrom) == 0 && checkDateFormat(dateTo) == 0) {
                                DateTimeFormatter dtfDATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());
                                LocalDate dateFromLD = LocalDate.parse(dateFrom, dtfDATE);
                                LocalDate dateToLD = LocalDate.parse(dateTo, dtfDATE);

                                // Проверка на то, что дата ОТ наступает раньше даты ДО
                                if (dateFromLD.isAfter(dateToLD)) {
                                    Toast.makeText(getApplicationContext(),
                                            "Ошибка сохранения! Дата ОТ должна наступать раньше даты ДО!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    savePlan();
                                    startActivity(new Intent(EditPlanActivity.this, PlansActivity.class));
                                }
                            } else {
                                savePlan();
                                startActivity(new Intent(EditPlanActivity.this, PlansActivity.class));
                            }
                        }


                    }
                }

            }
        });
    }

    protected void savePlan() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (!dateFrom.equals("null")) dateFrom = changeDateFormat(dateFrom, inputFormat, outputFormat);
        if (!dateTo.equals("null")) dateTo = changeDateFormat(dateTo, inputFormat, outputFormat);

        Plan editPlan = new Plan(idPlan, 1, namePlan, description, dateFrom, dateTo, status);
        String jsonData = new Gson().toJson(editPlan);
        sendDataToServer("PUT", jsonData);
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
            status = parseInt(jsonObject.getString("status"));

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
        tasksAdapter = new TaskAdapter(EditPlanActivity.this, tasks, "com.example.assistant.plans.PlansActivity");
        tasksRecyclerView.setAdapter(tasksAdapter);


    }

    protected void sendDataToServer(String requestMethod, String jsonData) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // Создаем URL и соединение
                URL serverUrl = new URL(urlAllPlans);
                connection = (HttpURLConnection) serverUrl.openConnection();

                // Настраиваем запрос
                connection.setRequestMethod(requestMethod);
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
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        //Toast.makeText(getApplicationContext(), "Данные успешно отправлены", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Ошибка отправки данных: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (IOException e) {
                Log.e("SEND_ERROR", "Ошибка при отправке данных", e);
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Произошла ошибка при отправке данных", Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
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


    /*
        Вывод календарей и часов для дат и времени при создании задачи
     */

    public void setDateFrom(View v) {
        new DatePickerDialog(EditPlanActivity.this, d1, dateFromCld.get(Calendar.YEAR),
                dateFromCld.get(Calendar.MONTH), dateFromCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d1=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFromCld.set(Calendar.YEAR, year);
            dateFromCld.set(Calendar.MONTH, monthOfYear);
            dateFromCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDate(year, monthOfYear+1, dayOfMonth, dateFromET);
        }
    };


    public void setDateTo(View v) {
        new DatePickerDialog(EditPlanActivity.this, d2, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateToCld.set(Calendar.YEAR, year);
            dateToCld.set(Calendar.MONTH, monthOfYear);
            dateToCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDate(year, monthOfYear+1, dayOfMonth, dateToET);
        }
    };

    public static void setInitialDate(int year, int monthOfYear, int dayOfMonth, EditText editDate) {
        String dateForEndStr;
        if (dayOfMonth < 10 && monthOfYear < 10) {
            dateForEndStr = "0" + dayOfMonth + "." + "0" + monthOfYear + "." + year;
        }
        else {
            if (dayOfMonth > 9 && monthOfYear < 10) {
                dateForEndStr = dayOfMonth + "." + "0" + monthOfYear + "." + year;
            } else if (dayOfMonth < 10 && monthOfYear > 9) {
                dateForEndStr = "0" + dayOfMonth + "." + monthOfYear + "." + year;
            } else {
                dateForEndStr = dayOfMonth + "." + monthOfYear + "." + year;
            }
        }

        editDate.setText(dateForEndStr);
    }

}
