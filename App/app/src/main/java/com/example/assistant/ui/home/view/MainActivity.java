package com.example.assistant.ui.home.view;


import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;


import com.example.assistant.R;
import com.example.assistant.databinding.ActivityMainBinding;
import com.example.assistant.ui.auth.view.LoginActivity;
import com.example.assistant.ui.diary.view.DiaryActivity;
import com.example.assistant.ui.home.viewmodel.MainViewModel;
import com.example.assistant.ui.plans.view.PlansActivity;
import com.example.assistant.ui.timetable.view.TimetableAdapter;
import com.example.assistant.ui.wheel.view.WheelActivity;
import com.example.assistant.utils.SharedPreferencesHelper;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Calendar dateCld = Calendar.getInstance();
    private ActivityMainBinding binding;
    MainViewModel viewModel;
    Resources res;
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SharedPreferencesHelper.saveToken(getApplicationContext(), null);

        if(SharedPreferencesHelper.getToken(getApplicationContext()).isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        flag = 3;
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
        viewModel.setToken(SharedPreferencesHelper.getToken(getApplicationContext()));
        viewModel.setUrl(res.getString(R.string.urlTuna), viewModel.getTodayData());
        viewModel.setNoteUrl(res.getString(R.string.urlTuna) + "notes");

        binding.dateText.setText(viewModel.getTodayData());

        bottNavItem();              // Нижнее меню
        showTimetableAndNote();     // Показ по нажатию расписания и заметки
        spinnerDays();              // Выпадающий список с днями
        binding.newTaskBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewTaskActivity.class)));
        outputTodayRecord();
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

                binding.notesBtn.setVisibility(View.VISIBLE);
                if(viewModel.noteVisibility(binding.dateText.getText().toString()) == 1) {
                    binding.notesBtn.setVisibility(View.GONE);
                    binding.notesEditText.setVisibility(View.GONE);
                }

                if (viewModel.noteVisibility(binding.dateText.getText().toString()) == 3) {
                    binding.notesEditText.setFocusable(false);
                    binding.notesEditText.setClickable(false);
                }


                viewModel.getDataFromJSON(binding.dateText.getText().toString());

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                binding.timetableRecyclerView.setLayoutManager(linearLayoutManager);
                TimetableAdapter timetableAdapter = new TimetableAdapter(MainActivity.this, viewModel.getEvents().getValue(), SharedPreferencesHelper.getToken(getApplicationContext()));
                binding.timetableRecyclerView.setAdapter(timetableAdapter);

                LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getApplicationContext());
                binding.tasksRecyclerView.setLayoutManager(linearLayoutManager2);
                TaskAdapter adapter = new TaskAdapter(this, viewModel.getTasks().getValue(), "com.example.assistant.ui.home.view.MainActivity", SharedPreferencesHelper.getToken(getApplicationContext()));
                binding.tasksRecyclerView.setAdapter(adapter);

                if (viewModel.getNote().getValue().equals("null")) {
                    System.out.println("На сегодня НЕТ заметки!");
                    //binding.notesEditText.setText("");
                    //viewModel.createNote();

                }


            }
        });





    }


    /*
        Функция передачи текста записи на текущую дату
     */
    protected void outputTodayRecord() {
        //flag = 1;

        //Вывод текст записи на сегодня (если есть)
       /* if (viewModel.getNote().getValue() != null && !viewModel.getNote().getValue().equals("null")) {
            System.out.println("Запись уже есть! Выводим его...");
            flag = 0;
            binding.notesEditText.setText(viewModel.getNote().getValue());
        }*/

        if (viewModel.getNote().getValue() != null) {// && !viewModel.getNote().getValue().equals("null")) {
            System.out.println("Запись уже есть! Выводим его...");
            flag = 0;
            binding.notesEditText.setText(viewModel.getNote().getValue());
        }

        // Добавляем TextWatcher для отслеживания изменений
        binding.notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Этот метод вызывается перед изменением текста


                    //viewModel.createRecord(binding.notesEditText.getText().toString());
                    /*
                    viewModel.getSaveResult().observe(MainActivity.this, success -> {
                        if (success != null) {
                            Toast.makeText(MainActivity.this, "Новая запись успешно сохранена", Toast.LENGTH_SHORT).show();
                            viewModel.loadAllRecords();
                            viewModel.getResult().observe(MainActivity.this, success2 -> {
                                if (success2 != null) {
                                    Toast.makeText(MainActivity.this, "Данные успешно получены2", Toast.LENGTH_SHORT).show();
                                    viewModel.setDiary();
                                }
                            });
                        }
                    });

                     */

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Этот метод вызывается во время изменения текста
                // Здесь можно добавить дополнительную логику
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("\n 0. WE ARE HERE \n");

                if(flag == 3) {
                    System.out.println("1. Новая note");
                    viewModel.createNote(binding.notesEditText.getText().toString());
                    flag = 0;
                    setData();
                }
                else {
                    // Этот метод вызывается после изменения текста

                    System.out.println("2. Изменения в note = " + s.toString());
                    System.out.println(" + то что в поле = " + binding.notesEditText.getText());
                    //viewModel.updateRecord(binding.todayRecordEditText.getText().toString());
                }
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