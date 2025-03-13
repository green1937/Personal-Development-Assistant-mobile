package com.example.assistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.assistant.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    DateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    TextView dateTextView, notesBtn, timetableBtn;
    EditText notesEditText;
    LinearLayout timetableView;

    String dateCurrStr, anotherDateStr;
    String itemDay;
    String[] days = { "Сегодня", "Завтра", "Вчера"};

    RecyclerView taskRecyclerView, taskRecyclerView2;
    ArrayList<String> nameTaskExample = new ArrayList<>();
    TaskAdapter taskAdapter;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottNavItem();  // Нижнее меню

        dateTextView = findViewById(R.id.dateText);  // вывод даты текущей (либо выбранной)


        showSpinnerDays();  // Выпадающий список дней

        showHiddenElements();  // Показ скрытых элементов (расписание занятий, заметка)

        addTask(); // Добавление задачи


        taskRecyclerView = findViewById(R.id.forDayLIST);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        taskRecyclerView.setLayoutManager(linearLayoutManager);

        try {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(JsonTaskDataFromAssest("tasks_example.json")));
            JSONArray jsonArray = jsonObject.getJSONArray("tasks");
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject taskExampleData = jsonArray.getJSONObject(i);
                nameTaskExample.add(taskExampleData.getString("name"));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        taskAdapter = new TaskAdapter(MainActivity.this, nameTaskExample);
        taskRecyclerView.setAdapter(taskAdapter);




        taskRecyclerView2 = findViewById(R.id.overdieLIST);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        taskRecyclerView2.setLayoutManager(linearLayoutManager);

        taskAdapter = new TaskAdapter(MainActivity.this, nameTaskExample);
        taskRecyclerView2.setAdapter(taskAdapter);

    }

    private String JsonTaskDataFromAssest(String fileName) {
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
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Главная
            if (item.getItemId() == R.id.bottom_home) {
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



    /*
        Определение текущей даты в формате ДЕНЬ.МЕСЯЦ.ГОД.
    */
    protected static String getCurrDate(DateFormat formatForDate, String dateCurrStr) {
        Date currDate = new Date();
        dateCurrStr = formatForDate.format(currDate);
        return dateCurrStr;
    }


    /*
        Получение даты отличной на некоторое кол-во дней от текущей даты в формате ДЕНЬ.МЕСЯЦ.ГОД.
        Например, для "завтра" - это плюс один день, а для "вчера" - это минус один день
    */
    protected String getAnotherDate(int a) {

        try {
            Date date = formatForDate.parse(dateCurrStr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, a);  // number of days to add
            date = c.getTime();
            anotherDateStr = formatForDate.format(date);
            return anotherDateStr;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    /*
        Выпадающий список с днями "сегодня", "завтра", "вчера".
        Также меняет дату в зависимости от выбранного дня.
        Например, если "сегодня", то отобразится текущая дата.
    */
    protected void showSpinnerDays() {
        Spinner spinner = findViewById(R.id.daySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemDay = (String)parent.getItemAtPosition(position);

                if(itemDay.equals("Сегодня")) {
                    /* Отображение текущей даты */
                    dateCurrStr = getCurrDate(formatForDate, dateCurrStr);
                    dateTextView.setText(dateCurrStr);
                }
                if(itemDay.equals("Завтра")) {
                    anotherDateStr = getAnotherDate(1);
                    dateTextView.setText(anotherDateStr);
                }
                if(itemDay.equals("Вчера")) {
                    anotherDateStr = getAnotherDate(-1);
                    dateTextView.setText(anotherDateStr);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }


    /*
        Дает возможность увидеть свёрнутые поля - заметка и расписание занятий.
        Изначально они скрыты.
     */
    protected void showHiddenElements() {
        //РАСПИСАНИЕ ЗАНЯТИЙ
        timetableBtn = findViewById(R.id.timetableBtn);
        timetableView = findViewById(R.id.timetableLayout);

        timetableBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(timetableView.getVisibility() == View.GONE) {
                    timetableView.setVisibility(View.VISIBLE);
                }
                else {
                    timetableView.setVisibility(View.GONE);
                }
            }
        });

        //ЗАМЕТКА
        notesBtn = findViewById(R.id.notesBtn);
        notesEditText = findViewById(R.id.notesEditText);

        notesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(notesEditText.getVisibility() == View.GONE) {
                    notesEditText.setVisibility(View.VISIBLE);
                }
                else {
                    notesEditText.setVisibility(View.GONE);
                }
            }
        });
    }


    protected void addTask() {
        ImageButton addTask = findViewById(R.id.addTaskBtn);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OptionToAddNoteActivity.class));
            }
        });
    }


}