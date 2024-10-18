package com.example.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DiaryActivity extends AppCompatActivity {
    LinearLayout filterBtn, filterView;
    ImageButton filterRecordsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        bottNavItem();  // Нижнее меню

        filterBtn = findViewById(R.id.filterBtnView);
        filterView = findViewById(R.id.filterView);
        filterRecordsBtn = findViewById(R.id.filterRecordsBtn);

        showHiddenFilterSett();  // Показ настроек фильтрации планов
        filterAllRecords();

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



    /*
        Дает возможность увидеть свёрнутое поле - фильтрация записей в дневнике.
     */
    protected void showHiddenFilterSett() {

        filterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (filterView.getVisibility() == View.GONE) {
                    filterView.setVisibility(View.VISIBLE);
                    filterRecordsBtn.setVisibility(View.VISIBLE);

                } else {
                    filterView.setVisibility(View.GONE);
                    filterRecordsBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void filterAllRecords() {

        CheckBox textCheck = findViewById(R.id.checkBoxTextF);
        CheckBox audioCheck = findViewById(R.id.checkBoxAudioF);

        filterRecordsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(textCheck.isChecked() && !audioCheck.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Фильтрация по типу: текстовые записи", Toast.LENGTH_SHORT).show();
                }

                if(audioCheck.isChecked() && !textCheck.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Фильтрация по типу: аудио записи", Toast.LENGTH_SHORT).show();
                }

                if( (textCheck.isChecked() && audioCheck.isChecked()) || (!textCheck.isChecked() && !audioCheck.isChecked()) ) {
                    Toast.makeText(getApplicationContext(), "Фильтрация по типу: ВСЕ записи", Toast.LENGTH_SHORT).show();
                }

                filterView.setVisibility(View.GONE);
                filterRecordsBtn.setVisibility(View.GONE);
            }
        });

    }


}
