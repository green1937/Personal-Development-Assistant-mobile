package com.example.assistant;

import static com.example.assistant.PlansActivity.checkDateFormat;
import static com.example.assistant.NewEventActivity.checkDaysWeek;
import static com.example.assistant.NewEventActivity.colorWeeksBtn;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RepeatTaskSettActivity extends AppCompatActivity {
    Calendar dateAndTime = Calendar.getInstance();
    String[] paramRS = { "Неделя", "Месяц", "Год"};
    String[] paramES = { "Никогда", "До даты", "После n раз"};
    EditText countRepeat, dateEnd, countEnd;
    String itemRS, itemES;
    TextView textDayRepeat, monD, tuesD, wednesD, thursD, friD, saturD, sunD;
    int[] flagWeek;
    int flagSpinner;

    LinearLayout linLayoutWeek;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_task);

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



        backToNewTask();  // Переход обратно на экран создания задачи

        saveRepeatSettings();  // Сохранение настроек повтора задачи

        spinnerRepeat();  // Вывод данных в выпадающем списке

        spinnerWhenRepeatEnd();



    }


    /*
        Функция, отвечающая за переход на экран создания новой задачи (назад)
     */
    protected void backToNewTask() {
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RepeatTaskSettActivity.this, NewTaskActivity.class));
            }
        });
    }


    /*
        Сохранение настроек повтора задачи
        и переход обратно на экран создания задачи
     */
    protected void saveRepeatSettings() {
        ImageButton saveBtn = findViewById(R.id.tickBtn);

        // Покраска дней недели
        colorWeeksBtn(monD, tuesD, wednesD, thursD, friD, saturD, sunD, flagWeek);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countRepeatStr = countRepeat.getText().toString();
                String dateEndStr = dateEnd.getText().toString();
                String countEndStr = countEnd.getText().toString();

                //  Проверка на пустоту ввода: кол-ва повторов, даты окончания повторов, после какого кол-ва повторов заканчивать
                if (countRepeatStr.equals("") || (dateEndStr.equals("") && flagSpinner==1) || (countEndStr.equals("") && flagSpinner==2)) {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! Поля не заполнены!",
                            Toast.LENGTH_SHORT).show();
                }
                //  Если эти поля заполнены, то --- смотрим выбраны ли дни недели, если повторы на неделе
                else {
                    if (linLayoutWeek.getVisibility() == View.VISIBLE && checkDaysWeek(flagWeek) == 1) {
                        Toast.makeText(getApplicationContext(), "Ошибка сохранения! Не выбран ни один день недели!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // Проверка на кооректность введенной даты
                        if (flagSpinner == 1 && checkDateFormat(dateEndStr) == 1) { // Проверка на дату
                                Toast.makeText(getApplicationContext(), "Ошибка сохранения! ДАТА ДО не является датой!",
                                        Toast.LENGTH_SHORT).show();
                        }
                        else {
                            int countE = -1;
                            //  Перевод в числа
                            if (flagSpinner == 2) {
                                countE = Integer.parseInt(countEnd.getText().toString());
                            }
                            int countR = Integer.parseInt(countRepeat.getText().toString());

                            System.out.println("ПОВТОР ЗАДАЧИ --- " + countR + " " + flagWeek.toString() + " " + itemRS
                            + " " + itemES + " " + dateEndStr + " " + countE);

                            //  Успешно --- сохраняем параметры --- переходим на экран основного создания задачи
                            startActivity(new Intent(RepeatTaskSettActivity.this, NewTaskActivity.class));
                        }


                    }
                }

            }
        });
    }


    /*
        Вывод данных в выпадающем списке (неделя, месяц, год)
     */
    protected void spinnerRepeat() {
        Spinner repeatSpinner = findViewById(R.id.repeatSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, paramRS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(adapter);

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
        Вывод данных в выпадающем списке (никогда, до даты, после стольких раз)
        О том, когда заканчивается повтор
     */
    protected void spinnerWhenRepeatEnd() {
        Spinner endSpinner = findViewById(R.id.whenEndSpinner);


        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, paramES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endSpinner.setAdapter(adapter);

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
     Выбор даты (когда заканчивается повтор задачи) в календаре
     */
    public void setDate(View v) {
        new DatePickerDialog(RepeatTaskSettActivity.this, d, dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDateTime(year, monthOfYear+1, dayOfMonth);
        }
    };

    /*
        Вывод даты (когда заканчивается повтор задачи) в текстовое поле в формате ДД.ММ.ГГГГ
     */
    private void setInitialDateTime( int year, int monthOfYear, int dayOfMonth) {
        String dateForEndStr = dayOfMonth + "." + monthOfYear + "." + year;
        dateEnd.setText(dateForEndStr);
    }

}

