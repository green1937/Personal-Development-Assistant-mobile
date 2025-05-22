package com.example.assistant.views;

import static java.lang.Integer.parseInt;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.assistant.MainActivity;
import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.databinding.ActivityStorageBinding;
import com.example.assistant.tasks.TaskAdapter;
import com.example.assistant.viewmodel.StorageViewModel;

import java.util.Calendar;

public class StorageActivity extends AppCompatActivity {
    Calendar dateToCld = Calendar.getInstance();
    Calendar dateFromCld = Calendar.getInstance();
    Calendar doneDateToCld = Calendar.getInstance();
    Calendar doneDateFromCld = Calendar.getInstance();

    String[] groups = { "Дневник", "Заметка", "Задача", "План" };
    String[] variants = { "Не важно", "Да", "Нет" };
    String itemGroups, itemIsRepeated, itemBelongsToPlan, itemStatus;
    private ActivityStorageBinding binding;
    StorageViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_storage);
        viewModel = new ViewModelProvider(this).get(StorageViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "storage");

        bottNavItem();
        showSettingForSearch();

    }


    protected void showSettingForSearch() {
        binding.searchRL.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (binding.groupLL.getVisibility() == View.VISIBLE) {
                    System.out.println("here1");
                    binding.groupLL.setVisibility(View.GONE);
                    binding.seeSearchSettingsBtn.setImageDrawable(getResources().getDrawable(R.drawable.back));
                    binding.seeSearchSettingsBtn.getBackground().setColorFilter(Color.parseColor("#FFF3A972"),
                            PorterDuff.Mode.DARKEN);
                    binding.seeSearchSettingsBtn.setRotation(270);
                }
                else {
                    System.out.println("here2");
                    binding.groupLL.setVisibility(View.VISIBLE);
                    binding.seeSearchSettingsBtn.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                    binding.seeSearchSettingsBtn.getBackground().setColorFilter(Color.parseColor("#77F400"),
                            PorterDuff.Mode.DARKEN);
                    binding.seeSearchSettingsBtn.setRotation(0);
                    showGroupSettings();
                    saveSetting();
                }
            }
        });
    }


    /*
        Выпадающий список с группами (дневник, заметка, задача, план).
        Показывает соответствующие настройки к каждой группе.
    */
    protected void showGroupSettings() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.storageGroupsSpinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemGroups = (String)parent.getItemAtPosition(position);

                if(itemGroups.equals("Дневник") || itemGroups.equals("Заметка")) {
                    binding.forAllLL.setVisibility(View.VISIBLE);
                    binding.planAndTaskLL.setVisibility(View.GONE);
                    binding.taskLL.setVisibility(View.GONE);
                }

                if(itemGroups.equals("Задача")) {
                    binding.forAllLL.setVisibility(View.VISIBLE);
                    binding.planAndTaskLL.setVisibility(View.VISIBLE);
                    binding.taskLL.setVisibility(View.VISIBLE);
                    showStatus();
                    showIsRepeatedSpinner();
                    showBelongsToPlan();
                }
                if(itemGroups.equals("План")) {
                    binding.forAllLL.setVisibility(View.VISIBLE);
                    binding.planAndTaskLL.setVisibility(View.VISIBLE);
                    binding.taskLL.setVisibility(View.GONE);
                    showStatus();
                }
                viewModel.setEntityTypes(itemGroups);

                //saveSetting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        binding.storageGroupsSpinner.setOnItemSelectedListener(itemSelectedListener);
    }



    /*
        Сохранение настроек поиска
     */
    protected void saveSetting() {
        System.out.println("HERE 3");
        if (binding.groupLL.getVisibility() == View.VISIBLE) {
            binding.seeSearchSettingsBtn.setOnClickListener(v -> {

                if (binding.taskLL.getVisibility() == View.VISIBLE) {

                    if (binding.minPoints.getText().toString().equals("")) viewModel.setMinPoints(null);
                    else viewModel.setMinPoints(parseInt(binding.minPoints.getText().toString()));

                    if (binding.maxPoints.getText().toString().equals("")) viewModel.setMaxPoints(null);
                    else viewModel.setMaxPoints(parseInt(binding.maxPoints.getText().toString()));
                    //viewModel.setMinPoints(parseInt(binding.minPoints.getText().toString()));
                    //viewModel.setMaxPoints(parseInt(binding.maxPoints.getText().toString()));
                }

                viewModel.onSaveBtnClicked();
                viewModel.getPostResult().observe(this, success -> {
                    if (success != null) {
                        /* Передаем данные в RecyclerView */
                        for (int i=0; i<viewModel.getEntityType().size(); i++) {
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            binding.storageRV.setLayoutManager(linearLayoutManager);

                            if (viewModel.getEntityType().get(i).equals("task")) {
                                TaskAdapter adapter = new TaskAdapter(this, viewModel.getTask(), "com.example.assistant.views.StorageActivity");
                                binding.storageRV.setAdapter(adapter);
                            }
                            if (viewModel.getEntityType().get(i).equals("plan")) {
                                PlanAdapter adapter = new PlanAdapter(this, viewModel.getPlan(), "com.example.assistant.views.StorageActivity");
                                binding.storageRV.setAdapter(adapter);
                            }

                            if (viewModel.getEntityType().get(i).equals("diary")) {
                                AllRecordsAdapter adapter = new AllRecordsAdapter(this, viewModel.getDiaryOrNotes("diary"));
                                binding.storageRV.setAdapter(adapter);
                            }

                            if (viewModel.getEntityType().get(i).equals("note")) {
                                AllRecordsAdapter adapter = new AllRecordsAdapter(this, viewModel.getDiaryOrNotes("notes"));
                                binding.storageRV.setAdapter(adapter);
                            }
                        }

                        System.out.println("SEE RESULT ---POST---  " + success);
                        Toast.makeText(getApplicationContext(), "Поиск выполнен", Toast.LENGTH_SHORT).show();
                        binding.groupLL.setVisibility(View.GONE);
                        binding.seeSearchSettingsBtn.setImageDrawable(getResources().getDrawable(R.drawable.back));
                        binding.seeSearchSettingsBtn.getBackground().setColorFilter(Color.parseColor("#FFF3A972"),
                                PorterDuff.Mode.DARKEN);
                        binding.seeSearchSettingsBtn.setRotation(270);

                    }
                    else Toast.makeText(getApplicationContext(), "Ошибка получения!", Toast.LENGTH_SHORT).show();
                });
            });

        }


    }


    protected void showIsRepeatedSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, variants);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerIsRepeated.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemIsRepeated = (String)parent.getItemAtPosition(position);
                if (itemIsRepeated.equals("Не важно")) viewModel.setIsRepeated(null);
                if (itemIsRepeated.equals("Да")) viewModel.setIsRepeated(true);
                else viewModel.setIsRepeated(false);

                //saveSetting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        binding.spinnerIsRepeated.setOnItemSelectedListener(itemSelectedListener);
    }

    protected void showStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, variants);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerStatus.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemStatus = (String)parent.getItemAtPosition(position);

                if (itemStatus.equals("Не важно")) viewModel.setStatus(null);
                if (itemStatus.equals("Да")) viewModel.setStatus(1L);
                if (itemStatus.equals("Нет")) viewModel.setStatus(0L);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        binding.spinnerStatus.setOnItemSelectedListener(itemSelectedListener);
    }


    protected void showBelongsToPlan() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, variants);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBelongsToPlan.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemBelongsToPlan = (String)parent.getItemAtPosition(position);
                if (itemBelongsToPlan.equals("Не важно")) viewModel.setBelongsToPlan(null);
                if (itemBelongsToPlan.equals("Да")) viewModel.setBelongsToPlan(true);
                if (itemBelongsToPlan.equals("Нет")) viewModel.setBelongsToPlan(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        binding.spinnerBelongsToPlan.setOnItemSelectedListener(itemSelectedListener);
    }



    /*
        Функция, отвечающая за работу нижнего меню - переход на другие активности (главная, планы,
        колесо баланса, дневник, боковое/главное меню)
    */
    protected void bottNavItem() {

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






    /*
        Вывод календарей и часов для дат и времени при создании задачи
     */

    public void setDateFrom(View v) {
        new DatePickerDialog(StorageActivity.this, d1, dateFromCld.get(Calendar.YEAR),
                dateFromCld.get(Calendar.MONTH), dateFromCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d1=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFromCld.set(Calendar.YEAR, year);
            dateFromCld.set(Calendar.MONTH, monthOfYear);
            dateFromCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate(year, monthOfYear+1, dayOfMonth, binding.startDate);
        }
    };

    public void setDateTo(View v) {
        new DatePickerDialog(StorageActivity.this, d2, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateToCld.set(Calendar.YEAR, year);
            dateToCld.set(Calendar.MONTH, monthOfYear);
            dateToCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate(year, monthOfYear+1, dayOfMonth, binding.stopDate);

        }
    };


    public void setDoneDateFrom(View v) {
        new DatePickerDialog(StorageActivity.this, d11, dateFromCld.get(Calendar.YEAR),
                dateFromCld.get(Calendar.MONTH), dateFromCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d11=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFromCld.set(Calendar.YEAR, year);
            dateFromCld.set(Calendar.MONTH, monthOfYear);
            dateFromCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate(year, monthOfYear+1, dayOfMonth, binding.doneStartDate);
        }
    };

    public void setDoneDateTo(View v) {
        new DatePickerDialog(StorageActivity.this, d22, dateToCld.get(Calendar.YEAR),
                dateToCld.get(Calendar.MONTH), dateToCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d22=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateToCld.set(Calendar.YEAR, year);
            dateToCld.set(Calendar.MONTH, monthOfYear);
            dateToCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            viewModel.setInitialDate(year, monthOfYear+1, dayOfMonth, binding.doneStopDate);

        }
    };

}
