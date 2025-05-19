package com.example.assistant.mediabook;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.MainActivity;
import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.views.DiaryActivity;
import com.example.assistant.views.PlansActivity;
import com.example.assistant.views.WheelActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MediabookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediabook);

        bottNavItem();
    }

    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

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
                startActivity(new Intent(getApplicationContext(), SideMenuActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            return false;
        });
    }
}
