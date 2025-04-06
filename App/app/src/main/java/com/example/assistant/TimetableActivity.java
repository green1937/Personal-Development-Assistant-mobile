package com.example.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
        addNewEvent();           // Сздание нового мероприятия - переход на новый экран


        getTimetableFromJSON();             // Получение расписания из JSON
        outputTimetableToRecyclerView();    // Передача расписания в RecyclerView


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


    private String JsonDataFromAssest(String fileName) {
        String json = null;
        try {
            InputStream inputStream = getAssets().open(fileName);
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, StandardCharsets.UTF_8);

        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;

    }


    /*
        Получение расписания мероприятий из JSON в виде трехмерного массива
        (разбиение на дни недели и четность/нечетность недели)
     */
    protected void getTimetableFromJSON() {

        try {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(JsonDataFromAssest("events_example.json")));
            JSONArray jsonArray = jsonObject.getJSONArray("days");
            for (int i=0; i<jsonArray.length(); i++) {
                // Массив, хранящий мероприятия одного дня (пн, вт, ср, ...) с четной и с нечетной недели
                fullWeek = new ArrayList<>();

                JSONObject weekData = jsonArray.getJSONObject(i);

                addEventInWeek(weekData, "odd_week");  // Нечетная неделя
                fullWeek.add(fullEvent);

                addEventInWeek(weekData, "even_week");  // Четная неделя
                fullWeek.add(fullEvent);

                allEvents.add(fullWeek);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
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
