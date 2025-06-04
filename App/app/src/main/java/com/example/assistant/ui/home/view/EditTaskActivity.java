package com.example.assistant.ui.home.view;

import static com.example.assistant.ui.home.view.NewTaskActivity.spinnerCtg;
import static com.example.assistant.ui.home.view.NewTaskActivity.spinnerPlans;
import static com.example.assistant.ui.home.view.NewTaskActivity.spinnerRepeatEnd;
import static com.example.assistant.ui.home.view.NewTaskActivity.spinnerRepeat;
import static java.lang.Integer.parseInt;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.assistant.R;
import com.example.assistant.databinding.ActivityNewTaskBinding;
import com.example.assistant.ui.home.viewmodel.TaskViewModel;
import java.util.Calendar;



public class EditTaskActivity extends AppCompatActivity {
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    Calendar timeFromCld = Calendar.getInstance();
    Calendar timeToCld = Calendar.getInstance();
    Calendar dateStartCld = Calendar.getInstance();
    Calendar dateEndCld = Calendar.getInstance();

    int idEditTask;
    private ActivityNewTaskBinding binding;
    TaskViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_task);
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        Bundle bundle = getIntent().getExtras();
        idEditTask = (int) bundle.getSerializable("id");
        viewModel.setIdEditTask(idEditTask);

        Resources res = getApplicationContext().getResources();
        viewModel.setEditUrl(res.getString(R.string.urlTuna) + "tasks/" + idEditTask);
        viewModel.setUrl(res.getString(R.string.urlTuna) + "tasks");
        viewModel.setUrlCtg(res.getString(R.string.urlTuna) + "categories");
        viewModel.setUrlPlans(res.getString(R.string.urlTuna) + "plans/full?status=0");

        binding.backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        viewModel.loadEditTaskData();
        viewModel.getEditTaskResult().observe(this, successEditTask -> {
            if (successEditTask!=null) {
                //ТУТ ВСЯ РАБОТЫ С ЗАДАЧЕЙ
                Toast.makeText(this, "Данные редактируемой задачи получены", Toast.LENGTH_SHORT).show();

                viewModel.setEditTask(); // Получение данных задачи
                // Вывод данных в поля
                binding.taskName.setText(String.valueOf(viewModel.getTask().getValue().getName()));
                binding.decrTaskEditText.setText(String.valueOf(viewModel.getTask().getValue().getDescription()));
                binding.scoreEditText.setText(String.valueOf(viewModel.getTask().getValue().getEstimate()));
                binding.dateTimeTaskFromDate.setText(String.valueOf(viewModel.getTask().getValue().getStartDate()));
                binding.dateTimeTaskToDate.setText(String.valueOf(viewModel.getTask().getValue().getStopDate()));
                if (viewModel.getTask().getValue().getStartTime() !=null && !viewModel.getTask().getValue().getStartTime().equals("null"))  binding.dateTimeTaskFromTime.setText(String.valueOf(viewModel.getTask().getValue().getStartTime()));
                if (viewModel.getTask().getValue().getStopTime() !=null && !viewModel.getTask().getValue().getStopTime().equals("null"))    binding.dateTimeTaskToTime.setText(String.valueOf(viewModel.getTask().getValue().getStopTime()));


                //Получение всех категорий и активных планов и вывод их в Spinner
                viewModel.loadNameAllCtgForTask();
                viewModel.loadNameAllPlanForTask();
                viewModel.getCtgResult().observe(this, success -> {
                    if (success!=null) {
                        spinnerCtg(EditTaskActivity.this, binding, viewModel, viewModel.getItemCtg());
                    } else {
                        Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                    }
                });
                viewModel.getPlanResult().observe(this, success -> {
                    if (success!=null) {
                        spinnerPlans(EditTaskActivity.this, binding, viewModel, viewModel.getItemPlan());
                    } else {
                        Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                    }
                });


                if(viewModel.getTask().getValue().getRepeat() != null) {
                    binding.taskRepeatLL.setVisibility(View.VISIBLE);
                    binding.repeatOrNotRepeatText.setText("Задача повторяется");
                    viewModel.setIsTaskRepeat(true);

                    binding.dateStart.setText(String.valueOf(viewModel.getTask().getValue().getRepeat().getStart()));
                    binding.dateEnd.setText(String.valueOf(viewModel.getTask().getValue().getRepeat().getStop()));
                    binding.countEnd.setText(String.valueOf(viewModel.getTask().getValue().getRepeat().getNumberOfRepeat()));
                    binding.count.setText(String.valueOf(viewModel.getTask().getValue().getRepeat().getRepeatInterval()));
                    spinnerRepeatEnd(EditTaskActivity.this, binding, viewModel, viewModel.getTask().getValue().getRepeat().getStop(), viewModel.getTask().getValue().getRepeat().getNumberOfRepeat());
                    spinnerRepeat(EditTaskActivity.this, binding, viewModel, viewModel.getTask().getValue().getRepeat().getTerm());
                    colorWeek(viewModel.getTask().getValue().getRepeat().getDays());


                    binding.dateStart.setFocusable(false);
                    binding.dateStart.setClickable(false);

                    binding.dateEnd.setFocusable(false);
                    binding.dateEnd.setClickable(false);

                    binding.countEnd.setFocusable(false);
                    binding.countEnd.setClickable(false);

                    binding.count.setFocusable(false);
                    binding.count.setClickable(false);

                }


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



            }
            else {
                Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
            }
        });

    }

    protected void colorWeek(int[] days) {
        int[] flagWeek = new int[] {3, 3, 3, 3, 3, 3, 3};
        for (int i =0; i<days.length; i++) {
            int j = parseInt(String.valueOf(days[i]));
            flagWeek[j] = 1;
        }

        if (flagWeek[0] == 1) binding.mondayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[1] == 1) binding.tuesdayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[2] == 1) binding.wednesdayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[3] == 1) binding.thursdayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[4] == 1) binding.fridayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[5] == 1) binding.saturdayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
        if (flagWeek[6] == 1) binding.sundayBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
    }

    /*
        Вывод календарей и часов
     */

    /*
        Вывод календарей и часов для дат и времени при создании задачи
     */
    public void setDateFrom(View v) {
        new DatePickerDialog(EditTaskActivity.this, d1, dateFromCld.get(Calendar.YEAR),
                dateFromCld.get(Calendar.MONTH), dateFromCld.get(Calendar.DAY_OF_MONTH)).show();
    }
    DatePickerDialog.OnDateSetListener d1 = (view, year, month, day) -> viewModel.handleDateSet(dateFromCld, binding.dateTimeTaskFromDate, year, month, day);



    public void setTimeFrom(View v) {
        new TimePickerDialog(EditTaskActivity.this, t1, timeFromCld.get(Calendar.HOUR_OF_DAY),
                timeFromCld.get(Calendar.MINUTE), true).show();
    }
    TimePickerDialog.OnTimeSetListener t1 = (view, hourOfDay, minute) -> viewModel.handleTimeSet(timeFromCld, binding.dateTimeTaskFromTime, hourOfDay, minute);

    public void setDateTo(View v) {
        new DatePickerDialog(EditTaskActivity.this, d2, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }
    DatePickerDialog.OnDateSetListener d2 = (view, year, month, day) -> viewModel.handleDateSet(dateToCld, binding.dateTimeTaskToDate, year, month, day);

    public void setTimeTo(View v) {
        new TimePickerDialog(EditTaskActivity.this, t2, timeToCld.get(Calendar.HOUR_OF_DAY),
                timeToCld.get(Calendar.MINUTE), true).show();
    }
    TimePickerDialog.OnTimeSetListener t2 = (view, hourOfDay, minute) -> viewModel.handleTimeSet(timeToCld, binding.dateTimeTaskToTime, hourOfDay, minute);

    /*
     Выбор даты начала повторов в календаре
     */
    public void setDateRepeat(View v) {
        new DatePickerDialog(EditTaskActivity.this, dR, dateStartCld.get(Calendar.YEAR),
                dateStartCld.get(Calendar.MONTH), dateStartCld.get(Calendar.DAY_OF_MONTH)).show();
    }
    DatePickerDialog.OnDateSetListener dR = (view, year, month, day) -> viewModel.handleDateSet(dateStartCld, binding.dateStart, year, month, day);


    /*
     Выбор даты (когда заканчивается повтор задачи) в календаре
     */
    public void setDate(View v) {
        new DatePickerDialog(EditTaskActivity.this, d, dateEndCld.get(Calendar.YEAR),
                dateEndCld.get(Calendar.MONTH), dateEndCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d = (view, year, month, day) -> viewModel.handleDateSet(dateEndCld, binding.dateEnd, year, month, day);


}
