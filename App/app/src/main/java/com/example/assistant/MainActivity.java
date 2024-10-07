package com.example.assistant;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String dateCurrStr, anotherDateStr;
    String itemDay;
    String[] days = { "Сегодня", "Завтра", "Вчера"};
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.US);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView dateTextView = findViewById(R.id.dateText);  // вывод даты текущей (либо выбранной)

        /* Отображение текущей даты */
        dateCurrStr = getCurrDate();
        dateTextView.setText(dateCurrStr);


        /* Выпадающий список с днями "сегодня", "завтра", "вчера" */
        LocalDate dateLocalDate = LocalDate.parse(dateCurrStr, formatter);

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


        TextView notesTextView = findViewById(R.id.notesBtn);
        EditText notesEditText = findViewById(R.id.notesEditText);

        notesTextView.setOnClickListener(new View.OnClickListener() {
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



    /* Определение текущей даты в формате день.месяц.год */
    protected String getCurrDate() {
        Date currDate = new Date();
        DateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateCurrStr = formatForDate.format(currDate);
        return dateCurrStr;
    }

    protected String getAnotherDate(int a) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = formatDate.parse(dateCurrStr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, a);  // number of days to add
            date = c.getTime();
            anotherDateStr = formatDate.format(date);
            return anotherDateStr;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }




}