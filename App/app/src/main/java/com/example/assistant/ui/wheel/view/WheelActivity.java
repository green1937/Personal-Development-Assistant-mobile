package com.example.assistant.ui.wheel.view;

import static java.lang.Integer.parseInt;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assistant.R;
import com.example.assistant.ui.home.view.SideMenuActivity;
import com.example.assistant.databinding.ActivityWheelBinding;
import com.example.assistant.ui.diary.view.DiaryActivity;
import com.example.assistant.ui.plans.view.PlansActivity;
import com.example.assistant.ui.wheel.viewmodel.WheelViewModel;
import com.example.assistant.ui.home.view.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class WheelActivity extends AppCompatActivity {
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    private ActivityWheelBinding binding;
    WheelViewModel viewModel;
    WheelView wheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_wheel);
        viewModel = new ViewModelProvider(this).get(WheelViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "wheel");
        viewModel.setUrlCtg(res.getString(R.string.urlTuna) + "categories");

        binding.dateWheelFrom.setText(viewModel.getStartDate());
        binding.dateWheelTo.setText(viewModel.getStopDate());

        viewModel.getInitialDate();

        viewModel.getResultWheel().observe(this, success -> {
            if (success!=null) {
                viewModel.setWheel();

                /*
                LinearLayoutManager linearLayoutManagerCtg = new LinearLayoutManager(getApplicationContext());
                binding.ctgRV.setLayoutManager(linearLayoutManagerCtg);
                WheelCtgAdapter categoriesAdapter = new WheelCtgAdapter(WheelActivity.this, viewModel.getCtg());
                binding.ctgRV.setAdapter(categoriesAdapter);
                 */

                List<WheelView.WheelSector> sectors = new ArrayList<>();

                for (int i = 0; i < viewModel.getCtg().size(); i++) {
                    if (viewModel.getCtg().get(i).get(3).equals("1")) {
                        String name = viewModel.getCtg().get(i).get(0);
                        int color = Color.parseColor(viewModel.getCtg().get(i).get(2));
                        int point = parseInt(viewModel.getCtg().get(i).get(1));
                        sectors.add(new WheelView.WheelSector(name, point, color));
                    }
                }
                binding.wheelView.setWheelData(sectors);
            }
            else {
                Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
            }
        });



        viewModel.getAllCtg();

        viewModel.getResultCtg().observe(this, success -> {
            if (success != null) {
                Toast.makeText(this, "Все категории успешно получены", Toast.LENGTH_SHORT).show();
                LinearLayoutManager linearLayoutManagerCtg = new LinearLayoutManager(getApplicationContext());
                binding.ctgRV.setLayoutManager(linearLayoutManagerCtg);
                WheelCtgAdapter categoriesAdapter = new WheelCtgAdapter(WheelActivity.this, viewModel.setAllCtg());
                binding.ctgRV.setAdapter(categoriesAdapter);

            } else {
                Toast.makeText(this, "Ошибка получения!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.addNewCtg.setOnClickListener(v -> startActivity(new Intent(this, NewCategoriesActivity.class)));

        bottNavItem();  // Нижнее меню

    }

    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        binding.bottomNavigationView.setSelectedItemId(R.id.bottom_wheel);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
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


    /*
        Вывод календарей и часов для дат и времени при создании задачи
     */

    public void setDateFrom(View v) {
        new DatePickerDialog(WheelActivity.this, d1, dateFromCld.get(Calendar.YEAR),
                dateFromCld.get(Calendar.MONTH), dateFromCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d1=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFromCld.set(Calendar.YEAR, year);
            dateFromCld.set(Calendar.MONTH, monthOfYear);
            dateFromCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate(year, monthOfYear+1, dayOfMonth, binding.dateWheelFrom, true);
        }
    };


    public void setDateTo(View v) {
        new DatePickerDialog(WheelActivity.this, d2, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateToCld.set(Calendar.YEAR, year);
            dateToCld.set(Calendar.MONTH, monthOfYear);
            dateToCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate(year, monthOfYear+1, dayOfMonth, binding.dateWheelTo, false);
        }
    };
}
