package com.example.assistant;

import static com.example.assistant.PlansActivity.checkDateFormat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NewPlanActivity extends AppCompatActivity {
    SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan);

        backToOption();  // Возвращение назад

        savePlan();  // Сохранение задачи




    }


    /*
        Возвращение назад на страницу просмотра всех планов.
     */
    protected void backToOption() {
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewPlanActivity.this, PlansActivity.class));
            }
        });
    }


    /*
        Функция, отвечающая за сохранение созданного плана.

        1. В случае, если ошибок никаких нет, то план успешно сохраняется,
           и пользователь переходит на экран просмотра всех планов.
        2. В противном случае, план не сохраняется и пользователь получает сообщение
           об ошибке сохранения.

     */
    protected void savePlan() {

        EditText namePlan = findViewById(R.id.planName);      // название плана
        EditText dateFrom = findViewById(R.id.datePlanFrom);  // дата от
        EditText dateTo = findViewById(R.id.datePlanTo);      // дата до

        ImageButton saveTaskBtn = findViewById(R.id.tickBtn);
        saveTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String taskNameStr = namePlan.getText().toString();
                String dateFromStr = dateFrom.getText().toString();
                String dateToStr = dateTo.getText().toString();


                // Оценка не может быть пустой и нельзя указывать только время без даты
                if ( taskNameStr.equals("") || dateFromStr.equals("") || dateToStr.equals("") ) {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! Поля не могут быть пустыми!", Toast.LENGTH_SHORT).show();
                }


                else {

                    /*
                        Если введенные даты НЕкорректных типов, то выводиться ошибка и план не сохранится,
                        пока ошибка не будут исправлена.

                     */
                    if ( checkDateFormat(dateFromStr) == 1 || checkDateFormat(dateToStr) == 1 ){
                        Toast.makeText(getApplicationContext(), "Ошибка сохранения!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if ((dateFromStr.length() != 10 &&  checkDateFormat(dateFromStr) == 0)
                                || (dateToStr.length() != 10 && checkDateFormat(dateToStr) == 0)) {
                            Toast.makeText(getApplicationContext(), "Ошибка! Формат даты должен быть ДД.ММ.ГГГГ", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Если введены были обе даты
                            if (checkDateFormat(dateFromStr) == 0 && checkDateFormat(dateToStr) == 0) {
                                DateTimeFormatter dtfDATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());
                                LocalDate dateFromLD = LocalDate.parse(dateFromStr, dtfDATE);
                                LocalDate dateToLD = LocalDate.parse(dateToStr, dtfDATE);

                                // Проверка на то, что дата ОТ наступает раньше даты ДО
                                if (dateFromLD.isAfter(dateToLD)) {
                                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! " +
                                                    "Дата ОТ должна наступать раньше даты ДО!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(NewPlanActivity.this, PlansActivity.class));
                                }
                            } else {
                                startActivity(new Intent(NewPlanActivity.this, PlansActivity.class));
                            }
                        }


                    }
                }

            }
        });
    }


}
