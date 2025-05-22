package com.example.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.views.DiaryActivity;
import com.example.assistant.views.BookmarkActivity;
import com.example.assistant.phrases.DayPhrasesActivity;
import com.example.assistant.views.PlansActivity;
import com.example.assistant.views.StorageActivity;
import com.example.assistant.views.TimetableActivity;
import com.example.assistant.wheel.CtgForWheelActivity;
import com.example.assistant.views.WheelActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SideMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);

        bottNavItem();    // Нижнее меню
        getColor();       // Переход на экран с категориями колеса баланса
        showTimetable();  // Переход на экран с расписанием занятий
        showStorage();
        showMediabook();
        showPhrase();

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

    protected void getColor() {
        LinearLayout goCtgActivity = findViewById(R.id.goCtgActivity);
        goCtgActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CtgForWheelActivity.class));
            }
        });
    }

    protected void showTimetable() {
        LinearLayout goTimetableActivity = findViewById(R.id.goTimetableActivity);
        goTimetableActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TimetableActivity.class));
            }
        });
    }


    protected void showStorage() {
        LinearLayout goStorageActivity = findViewById(R.id.goStorageActivity);
        goStorageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StorageActivity.class));
            }
        });
    }

    protected void showMediabook() {
        LinearLayout goMediabookActivity = findViewById(R.id.goMediabookActivity);
        goMediabookActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BookmarkActivity.class));
            }
        });
    }

    protected void showPhrase() {
        LinearLayout goPhraseActivity = findViewById(R.id.goPhraseActivity);
        goPhraseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DayPhrasesActivity.class));
            }
        });
    }
}

