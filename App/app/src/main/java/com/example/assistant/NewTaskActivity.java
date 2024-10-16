package com.example.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {
    SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
    SimpleDateFormat sdfTIME = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        backToOption();  // Возвращение назад

        saveTask();  // Сохранение задачи

        openRepeatSettings();  // Переход на страницу настроек повтора



    }


    /*
        Возвращение назад на страницу выбора типа создания (голосая заметка или задача).
     */
    protected void backToOption() {
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewTaskActivity.this, OptionToAddNoteActivity.class));
            }
        });
    }


    /*
        Функция, отвечающая за сохранение созданной задачи.

        1. В случае, если ошибок никаких нет, то задача успешно сохраняется,
           и пользователь переходит на главный экран.
        2. В противном случае, задача не сохраняется и пользователь получает сообщение
           об ошибке сохранения.

     */
    protected void saveTask() {

        EditText nameTask = findViewById(R.id.taskName);          // название задачи
        EditText scoreEditText = findViewById(R.id.scoreEditText);    // оценка
        EditText dateFrom = findViewById(R.id.dateTimeTaskFromDate);  // дата от
        EditText timeFrom = findViewById(R.id.dateTimeTaskFromTime);  // время от
        EditText dateTo = findViewById(R.id.dateTimeTaskToDate);      // дата до
        EditText timeTo = findViewById(R.id.dateTimeTaskToTime);      // время до

        ImageButton saveTaskBtn = findViewById(R.id.tickBtn);
        saveTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskNameStr = nameTask.getText().toString();
                String scoreStr = scoreEditText.getText().toString();

                String dateToStr = dateTo.getText().toString();
                String timeToStr = timeTo.getText().toString();
                String dateFromStr = dateFrom.getText().toString();
                String timeFromStr = timeFrom.getText().toString();

                // Оценка не может быть пустой и нельзя указывать только время без даты
                if (taskNameStr.equals("") || scoreStr.equals("") || (dateToStr.equals("")  && !timeToStr.equals("")) || (dateFromStr.equals("")  && !timeFromStr.equals(""))) {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! Поля не могут быть пустыми!", Toast.LENGTH_SHORT).show();
                }


                else {
                    int score = Integer.parseInt(scoreEditText.getText().toString());

                    /*
                        Если оценка НЕ в диапазоне от 1 до 100 и введенные даты
                        и время НЕкорректных типов, то выводиться ошибка и задача не сохранится,
                        пока ошибки не будут исправлены.

                     */
                    if (score<=0 || score > 100
                            || checkDateTimeFormat(dateFromStr, "date") == 1
                            || checkDateTimeFormat(dateToStr, "date") == 1
                            || checkDateTimeFormat(timeFromStr, "time") == 1
                            || checkDateTimeFormat(timeToStr, "time") == 1) {
                        Toast.makeText(getApplicationContext(), "Ошибка сохранения!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if ((dateFromStr.length() != 10 &&  checkDateTimeFormat(dateFromStr, "date") == 0)
                                || (dateToStr.length() != 10 && checkDateTimeFormat(dateToStr, "date") == 0)) {
                            Toast.makeText(getApplicationContext(), "Ошибка! Формат даты должен быть ДД.ММ.ГГГГ", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Если введены были обе даты
                            if (checkDateTimeFormat(dateFromStr, "date") == 0 && checkDateTimeFormat(dateToStr, "date") == 0) {
                                DateTimeFormatter dtfDATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());
                                LocalDate dateFromLD = LocalDate.parse(dateFromStr, dtfDATE);
                                LocalDate dateToLD = LocalDate.parse(dateToStr, dtfDATE);

                                // Проверка на то, что дата ОТ наступает раньше даты ДО
                                if (dateFromLD.isAfter(dateToLD)) {
                                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! " +
                                                    "Дата ОТ должна наступать раньше даты ДО!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(NewTaskActivity.this, MainActivity.class));
                                }
                            } else {
                                startActivity(new Intent(NewTaskActivity.this, MainActivity.class));
                            }
                        }


                    }
                }

            }
        });
    }


    protected void openRepeatSettings() {
        TextView repeatTaskSettings = findViewById(R.id.repeatTaskText);
        repeatTaskSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewTaskActivity.this, RepeatTaskSettActivity.class));
            }
        });
    }

    protected int checkDateTimeFormat(String param1, String param2) {
        int flag = 0;
        if (!param1.equals("")) {
            if (param2.equals("date")) {
                sdfDATE.setLenient(false);
                try {
                    sdfDATE.parse(param1);
                    System.out.println("Valid date");
                } catch (ParseException e) {
                    System.out.println("Invalid date");
                    flag = 1;
                }
            }
            if (param2.equals("time")) {
                sdfTIME.setLenient(false);
                try {
                    sdfTIME.parse(param1);
                    System.out.println("Valid time");
                } catch (ParseException e) {
                    System.out.println("Invalid time");
                    flag = 1;
                }
            }
        }
        else {
            flag = 2;
        }
        return flag;
    }
}
