package com.example.assistant.views;


import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;


import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.databinding.ActivityMainBinding;
import com.example.assistant.viewmodel.MainViewModel;
import com.example.assistant.viewmodel.TimetableAdapter;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Calendar dateCld = Calendar.getInstance();
    private ActivityMainBinding binding;
    MainViewModel viewModel;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
        res = getResources();

        viewModel.loadPhrase();
        viewModel.getPhraseResult().observe(this, success2 -> {
            if (success2!=null) {
                binding.phraseDayText.setText(viewModel.getPhraseText().get(0));
                binding.phraseDayAuthor.setText(viewModel.getPhraseText().get(1));
                setData();
            }
        });
        viewModel.setUrl(res.getString(R.string.urlTuna), viewModel.getTodayData());
        binding.dateText.setText(viewModel.getTodayData());

        bottNavItem();              // Нижнее меню
        showTimetableAndNote();     // Показ по нажатию расписания и заметки
        spinnerDays();              // Выпадающий список с днями
        binding.newTaskBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewTaskActivity.class)));

    }


    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        binding.bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
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
        Выпадающий список с днями "сегодня", "завтра", "вчера".
        Также меняет дату в зависимости от выбранного дня.
        Например, если "сегодня", то отобразится текущая дата.
     */
    protected void spinnerDays() {
        String[] days = { "Сегодня", "Завтра", "Вчера"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.daySpinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                String itemDay = (String)parent.getItemAtPosition(position);

                if(itemDay.equals("Сегодня"))   binding.dateText.setText(viewModel.getTodayData());
                if(itemDay.equals("Завтра"))    binding.dateText.setText(viewModel.getAnotherDate(1));
                if(itemDay.equals("Вчера"))     binding.dateText.setText(viewModel.getAnotherDate(-1));

                setData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        binding.daySpinner.setOnItemSelectedListener(itemSelectedListener);
    }


    protected void setData() {
        viewModel.setUrl(res.getString(R.string.urlTuna), binding.dateText.getText().toString());
        viewModel.loadHomeData();
        viewModel.getResult().observe(MainActivity.this, success -> {
            if (success != null) {
                if (binding.phraseDayLL.getVisibility() == View.VISIBLE) {
                    binding.phraseDayLL.setVisibility(View.GONE);
                    binding.scroll.setVisibility(View.VISIBLE);
                    binding.bottomNavigationView.setVisibility(View.VISIBLE);
                    binding.newTaskBtn.setVisibility(View.VISIBLE);
                }

                viewModel.getDataFromJSON(binding.dateText.getText().toString());

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                binding.timetableRecyclerView.setLayoutManager(linearLayoutManager);
                TimetableAdapter timetableAdapter = new TimetableAdapter(MainActivity.this, viewModel.getEvents().getValue());
                binding.timetableRecyclerView.setAdapter(timetableAdapter);

                LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getApplicationContext());
                binding.tasksRecyclerView.setLayoutManager(linearLayoutManager2);
                TaskAdapter adapter = new TaskAdapter(this, viewModel.getTasks().getValue(), "com.example.assistant.views.MainActivity");
                binding.tasksRecyclerView.setAdapter(adapter);

            }
        });


    }


    /*
        Дает возможность увидеть свёрнутые поля - заметка и расписание занятий.
        Изначально они скрыты.
     */
    protected void showTimetableAndNote() {
        //РАСПИСАНИЕ ЗАНЯТИЙ
        binding.timetableBtn.setOnClickListener(view -> {
            if(binding.timetableLayout.getVisibility() == View.GONE)
                binding.timetableLayout.setVisibility(View.VISIBLE);
            else
                binding.timetableLayout.setVisibility(View.GONE);
        });

        //ЗАМЕТКА
        binding.notesBtn.setOnClickListener(view -> {
            if(binding.notesEditText.getVisibility() == View.GONE)
                binding.notesEditText.setVisibility(View.VISIBLE);
            else
                binding.notesEditText.setVisibility(View.GONE);
        });
    }


    public void setDate(View v) {
        new DatePickerDialog(MainActivity.this, d, dateCld.get(Calendar.YEAR),
                dateCld.get(Calendar.MONTH), dateCld.get(Calendar.DAY_OF_MONTH)).show();
    }


    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCld.set(Calendar.YEAR, year);
            dateCld.set(Calendar.MONTH, monthOfYear);
            dateCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate1(year, monthOfYear+1, dayOfMonth, binding.dateText);
            setData();

        }
    };
}