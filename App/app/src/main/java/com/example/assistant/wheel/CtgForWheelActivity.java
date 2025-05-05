package com.example.assistant.wheel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.MainActivity;
import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.diary.DiaryActivity;
import com.example.assistant.plans.PlansActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;


public class CtgForWheelActivity extends AppCompatActivity {
    ColorPickerView colorPickerView;
    TextView colorNewCtgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctg_for_wheel);

        colorPickerView = findViewById(R.id.colorPickerView);
        colorNewCtgView = findViewById(R.id.colorNewCtg);


        bottNavItem();  // Нижнее меню
        backToSideMenu(); // Возвращение назад
        getColorPicker();
        openWindowNewCtg();  // Показ окна создания новой категории колеса баланса
    }


    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //bottomNavigationView.setSelectedItemId(R.id.bottom_wheel);

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

    protected void backToSideMenu() {
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SideMenuActivity.class));
            }
        });
    }



    protected void openWindowNewCtg() {
        LinearLayout openLayout = findViewById(R.id.linL);
        RelativeLayout windowNewCtg = findViewById(R.id.addCtgRelativeLayout);
        TextView closeBtn = findViewById(R.id.closeBtn);
        TextView saveBtn = findViewById(R.id.saveNewCtgBtn);
        EditText nameNewCtg = findViewById(R.id.nameNewCtg);

        openLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openLayout.getVisibility() == View.VISIBLE) {
                    openLayout.setVisibility(View.GONE);
                    windowNewCtg.setVisibility(View.VISIBLE);

                } else {
                    openLayout.setVisibility(View.VISIBLE);
                    windowNewCtg.setVisibility(View.GONE);
                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLayout.setVisibility(View.VISIBLE);
                windowNewCtg.setVisibility(View.GONE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Получение названия категории и ее цвета */
                String name = String.valueOf(nameNewCtg.getText());
                String color = colorNewCtgView.getBackground().toString();

                if (name.equals("")) {
                    Toast.makeText(getApplicationContext(), "Ошибка сохранения! Поле Название не заполнено!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Категория сохранена", Toast.LENGTH_SHORT).show();
                    openLayout.setVisibility(View.VISIBLE);
                    windowNewCtg.setVisibility(View.GONE);
                }
                System.out.println("NAME = " + name + ", COLOR = " + color);


            }
        });



    }
    protected void getColorPicker() {
        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color, boolean fromUser) {
                colorNewCtgView.setBackgroundColor(color);

            }
        });
    }

}
