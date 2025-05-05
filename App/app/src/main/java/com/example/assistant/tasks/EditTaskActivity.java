package com.example.assistant.tasks;

import static com.example.assistant.timetable.TimetableActivity.getJsonFromUrl;
import static com.example.assistant.tasks.NewTaskActivity.changeDateFormat;
import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.MainActivity;
import com.example.assistant.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class EditTaskActivity extends AppCompatActivity {
    String taskNameStr, descriptionTask, dateFromStr, dateToStr, timeFromStr, timeToStr, term, start, end, ctgName;
    int score, countR, numberOfRepeats, ctgId;
    int[] arr;
    String planName = "";
    Long planId = null;
    //ArrayList<Integer> days = new ArrayList<>();
    Repeat repeat = null;

    String itemCtg;
    String urlCategories, urlEditTask;
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
    EditText dateStart, countRepeat, dateEnd, countEnd;

    CalendarView calendarView;
    TextView calendarBtn, repeatOrNotRepeatText;
    ImageButton saveTaskBtn, backBtn;
    LinearLayout repeatOrNotRepeatLL, taskRepeatLL;
    Calendar dateAndTime = Calendar.getInstance();
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
        System.out.println("----- edit task's id  = " + idEditTask);

        //ссылка
        Resources res = getApplicationContext().getResources();
        urlEditTask = res.getString(R.string.urlTuna) + "tasks/" + idEditTask;
        urlCategories = res.getString(R.string.urlTuna) + "categories";

        nameTask = findViewById(R.id.taskName);              // название задачи
        scoreEditText = findViewById(R.id.scoreEditText);    // оценка
        descriptionEditText = findViewById(R.id.decrTaskEditText);  // Описание задачи
        dateFrom = findViewById(R.id.dateTimeTaskFromDate);  // дата от
        timeFrom = findViewById(R.id.dateTimeTaskFromTime);  // время от
        dateTo = findViewById(R.id.dateTimeTaskToDate);      // дата до
        timeTo = findViewById(R.id.dateTimeTaskToTime);      // время до


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


        backToOption();  // Назад на Главный Экран
        loadTaskData();  // Загрузка данных редактируемой задачи
        loadJsonFromUrlCategories(); // Загрузка категорий колеса баланса
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
            showSpinnerCtg(nameAllCategories);
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

        //plan


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

        System.out.println("EDIT TASK DATA   -----   " + taskData);
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

        String posName = "Неделя";
        if (term.equals("month"))  posName = "Месяц";
        if (term.equals("month"))  posName = "Год";

        // Находим позицию нужного элемента
        int position = adapter.getPosition(ctgName);
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
        Выпадающий список с примерами категорий (сфер жизни).
    */
    protected void showSpinnerCtg(ArrayList<String> nameAllCategories) {
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

}
