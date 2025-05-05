package com.example.assistant.plans;

import static com.example.assistant.tasks.NewTaskActivity.changeDateFormat;
import static com.example.assistant.plans.PlansActivity.checkDateFormat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class NewPlanActivity extends AppCompatActivity {
    String urlPlan;

    SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

    String planNameStr, detailsStr, dateFromStr, dateToStr;
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    EditText namePlan, detailsPlan, dateFrom, dateTo;
    TextView calendarBtn;
    ImageButton saveTaskBtn, backBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan);

        Resources res = getResources();
        urlPlan = res.getString(R.string.urlTuna) + "plans";

        namePlan = findViewById(R.id.planName);                 // название плана
        detailsPlan = findViewById(R.id.decrPlanEditText);      // описание плана
        dateFrom = findViewById(R.id.datePlanFrom);             // дата от
        dateTo = findViewById(R.id.datePlanTo);                 // дата до

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

                planNameStr = namePlan.getText().toString();
                detailsStr = detailsPlan.getText().toString();
                dateFromStr = dateFrom.getText().toString();
                dateToStr = dateTo.getText().toString();


                // Оценка не может быть пустой и нельзя указывать только время без даты
                if ( planNameStr.equals("") || dateFromStr.equals("") || dateToStr.equals("") ) {
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
                                    saveNewPlan();
                                    startActivity(new Intent(NewPlanActivity.this, PlansActivity.class));
                                }
                            } else {
                                saveNewPlan();
                                startActivity(new Intent(NewPlanActivity.this, PlansActivity.class));
                            }
                        }


                    }
                }

            }
        });
    }

    protected void saveNewPlan() {
        NewPlan plan = new NewPlan(1, planNameStr, detailsStr, changeDateFormat(dateFromStr, inputFormat, outputFormat), changeDateFormat(dateToStr, inputFormat, outputFormat));
        String jsonData = new Gson().toJson(plan);
        System.out.println("DATA TASK = " + jsonData);
        sendDataToServer("POST", jsonData);
    }

    protected void sendDataToServer(String requestMethod, String jsonData) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // Создаем URL и соединение
                URL serverUrl = new URL(urlPlan);
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
                    runOnUiThread(() -> {
                        //Toast.makeText(getApplicationContext(), "Данные успешно отправлены", Toast.LENGTH_SHORT).show();
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

            setInitialDate(year, monthOfYear+1, dayOfMonth, dateFrom);
        }
    };


    public void setDateTo(View v) {
        new DatePickerDialog(NewPlanActivity.this, d2, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateToCld.set(Calendar.YEAR, year);
            dateToCld.set(Calendar.MONTH, monthOfYear);
            dateToCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDate(year, monthOfYear+1, dayOfMonth, dateTo);
        }
    };

    public static void setInitialDate(int year, int monthOfYear, int dayOfMonth, EditText editDate) {
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
    }

}
