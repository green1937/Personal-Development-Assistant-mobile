package com.example.assistant.wheel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assistant.MainActivity;
import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.diary.DiaryActivity;
import com.example.assistant.plans.PlansActivity;
import com.github.mikephil.charting.charts.RadarChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;



import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
//import com.xxmassdeveloper.mpchartexample.custom.RadarMarkerView;
//import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class WheelActivity extends AppCompatActivity {
    RadarChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        WheelView wheelView = findViewById(R.id.wheelView);

        List<WheelView.WheelSector> sectors = new ArrayList<>();
        Random random = new Random();
        int cnt = 5;

        for (int i = 0; i < cnt; i++) {
            float value = 0.3f + random.nextFloat() * 0.7f; // от 0.3 до 1.0
            int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            sectors.add(new WheelView.WheelSector("Cat " + (i+1), value, color));
        }

        wheelView.setWheelData(sectors);

        bottNavItem();  // Нижнее меню

    }


    private void setData() {

        float max = 100;  // Максимум
        float min = 30;   // Минимум
        int cnt = 17;     // Количество категорий колеса баланса
        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        int i;

        // Цикл по всем категориям
        for (i = 0; i < cnt; i++) {
            int score = (int) ((int) (Math.random() * (max-min)) + min);

            int r = (int) ((int) (Math.random() * 256));  // Red
            int g = (int) ((int) (Math.random() * 256));  // Green
            int b = (int) ((int) (Math.random() * 256));  // Blue

            String nameCtg = "ctg " + r;  //Название категории

            drawCtgForWheel(sets, nameCtg, cnt, i, score, r, g, b);  // Отрисовка категории
        }

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.invalidate();

    }


    /*
        Функция, которая отрисовывает категорию из колеса баланса
        sets      Сет для категорий
        nameCtg   Название категории
        cnt       Количество категорий всего
        i         Порядковый номер категории
        r, g, b   Цвета категории
     */
    protected void drawCtgForWheel(ArrayList<IRadarDataSet> sets, String nameCtg, int cnt, int i, int score, int r, int g, int b) {

        ArrayList<RadarEntry> entries = new ArrayList<>();
        RadarDataSet set = new RadarDataSet(entries, nameCtg);
        int j;
        int flag = 0;

        // Ставим точки (количество точек равно количеству категорий, т.е. cnt)
        for (j=0; j<cnt; j++) {
            if ((i+1 == cnt) && (flag <=1) && (j==0)) {  // Для последного сигмента определяет точку
                entries.add(new RadarEntry(score));
                flag+=1;
            }
            else {
                if ((j >= i) && (flag <=1)) {
                    entries.add(new RadarEntry(score));
                    flag += 1;
                } else {
                    entries.add(new RadarEntry(0));  // Нулевая координата - центр
                }
            }
        }

        set.setColor(Color.rgb(r, g, b));
        set.setFillColor(Color.rgb(r, g, b));
        set.setDrawFilled(true);
        set.setDrawValues(true);

        set.setFillAlpha(230);
        set.setLineWidth(1f);
        set.setDrawHighlightCircleEnabled(true);
        set.setDrawHighlightIndicators(false);

        sets.add(set);

    }

    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_wheel);

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
