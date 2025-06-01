package com.example.assistant.views;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.databinding.ActivityPlansBinding;
import com.example.assistant.viewmodel.PlansViewModel;


public class PlansActivity extends AppCompatActivity {
    private ActivityPlansBinding binding;
    PlansViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plans);
        viewModel = new ViewModelProvider(this).get(PlansViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "plans");

        viewModel.loadPlanData();


        viewModel.getResultActive().observe(this, success -> {
            if (success!=null) {
                viewModel.getResultArchive().observe(this, success2 -> {
                    if (success2!=null) {
                        viewModel.setPlans();

                        //recyclerview
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        binding.allPlansRecyclerView.setLayoutManager(linearLayoutManager);
                        PlanAdapter planAdapter = new PlanAdapter(PlansActivity.this, viewModel.getPlans(), "com.example.assistant.views.PlansActivity");
                        binding.allPlansRecyclerView.setAdapter(planAdapter);

                    } else {
                        Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        bottNavItem();
        binding.addPlansBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, NewPlanActivity.class));
        });

    }


    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {
        binding.bottomNavigationView.setSelectedItemId(R.id.bottom_plans);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
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
