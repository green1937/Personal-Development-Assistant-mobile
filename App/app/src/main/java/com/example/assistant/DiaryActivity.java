package com.example.assistant;

import static com.example.assistant.MainActivity.getCurrDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {
    DateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    String dateCurrStr;
    TextView dateCurrDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        dateCurrDiary = findViewById(R.id.dateDiary);

        bottNavItem();                // Нижнее меню
        dateCurrStr = seeCurrDate();  // Определение текущей даты
        goToAllRecordsActivity();     // Переход на экран всех записей


    }


    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_plans);

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


    /*
        Функция, отвечающая за показ на главной экране и получение текущей даты
     */
    protected String seeCurrDate() {
        dateCurrStr = getCurrDate(formatForDate, dateCurrStr);  // Получение текущей даты
        dateCurrDiary.setText(dateCurrStr);                     // Вывод текущей даты
        return dateCurrStr;
    }


    /*
        Функция, осуществляющая переход на экран со всеми записями
     */
    protected void goToAllRecordsActivity() {
        TextView allRecordsBtn = findViewById(R.id.allRecordsBtn);
        allRecordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AllRecordsDiaryActivity.class));
            }
        });
    }

}
