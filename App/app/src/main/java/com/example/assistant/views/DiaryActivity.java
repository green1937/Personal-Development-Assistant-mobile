package com.example.assistant.views;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.assistant.MainActivity;
import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.databinding.ActivityDiaryBinding;
import com.example.assistant.viewmodel.DiaryViewModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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



    private ActivityDiaryBinding binding;
    DiaryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary);
        viewModel = new ViewModelProvider(this).get(DiaryViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "diary");
        binding.dateDiary.setText(viewModel.getTodayDate());
        viewModel.loadAllRecords();
        viewModel.getResult().observe(this, success -> {
            if (success != null) {
                Toast.makeText(this, "Данные успешно получены", Toast.LENGTH_SHORT).show();
                //Вывод данных в поля
                viewModel.setDiary();
                outputTodayRecord();
                //setTodayDiary();
            }
        });
        binding.allRecordsBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("allRecords", (Serializable) viewModel.getAllRecords());
            Intent intent = new Intent(v.getContext(), AllRecordsDiaryActivity.class);
            intent.putExtras(bundle);
            v.getContext().startActivity(intent);
        });

        bottNavItem();
    }
    /*
            Функция передачи текста записи на текущую дату
         */
    protected void outputTodayRecord() {
        flag = 1;

        //Вывод текст записи на сегодня (если есть)
        if (viewModel.getTodayRecord() != null) {
            flag = 0;
            binding.todayRecordEditText.setText(viewModel.getTodayRecord().getText());
        }

        // Добавляем TextWatcher для отслеживания изменений
        binding.todayRecordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Этот метод вызывается перед изменением текста
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Этот метод вызывается во время изменения текста
                // Здесь можно добавить дополнительную логику
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Этот метод вызывается после изменения текста
                if (flag == 1) {
                    flag = 0;
                    viewModel.createRecord(binding.todayRecordEditText.getText().toString());

                    viewModel.getSaveResult().observe(DiaryActivity.this, success -> {
                        if (success != null) {
                            Toast.makeText(DiaryActivity.this, "Новая запись успешно сохранена", Toast.LENGTH_SHORT).show();
                            viewModel.loadAllRecords();
                            viewModel.getResult().observe(DiaryActivity.this, success2 -> {
                                if (success2 != null) {
                                    Toast.makeText(DiaryActivity.this, "Данные успешно получены2", Toast.LENGTH_SHORT).show();
                                    viewModel.setDiary();
                                }
                            });
                        }
                    });
                }
                else {
                    viewModel.updateRecord(binding.todayRecordEditText.getText().toString());
                }
            }
        });
    }


    /*
            Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
            колесо баланса, дневник, боковое/главное меню)
        */
    protected void bottNavItem() {
        binding.bottomNavigationView.setSelectedItemId(R.id.bottom_diary);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
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
}
