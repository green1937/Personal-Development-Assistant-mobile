package com.example.assistant.diary;

import static com.example.assistant.MainActivity.getCurrDate;
import static com.example.assistant.timetable.TimetableActivity.getJsonFromUrl;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.MainActivity;
import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.wheel.WheelActivity;
import com.example.assistant.plans.PlansActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {
    DateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    DateFormat formatForDateVariant2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String dateCurrStr, dateCurrStr2;
    TextView dateCurrDiary;
    String urlDiary;
    ArrayList<String> recordData = new ArrayList<>();
    ArrayList<String> todayRecordData = new ArrayList<>();

    List<ArrayList<String>> allRecords = new ArrayList<>();
    EditText todayRecordEditText;
    int flag, todayRecordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        dateCurrDiary = findViewById(R.id.dateDiary);
        todayRecordEditText = findViewById(R.id.todayRecordEditText);

        Resources res = getResources();
        urlDiary = res.getString(R.string.urlTuna) + "diary";  // Ссылка на дневник (все записи)

        Date currDate = new Date();
        dateCurrStr2 = formatForDateVariant2.format(currDate);
        loadJsonFromUrlDiary(urlDiary, dateCurrStr2);
        //outputRecord(dateCurrStr2);


        bottNavItem();                // Нижнее меню
        dateCurrStr = seeCurrDate();  // Определение текущей даты
        goToAllRecordsActivity();     // Переход на экран всех записей


    }

    /*
        Загрузка данных дневника (все записи) в JSON через Tuna
    */
    private void loadJsonFromUrlDiary(String url, String dateCurrStr) {
        new Thread(() -> {
            try {
                // ссылка
                String json = getJsonFromUrl(url);

                if (json != null) {
                    runOnUiThread(() -> {
                        getDiaryFromJSON(json, dateCurrStr);
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


    protected void getDiaryFromJSON(String json, String dateCurrStr) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                recordData = new ArrayList<>();
                JSONObject recordObject = jsonArray.getJSONObject(i);

                // Если дата записи эквивалента сегодняшней дате
                if (recordObject.getString("assigned_day").equals(dateCurrStr)) {
                    todayRecordId = recordObject.getInt("id");
                    todayRecordData.add(recordObject.getString("id"));
                    todayRecordData.add(recordObject.getString("text"));
                    todayRecordData.add(recordObject.getString("assigned_day"));
                }

                else {
                    recordData.add(recordObject.getString("text"));
                    recordData.add(recordObject.getString("assigned_day"));

                    allRecords.add(recordData);
                }

            }
            System.out.println("ВСЕ ЗАПИСИ БЕЗ СЕГОДНЯ  " + allRecords);
            System.out.println("СЕГОДНЯ ДАННЫЕ = " + todayRecordData);
            //outputDiaryToRecyclerView(allRecords);    // Передача записей в дневнике в RecyclerView
            outputRecord(dateCurrStr2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
        Функция передачи текста записи на текущую дату
     */
    protected void outputRecord(String dateCurrStr) {
        flag = 3;
        System.out.println("Today data = " + todayRecordData);
        if (todayRecordData.size() != 0) { // есть запись на сегодня
            flag = 0;
            todayRecordEditText.setText(todayRecordData.get(1));
        }
        // Добавляем TextWatcher для отслеживания изменений
        todayRecordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Этот метод вызывается перед изменением текста

            }

           @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Этот метод вызывается во время изменения текста
                // Здесь можно добавить дополнительную логику
                //Toast.makeText(getApplicationContext(), "Поменялось что-тоооо",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Этот метод вызывается после изменения текста
                if (flag == 1) {
                    // POST --- создаем запись
                    flag = 0;
                    createRecord(dateCurrStr, todayRecordEditText.getText().toString());
                    //Возможно это. Нам просто нужно как -то получить id новой записи
                    loadJsonFromUrlDiary(urlDiary, dateCurrStr);
                }
                else {
                    updateRecord(todayRecordId, dateCurrStr, todayRecordEditText.getText().toString());
                }
            }
        });
    }

    /*
        Создание новой записи на сегодня, если таковой не имеется
     */
    protected void createRecord(String dateCurrStr, String text) {
        NewRecord record = new NewRecord(1, dateCurrStr,text);
        String jsonData = new Gson().toJson(record);
        System.out.println("НОВАЯ ЗАПИСЬ В ДНЕВНИКЕ С ТАКИМИ ДАННЫМИ  " + jsonData);
        sendDataToServer("POST", jsonData);
    }


    /*
        Обновление записи на сегодня
     */
    protected void updateRecord(int todayRecordId, String dateCurrStr, String text) {

        Record record = new Record(todayRecordId, 1, dateCurrStr,text);
        String jsonData = new Gson().toJson(record);
        System.out.println("ОБНОВИЛИ ЗАПИСЬ В ДНЕВНИКЕ НА СЕГОДНЯ. ДАННЫЕ:  " + jsonData);
        sendDataToServer("PUT", jsonData);
    }


    /*
        Функция отправки данных на сервер (применяется для создания новой записи --- POST;
        и для обновление записи на сегодня --- PUT)
     */
    protected void sendDataToServer(String requestMethod, String jsonData) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // Создаем URL и соединение
                URL serverUrl = new URL(urlDiary);
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
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_diary);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Главная
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
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
        Функция, отвечающая за показ на главной экране и получение текущей даты
     */
    protected String seeCurrDate() {
        dateCurrStr = getCurrDate(formatForDate, dateCurrStr);  // Получение текущей даты
        dateCurrDiary.setText(dateCurrStr);                     // Вывод текущей даты
        return dateCurrStr;
    }


    /*
        Функция, осуществляющая переход на экран со всеми записями
     */
    protected void goToAllRecordsActivity() {
        TextView allRecordsBtn = findViewById(R.id.allRecordsBtn);
        allRecordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("allRecords", (Serializable) allRecords);
                Intent intent = new Intent(v.getContext(), AllRecordsDiaryActivity.class);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }

}
