package com.example.assistant;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DiaryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        bottNavItem();  // Нижнее меню

    }


    protected void bottNavItem() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_plans);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Главная
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
            // Планы
            if (item.getItemId() == R.id.bottom_plans) {
                return true;
            }
            // Колесо баланса
            /*
            if (item.getItemId() == R.id.bottom_wheel) {
                startActivity(new Intent(getApplicationContext(), WheelActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            */

            // Дневник
            if (item.getItemId() == R.id.bottom_diary) {
                startActivity(new Intent(getApplicationContext(), DiaryActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            // Боковое меню
            /*
            if (item.getItemId() == R.id.bottom_mainMenu) {
                startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
             */
            return false;
        });


    }
}
