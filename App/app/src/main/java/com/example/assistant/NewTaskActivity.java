package com.example.assistant;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    Calendar timeFromCld = Calendar.getInstance();
    Calendar timeToCld = Calendar.getInstance();
    SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
    SimpleDateFormat sdfTIME = new SimpleDateFormat("HH:mm", Locale.getDefault());

    EditText nameTask, scoreEditText, dateFrom, timeFrom, dateTo, timeTo;

    CalendarView calendarView;
    TextView calendarBtn;
    ImageButton saveTaskBtn, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        nameTask = findViewById(R.id.taskName);              // название задачи
        scoreEditText = findViewById(R.id.scoreEditText);    // оценка
        dateFrom = findViewById(R.id.dateTimeTaskFromDate);  // дата от
        timeFrom = findViewById(R.id.dateTimeTaskFromTime);  // время от
        dateTo = findViewById(R.id.dateTimeTaskToDate);      // дата до
        timeTo = findViewById(R.id.dateTimeTaskToTime);      // время до

        saveTaskBtn = findViewById(R.id.tickBtn);
        backBtn = findViewById(R.id.backBtn);

        backToOption();  // Возвращение назад

        saveTask();  // Сохранение задачи

        openRepeatSettings();  // Переход на страницу настроек повтора

    }


    /*
        Возвращение назад на страницу выбора типа создания (голосая заметка или задача).
     */
    protected void backToOption() {
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


    /*
        Функция, отвечающая за переход на экран настроек повтора задачи
     */
    protected void openRepeatSettings() {
        TextView repeatTaskSettings = findViewById(R.id.repeatTaskText);
        repeatTaskSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewTaskActivity.this, RepeatTaskSettActivity.class));
            }
        });
    }


    /*
        Функция, отвечающая за проверку даты или времени
     */
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



    /*
        Вывод календарей и часов для дат и времени при создании задачи
     */


    public void setDateFrom(View v) {
        new DatePickerDialog(NewTaskActivity.this, d1, dateFromCld.get(Calendar.YEAR),
                dateFromCld.get(Calendar.MONTH), dateFromCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d1=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFromCld.set(Calendar.YEAR, year);
            dateFromCld.set(Calendar.MONTH, monthOfYear);
            dateFromCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDateFrom(year, monthOfYear+1, dayOfMonth);
        }
    };

    private void setInitialDateFrom( int year, int monthOfYear, int dayOfMonth) {
        String dateForEndStr = dayOfMonth + "." + monthOfYear + "." + year;
        dateFrom.setText(dateForEndStr);
    }




    public void setTimeFrom(View v) {
        new TimePickerDialog(NewTaskActivity.this, t1, timeFromCld.get(Calendar.HOUR_OF_DAY),
                timeFromCld.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t1=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet (TimePicker view, int hourOfDay, int minute) {
            timeFromCld.set(Calendar.HOUR_OF_DAY, hourOfDay);
            timeFromCld.set(Calendar.MINUTE, minute);

            setInitialTimeFrom();
        }
    };

    private void setInitialTimeFrom() {
        timeFrom.setText(DateUtils.formatDateTime(this, timeFromCld.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }



    public void setDateTo(View v) {
        new DatePickerDialog(NewTaskActivity.this, d2, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateToCld.set(Calendar.YEAR, year);
            dateToCld.set(Calendar.MONTH, monthOfYear);
            dateToCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDateTo(year, monthOfYear+1, dayOfMonth);
        }
    };

    private void setInitialDateTo( int year, int monthOfYear, int dayOfMonth) {
        String dateForEndStr = dayOfMonth + "." + monthOfYear + "." + year;
        dateTo.setText(dateForEndStr);
    }



    public void setTimeTo(View v) {
        new TimePickerDialog(NewTaskActivity.this, t2, timeToCld.get(Calendar.HOUR_OF_DAY),
                timeToCld.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t2=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet (TimePicker view, int hourOfDay, int minute) {
            timeToCld.set(Calendar.HOUR_OF_DAY, hourOfDay);
            timeToCld.set(Calendar.MINUTE, minute);

            setInitialTimeTo();
        }
    };

    private void setInitialTimeTo() {
        timeTo.setText(DateUtils.formatDateTime(this, timeToCld.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }






}
