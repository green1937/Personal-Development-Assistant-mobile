package com.example.assistant.storage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.MainActivity;
import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.diary.DiaryActivity;
import com.example.assistant.plans.PlansActivity;
import com.example.assistant.wheel.WheelActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StorageActivity extends AppCompatActivity {


    LinearLayout groupLL, noteDiaryLL, taskAndPlanLL, taskLL, planLL;
    String[] groups = { "Дневник", "Заметка", "Задача", "План" };
    String itemGroups;
    ImageButton searchAndSaveIB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        searchAndSaveIB = findViewById(R.id.seeSearchSettingsBtn);

        groupLL = findViewById(R.id.groupLL);
        noteDiaryLL = findViewById(R.id.noteDiaryLL);
        taskAndPlanLL = findViewById(R.id.taskAndPlanLL);
        taskLL = findViewById(R.id.taskLL);
        planLL = findViewById(R.id.planLL);

        bottNavItem();
        showSettingForSearch();

    }


    protected void showSettingForSearch() {
        RelativeLayout searchRL = findViewById(R.id.searchRL);

        searchRL.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (groupLL.getVisibility() == View.VISIBLE) {
                    groupLL.setVisibility(View.GONE);
                    searchAndSaveIB.setImageDrawable(getResources().getDrawable(R.drawable.back));
                    searchAndSaveIB.getBackground().setColorFilter(Color.parseColor("#FFF3A972"),
                            PorterDuff.Mode.DARKEN);
                    searchAndSaveIB.setRotation(270);
                }
                else {
                    groupLL.setVisibility(View.VISIBLE);
                    searchAndSaveIB.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                    searchAndSaveIB.getBackground().setColorFilter(Color.parseColor("#77F400"),
                            PorterDuff.Mode.DARKEN);
                    searchAndSaveIB.setRotation(0);
                    showGroupSettings();
                }
            }
        });
    }


    /*
        Выпадающий список с группами (дневник, заметка, задача, план).
        Показывает соответствующие настройки к каждой группе.
    */
    protected void showGroupSettings() {
        Spinner groupSpinner = findViewById(R.id.storageGroupsSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemGroups = (String)parent.getItemAtPosition(position);

                if(itemGroups.equals("Дневник") || itemGroups.equals("Заметка")) {
                    noteDiaryLL.setVisibility(View.VISIBLE);
                    taskAndPlanLL.setVisibility(View.GONE);
                    taskLL.setVisibility(View.GONE);
                    planLL.setVisibility(View.GONE);
                }

                if(itemGroups.equals("Задача")) {
                    noteDiaryLL.setVisibility(View.VISIBLE);
                    taskAndPlanLL.setVisibility(View.VISIBLE);
                    taskLL.setVisibility(View.VISIBLE);
                    planLL.setVisibility(View.GONE);
                }
                if(itemGroups.equals("План")) {
                    noteDiaryLL.setVisibility(View.VISIBLE);
                    taskAndPlanLL.setVisibility(View.VISIBLE);
                    taskLL.setVisibility(View.GONE);
                    planLL.setVisibility(View.VISIBLE);
                }

                saveSetting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        groupSpinner.setOnItemSelectedListener(itemSelectedListener);
    }



    /*
        Сохранение настроек поиска
     */
    protected void saveSetting() {
        searchAndSaveIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Поиск выполнен", Toast.LENGTH_SHORT).show();
            }
        });

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
