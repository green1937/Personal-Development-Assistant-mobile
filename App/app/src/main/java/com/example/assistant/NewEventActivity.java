package com.example.assistant;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        backToSideMenu();        // Возвращение назад
        saveEvent();             // Сохранение мероприятия
        setInitialDateTime();

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

        showSpinnerFormat();    // Выпадающий список ФОРМАТ
        showSpinnerRepeat();    // Выпадающий список ПОВТОР
        colorWeeksBtn();        // Смена цвета кнопок дне недели --- в дальнейшем отработка нажатия


        saveEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventNameStr = nameNewSubj.getText().toString();
                String placeStr = placeNewSubj.getText().toString();
                String timeToStr = timeTo.getText().toString();
                String timeFromStr = timeFrom.getText().toString();


                //Проверка на пустоту название
                if (eventNameStr.equals("")) {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! " +
                            "Поле Название мероприятия должно быть заполнено!",
                            Toast.LENGTH_SHORT).show();

                }

                //Проверка на пустоту поля место проведения
                if (placeStr.equals("")) {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! " +
                                    "Поле Место/ссылка должно быть заполнено!",
                            Toast.LENGTH_SHORT).show();
                }

                //Проверка на пустоту времени
                if (timeToStr.equals("") || timeFromStr.equals("")) {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! " +
                                    "Поля Время должны быть заполнены!",
                            Toast.LENGTH_SHORT).show();
                }

                // Проверка на то, что выбран хотя бы один день недели
                if (checkDaysWeek(flagWeek) == 1) {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! " +
                                    "Не выбран ни один день недели!",
                            Toast.LENGTH_SHORT).show();
                }

                else {

                    // В случае если все заполнено --- проверяем формат времени
                    if (checkTimeFormat(timeToStr) == 1 || checkTimeFormat(timeFromStr) == 1) {
                        Toast.makeText(getApplicationContext(), "Ошибка сохранения! " +
                                        "Поля Время не соответсвует формату ВРЕМЯ!",
                                Toast.LENGTH_SHORT).show();
                    }

                    // Если все ок --- сохраняем
                    else {
                        startActivity(new Intent(NewEventActivity.this, TimetableActivity.class));
                        System.out.println("ВРЕМЯ ОТ = " + timeToStr + " ВРЕМЯ ДО = " + timeFromStr);
                        Toast.makeText(getApplicationContext(), "Мероприятие успешно сохранено!",
                                Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });

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
    protected int checkDaysWeek(int[] flagsForWeek) {
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
    protected void colorWeeksBtn() {
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

            setInitialDateTime();
        }
    };

    private void setInitialDateTime() {
        timeTo.setText(DateUtils.formatDateTime(this, dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }

    public void setTimeFrom(View v) {
        new TimePickerDialog(NewEventActivity.this, t2, dateAndTimeFrom.get(Calendar.HOUR_OF_DAY),
                dateAndTimeFrom.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t2=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet (TimePicker view, int hourOfDay, int minute) {
            dateAndTimeFrom.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTimeFrom.set(Calendar.MINUTE, minute);

            setInitialDateTimeFrom();
        }
    };

    private void setInitialDateTimeFrom() {
        timeFrom.setText(DateUtils.formatDateTime(this, dateAndTimeFrom.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));

    }



}
