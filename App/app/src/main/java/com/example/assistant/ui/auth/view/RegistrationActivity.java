package com.example.assistant.ui.auth.view;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.example.assistant.R;
import com.example.assistant.databinding.ActivityRegistrationBinding;
import com.example.assistant.ui.auth.viewmodel.RegistrationViewModel;



public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;
    RegistrationViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "auth/register");

        viewModel.getSaveResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Вы успешно зарегистрированы!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this, "Ошибка регистрации!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });


    }
}
