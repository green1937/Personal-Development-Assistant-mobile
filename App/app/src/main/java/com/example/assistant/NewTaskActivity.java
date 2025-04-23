package com.example.assistant;

import static com.example.assistant.NewPlanActivity.setInitialDate;
import static com.example.assistant.TimetableActivity.getJsonFromUrl;

import static java.lang.Integer.parseInt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NewTaskActivity extends AppCompatActivity {
    String[] ctgExamples = { "Учеба", "Семья", "Здоровье", "Спорт", "Работа"};
    String itemCtg;
    String urlCategories, urlTask;
    List<ArrayList<String>> allCategories = new ArrayList<>();
    ArrayList<String> nameAllCategories = new ArrayList<>();


    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    Calendar timeFromCld = Calendar.getInstance();
    Calendar timeToCld = Calendar.getInstance();
    SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    DateFormat formatForDateVariant2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat sdfTIME = new SimpleDateFormat("HH:mm", Locale.getDefault());

    EditText nameTask, scoreEditText, descriptionEditText, dateFrom, timeFrom, dateTo, timeTo;

    CalendarView calendarView;
    TextView calendarBtn;
    ImageButton saveTaskBtn, backBtn;

    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        nameTask = findViewById(R.id.taskName);              // название задачи
        scoreEditText = findViewById(R.id.scoreEditText);    // оценка
        descriptionEditText = findViewById(R.id.decrTaskEditText);  // Описание задачи
        dateFrom = findViewById(R.id.dateTimeTaskFromDate);  // дата от
        timeFrom = findViewById(R.id.dateTimeTaskFromTime);  // время от
        dateTo = findViewById(R.id.dateTimeTaskToDate);      // дата до
        timeTo = findViewById(R.id.dateTimeTaskToTime);      // время до

        saveTaskBtn = findViewById(R.id.tickBtn);
        backBtn = findViewById(R.id.backBtn);

        Resources res = getResources();
        urlCategories = res.getString(R.string.urlTuna) + "categories";
        urlTask = res.getString(R.string.urlTuna) + "tasks";
        loadJsonFromUrlCategories(urlCategories); // Загрузка категорий колеса баланса

        //showSpinnerCtg();       // Отображение в выпадающем списке примеров категорий

        backToOption();         // Возвращение назад

        saveTask();             // Сохранение задачи

        openRepeatSettings();   // Переход на страницу настроек повтора

    }


    /*
        Возвращение назад на страницу выбора типа создания (голосая заметка или задача).
     */
    protected void backToOption() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewTaskActivity.this, MainActivity.class));
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
                String descriptionTask = descriptionEditText.getText().toString();

                String dateToStr = dateTo.getText().toString();
                String timeToStr = timeTo.getText().toString();
                String dateFromStr = dateFrom.getText().toString();
                String timeFromStr = timeFrom.getText().toString();

                // Оценка не может быть пустой и нельзя указывать только время без даты
                if (taskNameStr.equals("") || scoreStr.equals("") || (dateToStr.equals("")  && !timeToStr.equals("")) || (dateFromStr.equals("")  && !timeFromStr.equals(""))) {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! Поля не могут быть пустыми!", Toast.LENGTH_SHORT).show();
                }


                else {
                    score = parseInt(scoreEditText.getText().toString());

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
                                    try {
                                        dateFromStr = formatForDateVariant2.format(Objects.requireNonNull(sdfDATE.parse(dateFromStr)));
                                        dateToStr = formatForDateVariant2.format(Objects.requireNonNull(sdfDATE.parse(dateToStr)));
                                        Category ctgTask = new Category(getIdCtg(itemCtg));
                                        Task taskData = new Task(taskNameStr, descriptionTask, score, ctgTask, dateFromStr, dateToStr, timeFromStr, timeToStr);
                                        String jsonData = new Gson().toJson(taskData);
                                        sendDataToServer("POST", jsonData);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                    startActivity(new Intent(NewTaskActivity.this, MainActivity.class));
                                }


                            } else {
                                if (!dateFromStr.equals("")) {
                                    try {
                                        dateFromStr = formatForDateVariant2.format(Objects.requireNonNull(sdfDATE.parse(dateFromStr)));
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                if (!dateToStr.equals("")) {
                                    try {
                                        dateFromStr = formatForDateVariant2.format(Objects.requireNonNull(sdfDATE.parse(dateFromStr)));
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                Category ctgTask = new Category(getIdCtg(itemCtg));
                                Task taskData = new Task(taskNameStr, descriptionTask, score, ctgTask, dateFromStr, dateToStr, timeFromStr, timeToStr);
                                String jsonData = new Gson().toJson(taskData);
                                sendDataToServer("POST", jsonData);
                                startActivity(new Intent(NewTaskActivity.this, MainActivity.class));
                            }
                        }


                    }
                }

            }
        });
    }

    protected void sendDataToServer(String requestMethod, String jsonData) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // Создаем URL и соединение
                URL serverUrl = new URL(urlTask);
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

    protected int getIdCtg(String nameCtg) {
        for (int i=0; i<allCategories.size(); i++) {
            if (allCategories.get(i).get(1).equals(nameCtg)) {
                return parseInt(allCategories.get(i).get(0));
            }
        }
        return 0;
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

            setInitialDate(year, monthOfYear+1, dayOfMonth, dateFrom);
        }
    };


    public void setTimeFrom(View v) {
        new TimePickerDialog(NewTaskActivity.this, t1, timeFromCld.get(Calendar.HOUR_OF_DAY),
                timeFromCld.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t1=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet (TimePicker view, int hourOfDay, int minute) {
            timeFromCld.set(Calendar.HOUR_OF_DAY, hourOfDay);
            timeFromCld.set(Calendar.MINUTE, minute);

            setInitialTime(timeFrom, timeFromCld);
        }
    };



    public void setDateTo(View v) {
        new DatePickerDialog(NewTaskActivity.this, d2, dateToCld.get(Calendar.YEAR),
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


    public void setTimeTo(View v) {
        new TimePickerDialog(NewTaskActivity.this, t2, timeToCld.get(Calendar.HOUR_OF_DAY),
                timeToCld.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t2=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet (TimePicker view, int hourOfDay, int minute) {
            timeToCld.set(Calendar.HOUR_OF_DAY, hourOfDay);
            timeToCld.set(Calendar.MINUTE, minute);

            setInitialTime(timeTo, timeToCld);
        }
    };


    private void setInitialTime(EditText editTime, Calendar timeCld) {
        editTime.setText(DateUtils.formatDateTime(this, timeCld.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }


    /*
            Загрузка данных расписания мероприятий в JSON через Tuna
         */
    private void loadJsonFromUrlCategories(String url) {
        new Thread(() -> {
            try {
                // ссылка
                String json = getJsonFromUrl(url);

                if (json != null) {
                    runOnUiThread(() -> {
                        getCategoriesFromJSON(json);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Ошибка загрузки данных",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e("THREAD_ERROR", "Ошибка в потоке:", e);
            }
        }).start();
    }

    /*

     */
    private void getCategoriesFromJSON(String json) {
        allCategories = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject ctgData = jsonArray.getJSONObject(i);
                ArrayList<String> oneCategory = new ArrayList<>();

                oneCategory.add(ctgData.getString("id"));
                oneCategory.add(ctgData.getString("title"));


                nameAllCategories.add(ctgData.getString("title"));


                allCategories.add(oneCategory);
            }
            showSpinnerCtg(nameAllCategories);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    /*
        Выпадающий список с примерами категорий (сфер жизни).
    */
    protected void showSpinnerCtg(ArrayList<String> nameAllCategories) {
        Spinner spinner = findViewById(R.id.ctgSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, nameAllCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemCtg = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }
}
