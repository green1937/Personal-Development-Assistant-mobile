package com.example.assistant.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TimetableViewModelFactory implements ViewModelProvider.Factory {
    private Application application;

    public TimetableViewModelFactory(Application application) {
        this.application = application;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TimetableViewModel.class)) {
            try {
                return (T) new TimetableViewModel(application);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}