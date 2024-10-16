package com.example.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PlansActivity extends AppCompatActivity {
    SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    LinearLayout filterBtn;
    LinearLayout filterView;
    ImageButton filterPlansBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_plans);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
            if (item.getItemId() == R.id.bottom_plans) {
                return true;
            }
            return false;
        });


        filterBtn = findViewById(R.id.filterBtnView);
        filterView = findViewById(R.id.filterView);
        filterPlansBtn = findViewById(R.id.filterPlansBtn);

        showHiddenFilterSett();  // Показ настроек фильтрации планов
        filterAllPlans();
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



    protected int checkDateFormat(String param) {
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
