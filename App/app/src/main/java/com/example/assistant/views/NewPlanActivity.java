package com.example.assistant.views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.assistant.R;
import com.example.assistant.databinding.ActivityNewPlanBinding;
import com.example.assistant.viewmodel.NewPlanViewModel;
import java.util.Calendar;

public class NewPlanActivity extends AppCompatActivity {
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();

    private ActivityNewPlanBinding binding;
    NewPlanViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_plan);
        viewModel = new ViewModelProvider(this).get(NewPlanViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "plans");

        binding.backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, PlansActivity.class));
        });

        viewModel.getSaveResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, PlansActivity.class));
            } else {
                Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /*
        Вывод календарей и часов для дат и времени при создании задачи
     */

    public void setDateFrom(View v) {
        new DatePickerDialog(NewPlanActivity.this, d1, dateFromCld.get(Calendar.YEAR),
                dateFromCld.get(Calendar.MONTH), dateFromCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d1=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFromCld.set(Calendar.YEAR, year);
            dateFromCld.set(Calendar.MONTH, monthOfYear);
            dateFromCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate(year, monthOfYear+1, dayOfMonth, binding.datePlanFrom);
        }
    };

    public void setDateTo(View v) {
        new DatePickerDialog(NewPlanActivity.this, d2, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateToCld.set(Calendar.YEAR, year);
            dateToCld.set(Calendar.MONTH, monthOfYear);
            dateToCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate(year, monthOfYear+1, dayOfMonth, binding.datePlanTo);

        }
    };

}
