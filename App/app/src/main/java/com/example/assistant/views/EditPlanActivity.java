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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assistant.R;
import com.example.assistant.databinding.ActivityEditPlanBinding;
import com.example.assistant.viewmodel.EditPlanViewModel;
import java.util.Calendar;

public class EditPlanActivity extends AppCompatActivity {
    int idPlan;
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    private ActivityEditPlanBinding binding;
    EditPlanViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        idPlan = (int) bundle.getSerializable("id");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_plan);
        viewModel = new ViewModelProvider(this).get(EditPlanViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "plans");
        viewModel.setIdEditPlan(idPlan);

        binding.backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, PlansActivity.class));
        });

        //Грузим данные выбранного плана
        viewModel.loadPlanData();


        viewModel.getResult().observe(this, success -> {
            if (success!=null) {
                Toast.makeText(this, "Данные успешно получены", Toast.LENGTH_SHORT).show();
                //Вывод данных в поля
                viewModel.setPlan();
                binding.planName.setText(viewModel.getPlanName());
                binding.decrPlanEditText.setText(viewModel.getPlanDetails());
                binding.dateFromEditText.setText(viewModel.getStartDate());
                binding.dateToEditText.setText(viewModel.getStopDate());
                binding.scoreEditText.setText(viewModel.getScore());

                //categoriesRecyclerView
                if(viewModel.getCategories() != null) {
                    LinearLayoutManager linearLayoutManagerCtg = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    binding.ctgInPlanRV.setLayoutManager(linearLayoutManagerCtg);
                    PlanCtgAdapter categoriesAdapter = new PlanCtgAdapter(EditPlanActivity.this, viewModel.getCategories());
                    binding.ctgInPlanRV.setAdapter(categoriesAdapter);
                }
                //tasksRecyclerView
                if(viewModel.getTasks() != null) {
                    LinearLayoutManager linearLayoutManagerTask = new LinearLayoutManager(getApplicationContext());
                    binding.allTaskInPlanRV.setLayoutManager(linearLayoutManagerTask);
                    TaskAdapter tasksAdapter = new TaskAdapter(EditPlanActivity.this, viewModel.getTasks(), "com.example.assistant.views.PlansActivity");
                    binding.allTaskInPlanRV.setAdapter(tasksAdapter);
                }
            } else {
                Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
            }
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
        Вывод календарей
     */

    public void setDateFrom(View v) {
        new DatePickerDialog(EditPlanActivity.this, d1, dateFromCld.get(Calendar.YEAR),
                dateFromCld.get(Calendar.MONTH), dateFromCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d1=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFromCld.set(Calendar.YEAR, year);
            dateFromCld.set(Calendar.MONTH, monthOfYear);
            dateFromCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate(year, monthOfYear+1, dayOfMonth, binding.dateFromEditText);
        }
    };


    public void setDateTo(View v) {
        new DatePickerDialog(EditPlanActivity.this, d2, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateToCld.set(Calendar.YEAR, year);
            dateToCld.set(Calendar.MONTH, monthOfYear);
            dateToCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate(year, monthOfYear+1, dayOfMonth, binding.dateToEditText);
        }
    };

}
