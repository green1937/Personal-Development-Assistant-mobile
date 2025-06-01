package com.example.assistant.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.assistant.databinding.ActivityNewTaskBinding;
import com.example.assistant.R;
import com.example.assistant.viewmodel.NewTaskViewModel;
import java.util.Calendar;

public class NewTaskActivity extends AppCompatActivity {
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    Calendar timeFromCld = Calendar.getInstance();
    Calendar timeToCld = Calendar.getInstance();
    Calendar dateStartCld = Calendar.getInstance();
    Calendar dateEndCld = Calendar.getInstance();
    int[] flagWeek;
    private ActivityNewTaskBinding binding;
    NewTaskViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_task);
        viewModel = new ViewModelProvider(this).get(NewTaskViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "tasks");
        viewModel.setUrlCtg(res.getString(R.string.urlTuna) + "categories");
        viewModel.setUrlPlans(res.getString(R.string.urlTuna) + "plans/full?status=0");

        viewModel.loadNameAllCtgForTask();
        viewModel.loadNameAllPlanForTask();
        viewModel.getCtgResult().observe(this, success -> {
            if (success!=null) {
                spinnerCtg(NewTaskActivity.this, binding, viewModel, "");
            } else {
                Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
            }
        });
        viewModel.getPlanResult().observe(this, success -> {
            if (success!=null) {
                spinnerPlans(NewTaskActivity.this, binding, viewModel, "");
            } else {
                Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
            }
        });


        binding.backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        binding.tickBtn.setOnClickListener(v -> {
            viewModel.setEstimate(binding.scoreEditText.getText().toString());
            viewModel.setRepeatInterval(binding.count.getText().toString());
            viewModel.setNumOfRepeats(binding.countEnd.getText().toString());
            viewModel.onSaveBtnClicked();

        });

        viewModel.getSaveResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
            }
        });
        binding.repeatOrNotRepeatLL.setOnClickListener(v -> {
            // Если выбран повтор задачи
                if (binding.repeatOrNotRepeatText.getText().equals("Не повторяется")) {
                    binding.taskRepeatLL.setVisibility(View.VISIBLE);
                    binding.repeatOrNotRepeatText.setText("Задача повторяется");
                    viewModel.setIsTaskRepeat(true);

                } else {
                    binding.taskRepeatLL.setVisibility(View.GONE);
                    binding.repeatOrNotRepeatText.setText("Не повторяется");
                    viewModel.setIsTaskRepeat(false);
                }
            });

        spinnerRepeatEnd(NewTaskActivity.this, binding, viewModel, "", 0);
        spinnerRepeat(NewTaskActivity.this, binding, viewModel, "");

        flagWeek = new int[] {3, 3, 3, 3, 3, 3, 3};
        colorWeeks();

    }


    /*
        Выпадающий список с примерами категорий (сфер жизни).
    */
    public static void spinnerCtg(Context context, ActivityNewTaskBinding binding, NewTaskViewModel viewModel, String ctgName) {
        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, viewModel.getNameAllCtgForTask());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.ctgSpinner.setAdapter(adapter);

        // Находим позицию нужного элемента
        if(context instanceof EditTaskActivity) binding.ctgSpinner.setSelection(adapter.getPosition(ctgName));

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                viewModel.setItemCtg((String)parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        binding.ctgSpinner.setOnItemSelectedListener(itemSelectedListener);
    }


    /*
        Выпадающий список с планами
    */
    public static void spinnerPlans(Context context, ActivityNewTaskBinding binding, NewTaskViewModel viewModel, String planName) {
        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, viewModel.getNameAllPlanForTask());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.plansSpinner.setAdapter(adapter);

        if(context instanceof EditTaskActivity) {
            int position;
            if (planName == null || planName.isEmpty()) position = adapter.getPosition("Без плана");
            else position = adapter.getPosition(planName);
            binding.plansSpinner.setSelection(position);
        }

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                //itemPlan = (String)parent.getItemAtPosition(position);
                viewModel.setItemPlan((String)parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        binding.plansSpinner.setOnItemSelectedListener(itemSelectedListener);
    }


    protected static void spinnerRepeatEnd(Context context, ActivityNewTaskBinding binding, NewTaskViewModel viewModel, String end, int numberOfRepeats) {
        String[] paramES = { "Никогда", "До даты", "После n раз"};
        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, paramES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.whenEndSpinner.setAdapter(adapter);

        if(context instanceof EditTaskActivity) {
            int position = adapter.getPosition("Никогда");
            if (!end.equals("null")) position = adapter.getPosition("До даты");
            if (numberOfRepeats != 0) position = adapter.getPosition("После n раз");
            binding.whenEndSpinner.setSelection(position);
        }

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                String itemES = (String)parent.getItemAtPosition(position);

                /* Если выбрана неделя, то показываем выбор дней недели,
                   если нет, то скрываем их
                   */
                if(itemES.equals("Никогда")) {
                    binding.dateEnd.setVisibility(View.GONE);
                    binding.countEnd.setVisibility(View.GONE);
                    viewModel.setFlagSpinnerWhenRepeatEnd(0);
                }
                else if (itemES.equals("До даты")){
                    binding.dateEnd.setVisibility(View.VISIBLE);
                    binding.countEnd.setVisibility(View.GONE);
                    viewModel.setFlagSpinnerWhenRepeatEnd(1);
                }
                else {
                    binding.dateEnd.setVisibility(View.GONE);
                    binding.countEnd.setVisibility(View.VISIBLE);
                    viewModel.setFlagSpinnerWhenRepeatEnd(2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        binding.whenEndSpinner.setOnItemSelectedListener(itemSelectedListener);
    }

    protected static void spinnerRepeat(Context context, ActivityNewTaskBinding binding, NewTaskViewModel viewModel, String term) {
        String[] paramRS = { "Неделя", "Месяц", "Год"};
        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, paramRS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.repeatSpinner.setAdapter(adapter);

        if(context instanceof EditTaskActivity) {
            // "перевод"
            String posName = "Неделя";
            if (term.equals("month"))  posName = "Месяц";
            if (term.equals("year"))  posName = "Год";

            // Находим позицию нужного элемента
            int position = adapter.getPosition(posName);
            binding.repeatSpinner.setSelection(position);
        }

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                String itemRS = (String)parent.getItemAtPosition(position);

                /* Если выбрана неделя, то показываем выбор дней недели,
                   если нет, то скрываем их
                   */
                if(itemRS.equals("Неделя")) {
                    binding.linearWeek.setVisibility(View.VISIBLE);
                    binding.textDayRepeat.setVisibility(View.VISIBLE);
                    viewModel.setSpinnerTimeParam(itemRS);
                }
                else {
                    binding.linearWeek.setVisibility(View.GONE);
                    binding.textDayRepeat.setVisibility(View.GONE);
                    viewModel.setSpinnerTimeParam(itemRS);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        binding.repeatSpinner.setOnItemSelectedListener(itemSelectedListener);
    }



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


    /*
        Вывод календарей и часов для дат и времени при создании задачи
     */
    public void setDateFrom(View v) {
        new DatePickerDialog(NewTaskActivity.this, d1, dateFromCld.get(Calendar.YEAR),
                dateFromCld.get(Calendar.MONTH), dateFromCld.get(Calendar.DAY_OF_MONTH)).show();
    }
    DatePickerDialog.OnDateSetListener d1 = (view, year, month, day) -> viewModel.handleDateSet(dateFromCld, binding.dateTimeTaskFromDate, year, month, day);



    public void setTimeFrom(View v) {
        new TimePickerDialog(NewTaskActivity.this, t1, timeFromCld.get(Calendar.HOUR_OF_DAY),
                timeFromCld.get(Calendar.MINUTE), true).show();
    }
    TimePickerDialog.OnTimeSetListener t1 = (view, hourOfDay, minute) -> viewModel.handleTimeSet(timeFromCld, binding.dateTimeTaskFromTime, hourOfDay, minute);

    public void setDateTo(View v) {
        new DatePickerDialog(NewTaskActivity.this, d2, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }
    DatePickerDialog.OnDateSetListener d2 = (view, year, month, day) -> viewModel.handleDateSet(dateToCld, binding.dateTimeTaskToDate, year, month, day);

    public void setTimeTo(View v) {
        new TimePickerDialog(NewTaskActivity.this, t2, timeToCld.get(Calendar.HOUR_OF_DAY),
                timeToCld.get(Calendar.MINUTE), true).show();
    }
    TimePickerDialog.OnTimeSetListener t2 = (view, hourOfDay, minute) -> viewModel.handleTimeSet(timeToCld, binding.dateTimeTaskToTime, hourOfDay, minute);

    /*
     Выбор даты начала повторов в календаре
     */
    public void setDateRepeat(View v) {
        new DatePickerDialog(NewTaskActivity.this, dR, dateStartCld.get(Calendar.YEAR),
                dateStartCld.get(Calendar.MONTH), dateStartCld.get(Calendar.DAY_OF_MONTH)).show();
    }
    DatePickerDialog.OnDateSetListener dR = (view, year, month, day) -> viewModel.handleDateSet(dateStartCld, binding.dateStart, year, month, day);



    /*
     Выбор даты (когда заканчивается повтор задачи) в календаре
     */
    public void setDate(View v) {
        new DatePickerDialog(NewTaskActivity.this, d, dateEndCld.get(Calendar.YEAR),
                dateEndCld.get(Calendar.MONTH), dateEndCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d = (view, year, month, day) -> viewModel.handleDateSet(dateEndCld, binding.dateEnd, year, month, day);
}
