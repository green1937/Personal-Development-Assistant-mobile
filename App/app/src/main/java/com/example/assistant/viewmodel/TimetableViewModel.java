package com.example.assistant.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.repository.TimetableRepository;
import com.example.assistant.utils.Callback;

import java.util.ArrayList;
import java.util.List;


public class TimetableViewModel extends ViewModel {
    private MutableLiveData<Integer> currentWeekType = new MutableLiveData<>(0); // 0 - четная, 1 - нечетная
    private MutableLiveData<List<List<ArrayList<String>>>> allEventsInTwoWeek = new MutableLiveData<>();
    private MutableLiveData<List<ArrayList<String>>> allEventsInWeek = new MutableLiveData<>();
    private TimetableRepository repository;

    public TimetableViewModel(Application application) throws InterruptedException {
        System.out.println("viewModel initial   ---------- ");
        repository = new TimetableRepository(application);
        List<List<ArrayList<String>>> data;
        data = loadData(); // Загружаем данные при создании ViewModel


    }


    // Получаем неделю (чет, нечет)
    public LiveData<List<ArrayList<String>>> getAllEventsInWeek() {
        int type = currentWeekType.getValue();
        allEventsInWeek.setValue(allEventsInTwoWeek.getValue().get(type));
        return allEventsInWeek;
    }


    // Передача параметра недели
    public void setCurrentWeekType(int type) {
        currentWeekType.setValue(type);
    }


    // Получаем тип недели (чет, нечет)
    public LiveData<Integer> getCurrentWeekType() {
        return currentWeekType;
    }

    // Загрузка расписания за две недели
    private List<List<ArrayList<String>>> loadData() throws InterruptedException {
        List<List<ArrayList<String>>> events = new ArrayList<>();
        repository.getTimetableData(new Callback() {
            @Override
            public void onSuccess(List<List<ArrayList<String>>> data) {
                events.addAll(data);
            }

            @Override
            public void onFailure(String error) {
                // Обработка ошибок
            }
        });
        Thread.sleep(1500);
        allEventsInTwoWeek.setValue(events); //Расписание на ДВЕ недели
        allEventsInWeek.setValue(allEventsInTwoWeek.getValue().get(currentWeekType.getValue()));
        return events;

    }
}