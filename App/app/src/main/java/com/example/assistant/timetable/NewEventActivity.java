package com.example.assistant.timetable;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class NewEventActivity extends AppCompatActivity {
    SimpleDateFormat sdfTIME = new SimpleDateFormat("HH:mm", Locale.getDefault());
    Calendar dateAndTime = Calendar.getInstance();
    Calendar dateAndTimeFrom = Calendar.getInstance();
    EditText nameNewSubj, placeNewSubj, timeTo, timeFrom;
    ImageButton saveEventBtn;
    Spinner formatSpinner;
    String itemFormat, itemRepeat;
    Spinner repeatSpinner;

    int monF, tuesF, wednesF, thursF, friF, saturF, sunF;

    TextView monD, tuesD, wednesD, thursD, friD, saturD, sunD;
    int[] flagWeek;

    String[] format = { "Онлайн", "Офлайн"};
    String[] repeat = { "Каждую неделю", "Четную неделю", "Нечетную неделю"};
    String urlEvents;
    LocalTime timeFromLD;
    LocalTime timeToLD;
    ArrayList<Integer> daysRepeat = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);


        formatSpinner = findViewById(R.id.formatSubjSpinner);
        repeatSpinner = findViewById(R.id.repeatSubjSpinner);

        nameNewSubj = findViewById(R.id.nameNewSubj);     // Название
        placeNewSubj = findViewById(R.id.placeNewSubj);   // Место или ссылка
        saveEventBtn = findViewById(R.id.saveEventBtn);

        timeTo = findViewById(R.id.timeTo);               // Время проведения ОТ
        timeFrom = findViewById(R.id.timeFrom);           // Время проведения ДО

        monD = findViewById(R.id.mondayBtn);
        tuesD = findViewById(R.id.tuesdayBtn);
        wednesD = findViewById(R.id.wednesdayBtn);
        thursD = findViewById(R.id.thursdayBtn);
        friD = findViewById(R.id.fridayBtn);
        saturD = findViewById(R.id.saturdayBtn);
        sunD = findViewById(R.id.sundayBtn);

        flagWeek = new int[] {3, 3, 3, 3, 3, 3, 3};

        Resources res = getResources();
        urlEvents = res.getString(R.string.urlTuna) + "events";  // Ссылка на расписание

        backToSideMenu();        // Возвращение назад
        saveEvent();             // Сохранение мероприятия

    }



    protected void backToSideMenu() {
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TimetableActivity.class));
            }
        });
    }


    /*
        Функция, отвечающая за сохранение созданного мероприятия.

        1. В случае, если ошибок никаких нет, то мероприятие успешно сохраняется,
           и пользователь переходит на экран со всем расписанием мероприятий.
        2. В противном случае, мероприятие не сохраняется и пользователь получает сообщение
           об ошибке сохранения.

     */
    protected void saveEvent() {
        daysRepeat = new ArrayList<>();

        showSpinnerFormat();    // Выпадающий список ФОРМАТ
        showSpinnerRepeat();    // Выпадающий список ПОВТОР

        // Смена цвета кнопок дне недели --- в дальнейшем отработка нажатия
        colorWeeksBtn(monD, tuesD, wednesD, thursD, friD, saturD, sunD, flagWeek);


        saveEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventNameStr = nameNewSubj.getText().toString();
                String placeStr = placeNewSubj.getText().toString();
                String timeToStr = timeTo.getText().toString();
                String timeFromStr = timeFrom.getText().toString();


                // Если все поля не пустые и выбран хотя бы один день недели
                if (!eventNameStr.equals("") && !placeStr.equals("") && !timeToStr.equals("")
                        && !timeFromStr.equals("") && checkDaysWeek(flagWeek) != 1) {

                    // Если формат времени правильный mm:hh
                    if (checkTimeFormat(timeToStr) != 1 && checkTimeFormat(timeFromStr) != 1) {

                        // Если время ОТ меньше времени ДО
                        DateTimeFormatter dtfTIME = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
                        timeFromLD = LocalTime.parse(timeFromStr, dtfTIME);
                        timeToLD = LocalTime.parse(timeToStr, dtfTIME);

                        // Проверка на то, что дата ОТ наступает раньше даты ДО
                        if (timeToLD.isAfter(timeFromLD)) {
                            Toast.makeText(getApplicationContext(), "Ошибка сохранения! " +
                                            "Время ОТ должно наступать раньше времени ДО!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // Сохранение мероприятия
                        else {

                            for (int i=0; i< 7; i++) {
                                Event event;
                                if (flagWeek[i] == 1) {
                                    if (itemRepeat.equals("Четную неделю")) {
                                        event = new Event(1, 2, i, eventNameStr, placeStr, itemFormat, timeToStr, timeFromStr);
                                        String json = new Gson().toJson(event);
                                        System.out.println("ЧЕТНАЯЯ  " + json);
                                        sendDataToServer(urlEvents, json);
                                    } else if (itemRepeat.equals("Нечетную неделю")) {
                                        event = new Event(1, 1, i, eventNameStr, placeStr, itemFormat, timeToStr, timeFromStr);
                                        String json = new Gson().toJson(event);
                                        sendDataToServer(urlEvents, json);
                                    } else {
                                        for (int j =1; j<3; j++) {
                                            event = new Event(1, j, i, eventNameStr, placeStr, itemFormat, timeToStr, timeFromStr);
                                            String json = new Gson().toJson(event);
                                            sendDataToServer(urlEvents, json);
                                        }

                                    }

                                }
                            }

                            /*Toast.makeText(getApplicationContext(), "Мероприятие сохранено успешно!",
                                    Toast.LENGTH_SHORT).show(); */
                            startActivity(new Intent(NewEventActivity.this, TimetableActivity.class));
                        }
                    }

                    // Ошибка - неверный формат времени
                    else {
                        Toast.makeText(getApplicationContext(), "Ошибка сохранения! Неверный формат времени час:мин.",
                                Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! Заполните поля.",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });

    }



    private void sendDataToServer(String url, String jsonData) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // Создаем URL и соединение
                URL serverUrl = new URL(url);
                connection = (HttpURLConnection) serverUrl.openConnection();

                // Настраиваем запрос
                connection.setRequestMethod("POST");
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


    /*
      Функция проверки формата "Время"
     */
    protected int checkTimeFormat(String param) {
        int flag = 0;
        sdfTIME.setLenient(false);
        try {
            sdfTIME.parse(param);
            System.out.println("Valid time");
        } catch (ParseException e) {
            System.out.println("Invalid time");
            flag = 1;
        }
        return flag;
    }

    protected void showSpinnerFormat() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, format);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatSpinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemFormat = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        formatSpinner.setOnItemSelectedListener(itemSelectedListener);

    }

    /*
     Функция, проверяющая есть ли выбранные дни недели для мероприятия.
     Если есть хотя бы один выбранный день недели,
     то рассматривать дальше все дни не имеет смысла.
     */
    public static int checkDaysWeek(int[] flagsForWeek) {
        int flag = 1;
        for (int i=0; i<7; i++) {
            if (flagsForWeek[i] == 1) {  // Проверка на выбранный день
                flag = 0;
                break;
            }
        }
        return flag;
    }


    protected void showSpinnerRepeat() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, repeat);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemRepeat = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        repeatSpinner.setOnItemSelectedListener(itemSelectedListener);

    }


    /*
    Покраска каждой кнопки дня недели при нажатии на нее
    В дальнейшем также и передача выбранных дней
     Пока криво сделано --- переделать потом в дальнейшем
    */
    public static void colorWeeksBtn(TextView monD, TextView tuesD, TextView wednesD, TextView thursD, TextView friD,
                                        TextView saturD, TextView sunD, int[] flagWeek) {
        monD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagWeek[0] == 1) {
                    monD.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[0] = 0;
                }
                else {
                    monD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[0] = 1;
                }


            }
        });


        tuesD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagWeek[1] == 1) {
                    tuesD.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[1] = 0;
                }
                else {
                    tuesD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[1] = 1;
                }
            }
        });

        wednesD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagWeek[2] == 1) {
                    wednesD.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[2] = 0;
                }
                else {
                    wednesD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[2] = 1;
                }
            }
        });

        thursD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagWeek[3] == 1) {
                    thursD.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[3] = 0;
                }
                else {
                    thursD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[3] = 1;
                }
            }
        });

        friD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagWeek[4] == 1) {
                    friD.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[4] = 0;
                }
                else {
                    friD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[4] = 1;
                }
            }
        });
        saturD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagWeek[5] == 1) {
                    saturD.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[5] = 0;
                }
                else {
                    saturD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[5] = 1;
                }
            }
        });
        sunD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagWeek[6] == 1) {
                    sunD.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[6] = 0;
                }
                else {
                    sunD.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                    flagWeek[6] = 1;
                }
            }
        });

    }


    /*
     Вывод часов и выбор времени для мероприятия (от и до)
     */

    public void setTime(View v) {
        new TimePickerDialog(NewEventActivity.this, t, dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet (TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);

            setInitialDateTime(timeTo, dateAndTime);
        }
    };

    public void setTimeFrom(View v) {
        new TimePickerDialog(NewEventActivity.this, t2, dateAndTimeFrom.get(Calendar.HOUR_OF_DAY),
                dateAndTimeFrom.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t2=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet (TimePicker view, int hourOfDay, int minute) {
            dateAndTimeFrom.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTimeFrom.set(Calendar.MINUTE, minute);

            setInitialDateTime(timeFrom, dateAndTimeFrom);
        }
    };

    private void setInitialDateTime(EditText timeEdit, Calendar timeCld) {
        timeEdit.setText(DateUtils.formatDateTime(this, timeCld.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));

    }



}
