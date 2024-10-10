package com.example.assistant;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    TextView dateTextView, notesBtn, timetableBtn;
    EditText notesEditText;
    LinearLayout timetableView;

    String dateCurrStr, anotherDateStr;
    String itemDay;
    String[] days = { "Сегодня", "Завтра", "Вчера"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                return true;
            }
            if (item.getItemId() == R.id.bottom_plans) {
                startActivity(new Intent(getApplicationContext(), PlansActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        dateTextView = findViewById(R.id.dateText);  // вывод даты текущей (либо выбранной)


        showSpinnerDays();  // Выпадающий список дней

        showHiddenElements();  // Показ скрытых элементов (расписание занятий, заметка)

        ImageButton addTask = findViewById(R.id.addTaskBtn);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OptionToAddNoteActivity.class));
            }
        });


    }



    /*
        Определение текущей даты в формате ДЕНЬ.МЕСЯЦ.ГОД.
    */
    protected String getCurrDate() {
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
                    dateCurrStr = getCurrDate();
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



}