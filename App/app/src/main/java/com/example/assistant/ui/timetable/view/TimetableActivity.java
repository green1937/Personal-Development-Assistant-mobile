package com.example.assistant.ui.timetable.view;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assistant.R;
import com.example.assistant.ui.home.view.SideMenuActivity;
import com.example.assistant.databinding.ActivityTimetableBinding;
import com.example.assistant.ui.diary.view.DiaryActivity;
import com.example.assistant.ui.home.view.MainActivity;
import com.example.assistant.ui.plans.view.PlansActivity;
import com.example.assistant.ui.timetable.viewmodel.TimetableViewModel;
import com.example.assistant.ui.wheel.view.WheelActivity;
import com.example.assistant.utils.SharedPreferencesHelper;



public class TimetableActivity extends AppCompatActivity {

    private TimetableViewModel viewModel;
    private ActivityTimetableBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timetable);
        viewModel = new ViewModelProvider(this).get(TimetableViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        viewModel.setToken(SharedPreferencesHelper.getToken(getApplicationContext()));
        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "events");

        viewModel.loadTimetableData();

        viewModel.getResult().observe(this, success -> {
            if (success!=null) {
                viewModel.setEvents();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                binding.recyclerViewTimetableMonday.setLayoutManager(linearLayoutManager);
                TimetableAdapter timetableAdapter = new TimetableAdapter(TimetableActivity.this, viewModel.getEvents().get(0), SharedPreferencesHelper.getToken(getApplicationContext()));
                binding.recyclerViewTimetableMonday.setAdapter(timetableAdapter);
            } else {
                Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
            }
        });


        // Обработка клика по кнопке
        binding.textLL.setOnClickListener(v -> {
            System.out.println("Click!");
            int currentType = viewModel.getCurrentWeekType().getValue();
            System.out.println("type = " + currentType);
            viewModel.setCurrentWeekType(currentType == 1 ? 0 : 1); //viewModel.setCurrentWeekType(currentType == 0 ? 1 : 0);

            if (currentType==0) binding.textLL.setText("Нечетная неделя");
            else binding.textLL.setText("Четная неделя");

            viewModel.getResult().observe(this, success -> {
                if (success!=null) {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    binding.recyclerViewTimetableMonday.setLayoutManager(linearLayoutManager);
                    TimetableAdapter timetableAdapter = new TimetableAdapter(TimetableActivity.this, viewModel.getEvents().get(currentType), SharedPreferencesHelper.getToken(getApplicationContext()));
                    binding.recyclerViewTimetableMonday.setAdapter(timetableAdapter);
                } else {
                    Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                }
            });

        });

        // кнопка добавления события
        binding.addEventBtn.setOnClickListener(v -> startActivity(new Intent(this, NewEventActivity.class)));

        // кнопка возврата на боковое меню
        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(this, SideMenuActivity.class)));
        // Нижнее меню
        bottNavItem();
    }

    /*
        Нижнее меню
     */
    protected void bottNavItem() {
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
}
