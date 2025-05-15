package com.example.assistant.views;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.assistant.R;
import com.example.assistant.databinding.ActivityNewEventBinding;
import com.example.assistant.viewmodel.EventViewModel;
import com.example.assistant.viewmodel.TimetableViewModel;
import com.example.assistant.viewmodel.TimetableViewModelFactory;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;


public class NewEventActivity extends AppCompatActivity {
    SimpleDateFormat sdfTIME = new SimpleDateFormat("HH:mm", Locale.getDefault());
    Calendar dateAndTime = Calendar.getInstance();
    Calendar dateAndTimeFrom = Calendar.getInstance();
    EditText nameNewSubj, placeNewSubj;

    String itemFormat, itemRepeat;

    int[] flagWeek;

    String[] format = {"Онлайн", "Офлайн"};
    String[] repeat = {"Каждую неделю", "Четную неделю", "Нечетную неделю"};

    private ActivityNewEventBinding binding;
    EventViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        flagWeek = new int[]{3, 3, 3, 3, 3, 3, 3};
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_event);
        viewModel = new ViewModelProvider(this).get(EventViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "events");
        showSpinnerFormat();
        showSpinnerRepeat();
        colorWeeks();


        viewModel.getSaveResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, TimetableActivity.class));
            } else {
                Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, TimetableActivity.class));
        });

    }

    /*
    Покраска каждой кнопки дня недели при нажатии на нее
    В дальнейшем также и передача выбранных дней
    Пока криво сделано --- переделать потом в дальнейшем
*/
    public void colorWeeks() {
        binding.mondayBtn.setOnClickListener(v -> {
            if (flagWeek[0] == 1) {
                binding.mondayBtn.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[0] = 0;
            } else {
                binding.mondayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[0] = 1;
            }
            viewModel.setFlagWeek(flagWeek);
        });


        binding.tuesdayBtn.setOnClickListener(v -> {
            if (flagWeek[1] == 1) {
                binding.tuesdayBtn.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[1] = 0;
            } else {
                binding.tuesdayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[1] = 1;
            }
            viewModel.setFlagWeek(flagWeek);
        });

        binding.wednesdayBtn.setOnClickListener(v -> {
            if (flagWeek[2] == 1) {
                binding.wednesdayBtn.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[2] = 0;
            } else {
                binding.wednesdayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[2] = 1;
            }
            viewModel.setFlagWeek(flagWeek);
        });

        binding.thursdayBtn.setOnClickListener(v -> {
            if (flagWeek[3] == 1) {
                binding.thursdayBtn.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[3] = 0;
            } else {
                binding.thursdayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[3] = 1;
            }
            viewModel.setFlagWeek(flagWeek);
        });

        binding.fridayBtn.setOnClickListener(v -> {
            if (flagWeek[4] == 1) {
                binding.fridayBtn.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[4] = 0;
            } else {
                binding.fridayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[4] = 1;
            }
            viewModel.setFlagWeek(flagWeek);
        });
        binding.saturdayBtn.setOnClickListener(v -> {
            if (flagWeek[5] == 1) {
                binding.saturdayBtn.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[5] = 0;
            } else {
                binding.saturdayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[5] = 1;
            }
            viewModel.setFlagWeek(flagWeek);
        });
        binding.sundayBtn.setOnClickListener(v -> {
            if (flagWeek[6] == 1) {
                binding.sundayBtn.getBackground().setColorFilter(Color.parseColor("#FFFFFFFF"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[6] = 0;
            } else {
                binding.sundayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                        PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                flagWeek[6] = 1;
            }
            viewModel.setFlagWeek(flagWeek);
        });
    }


    protected void showSpinnerFormat() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, format);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.formatSubjSpinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemFormat = (String) parent.getItemAtPosition(position);
                viewModel.setFormat(itemFormat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        binding.formatSubjSpinner.setOnItemSelectedListener(itemSelectedListener);


    }


    protected void showSpinnerRepeat() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, repeat);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.repeatSubjSpinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemRepeat = (String) parent.getItemAtPosition(position);
                viewModel.setRepeat(itemRepeat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        binding.repeatSubjSpinner.setOnItemSelectedListener(itemSelectedListener);

    }

    /*
     Вывод часов и выбор времени для мероприятия (от и до)
     */
    public void setTime(View v) {
        new TimePickerDialog(NewEventActivity.this, t, dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);

            setInitialDateTime(binding.timeTo, dateAndTime);
        }
    };

    public void setTimeFrom(View v) {
        new TimePickerDialog(NewEventActivity.this, t2, dateAndTimeFrom.get(Calendar.HOUR_OF_DAY),
                dateAndTimeFrom.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t2 = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTimeFrom.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTimeFrom.set(Calendar.MINUTE, minute);

            setInitialDateTime(binding.timeFrom, dateAndTimeFrom);
        }
    };

    private void setInitialDateTime(EditText timeEdit, Calendar timeCld) {
        timeEdit.setText(DateUtils.formatDateTime(this, timeCld.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));

    }
}