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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PlansActivity extends AppCompatActivity {
    LinearLayout filterBtn;
    LinearLayout filterView;
    ImageButton filterPlansBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        bottNavItem();  // Нижнее меню
        addPlan();  // Переход на страницу добавления плана

        filterBtn = findViewById(R.id.filterBtnView);
        filterView = findViewById(R.id.filterView);
        filterPlansBtn = findViewById(R.id.filterPlansBtn);

        showHiddenFilterSett();  // Показ настроек фильтрации планов
        filterAllPlans();
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
        Функция, отвечающая за переход
        на страницу добавления нового плана
     */
    protected void addPlan() {
        ImageButton addPlans = findViewById(R.id.addPlansBtn);
        addPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlansActivity.this, NewPlanActivity.class));
            }
        });
    }


    /*
        Дает возможность увидеть свёрнутое поле - фильтрация планов.
     */
    protected void showHiddenFilterSett() {

        filterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (filterView.getVisibility() == View.GONE) {
                    filterView.setVisibility(View.VISIBLE);
                    filterPlansBtn.setVisibility(View.VISIBLE);

                } else {
                    filterView.setVisibility(View.GONE);
                    filterPlansBtn.setVisibility(View.GONE);
                }
            }
        });
    }


    /*
        Фильтрация планов
     */
    protected void filterAllPlans() {

        CheckBox dateCheck = findViewById(R.id.checkBoxDateF);
        EditText dateForFilterED = findViewById(R.id.addDateFilter);

        filterPlansBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(dateCheck.isChecked()) {
                    String dateForFilter = dateForFilterED.getText().toString();
                    System.out.println("checked TRUE");
                    if (dateForFilter.equals("")) {
                        Toast.makeText(getApplicationContext(), "Вы не ввели дату! Фильтрация невозможна", Toast.LENGTH_SHORT).show();
                    }
                    // Поскольку поле не пустое, проверяем дату на корректность
                    else if (checkDateFormat(dateForFilter) == 0 && dateForFilter.length() == 10) {
                        Toast.makeText(getApplicationContext(), "Планы были отфильтрованы успешно!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Фильтрация невозможна. Формат даты неверный", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    System.out.println("checked FALSE");
                    Toast.makeText(getApplicationContext(), "Вы не выбрали параметр для фильтрации", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    protected static int checkDateFormat(String param) {
        SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        int flag = 0;
        if (!param.equals("")) {
            sdfDATE.setLenient(false);
            try {
                sdfDATE.parse(param);
                System.out.println("Valid date");
            } catch (ParseException e) {
                System.out.println("Invalid date");
                flag = 1;
            }
        }
        else {
            flag = 2;
        }
        return flag;
    }

}
