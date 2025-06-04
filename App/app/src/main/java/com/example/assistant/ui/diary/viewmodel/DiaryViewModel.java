package com.example.assistant.ui.diary.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.ui.diary.model.NewRecord;
import com.example.assistant.ui.diary.model.Record;
import com.example.assistant.repository.GetDataRepository;
import com.example.assistant.repository.NewObjectRepository;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryViewModel extends ViewModel {
    DateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    DateFormat formatForDateVariant2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private MutableLiveData<NewRecord> newRecord = new MutableLiveData<>();
    private MutableLiveData<Record> record = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<String> todayDate = new MutableLiveData<>();
    private MutableLiveData<List<ArrayList<String>>> allRecords = new MutableLiveData<>();
    private MutableLiveData<Integer> todayRecordId = new MutableLiveData<>();
    private GetDataRepository getDataRepository = new GetDataRepository();
    private NewObjectRepository repository = new NewObjectRepository();
    private MutableLiveData<Boolean> saveResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateResult = new MutableLiveData<>();

    private MutableLiveData<String> getResult = new MutableLiveData<>();


    public DiaryViewModel() { }


    public LiveData<Record> getRecord() {
        return record;
    }

    public LiveData<String> getResult() {
        return getResult;
    }

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }
    public List<ArrayList<String>> getAllRecords() {
        return allRecords.getValue();
    }

    public String getTodayDate() {
        Date currDate = new Date();
        return formatForDate.format(currDate);
    }

    public Record getTodayRecord() {
        return record.getValue();
    }


    public void setUrl(String urlPlan) {
        url.setValue(urlPlan);
    }


    public void loadAllRecords() {
        Date currDate = new Date();
        todayDate.setValue(formatForDateVariant2.format(currDate));

        getDataRepository.getDataObj(url.getValue()).observeForever(result -> {
            getResult.setValue(result);
        });
    }

    /*
        Передача данных плана в модель
     */
    public void setDiary() {
        String json = getResult.getValue();

        try {
            JSONArray jsonArray = new JSONArray(json);
            List<ArrayList<String>> allRec = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                ArrayList<String> recordData = new ArrayList<>();
                JSONObject recordObject = jsonArray.getJSONObject(i);

                // Если дата записи эквивалента сегодняшней дате
                if (recordObject.getString("assigned_day").equals(todayDate.getValue())) {
                    record.setValue(new Record(recordObject.getInt("id"),1, recordObject.getString("assigned_day"), recordObject.getString("text")));
                    todayRecordId.setValue(recordObject.getInt("id"));
                }

                else {
                    recordData.add(recordObject.getString("text"));
                    recordData.add(recordObject.getString("assigned_day"));

                    allRec.add(recordData);
                }

            }
            allRecords.setValue(allRec);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void createRecord(String text) {
        NewRecord record = new NewRecord(1, todayDate.getValue(),text);
        String jsonData = new Gson().toJson(record);
        repository.sendDataObj(Collections.singletonList(jsonData), url.getValue(), "POST")
                .observeForever(result -> saveResult.setValue(result));
    }


    public void updateRecord(String text) {
        Record record = new Record(todayRecordId.getValue(), 1, todayDate.getValue(), text);
        String jsonData = new Gson().toJson(record);
        repository.sendDataObj(Collections.singletonList(jsonData), url.getValue(), "PUT")
                .observeForever(result -> updateResult.setValue(result));
    }

}