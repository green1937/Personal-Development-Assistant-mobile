package com.example.assistant.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.assistant.R;
import com.example.assistant.SideMenuActivity;
import com.example.assistant.databinding.ActivityProfileBinding;
import com.example.assistant.viewmodel.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        binding.exit.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(this, SideMenuActivity.class)));

        viewModel.getDataProfile();

        viewModel.getResult().observe(this, success -> {
            if (success != null) {
                Toast.makeText(this, "Данные профиля успешно получены", Toast.LENGTH_SHORT).show();
                if (viewModel.setData().size() >= 2) {
                    binding.userName.setText(viewModel.setData().get(0));
                    binding.email.setText(viewModel.setData().get(1));
                }
            } else {
                Toast.makeText(this, "Ошибка получения!", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
