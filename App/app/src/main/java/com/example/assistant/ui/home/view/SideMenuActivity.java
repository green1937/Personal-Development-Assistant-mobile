package com.example.assistant.ui.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.R;
import com.example.assistant.ui.diary.view.DiaryActivity;
import com.example.assistant.ui.plans.view.PlansActivity;
import com.example.assistant.ui.profile.view.ProfileActivity;
import com.example.assistant.ui.storage.view.StorageActivity;
import com.example.assistant.ui.timetable.view.TimetableActivity;
import com.example.assistant.ui.wheel.view.WheelActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SideMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);

        bottNavItem();    // Нижнее меню
        showProfile();    // Профиль
        showTimetable();  // Переход на экран с расписанием занятий
        showStorage();    // Хранилище

    }


    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_mainMenu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
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
                return true;
            }

            return false;
        });
    }


    protected void showProfile() {
        LinearLayout goProfileActivity = findViewById(R.id.goProfileActivity);
        goProfileActivity.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));
    }


    protected void showTimetable() {
        LinearLayout goTimetableActivity = findViewById(R.id.goTimetableActivity);
        goTimetableActivity.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), TimetableActivity.class)));
    }


    protected void showStorage() {
        LinearLayout goStorageActivity = findViewById(R.id.goStorageActivity);
        goStorageActivity.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), StorageActivity.class)));
    }

}

