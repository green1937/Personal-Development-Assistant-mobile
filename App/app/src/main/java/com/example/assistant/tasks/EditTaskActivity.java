package com.example.assistant.tasks;

import static com.example.assistant.plans.NewPlanActivity.setInitialDate;
import static com.example.assistant.plans.PlansActivity.checkDateFormat;
import static com.example.assistant.tasks.NewTaskActivity.checkDateTimeFormat;
import static com.example.assistant.tasks.NewTaskActivity.getDaysForRepeat;
import static com.example.assistant.tasks.NewTaskActivity.getIdObj;
import static com.example.assistant.tasks.NewTaskActivity.isDateFromAfterDateTo;
import static com.example.assistant.tasks.NewTaskActivity.isInvalidInput;
import static com.example.assistant.tasks.NewTaskActivity.isTimeFromAfterTimeTo;
import static com.example.assistant.tasks.NewTaskActivity.isValidScore;
import static com.example.assistant.timetable.NewEventActivity.checkDaysWeek;
import static com.example.assistant.timetable.NewEventActivity.colorWeeksBtn;
import static com.example.assistant.timetable.TimetableActivity.getJsonFromUrl;
import static com.example.assistant.tasks.NewTaskActivity.changeDateFormat;
import static java.lang.Integer.parseInt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.MainActivity;
import com.example.assistant.R;
import com.example.assistant.wheel.Category;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class EditTaskActivity extends AppCompatActivity {
    String taskNameStr, descriptionTask, dateFromStr, dateToStr, timeFromStr, timeToStr, term, start, end, ctgName, planName;
    int score, countR, numberOfRepeats, ctgId;
    int[] arr;
    Long planId = null;
    ArrayList<Integer> days = new ArrayList<>();
    Repeat repeat = null;

    String itemCtg, itemPlan, urlCategories, urlEditTask, urlPlans, urlAllTask;
    List<ArrayList<String>> allCategories = new ArrayList<>();
    ArrayList<String> nameAllCategories = new ArrayList<>();
    List<ArrayList<String>> allPlans = new ArrayList<>();
    ArrayList<String> nameAllPlans = new ArrayList<>();


    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    Calendar timeFromCld = Calendar.getInstance();
    Calendar timeToCld = Calendar.getInstance();
    Calendar dateStartCld = Calendar.getInstance();
    Calendar dateEndCld = Calendar.getInstance();
    SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    DateFormat formatForDateVariant2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat sdfTIME = new SimpleDateFormat("HH:mm", Locale.getDefault());

    EditText nameTask, scoreEditText, descriptionEditText, dateFrom, timeFrom, dateTo, timeTo;
    EditText dateStart, countRepeat, dateEnd, countEnd;

    CalendarView calendarView;
    TextView calendarBtn, repeatOrNotRepeatText;
    ImageButton saveTaskBtn, backBtn;
    LinearLayout repeatOrNotRepeatLL, taskRepeatLL;
    String[] paramRS = { "Неделя", "Месяц", "Год"};
    String[] paramES = { "Никогда", "До даты", "После n раз"};
    String itemRS, itemES;
    TextView textDayRepeat, monD, tuesD, wednesD, thursD, friD, saturD, sunD;
    int[] flagWeek;
    int flagSpinner;

    LinearLayout linLayoutWeek;
    ArrayList<Integer> daysRepeat = new ArrayList<>();

    int idEditTask;
    ArrayList<String> taskData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        Bundle bundle = getIntent().getExtras();
        idEditTask = (int) bundle.getSerializable("id");

        //ссылка
        Resources res = getApplicationContext().getResources();
        urlEditTask = res.getString(R.string.urlTuna) + "tasks/" + idEditTask;
        urlCategories = res.getString(R.string.urlTuna) + "categories";
        urlPlans = res.getString(R.string.urlTuna) + "plans";
        urlAllTask = res.getString(R.string.urlTuna) + "tasks";


        nameTask = findViewById(R.id.taskName);                         // название задачи
        scoreEditText = findViewById(R.id.scoreEditText);               // оценка
        descriptionEditText = findViewById(R.id.decrTaskEditText);      // Описание задачи
        dateFrom = findViewById(R.id.dateTimeTaskFromDate);             // дата от
        timeFrom = findViewById(R.id.dateTimeTaskFromTime);             // время от
        dateTo = findViewById(R.id.dateTimeTaskToDate);                 // дата до
        timeTo = findViewById(R.id.dateTimeTaskToTime);                 // время до


        // Повтор задачи
        repeatOrNotRepeatLL = findViewById(R.id.repeatOrNotRepeatLL);
        taskRepeatLL = findViewById(R.id.taskRepeatLL);
        repeatOrNotRepeatText = findViewById(R.id.repeatOrNotRepeatText);

        dateStart = findViewById(R.id.dateStart);
        countRepeat = findViewById(R.id.count);
        dateEnd = findViewById(R.id.dateEnd);
        countEnd = findViewById(R.id.countEnd);

        textDayRepeat = findViewById(R.id.textDayRepeat);

        monD = findViewById(R.id.mondayBtn);
        tuesD = findViewById(R.id.tuesdayBtn);
        wednesD = findViewById(R.id.wednesdayBtn);
        thursD = findViewById(R.id.thursdayBtn);
        friD = findViewById(R.id.fridayBtn);
        saturD = findViewById(R.id.saturdayBtn);
        sunD = findViewById(R.id.sundayBtn);
        flagWeek = new int[] {3, 3, 3, 3, 3, 3, 3};
        linLayoutWeek = findViewById(R.id.linearWeek);
        flagSpinner = 3;


        saveTaskBtn = findViewById(R.id.tickBtn);
        backBtn = findViewById(R.id.backBtn);


        backToOption();                             // Назад на Главный Экран
        loadTaskData();                             // Загрузка данных редактируемой задачи
        loadJsonFromUrlCategories();                // Загрузка категорий колеса баланса
        loadJsonFromUrlPlans();                     // Загрузка категорий колеса баланса
    }

    /*
    Загрузка категорий JSON через Tuna
    */
    private void loadJsonFromUrlCategories() {
        new Thread(() -> {
            try {
                // ссылка
                String json = getJsonFromUrl(urlCategories);

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
    Загрузка планов JSON через Tuna
    */
    private void loadJsonFromUrlPlans() {
        new Thread(() -> {
            try {
                // ссылка
                String json = getJsonFromUrl(urlPlans);

                if (json != null) {
                    runOnUiThread(() -> {
                        getPlansFromJSON(json);
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
        Получение всех категорий Колеса баланса
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
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /*
        Получение всех планов
     */
    private void getPlansFromJSON(String json) {
        allPlans = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject planData = jsonArray.getJSONObject(i);
                ArrayList<String> onePlan = new ArrayList<>();

                onePlan.add(planData.getString("id"));
                onePlan.add(planData.getString("name"));

                nameAllPlans.add(planData.getString("name"));

                allPlans.add(onePlan);
            }
            nameAllPlans.add(0, "Нет плана");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    /*
        Загрузка данных редактируемой задачи по ее id
     */
    protected void loadTaskData() {
        new Thread(() -> {
            try {
                // ссылка
                String json = getJsonFromUrl(urlEditTask);

                if (json != null) {
                    runOnUiThread(() -> {
                        getTaskFromJSON(json);
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


    protected void getTaskFromJSON(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);

            taskNameStr = jsonObject.getString("name");
            descriptionTask = jsonObject.getString("description");
            dateFromStr = jsonObject.getString("start_date");
            dateToStr = jsonObject.getString("stop_date");

            timeFromStr = jsonObject.getString("start_time");
            timeToStr = jsonObject.getString("stop_time");

            score = parseInt(jsonObject.getString("estimate"));


            //repeat
            if (!jsonObject.getString("repeat").equals("null")) {
                JSONObject repeatObject = jsonObject.getJSONObject("repeat");

                term = repeatObject.getString("term");

                // Получаем JSONArray для days
                JSONArray daysArray = repeatObject.getJSONArray("days");
                arr = new int[daysArray.length()];

                // Заполняем массив целыми числами
                for (int i = 0; i < daysArray.length(); i++) {
                    arr[i] = daysArray.getInt(i);
                }

                countR = parseInt(repeatObject.getString("repeat_interval"));
                start = repeatObject.getString("start");
                end = repeatObject.getString("end");
                numberOfRepeats = parseInt(repeatObject.getString("number_of_repeats"));

                repeat = new Repeat(countR, term, arr, start, end, numberOfRepeats);
            }

            //plan
            if (!jsonObject.getString("plan").equals("null")) {
                JSONObject planObject = jsonObject.getJSONObject("plan");
                planId = Long.valueOf(parseInt(planObject.getString("id")));
                planName = planObject.getString("name");
            }


            //category
            JSONObject categoryObject = jsonObject.getJSONObject("task_category");
            ctgName = categoryObject.getString("title");
            ctgId = parseInt(categoryObject.getString("id"));

            outputTaskDataInForm();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*
        Возвращение назад на Главный экран
     */
    protected void backToOption() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditTaskActivity.this, MainActivity.class));
            }
        });
    }


    /*
        Функция заполняющая поля данными выбранной задачи
     */
    protected void outputTaskDataInForm() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

        nameTask.setText(taskNameStr);
        descriptionEditText.setText(descriptionTask);
        System.out.println("dateeeeeee " + dateFromStr + " " + dateToStr);
        if (!dateFromStr.equals("null")) dateFrom.setText(changeDateFormat(dateFromStr, inputFormat, outputFormat));
        if (!dateToStr.equals("null")) dateTo.setText(changeDateFormat(dateToStr, inputFormat, outputFormat));
        if (!timeFromStr.equals("null")) timeFrom.setText(timeFromStr);
        if (!timeToStr.equals("null")) timeTo.setText(timeToStr);
        scoreEditText.setText(String.valueOf(score));

        //category
        showSpinnerCtg();

        //plan
        showSpinnerPlan();

        // Если задача была с повторами
        if(repeat != null) {
            taskRepeatLL.setVisibility(View.VISIBLE);
            repeatOrNotRepeatText.setText("Задача повторяется");

            dateStart.setText(changeDateFormat(start, inputFormat, outputFormat));
            countRepeat.setText(String.valueOf(countR));
            if (numberOfRepeats != 0) countEnd.setText(String.valueOf(numberOfRepeats));
            if (!end.equals("null")) dateEnd.setText(changeDateFormat(end, inputFormat, outputFormat));

            // term
            spinnerRepeat();
            if (term.equals("week")) {
                colorDayOfWeek();
            }
            //when end repeat...
            spinnerWhenRepeatEnd();
        }
        saveEditTask();
    }


    protected void saveEditTask() {
        // Покраска дней недели
        colorWeeksBtn(monD, tuesD, wednesD, thursD, friD, saturD, sunD, flagWeek);
        getDaysForRepeat(flagWeek);

        //сохранение по нажатию на кнопку
        saveTaskBtn.setOnClickListener(v -> {
            taskNameStr = nameTask.getText().toString();
            String scoreStr = scoreEditText.getText().toString();
            descriptionTask = descriptionEditText.getText().toString();
            dateToStr = dateTo.getText().toString();
            timeToStr = timeTo.getText().toString();
            dateFromStr = dateFrom.getText().toString();
            timeFromStr = timeFrom.getText().toString();

            // Оценка не может быть пустой и нельзя указывать только время без даты
            if (taskNameStr.equals("") || scoreStr.equals("") || (dateToStr.equals("") && !timeToStr.equals("")) || (dateFromStr.equals("") && !timeFromStr.equals(""))) {
                Toast.makeText(getApplicationContext(), "Ошибка сохранения! Поля не могут быть пустыми!", Toast.LENGTH_SHORT).show();
                return;
            }

            score = Integer.parseInt(scoreStr);

            // Валидация оценки и дат
            if (!isValidScore(score) || (checkDateTimeFormat(dateFromStr, "date") == 1 ||  checkDateTimeFormat(dateToStr, "date") == 1
                    || checkDateTimeFormat(timeFromStr, "time") == 1 || checkDateTimeFormat(timeToStr, "time") == 1)) {
                showToast("Ошибка сохранения!");
                return;
            }


            // Проверка последовательности дат
            if (!isInvalidInput(dateFromStr, dateToStr) && isDateFromAfterDateTo(dateFromStr, dateToStr)) {
                showToast("Дата ОТ должна быть раньше даты ДО!");
                return;
            }

            if(!isInvalidInput(dateFromStr, dateToStr, timeFromStr, timeToStr) && dateFromStr.equals(dateToStr) && isTimeFromAfterTimeTo(timeFromStr, timeToStr)) {
                showToast("Время ОТ должна быть раньше времени ДО!");
                return;
            }

            // Форматирование дат
            formatDates();

            getAllParametersOfTaskAndSave();
        });
    }


    /*
        Получение всех параметров задачи (включая повторы)
        и отправка на сервер для сохранения
     */
    protected void getAllParametersOfTaskAndSave() {
        Category ctgTask = new Category(getIdObj(itemCtg, allCategories));
        Long planId = null;
        if(!itemPlan.equals("Нет плана")) {
            planId = Long.valueOf(getIdObj(itemPlan, allPlans));
        }
        NewTask taskData = new NewTask(idEditTask, taskNameStr, descriptionTask, score, ctgTask,
                dateFromStr, dateToStr, timeFromStr, timeToStr, planId);
        String jsonData = new Gson().toJson(taskData);
        System.out.println("DATA TASK = " + jsonData);
        sendDataToServer("PUT", jsonData);

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    private void formatDates() {
        try {
            if (!dateFromStr.isEmpty()) {
                dateFromStr = formatForDateVariant2.format(sdfDATE.parse(dateFromStr));
            }
            if (!dateToStr.isEmpty()) {
                dateToStr = formatForDateVariant2.format(sdfDATE.parse(dateToStr));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    /*
    Покраска каждой кнопки дня недели при нажатии на нее
    В дальнейшем также и передача выбранных дней
     Пока криво сделано --- переделать потом в дальнейшем
    */
    protected void colorDayOfWeek() {
        for (int i =0; i<arr.length; i++) {
            int j = parseInt(String.valueOf(arr[i]));
            flagWeek[j] = 1;
        }

        if (flagWeek[0] == 1) monD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[1] == 1) tuesD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                    PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[2] == 1) wednesD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                    PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[3] == 1) thursD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                    PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[4] == 1) friD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                    PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[5] == 1) saturD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                    PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[6] == 1) sunD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                PorterDuff.Mode.DARKEN);  // Смена цвета кнопки

    }


    /*
        Вывод данных в выпадающем списке (никогда, до даты, после стольких раз)
        О том, когда заканчивается повтор
     */
    protected void spinnerWhenRepeatEnd() {
        Spinner endSpinner = findViewById(R.id.whenEndSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, paramES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endSpinner.setAdapter(adapter);

        int position = adapter.getPosition("Никогда");;
        if(!end.equals("null")) position = adapter.getPosition("До даты");
        if(numberOfRepeats!=0) position = adapter.getPosition("После n раз");

        endSpinner.setSelection(position);


        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemES = (String)parent.getItemAtPosition(position);

                /* Если выбрана неделя, то показываем выбор дней недели,
                   если нет, то скрываем их
                   */
                if(itemES.equals("Никогда")) {
                    dateEnd.setVisibility(View.GONE);
                    countEnd.setVisibility(View.GONE);
                    flagSpinner = 0;
                }
                else if (itemES.equals("До даты")){
                    dateEnd.setVisibility(View.VISIBLE);
                    countEnd.setVisibility(View.GONE);
                    flagSpinner = 1;
                }
                else {
                    dateEnd.setVisibility(View.GONE);
                    countEnd.setVisibility(View.VISIBLE);
                    flagSpinner = 2;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        endSpinner.setOnItemSelectedListener(itemSelectedListener);
    }



    /*
        Вывод данных в выпадающем списке (неделя, месяц, год)
     */
    protected void spinnerRepeat() {
        Spinner repeatSpinner = findViewById(R.id.repeatSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, paramRS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(adapter);

        // "перевод"
        String posName = "Неделя";
        if (term.equals("month"))  posName = "Месяц";
        if (term.equals("year"))  posName = "Год";

        // Находим позицию нужного элемента
        int position = adapter.getPosition(posName);
        repeatSpinner.setSelection(position);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemRS = (String)parent.getItemAtPosition(position);

                /* Если выбрана неделя, то показываем выбор дней недели,
                   если нет, то скрываем их
                   */
                if(itemRS.equals("Неделя")) {
                    linLayoutWeek.setVisibility(View.VISIBLE);
                    textDayRepeat.setVisibility(View.VISIBLE);
                }
                else {
                    linLayoutWeek.setVisibility(View.GONE);
                    textDayRepeat.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        repeatSpinner.setOnItemSelectedListener(itemSelectedListener);
    }


    /*
        Выпадающий список с категориями (сферами жизни).
    */
    protected void showSpinnerCtg() {
        Spinner spinner = findViewById(R.id.ctgSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, nameAllCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Находим позицию нужного элемента
        int position = adapter.getPosition(ctgName);
        spinner.setSelection(position);

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


    /*
        Выпадающий список с планами.
    */
    protected void showSpinnerPlan() {
        Spinner spinner = findViewById(R.id.plansSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, nameAllPlans);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int position;
        if (planName == null || planName.isEmpty()) { position = adapter.getPosition("Без плана"); System.out.println("plan = " + planName ); }
        else position = adapter.getPosition(planName);
        spinner.setSelection(position);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemPlan = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }



    /*
        Вывод календарей и часов для дат и времени при редактировании задачи
     */


    public void setDateFrom(View v) {
        new DatePickerDialog(EditTaskActivity.this, d1, dateFromCld.get(Calendar.YEAR),
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
        new TimePickerDialog(EditTaskActivity.this, t1, timeFromCld.get(Calendar.HOUR_OF_DAY),
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
        new DatePickerDialog(EditTaskActivity.this, d2, dateToCld.get(Calendar.YEAR),
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
        new TimePickerDialog(EditTaskActivity.this, t2, timeToCld.get(Calendar.HOUR_OF_DAY),
                timeToCld.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t2=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet (TimePicker view, int hourOfDay, int minute) {
            timeToCld.set(Calendar.HOUR_OF_DAY, hourOfDay);
            timeToCld.set(Calendar.MINUTE, minute);

            setInitialTime(timeTo, timeToCld);
        }
    };

    /*
     Выбор даты начала повторов в календаре
     */
    public void setDateRepeat(View v) {
        new DatePickerDialog(EditTaskActivity.this, dR, dateStartCld.get(Calendar.YEAR),
                dateStartCld.get(Calendar.MONTH), dateStartCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener dR=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateStartCld.set(Calendar.YEAR, year);
            dateStartCld.set(Calendar.MONTH, monthOfYear);
            dateStartCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDate(year, monthOfYear+1, dayOfMonth, dateStart);
        }
    };





    /*
     Выбор даты (когда заканчивается повтор задачи) в календаре
     */
    public void setDate(View v) {
        new DatePickerDialog(EditTaskActivity.this, d, dateEndCld.get(Calendar.YEAR),
                dateEndCld.get(Calendar.MONTH), dateEndCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateEndCld.set(Calendar.YEAR, year);
            dateEndCld.set(Calendar.MONTH, monthOfYear);
            dateEndCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDate(year, monthOfYear+1, dayOfMonth, dateEnd);
        }
    };


    private void setInitialTime(EditText editTime, Calendar timeCld) {
        editTime.setText(DateUtils.formatDateTime(this, timeCld.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }



    protected void sendDataToServer(String requestMethod, String jsonData) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // Создаем URL и соединение
                URL serverUrl = new URL(urlAllTask);
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
                        Toast.makeText(getApplicationContext(), "Данные успешно отправлены", Toast.LENGTH_SHORT).show();
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


}
