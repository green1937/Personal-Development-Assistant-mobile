package com.example.assistant.ui.diary.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assistant.R;
import com.example.assistant.ui.home.view.SideMenuActivity;
import com.example.assistant.ui.home.view.MainActivity;
import com.example.assistant.ui.plans.view.PlansActivity;
import com.example.assistant.ui.wheel.view.WheelActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AllRecordsDiaryActivity extends AppCompatActivity {
    String urlDiary;
    RecyclerView allRecordRV;
    TextView allRecordsTV, viewBtn;
    ArrayList<String> recordData = new ArrayList<>();
    List<ArrayList<String>> allRecords = new ArrayList<>();

    String allRecordsDataInText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_records_diary);

        allRecordRV = findViewById(R.id.allRecordsDiaryRecyclerView);
        allRecordsTV = findViewById(R.id.continuousViewingRecordsTextView);
        viewBtn = findViewById(R.id.textLL);

        Bundle bundle = getIntent().getExtras();
        allRecords = (List<ArrayList<String>>) bundle.getSerializable("allRecords");
        System.out.println("DATA = " + allRecords);
        outputDiaryToRecyclerView();    // Передача записей в дневнике в RecyclerView
        allRecordsDataInText = getRecordsText();

        bottNavItem();          // Нижнее меню
        backToDiaryActivity();  // Переход на экран с сегодняшней записью
        checkContViewRecords(); // Показ записей "сплошной просмотр"

    }


    protected String getRecordsText() {
        allRecordsDataInText = "";
        if (allRecords.size() != 0) {
            for (int i = 0; i < allRecords.size(); i++) {
                String oneRecordText = "";
                oneRecordText = allRecords.get(i).get(1).toString() + "\n" + allRecords.get(i).get(0).toString() + "\n\n";
                allRecordsDataInText = allRecordsDataInText + oneRecordText;
            }
        }

        return allRecordsDataInText;
    }
    protected void outputDiaryToRecyclerView() {
        // Передача данных для отрисовки RecyclerView
        RecyclerView allRecordsRecyclerView = findViewById(R.id.allRecordsDiaryRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        allRecordsRecyclerView.setLayoutManager(linearLayoutManager);

        AllRecordsAdapter allRecordsAdapterAdapter = new AllRecordsAdapter(AllRecordsDiaryActivity.this, allRecords);
        allRecordsRecyclerView.setAdapter(allRecordsAdapterAdapter);
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
                    allRecordsTV.setText(allRecordsDataInText);


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
