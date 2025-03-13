package com.example.assistant;

import static com.example.assistant.PlansActivity.checkDateFormat;

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
import android.widget.LinearLayout;
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

public class NewPlanActivity extends AppCompatActivity {
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    EditText namePlan, dateFrom, dateTo;
    TextView calendarBtn;
    ImageButton saveTaskBtn, backBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan);

        namePlan = findViewById(R.id.planName);      // название плана
        dateFrom = findViewById(R.id.datePlanFrom);  // дата от
        dateTo = findViewById(R.id.datePlanTo);      // дата до

        calendarBtn = findViewById(R.id.calendarBtn);

        saveTaskBtn = findViewById(R.id.tickBtn);
        backBtn = findViewById(R.id.backBtn);



        backToOption();         // Возвращение назад
        savePlan();             // Сохранение задачи

    }


    /*
        Возвращение назад на страницу просмотра всех планов.
     */
    protected void backToOption() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewPlanActivity.this, PlansActivity.class));
            }
        });
    }


    /*
        Функция, отвечающая за вывод даты
        в поле EditText при нажатии на любую дату в календаре (CalendarView)
     */
    protected static void dataFromCalendar(CalendarView calendarView, EditText dateED) {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if ((month + 1 < 10) &&(dayOfMonth >= 10)) {
                    dateED.setText(dayOfMonth + ".0" + (month + 1) + "." + year);
                }
                if ((dayOfMonth < 10) && (month + 1 < 10)) {
                    dateED.setText("0" + dayOfMonth + ".0" + (month + 1) + "." + year);
                }
                if ((month + 1 >= 10) && (dayOfMonth < 10)) {
                    dateED.setText("0" + dayOfMonth + "." + (month + 1) + "." + year);
                }
                if ((month + 1 >= 10) && (dayOfMonth >= 10)) {
                    dateED.setText(dayOfMonth + "." + (month + 1) + "." + year);
                }
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

    /*
        Вывод календарей и часов для дат и времени при создании задачи
     */


    public void setDateFrom(View v) {
        new DatePickerDialog(NewPlanActivity.this, d1, dateFromCld.get(Calendar.YEAR),
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


    public void setDateTo(View v) {
        new DatePickerDialog(NewPlanActivity.this, d2, dateToCld.get(Calendar.YEAR),
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

}
