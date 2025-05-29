package com.example.assistant.viewmodel;

import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.model.NewPlan;
import com.example.assistant.repository.NewObjectRepository;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewPlanViewModel extends ViewModel {

    private MutableLiveData<NewPlan> newPlan = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();

    private NewObjectRepository repository = new NewObjectRepository();
    private MutableLiveData<Boolean> saveResult = new MutableLiveData<>();


    public LiveData<NewPlan> getNewPlan() {
        return newPlan;
    }

    public void setNewPlanName(String planName) {
        NewPlan currentPlan = newPlan.getValue();
        if (currentPlan != null) {
            currentPlan.setName(planName);
            newPlan.setValue(currentPlan);
        }
    }

    public void setNewPlanDetails(String planDetails) {
        NewPlan currentPlan = newPlan.getValue();
        if (currentPlan != null) {
            currentPlan.setDetails(planDetails);
            newPlan.setValue(currentPlan);
        }
    }

    public void setStartDate(String startDate) {
        NewPlan currentPlan = newPlan.getValue();
        if (currentPlan != null) {
            currentPlan.setStartDate(startDate);
            newPlan.setValue(currentPlan);
        }
    }

    public void setStopDate(String stopDate) {
        NewPlan currentPlan = newPlan.getValue();
        if (currentPlan != null) {
            currentPlan.setStopDate(stopDate);
            newPlan.setValue(currentPlan);
        }
    }

    public void setUrl(String urlPlan) {
        url.setValue(urlPlan);
    }

    public NewPlanViewModel() {
        newPlan.setValue(new NewPlan(0, "", "", "", ""));
    }

    public void onSaveBtnClicked() {
        if (isInputDataValid()) {
            System.out.println("SUCCESS!");
            saveNewPlan();
        }
        else
            System.out.println("NO :(");
    }

    public boolean isInputDataValid() {
        NewPlan currentPlan = newPlan.getValue();
        return currentPlan != null &&
                !currentPlan.getName().isEmpty() &&
                !currentPlan.getStartDate().isEmpty() &&
                !currentPlan.getStopDate().isEmpty() &&
                checkDateFormat(currentPlan.getStartDate()) &&
                checkDateFormat(currentPlan.getStopDate()) &&
                !isDateStartAfterDateStop(currentPlan.getStartDate(), currentPlan.getStopDate());
    }


    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }
    public void saveNewPlan() {
        ArrayList<String> allJsonData = new ArrayList<>();
        NewPlan currentPlan = newPlan.getValue();
        newPlan.setValue(new NewPlan(1, currentPlan.getName(), currentPlan.getDetails(), changeDateFormat(currentPlan.getStartDate()), changeDateFormat(currentPlan.getStopDate())));
        allJsonData.add(new Gson().toJson(newPlan.getValue()));
        System.out.println(allJsonData + " " + url.getValue());
        repository.sendDataObj(allJsonData, url.getValue(), "POST").observeForever(result -> {
            saveResult.setValue(result);
        });
        System.out.println("end ");
    }

    public String changeDateFormat(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = "";

        try {
            Date date = inputFormat.parse(dateStr);
            formattedDate = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }


    public boolean checkDateFormat(String param) {
        SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        boolean flag = true;
        if (!param.equals("")) {
            sdfDATE.setLenient(false);
            try { sdfDATE.parse(param); }
            catch (ParseException e) { flag = false; }
        }
        return flag;
    }

    public boolean isDateStartAfterDateStop(String start, String stop) {
        DateTimeFormatter dtfDATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());
        LocalDate dateFromLD = LocalDate.parse(start, dtfDATE);
        LocalDate dateToLD = LocalDate.parse(stop, dtfDATE);
        return dateFromLD.isAfter(dateToLD);
    }



    public void setInitialDate(int year, int monthOfYear, int dayOfMonth, EditText editDate) {
        String dateForEndStr;
        if (dayOfMonth < 10 && monthOfYear < 10) {
            dateForEndStr = "0" + dayOfMonth + "." + "0" + monthOfYear + "." + year;
        }
        else {
            if (dayOfMonth > 9 && monthOfYear < 10) {
                dateForEndStr = dayOfMonth + "." + "0" + monthOfYear + "." + year;
            } else if (dayOfMonth < 10 && monthOfYear > 9) {
                dateForEndStr = "0" + dayOfMonth + "." + monthOfYear + "." + year;
            } else {
                dateForEndStr = dayOfMonth + "." + monthOfYear + "." + year;
            }
        }
        editDate.setText(dateForEndStr);
    }
}
