package com.example.assistant.views;

import static com.example.assistant.views.NewPlanActivity.setInitialDate;
import static com.example.assistant.views.TimetableActivity.getJsonFromUrl;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assistant.MainActivity;
import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.databinding.ActivityPlansBinding;
import com.example.assistant.viewmodel.PlansViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PlansActivity extends AppCompatActivity {
    String urlAllPlans;
    List<ArrayList<String>> allPlans;
    ArrayList<String> plansActive = new ArrayList<>();
    ArrayList<String> plansArchive = new ArrayList<>();
    LinearLayout filterBtn, filterView;
    ImageButton filterPlansBtn;
    Calendar dateCld = Calendar.getInstance();
    EditText dateForFilterED;

    RecyclerView planRecyclerView;
    LinearLayoutManager linearLayoutManager;
    PlanAdapter planAdapter;




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
                        PlanAdapter planAdapter = new PlanAdapter(PlansActivity.this, viewModel.getPlans());
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

        /*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        bottNavItem();           // Нижнее меню
        addPlan();               // Переход на страницу добавления плана

        filterBtn = findViewById(R.id.filterBtnView);
        filterView = findViewById(R.id.filterView);
        filterPlansBtn = findViewById(R.id.filterPlansBtn);

        Resources res = getResources();
        urlAllPlans = res.getString(R.string.urlTuna) + "plans/full";

        loadJsonFromUrl();       // Получение планов

        showHiddenFilterSett();  // Показ настроек фильтрации планов
        filterAllPlans();
         */
    }

    private void loadJsonFromUrl() {
        new Thread(() -> {
            try {
                String jsonActive = getJsonFromUrl(urlAllPlans + "?status=0");
                String jsonArchive = getJsonFromUrl(urlAllPlans + "?status=1");

                if (jsonActive != null && jsonArchive != null) {
                    runOnUiThread(() -> {
                        showPlans(jsonActive, jsonArchive);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e("THREAD_ERROR", "Ошибка в потоке:", e);
            }
        }).start();
    }

    protected void showPlans(String jsonActive, String jsonArchive) {
        allPlans = new ArrayList<>();
        
        getPlansFromJSON(jsonActive);

        allPlans.add(new ArrayList<>(Collections.singleton("Архивные планы")));
        getPlansFromJSON(jsonArchive);

        // Передача данных планов для отрисовки RecyclerView
        planRecyclerView = findViewById(R.id.allPlansRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        planRecyclerView.setLayoutManager(linearLayoutManager);

        planAdapter = new PlanAdapter(PlansActivity.this, allPlans);
        planRecyclerView.setAdapter(planAdapter);

    }


    protected void getPlansFromJSON(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                ArrayList<String> planData = new ArrayList<>();
                JSONObject planObject = jsonArray.getJSONObject(i);
                planData.add(planObject.getString("id"));
                planData.add(planObject.getString("name"));
                planData.add(planObject.getString("status"));

                allPlans.add(planData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        CheckBox dateCheck = findViewById(R.id.checkBoxDateF);          // По дате
        CheckBox lastDateCheck = findViewById(R.id.checkBoxChangesF);   // По дате последних изм-ий
        dateForFilterED = findViewById(R.id.addDateFilter);

        filterPlansBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(dateCheck.isChecked()) {
                    String dateForFilter = dateForFilterED.getText().toString();
                    System.out.println("checked TRUE");
                    if (dateForFilter.equals("")) {
                        Toast.makeText(getApplicationContext(), "Вы не ввели дату! Фильтрация невозможна", Toast.LENGTH_SHORT).show();
                    }
                    // Поскольку поле не пустое, проверяем дату на корректность
                    else if (checkDateFormat(dateForFilter) == 0) {
                        Toast.makeText(getApplicationContext(), "Планы были отфильтрованы успешно!", Toast.LENGTH_SHORT).show();
                        filterView.setVisibility(View.GONE);
                        filterPlansBtn.setVisibility(View.GONE);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Фильтрация невозможна. Формат даты неверный", Toast.LENGTH_SHORT).show();
                    }

                }
                else if (lastDateCheck.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Планы были отфильтрованы успешно!", Toast.LENGTH_SHORT).show();
                    filterView.setVisibility(View.GONE);
                    filterPlansBtn.setVisibility(View.GONE);

                }
                else {
                    System.out.println("checked FALSE");
                    Toast.makeText(getApplicationContext(), "Вы не выбрали параметр для фильтрации", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    /*
        Функция, проверяющая на то, является ли строка датой формата ДД.ММ.ГГГГ
     */
    public static int checkDateFormat(String param) {
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


    /*
        Вывод календаря при фильтрации планов
     */
    public void setDate(View v) {
        new DatePickerDialog(PlansActivity.this, d, dateCld.get(Calendar.YEAR),
                dateCld.get(Calendar.MONTH), dateCld.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateCld.set(Calendar.YEAR, year);
            dateCld.set(Calendar.MONTH, monthOfYear);
            dateCld.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setInitialDate(year, monthOfYear+1, dayOfMonth, dateForFilterED);
        }
    };


}
