package com.example.assistant.ui.wheel.view;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.assistant.R;
import com.example.assistant.databinding.ActivityNewCategoriesBinding;
import com.example.assistant.ui.wheel.viewmodel.CategoriesViewModel;
import com.example.assistant.utils.SharedPreferencesHelper;
import com.skydoves.colorpickerview.listeners.ColorListener;


public class NewCategoriesActivity extends AppCompatActivity {

    private ActivityNewCategoriesBinding binding;
    CategoriesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_categories);
        viewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(this, WheelActivity.class)));

        viewModel.setToken(SharedPreferencesHelper.getToken(getApplicationContext()));
        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "categories");

        binding.colorPickerView.setColorListener((ColorListener) (color, fromUser) -> binding.colorNewCtg.setBackgroundColor(color));

        binding.tickBtn.setOnClickListener(v -> {
            if(!binding.nameNewCtg.getText().toString().equals("")) {

                viewModel.setNameCtg(binding.nameNewCtg.getText().toString());

                int colorInt = ((ColorDrawable) binding.colorNewCtg.getBackground()).getColor();

                String colorHex = String.format("#%06X", (0xFFFFFF & colorInt));

                viewModel.setColorCtg(colorHex);
                viewModel.saveData();

                viewModel.getSaveResult().observe(this, success -> {
                    if (success != null) {
                        Toast.makeText(this, "Новая категория успешно сохранена", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, WheelActivity.class));
                    } else {
                        Toast.makeText(this, "Ошибка сохранения!", Toast.LENGTH_SHORT).show();
                    }
                });


            }

        });


    }
}
