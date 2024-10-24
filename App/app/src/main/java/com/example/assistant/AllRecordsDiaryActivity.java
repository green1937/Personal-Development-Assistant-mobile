package com.example.assistant;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AllRecordsDiaryActivity extends AppCompatActivity {
    RecyclerView allRecordRV;
    TextView allRecordsTV, viewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_records_diary);

        allRecordRV = findViewById(R.id.allRecordsDiaryRecyclerView);
        allRecordsTV = findViewById(R.id.continuousViewingRecordsTextView);
        viewBtn = findViewById(R.id.textLL);

        bottNavItem();          // Нижнее меню
        backToDiaryActivity();  // Переход на экран с сегодняшней записью
        checkContViewRecords(); // Показ записей "сплошной просмотр"

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
        Функция, осуществляющая переход на экран с сегодняшней записью
     */
    protected void backToDiaryActivity() {
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DiaryActivity.class));
            }
        });
    }


    /*
        Функция, отвечающая за показ всех записей при выборе "Сплошной просмотр"
     */
    protected void checkContViewRecords() {

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Если выбран "Сплошной просмотр"
                if (allRecordRV.getVisibility() == View.VISIBLE) {

                    allRecordsTV.setVisibility(View.VISIBLE);
                    allRecordRV.setVisibility(View.GONE);
                    viewBtn.getBackground().setColorFilter(Color.parseColor("#FFEFDACB"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки

                    allRecordsTV.setText("Это сплошной просмотр всех существующих в дневнике записей");  // Пример

                } else {

                    allRecordRV.setVisibility(View.VISIBLE);
                    allRecordsTV.setVisibility(View.GONE);
                    viewBtn.getBackground().setColorFilter(Color.parseColor("#FFF0F0F0"),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки
                }
            }
        });

    }

}
