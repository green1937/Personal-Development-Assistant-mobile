package com.example.assistant;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


import com.example.assistant.views.DiaryActivity;
import com.example.assistant.views.PlansActivity;
import com.example.assistant.views.NewTaskActivity;
import com.example.assistant.views.TaskAdapter;
import com.example.assistant.viewmodel.TimetableAdapter;
import com.example.assistant.views.WheelActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Calendar dateCld = Calendar.getInstance();
    String [] weeksDay = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье" };
    String nameDayOfWeek;
    List<ArrayList<String>> allEvents = new ArrayList<>();
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
        allTasks = new ArrayList<>();
        allEvents = new ArrayList<>();
        bottNavItem();  // Нижнее меню

        showSpinnerDays();      // Выпадающий список дней

        showHiddenElements();  // Показ скрытых элементов (расписание занятий, заметка)
        goToNewTaskActivity(); // Добавление задачи
    }


    /*
        Загрузка данных с начальной страницы
     */
    private void loadJsonFromUrlHome(String formattedDate) {
        new Thread(() -> {
            try {
                // ссылка
                Resources res = getResources();
                String url = res.getString(R.string.urlTuna) + formattedDate;
                String json = getJsonFromUrl(url);

                if (json != null) {
                    runOnUiThread(() -> {
                        getTimetableAndTasksFromJSON(json);
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
        String somethingDate = String.valueOf(dateTextView.getText());  // Получение даты

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        formatter = formatter.withLocale(Locale.getDefault());
        LocalDate date = LocalDate.parse(somethingDate, formatter);

        DayOfWeek day = date.getDayOfWeek();
        int valueDayOfWeek = day.getValue() - 1;  // Порядковый номер дня недели (отсчет начинается с 0)
        nameDayOfWeek = weeksDay[valueDayOfWeek];
    }


    /*
        Получение расписания и задач на выбранную дату
     */
    protected void getTimetableAndTasksFromJSON(String json) {
        try {
            // Получение всех задач
            String[] tasksCtgForMainPage = { "На день", "Просрочено", "Скоро дедлайн", "Бессрочно"};
            allTasks = new ArrayList<>();
            allTasks.add(new ArrayList<>(Collections.singleton(tasksCtgForMainPage[0])));

            JSONObject jsonObject = new JSONObject(json);
            JSONObject todayObject = jsonObject.getJSONObject("today");
            String[] todayTaskStr = {"fixed_tasks", "done_tasks", "late_tasks", "soon_tasks", "free_tasks"};

            for (int i=0; i<2; i++) {
                JSONArray tasksArray = todayObject.getJSONArray(todayTaskStr[i]);

                for (int j=0; j<tasksArray.length(); j++) {
                    JSONObject taskExampleData = tasksArray.getJSONObject(j);
                    ArrayList<String> oneTask = new ArrayList<>();

                    oneTask.add(taskExampleData.getString("id"));
                    oneTask.add(taskExampleData.getString("name"));
                    oneTask.add(taskExampleData.getString("status"));

                    allTasks.add(oneTask);
                }
            }

            JSONArray eventArray = todayObject.getJSONArray("day_classes");
            allEvents = new ArrayList<>();
            allEvents.add(new ArrayList<>(Collections.singleton(nameDayOfWeek)));

            for (int j=0; j<eventArray.length(); j++) {
                JSONObject eventData = eventArray.getJSONObject(j);
                ArrayList<String> oneEvent = new ArrayList<>();

                oneEvent.add(eventData.getString("id"));
                oneEvent.add(eventData.getString("name"));
                oneEvent.add(eventData.getString("start_time"));
                oneEvent.add(eventData.getString("stop_time"));


                String placeEvent = eventData.getString("place");
                if (placeEvent.length() > 5 && placeEvent.substring(0,4).equals("http")) {
                    placeEvent = "ссылка на мероприятие";
                }

                oneEvent.add(placeEvent);
                oneEvent.add(eventData.getString("format"));
                allEvents.add(oneEvent);
            }

            // Передача данных для отрисовки RecyclerView
            timetableRecyclerView = findViewById(R.id.timetableRecyclerView);
            linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            timetableRecyclerView.setLayoutManager(linearLayoutManager);

            timetableAdapter = new TimetableAdapter(MainActivity.this, allEvents);
            timetableRecyclerView.setAdapter(timetableAdapter);

            for (int i=2; i<5; i++) {
                allTasks.add(new ArrayList<>(Collections.singleton(tasksCtgForMainPage[i-1])));
                JSONArray tasksArray = jsonObject.getJSONArray(todayTaskStr[i]);

                for (int j=0; j<tasksArray.length(); j++) {
                    JSONObject taskExampleData = tasksArray.getJSONObject(j);
                    ArrayList<String> oneTask = new ArrayList<>();

                    oneTask.add(taskExampleData.getString("id"));
                    oneTask.add(taskExampleData.getString("name"));
                    oneTask.add(taskExampleData.getString("status"));

                    allTasks.add(oneTask);
                }
            }

            /* Передаем данные в RecyclerView */
            tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
            linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            tasksRecyclerView.setLayoutManager(linearLayoutManager);
            TaskAdapter adapter = new TaskAdapter(this, allTasks, "com.example.assistant.MainActivity");
            tasksRecyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public static String getCurrDate(DateFormat formatForDate, String dateCurrStr) {
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

                // Парсер для входного формата
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");
                try {
                    Date date = inputFormat.parse(dateTextView.getText().toString());
                    String formattedDate = outputFormat.format(date);

                    loadJsonFromUrlHome(formattedDate);
                } catch (Exception e) {
                    e.printStackTrace();
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


    /*
        Выбор даты в календаре
     */
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCld.set(Calendar.YEAR, year);
            dateCld.set(Calendar.MONTH, monthOfYear);
            dateCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDate(year, monthOfYear+1, dayOfMonth, dateTextView);

            // Создаем формат даты
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            String formattedDate = sdf.format(dateCld.getTime());

            getDataParameters();
            loadJsonFromUrlHome(formattedDate);
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

    public static String getJsonFromUrl(String urlString) {
        String json = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // Заголовок для обхода tuna browser warning
            urlConnection.setRequestProperty("tuna-skip-browser-warning", "true");

            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (inputStream == null) {
                Log.d("DEBUG", "inputStream == null");
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            if (buffer.length() == 0) return null;
            json = buffer.toString();

        } catch (IOException e) {
            Log.e("DEBUG", "IOException при получении JSON", e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("DEBUG", "Ошибка закрытия reader", e);
                }
            }
        }
        return json;
    }

}