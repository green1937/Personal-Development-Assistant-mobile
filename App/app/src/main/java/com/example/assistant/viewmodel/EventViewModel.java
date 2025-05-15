package com.example.assistant.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.model.Event;
import com.example.assistant.repository.EventRepository;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;


public class EventViewModel extends ViewModel {

    private MutableLiveData<Event> event = new MutableLiveData<>();
    private MutableLiveData<int[]> flagsWeek = new MutableLiveData<>();
    private MutableLiveData<String> formatItem = new MutableLiveData<>();
    private MutableLiveData<String> repeatItem = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();

    private EventRepository repository = new EventRepository();
    private MutableLiveData<Boolean> saveResult = new MutableLiveData<>();


    public LiveData<Event> getEvent() {
        return event;
    }

    public void setEventName(String eventName) {
        Event currentEvent = event.getValue();
        if (currentEvent != null) {
            currentEvent.setEventName(eventName);
            event.setValue(currentEvent);
        }
    }

    public void setEventPlace(String eventPlace) {
        Event currentEvent = event.getValue();
        if (currentEvent != null) {
            currentEvent.setPlace(eventPlace);
            event.setValue(currentEvent);
        }
    }

    public void setStartTime(String startTime) {
        Event currentEvent = event.getValue();
        if (currentEvent != null) {
            currentEvent.setStartTime(startTime);
            event.setValue(currentEvent);
        }
    }

    public void setStopTime(String stopTime) {
        Event currentEvent = event.getValue();
        if (currentEvent != null) {
            currentEvent.setStopTime(stopTime);
            event.setValue(currentEvent);
        }
    }

    public void setUrl(String urlEvent) {
        url.setValue(urlEvent);
    }

    public EventViewModel() {
        event.setValue(new Event(0, 0, 0, "", "", "", "", ""));
    }

    public void onSaveBtnClicked() {
        if (isInputDataValid()) {
            System.out.println("SUCCESS!");
            saveNewEvent();
        }
        else
            System.out.println("NO :(");
    }

    public boolean isInputDataValid() {
        Event currentEvent = event.getValue();
        return currentEvent != null &&
                !currentEvent.getEventName().isEmpty() &&
                !currentEvent.getPlace().isEmpty() &&
                !currentEvent.getStartTime().isEmpty() &&
                !currentEvent.getStopTime().isEmpty() &&
                checkDaysWeek(flagsWeek.getValue()) &&
                checkTimeFormat(currentEvent.getStartTime()) &&
                checkTimeFormat(currentEvent.getStopTime()) &&
                !isTimeStartAfterTimeStop(currentEvent.getStartTime(), currentEvent.getStopTime());
    }


    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }
    public void saveNewEvent() {
        ArrayList<String> allJsonData = new ArrayList<>();
        for (int i=0; i< 7; i++) {
            Event currentEvent = event.getValue();
            int[] flagWeek = flagsWeek.getValue();
            if (flagWeek[i] == 1) {
                String json = null;
                if (repeatItem.getValue().equals("Четную неделю") || repeatItem.getValue().equals("Каждую неделю")) {
                    event.setValue(new Event(1, 2, i, currentEvent.getEventName(), currentEvent.getPlace(), formatItem.getValue(), currentEvent.getStartTime(), currentEvent.getStopTime()));
                    allJsonData.add(new Gson().toJson(event.getValue()));
                }
                if (repeatItem.getValue().equals("Нечетную неделю") || repeatItem.getValue().equals("Каждую неделю")) {
                    event.setValue(new Event(1, 1, i, currentEvent.getEventName(), currentEvent.getPlace(), formatItem.getValue(), currentEvent.getStartTime(), currentEvent.getStopTime()));
                    allJsonData.add(new Gson().toJson(event.getValue()));
                }
            }
        }

        repository.sendEvents(allJsonData, url.getValue()).observeForever(result -> {
            saveResult.setValue(result);
        });

    }



    public boolean checkDaysWeek(int[] flagsForWeek) {
        boolean flag = false;
        if (flagsForWeek != null) {
            for (int i = 0; i < flagsForWeek.length; i++)
                if (flagsForWeek[i] == 1) {  // Проверка на выбранный день
                    flag = true;
                    break;
                }
        }
        return flag;
    }


    public void setFormat(String format) {
        formatItem.setValue(format);
    }
    public void setRepeat(String repeat) {
        repeatItem.setValue(repeat);
    }


    public void setFlagWeek(int[] flags) {
        // Получаем из активити список флагов (нажатых кнопок - дней недели)
        flagsWeek.setValue(flags);
    }


    public boolean checkTimeFormat(String param) {
        SimpleDateFormat sdfTIME = new SimpleDateFormat("HH:mm", Locale.getDefault());
        boolean flag = true;
        if (!param.equals("")) {
            sdfTIME.setLenient(false);
            try { sdfTIME.parse(param); }
            catch (ParseException e) { flag = false; }
        }
        return flag;
    }

    public boolean isTimeStartAfterTimeStop(String start, String stop) {
        DateTimeFormatter dtfTIME = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
        LocalTime timeFromLD = LocalTime.parse(start, dtfTIME);
        LocalTime timeToLD = LocalTime.parse(stop, dtfTIME);
        return timeFromLD.isAfter(timeToLD);
    }

}
