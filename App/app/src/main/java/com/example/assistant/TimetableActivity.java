package com.example.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TimetableActivity extends AppCompatActivity {
    TextView textParameterOddEvenWeek;
    List<ArrayList<String>> fullEvent = new ArrayList<>();
    List<List<ArrayList<String>>> fullWeek = new ArrayList<>();
    List<List<List<ArrayList<String>>>>  allEvents = new ArrayList<>();


    RecyclerView timetableRecyclerView;
    LinearLayoutManager linearLayoutManager;
    TimetableAdapter timetableAdapter;

    int[] nameOfRecyclerView = { R.id.recycler_view_timetable_monday,
            R.id.recycler_view_timetable_tuesday, R.id.recycler_view_timetable_wednesday,
            R.id.recycler_view_timetable_thursday, R.id.recycler_view_timetable_friday,
            R.id.recycler_view_timetable_saturday, R.id.recycler_view_timetable_sunday };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        // Кнопка определения того, какая неделя отображается (нечетная, четная)
        textParameterOddEvenWeek = findViewById(R.id.textLL);


        bottNavItem();           // Нижнее меню
        backToSideMenu();        // Возвращение назад
        addNewEvent();           // Создание нового мероприятия - переход на новый экран

        loadJsonFromUrl();       // Получение расписания

    }


    private void loadJsonFromUrl() {
        new Thread(() -> {
            try {
                // ссылка меняется
                String url = "https://mald3m-217-144-175-34.ru.tuna.am/assistant/api/events";

                String json = getJsonFromUrl(url);

                if (json != null) {
                    runOnUiThread(() -> {
                        getTimetableFromJSON(json);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e("THREAD_ERROR", "Ошибка в потоке:", e);
            }
        }).start();
    }


    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //bottomNavigationView.setSelectedItemId(R.id.bottom_wheel);

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
                startActivity(new Intent(getApplicationContext(), WheelActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
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


    protected void backToSideMenu() {
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SideMenuActivity.class));
            }
        });
    }

    /*
        Создание нового мероприятия
     */
    protected void addNewEvent() {
        ImageButton addBtn = findViewById(R.id.addEventBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewEventActivity.class));
            }
        });
    }


    /// получение данных из джейсон
    private String getJsonFromUrl(String urlString) {
        String json = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // если нужен заголовок для обхода tuna browser warning - не работает все равно нужно нажимать на галочку у новой ссылки
            urlConnection.setRequestProperty("tuna-skip-browser-warning", "true");

            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (inputStream == null) {
                Log.d("DEBUG", "inputStream == null");
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            if (buffer.length() == 0) return null;
            json = buffer.toString();

        } catch (IOException e) {
            Log.e("DEBUG", "IOException при получении JSON", e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("DEBUG", "Ошибка закрытия reader", e);
                }
            }
        }
        return json;
    }


    /*
        Получение расписания мероприятий из JSON в виде трехмерного массива
        (разбиение на дни недели и четность/нечетность недели)
     */
    protected void getTimetableFromJSON(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("days");
            for (int i = 0; i < jsonArray.length(); i++) {
                fullWeek = new ArrayList<>();

                JSONObject weekData = jsonArray.getJSONObject(i);

                addEventInWeek(weekData, "odd_week");  // Нечетная неделя
                fullWeek.add(fullEvent);

                addEventInWeek(weekData, "even_week");  // Четная неделя
                fullWeek.add(fullEvent);

                allEvents.add(fullWeek);
            }
            outputTimetableToRecyclerView();    // Передача расписания в RecyclerView
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("DEBUG", "ошибка в getTimetableFromJSON: " + json);
        }
    }


    /*
        Получение в массив данных каждого мероприятия нечетной/четной недели
     */
    protected List<ArrayList<String>> addEventInWeek(JSONObject weekData, String weekName) throws JSONException {
        fullEvent = new ArrayList();

        if (!weekData.get(weekName).toString().equals("null")) {

            JSONArray jsonArray2 = (JSONArray) weekData.get(weekName);


            for (int j = 0; j < jsonArray2.length(); j++) {
                ArrayList<String> eventExample = new ArrayList<>();
                JSONObject eventExampleData = jsonArray2.getJSONObject(j);
                //eventExample.add(eventExampleData.getString("id"));
                eventExample.add(eventExampleData.getString("name"));
                eventExample.add(eventExampleData.getString("start_time"));
                eventExample.add(eventExampleData.getString("stop_time"));


                String placeEvent = eventExampleData.getString("place");
                if (placeEvent.length() > 5 && placeEvent.substring(0,4).equals("http")) {
                    placeEvent = "ссылка на мероприятие";
                }

                eventExample.add(placeEvent);
                eventExample.add(eventExampleData.getString("format"));
                fullEvent.add(eventExample);
            }
        }

        return fullEvent;
    }


    /*
        Функция отвечает за передачу данных в RecyclerView и
        отображение в зависимости от выбора недели (нечетной/четной)
     */
    protected void outputTimetableToRecyclerView() {

        cycleByWeek(0);

        // При нажатии на кнопку меняется ее текста "четная <-> нечетная"

        textParameterOddEvenWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textParameterOddEvenWeek.getText().equals("Нечетная неделя")) {
                    textParameterOddEvenWeek.setText("Четная неделя");  // Смена текста
                    cycleByWeek(1);
                }
                else {
                    textParameterOddEvenWeek.setText("Нечетная неделя");
                    cycleByWeek(0);
                }
            }
        });
    }


    /*
        Цикл по всем дням недели (пн, вт, ср ...) отдельно для нечетной/четной
     */
    protected void cycleByWeek(int param) {
        for (int i=0; i<7; i++) {
            List<ArrayList<String>> eventData = allEvents.get(i).get(param);

            timetableRecyclerView = findViewById(nameOfRecyclerView[i]);
            linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            timetableRecyclerView.setLayoutManager(linearLayoutManager);

            timetableAdapter = new TimetableAdapter(TimetableActivity.this, eventData);
            timetableRecyclerView.setAdapter(timetableAdapter);

        }
    }

}
