package com.example.assistant.ui.wheel.viewmodel;


import android.content.Context;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.repository.GetDataRepository;
import com.example.assistant.repository.GetRequestRepository;
import com.example.assistant.utils.SharedPreferencesHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WheelViewModel extends ViewModel {
    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<Context> context = new MutableLiveData<>();
    private MutableLiveData<String> urlCtg = new MutableLiveData<>();

    private MutableLiveData<String> startDate = new MutableLiveData<>();
    private MutableLiveData<String> stopDate = new MutableLiveData<>();

    private MutableLiveData<String> startDateET = new MutableLiveData<>();
    private MutableLiveData<String> stopDateET = new MutableLiveData<>();

    private GetRequestRepository getRequestRepository = new GetRequestRepository();
    private GetDataRepository repositoryAllCtg = new GetDataRepository();

    private MutableLiveData<String> getResultWheel = new MutableLiveData<>();
    private MutableLiveData<String> getResultCtg = new MutableLiveData<>();

    private MutableLiveData<List<ArrayList<String>>> categories = new MutableLiveData<>();
    private MutableLiveData<String> token = new MutableLiveData<>();
    


    public void setToken(String t) {
        token.setValue(t);
    }

    public void setCtx(Context ctx) {
        context.setValue(ctx);
    }
    public void setUrl(String urlWheel) {
        url.setValue(urlWheel);
    }

    public void setUrlCtg(String urlCtgStr) {
        urlCtg.setValue(urlCtgStr);
    }
    public WheelViewModel() { }


    public LiveData<String> getResultWheel() {
        return getResultWheel;
    }


    public  List<ArrayList<String>> getCtg() {
        return categories.getValue();
    }

    public void getInitialDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate currentDate = LocalDate.now(); // Получаем текущую дату
        LocalDate sevenDaysAgo = currentDate.minus(7, ChronoUnit.DAYS); // Получаем дату 7 дней назад

        // Форматируем даты
        startDate.setValue(sevenDaysAgo.format(formatter));
        stopDate.setValue(currentDate.format(formatter));

        // Отправляем в форму
        startDateET.setValue(sevenDaysAgo.format(formatter2));
        stopDateET.setValue(currentDate.format(formatter2));

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode()
                .put("start_date", startDate.getValue())
                .put("end_date", stopDate.getValue());

        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(json);
            getRequestRepository.sendDataToServer(jsonString, url.getValue(), token.getValue()).observeForever(result -> getResultWheel.setValue(result));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public void setWheel() {
        List<ArrayList<String>> allCategories = new ArrayList<>();
        String responseString = getResultWheel.getValue();
        System.out.println("responseString " + responseString);

        try {
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject ctgData = jsonArray.getJSONObject(i);
                ArrayList<String> oneCategory = new ArrayList<>();

                oneCategory.add(ctgData.getString("name"));
                oneCategory.add(ctgData.getString("points"));
                oneCategory.add(ctgData.getString("color"));
                oneCategory.add("1");

                allCategories.add(oneCategory);
            }
            categories.setValue(allCategories);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public String getStartDate() {
        return startDateET.getValue();
    }

    public String getStopDate() {
        return stopDateET.getValue();
    }




    public void anotherDate(String date, boolean dateFrom) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (dateFrom) startDate.setValue(changeDateFormat(date, inputFormat, outputFormat));
        else stopDate.setValue(changeDateFormat(date, inputFormat, outputFormat));

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode()
                .put("start_date", startDate.getValue())
                .put("end_date", stopDate.getValue());

        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(json);
            System.out.println("SSSSSSSSSSSEEEEEEEEEE data wheel " + jsonString);
            getRequestRepository.sendDataToServer(jsonString, url.getValue(), token.getValue()).observeForever(result -> getResultWheel.setValue(result));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String changeDateFormat(String dateStr, SimpleDateFormat inputFormat, SimpleDateFormat outputFormat) {
        String formattedDate = "";

        try {
            Date date = inputFormat.parse(dateStr);
            formattedDate = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }


    public void setInitialDate(int year, int monthOfYear, int dayOfMonth, EditText editDate, boolean dateFrom) {
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
        anotherDate(dateForEndStr, dateFrom);
    }








    public void getAllCtg() {
        repositoryAllCtg.getDataObj(urlCtg.getValue(), token.getValue()).observeForever(result -> getResultCtg.setValue(result));
    }


    public LiveData<String> getResultCtg() {
        return getResultCtg;
    }
    public List<ArrayList<String>> setAllCtg() {
        List<ArrayList<String>> allCategories = new ArrayList<>();
        String responseString = getResultCtg.getValue();

        try {
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject ctgData = jsonArray.getJSONObject(i);
                ArrayList<String> oneCategory = new ArrayList<>();


                oneCategory.add(ctgData.getString("title"));
                oneCategory.add(ctgData.getString("color"));
                oneCategory.add(ctgData.getString("id"));

                allCategories.add(oneCategory);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return allCategories;

    }
}


