package com.example.assistant;


import static com.example.assistant.NewPlanActivity.setInitialDate;
import static com.example.assistant.TimetableActivity.getJsonFromUrl;
import static com.example.assistant.NewPlanActivity.setInitialDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Calendar dateCld = Calendar.getInstance();
    String [] weeksDay = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье" };

    String urlEvents, paramWeek, nameDayOfWeek, urlTasks;
    int valueDayOfWeek;
    List<ArrayList<String>> fullEvent = new ArrayList<>();
    List<ArrayList<String>> allTasks = new ArrayList<>();
    RecyclerView tasksRecyclerView, timetableRecyclerView;;

    DateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    EditText dateTextView;
    TextView notesBtn, timetableBtn;
    EditText notesEditText;
    LinearLayout timetableView;

    String dateCurrStr, anotherDateStr;
    String itemDay;
    String[] days = { "Сегодня", "Завтра", "Вчера"};

    TaskAdapter taskAdapter;
    LinearLayoutManager linearLayoutManager;
    TimetableAdapter timetableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = findViewById(R.id.dateText);  // вывод даты текущей (либо выбранной)

        bottNavItem();  // Нижнее меню

        showSpinnerDays();      // Выпадающий список дней

        Resources res = getResources();
        urlEvents = res.getString(R.string.urlTuna) + "events";  // Ссылка на расписание
        urlTasks = res.getString(R.string.urlTuna) + "tasks";    // Сслыка на все задачи


        showHiddenElements();  // Показ скрытых элементов (расписание занятий, заметка)
        goToNewTaskActivity(); // Добавление задачи

    }


    /*
        Загрузка данных расписания мероприятий в JSON через Tuna
     */
    private void loadJsonFromUrlEvents(String url, String paramWeek, String nameDayOfWeek, int valueDayOfWeek) {
        new Thread(() -> {
            try {
                // ссылка
                String json = getJsonFromUrl(url);

                if (json != null) {
                    runOnUiThread(() -> {
                        getTimetableFromJSON(json, paramWeek, nameDayOfWeek, valueDayOfWeek);
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
       Загрузка данных задачи в JSON через Tuna
    */
    private void loadJsonFromUrlTasks(String url, String date) {
        new Thread(() -> {
            try {
                // ссылка
                String json = getJsonFromUrl(url);

                if (json != null) {
                    runOnUiThread(() -> {
                        getTasksFromJSON(json, date);
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
        Получение параметров даты (день недели, номер дня недели, неделя)
        из EditText, в который поступает дата из выпадающего списка или из календаря
     */
    private void getDataParameters() {
        String somethindDate = String.valueOf(dateTextView.getText());  // Получение даты

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        formatter = formatter.withLocale(Locale.getDefault());
        LocalDate date = LocalDate.parse(somethindDate, formatter);

        DayOfWeek day = date.getDayOfWeek();
        valueDayOfWeek = day.getValue() - 1;  // Порядковый номер дня недели (отсчет начинается с 0)
        nameDayOfWeek = weeksDay[valueDayOfWeek];

        WeekFields wf = WeekFields.of(Locale.getDefault());
        TemporalField weekNum = wf.weekOfWeekBasedYear();
        @SuppressLint("DefaultLocale")
        int weekRemainder = Integer.parseInt(String.format("%02d",date.get(weekNum))) % 2; // если четная, то 0

        if (weekRemainder == 0) {
            paramWeek = "even_week";
        }
        else {
            paramWeek = "odd_week";
        }
    }

    /*
        Получение расписания мероприятий из JSON в виде массива
        и передача данных для отрисовки в RecyclerView

        paramWeek = odd_week/even_week
     */
    protected void getTimetableFromJSON(String json, String paramWeek, String nameDayOfWeek, int valueDayOfWeek) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("days");

            JSONObject weekData = jsonArray.getJSONObject(valueDayOfWeek);
            fullEvent = addEventInWeek(weekData, paramWeek);  // Все мероприятия Нечетная/Четная недели в зависимости от параметра

            ArrayList<String> dd = new ArrayList<>(Collections.singleton(nameDayOfWeek)); //День недели
            fullEvent.add(0, dd);

            // Передача данных для отрисовки RecyclerView
            timetableRecyclerView = findViewById(R.id.timetableRecyclerView);
            linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            timetableRecyclerView.setLayoutManager(linearLayoutManager);

            timetableAdapter = new TimetableAdapter(MainActivity.this, fullEvent);
            timetableRecyclerView.setAdapter(timetableAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
        Получение всех задач из JSON в зависимости от выбранной даты
     */
    protected void getTasksFromJSON (String json, String date) {
        allTasks = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject taskExampleData = jsonArray.getJSONObject(i);
                ArrayList<String> oneTask = new ArrayList<>();

                oneTask.add(taskExampleData.getString("name"));
                oneTask.add(taskExampleData.getString("start_date"));
                oneTask.add(taskExampleData.getString("stop_date"));

                oneTask.add(taskExampleData.getString("status"));

                if (!taskExampleData.getString("done_by").equals("null")) {
                    oneTask.add(taskExampleData.getString("done_by").substring(0, 10));
                }
                else {
                    oneTask.add(taskExampleData.getString("done_by"));
                }


                allTasks.add(oneTask);
            }
            outputTasksToRecyclerView(allTasks, date);
        } catch (JSONException | ParseException e) {
            throw new RuntimeException(e);
        }
    }


    /*
        Функция считает кол-во дней между двумя датами
        (Применяется для группы "Скоро дедлайн")
     */
    public static long calculateDaysBetween(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return java.time.temporal.ChronoUnit.DAYS.between(localDate1, localDate2);
    }


    /*
        Функция показа задач на Главном Экране (разбиение на группы,
        передача в RecyclerView, отрисовка)
     */
    protected void outputTasksToRecyclerView(List<ArrayList<String>> allTasks, String dateStr) throws ParseException {
        String[] tasksCtgForMainPage = { "На день", "Просрочено", "Бессрочно", "Скоро дедлайн" };
        Date date = formatForDate.parse(dateStr);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        formatter = formatter.withLocale(Locale.getDefault());

        List<ArrayList<String>> allTasksWithCtgForRV = new ArrayList<>();  // Все задачи с разделами


        List<ArrayList<String>> tasks1 = new ArrayList<>();
        List<ArrayList<String>> tasks2 = new ArrayList<>();
        List<ArrayList<String>> tasks3 = new ArrayList<>();
        List<ArrayList<String>> tasks4 = new ArrayList<>();


        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for(int i = 0; i < allTasks.size(); i++) {
            Date firstDate = null;    // Начало
            Date lastDate = null;     // Конец
            Date doneByDate = null;   // Дата завершения задачи

            /* Преобразуем в дату формата ДД.ММ.ГГГГ */
            if (!allTasks.get(i).get(1).equals("null")) {
                firstDate = formatForDate.parse(formatForDate.format(Objects.requireNonNull(inputFormat.parse(allTasks.get(i).get(1)))));
            }
            if (!allTasks.get(i).get(2).equals("null")) {
                lastDate = formatForDate.parse(formatForDate.format(Objects.requireNonNull(inputFormat.parse(allTasks.get(i).get(2)))));

            }
            if (!allTasks.get(i).get(4).equals("null")) {
                doneByDate = formatForDate.parse(formatForDate.format(Objects.requireNonNull(inputFormat.parse(allTasks.get(i).get(4)))));
            }



            /* Теперь разбиваем задачи на 4 категории */

            // На день
            if ( (firstDate != null && firstDate.compareTo(date) == 0) ||  ( lastDate != null && lastDate.compareTo(date) == 0) || (doneByDate != null && doneByDate.compareTo(date) == 0)) {
                tasks1.add(allTasks.get(i));

            } else if (lastDate != null && lastDate.before(date) && allTasks.get(i).get(3).equals("0")) {  // Просрочено
                tasks2.add(allTasks.get(i));
            }

            else {
                if ( ((firstDate == null && lastDate == null) || (firstDate != null && lastDate == null)) && allTasks.get(i).get(3).equals("0")) {  // Бессрочно
                    tasks3.add(allTasks.get(i));
                }
                else if (lastDate != null && calculateDaysBetween(date, lastDate) > 0 && calculateDaysBetween(date, lastDate) <= 3 && allTasks.get(i).get(3).equals("0")) {  // Скоро дедлайн
                    tasks4.add(allTasks.get(i));
                }
            }
        }


        /* Собираем все в один массив для отрисовки */
        allTasksWithCtgForRV.add(new ArrayList<>(Collections.singleton(tasksCtgForMainPage[0])));
        allTasksWithCtgForRV.addAll(tasks1);
        allTasksWithCtgForRV.add(new ArrayList<>(Collections.singleton(tasksCtgForMainPage[1])));
        allTasksWithCtgForRV.addAll(tasks2);
        allTasksWithCtgForRV.add(new ArrayList<>(Collections.singleton(tasksCtgForMainPage[2])));
        allTasksWithCtgForRV.addAll(tasks3);
        allTasksWithCtgForRV.add(new ArrayList<>(Collections.singleton(tasksCtgForMainPage[3])));
        allTasksWithCtgForRV.addAll(tasks4);


        /* Передаем данные в RecyclerView */
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        tasksRecyclerView.setLayoutManager(linearLayoutManager);

        taskAdapter = new TaskAdapter(MainActivity.this, allTasksWithCtgForRV);
        tasksRecyclerView.setAdapter(taskAdapter);


    }



    /*
        Получение мероприятий на определенный день в определенную неделю
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

                getDataParameters();
                loadJsonFromUrlEvents(urlEvents, paramWeek, nameDayOfWeek, valueDayOfWeek);  // Загрузка расписания Мероприятий
                loadJsonFromUrlTasks(urlTasks, dateTextView.getText().toString());


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


    /*
        Функция, отвечающая за переход на экран создания задачи
     */
    protected void goToNewTaskActivity() {
        ImageButton newTaskBtn = findViewById(R.id.newTaskBtn);
        newTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewTaskActivity.class));
            }
        });
    }


    /*
     Вывод календаря для выбора даты Главного Экрана
    */
    public void setDate(View v) {
        new DatePickerDialog(MainActivity.this, d, dateCld.get(Calendar.YEAR),
                dateCld.get(Calendar.MONTH), dateCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCld.set(Calendar.YEAR, year);
            dateCld.set(Calendar.MONTH, monthOfYear);
            dateCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDate(year, monthOfYear+1, dayOfMonth, dateTextView);

            getDataParameters();
            loadJsonFromUrlEvents(urlEvents, paramWeek, nameDayOfWeek, valueDayOfWeek);  // Загрузка расписания Мероприятий
            loadJsonFromUrlTasks(urlTasks, dateTextView.getText().toString());

        }
    };


}