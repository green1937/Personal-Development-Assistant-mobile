package com.example.assistant.views;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.assistant.R;
import com.example.assistant.databinding.ActivityLoginBinding;
import com.example.assistant.databinding.ActivityRegistrationBinding;
import com.example.assistant.viewmodel.LoginViewModel;
import com.example.assistant.viewmodel.RegistrationViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    LoginViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        Resources res = getResources();
        viewModel.setUrl(res.getString(R.string.urlTuna) + "auth/login");

        viewModel.getSaveResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Вы успешно вошли в аккаунт!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "Ошибка входа!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.register.setOnClickListener(v -> startActivity(new Intent(this, RegistrationActivity.class)));


    }
}
