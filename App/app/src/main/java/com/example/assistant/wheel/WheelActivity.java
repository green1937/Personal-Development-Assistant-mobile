package com.example.assistant.wheel;

import static com.example.assistant.tasks.NewTaskActivity.changeDateFormat;
import static java.lang.Integer.parseInt;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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

import com.example.assistant.MainActivity;
import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.diary.DiaryActivity;
import com.example.assistant.plans.EditPlanActivity;
import com.example.assistant.plans.PlanCtgAdapter;
import com.example.assistant.plans.PlansActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mikephil.charting.charts.RadarChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class WheelActivity extends AppCompatActivity {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    List<ArrayList<String>> allCategories = new ArrayList<>();
    String urlWheel, urlCtg, responseString;

    String startDate, stopDate;
    WheelView wheelView;
    List<WheelView.WheelSector> sectors = new ArrayList<>();
    EditText startDateET, stopDateET;
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        wheelView = findViewById(R.id.wheelView);
        startDateET = findViewById(R.id.dateWheelFrom);
        stopDateET = findViewById(R.id.dateWheelTo);

        Resources res = getResources();
        urlWheel = res.getString(R.string.urlTuna) + "wheel";
        urlCtg = res.getString(R.string.urlTuna) + "categories";

        getInitialDate();

        bottNavItem();  // Нижнее меню

    }

    protected void getInitialDate() {
        LocalDate currentDate = LocalDate.now(); // Получаем текущую дату
        LocalDate sevenDaysAgo = currentDate.minus(7, ChronoUnit.DAYS); // Получаем дату 7 дней назад

        // Форматируем даты
        startDate = sevenDaysAgo.format(formatter);
        stopDate = currentDate.format(formatter);

        // Отправляем в форму
        startDateET.setText(sevenDaysAgo.format(formatter2));
        stopDateET.setText(currentDate.format(formatter2));

        collectindDates();

    }

    protected void anotherDate() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");


        startDate = changeDateFormat(startDateET.getText().toString(), inputFormat, outputFormat);
        stopDate = changeDateFormat(stopDateET.getText().toString(), inputFormat, outputFormat);

        collectindDates();
    }


    protected void collectindDates() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode()
                .put("start_date", startDate)
                .put("end_date", stopDate);

        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(json);
            System.out.println("ПЕРЕДАЕМ ЭТО = " + jsonString);
            sendDataToServer("POST", jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    protected void drawWheel(List<ArrayList<String>> allCategories) {
        LinearLayoutManager linearLayoutManagerCtg = new LinearLayoutManager(getApplicationContext());
        RecyclerView categoriesRecyclerView = findViewById(R.id.ctgRV);
        categoriesRecyclerView.setLayoutManager(linearLayoutManagerCtg);
        WheelCtgAdapter categoriesAdapter = new WheelCtgAdapter(WheelActivity.this, allCategories);
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        sectors = new ArrayList<>();

        for (int i = 0; i < allCategories.size(); i++) {
            if (allCategories.get(i).get(3).equals("1")) {
                String name = allCategories.get(i).get(0);
                int color = Color.parseColor(allCategories.get(i).get(2));
                int point = parseInt(allCategories.get(i).get(1));
                sectors.add(new WheelView.WheelSector(name, point, color));
            }
        }

        wheelView.setWheelData(sectors);
    }



    protected void sendDataToServer(String requestMethod, String jsonData) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // Создаем URL и соединение
                URL serverUrl = new URL(urlWheel);
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
                    // Читаем ответ от сервера
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    // Обрабатываем полученный ответ
                    responseString = response.toString();
                    runOnUiThread(() -> {
                        System.out.println("ОТВЕТ ОТ СЕРВЕРА   " + responseString);
                        getCtgFromResponse(responseString);
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

    protected void getCtgFromResponse(String responseString) {
        System.out.println("ОТВЕТ ОТ СЕРВЕРА   " + responseString);
        allCategories = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject ctgData = jsonArray.getJSONObject(i);
                ArrayList<String> oneCategory = new ArrayList<>();

                oneCategory.add(ctgData.getString("name"));
                oneCategory.add(ctgData.getString("points"));
                oneCategory.add(ctgData.getString("color"));
                oneCategory.add("1");

                allCategories.add(oneCategory);
            }
            drawWheel(allCategories);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }


    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_wheel);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Главная
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            // Планы
            if (item.getItemId() == R.id.bottom_plans) {
                startActivity(new Intent(getApplicationContext(), PlansActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            // Колесо баланса
            if (item.getItemId() == R.id.bottom_wheel) {
                return true;
            }

            // Дневник
            if (item.getItemId() == R.id.bottom_diary) {
                startActivity(new Intent(getApplicationContext(), DiaryActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            // Боковое меню
            if (item.getItemId() == R.id.bottom_mainMenu) {
                startActivity(new Intent(getApplicationContext(), SideMenuActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            return false;
        });
    }


    /*
        Вывод календарей и часов для дат и времени при создании задачи
     */

    public void setDateFrom(View v) {
        new DatePickerDialog(WheelActivity.this, d1, dateFromCld.get(Calendar.YEAR),
                dateFromCld.get(Calendar.MONTH), dateFromCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d1=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFromCld.set(Calendar.YEAR, year);
            dateFromCld.set(Calendar.MONTH, monthOfYear);
            dateFromCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDate(year, monthOfYear+1, dayOfMonth, startDateET);
        }
    };


    public void setDateTo(View v) {
        new DatePickerDialog(WheelActivity.this, d2, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateToCld.set(Calendar.YEAR, year);
            dateToCld.set(Calendar.MONTH, monthOfYear);
            dateToCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDate(year, monthOfYear+1, dayOfMonth, stopDateET);
        }
    };

    public void setInitialDate(int year, int monthOfYear, int dayOfMonth, EditText editDate) {
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
        anotherDate();

    }

}
