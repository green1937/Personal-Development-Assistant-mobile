package com.example.assistant.ui.storage.viewmodel;

import static com.example.assistant.ui.plans.viewmodel.PlansViewModel.getPlansFromJSON;

import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.ui.storage.model.Storage;
import com.example.assistant.repository.GetWheelRepository;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StorageViewModel extends ViewModel {

    private MutableLiveData<Storage> storage = new MutableLiveData<>();
    private MutableLiveData<int[]> ctgFromRV = new MutableLiveData<>();
    private MutableLiveData<Boolean> isRepeated = new MutableLiveData<>();
    private MutableLiveData<Boolean> belongsToPlan = new MutableLiveData<>();
    private MutableLiveData<Integer> minPoints = new MutableLiveData<>();
    private MutableLiveData<Integer> maxPoints = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();

    private GetWheelRepository getWheelRepository = new GetWheelRepository();
    private MutableLiveData<String> postResult = new MutableLiveData<>();


    public LiveData<Storage> getStorage() {
        return storage;
    }

    public void setEntityTypes(String type) {
        ArrayList<String> types =new ArrayList<>();

        if (type.equals("Задача")) types.add("task");
        if (type.equals("План")) types.add("plan");
        if (type.equals("Дневник")) types.add("diary");
        if (type.equals("Заметка")) types.add("note");

        Storage currentStorage = storage.getValue();
        if (currentStorage != null) {
            currentStorage.setEntityTypes(types);
            storage.setValue(currentStorage);
        }
    }

    public void setIsRepeated(Boolean flag) {
        Storage currentStorage = storage.getValue();
        if (currentStorage != null) {
            currentStorage.setRepeated(flag);
            storage.setValue(currentStorage);
        }
        //isRepeated.setValue(flag);
    }

    public void setBelongsToPlan(Boolean flag) {
        Storage currentStorage = storage.getValue();
        if (currentStorage != null) {
            currentStorage.setBelongsToPlan(flag);
            storage.setValue(currentStorage);
        }
        //belongsToPlan.setValue(flag);
    }

    public void setStatus(Long flag) {
        Storage currentStorage = storage.getValue();
        if (currentStorage != null) {
            currentStorage.setStatus(flag);
            storage.setValue(currentStorage);
        }
    }

    public void setCtg(ArrayList<Integer> id) {
        Storage currentStorage = storage.getValue();
        if (currentStorage != null) {
            currentStorage.setCategories(id);
            storage.setValue(currentStorage);
        }
        //ctgFromRV.setValue(flag);
    }

    public void setMinPoints(Integer points) {
        Storage currentStorage = storage.getValue();
        if (currentStorage != null) {
            currentStorage.setMinPoints(points);
            storage.setValue(currentStorage);
        }
        //minPoints.setValue(flag);
    }

    public void setMaxPoints(Integer points) {
        Storage currentStorage = storage.getValue();
        if (currentStorage != null) {
            currentStorage.setMaxPoints(points);
            storage.setValue(currentStorage);
        }
        //maxPoints.setValue(flag);
    }



    public void setUrl(String urlStorage) {
        url.setValue(urlStorage);
    }

    public StorageViewModel() {
        storage.setValue(new Storage(null, null, null, null, null, null, null, null, null, new ArrayList<>(), null, null));
    }

    public ArrayList<String> getEntityType() {
        return storage.getValue().getEntityTypes();
    }

    public void onSaveBtnClicked() {
        postSearchSettings();
        /*if (isInputDataValid()) {
            System.out.println("SUCCESS!");
            //postSearchSettings();
        }
        else
            System.out.println("NO :(");

         */
    }

    /*
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
     */


    public LiveData<String> getPostResult() {
        return postResult;
    }
    public void postSearchSettings() {
        Storage currentStorage = storage.getValue();

        currentStorage.setStartDate(changeDateFormat(currentStorage.getStartDate()));
        currentStorage.setStopDate(changeDateFormat(currentStorage.getStopDate()));
        currentStorage.setDoneStartDate(changeDateFormat(currentStorage.getDoneStartDate()));
        currentStorage.setDoneStopDate(changeDateFormat(currentStorage.getDoneStopDate()));

        System.out.println("in viewModel data " + new Gson().toJson(currentStorage));
        getWheelRepository.sendDataToServer(new Gson().toJson(currentStorage),
                url.getValue()).observeForever(result -> postResult.setValue(result));

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



    public List<ArrayList<String>> getPlan() {
        List<ArrayList<String>> allPlans = new ArrayList<>();

        try {
            String json = getPostResult().getValue();
            JSONObject responseObject = new JSONObject(json);

            // Получаем массив планов
            JSONArray plansArray = responseObject.getJSONArray("plans");

            // Теперь можно передать массив планов в вашу функцию
            getPlansFromJSON(plansArray.toString(), allPlans);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("PLANS from SERVER " + allPlans);
        return allPlans;
    }


    public List<ArrayList<String>> getDiaryOrNotes(String param) {
        List<ArrayList<String>> allRecords = new ArrayList<>();
        try {
            String json = getPostResult().getValue();
            JSONObject responseObject = new JSONObject(json);

            // Получаем массив планов
            JSONArray diaryArray = responseObject.getJSONArray(param);

            // Теперь можно передать массив планов в вашу функцию
            getRecordsFromJSON(diaryArray.toString(), allRecords);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("DIARY/NOTES from SERVER " + allRecords);
        return allRecords;
    }


    protected void getRecordsFromJSON(String json, List<ArrayList<String>> allRecords) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                ArrayList<String> recordData = new ArrayList<>();
                JSONObject diaryObject = jsonArray.getJSONObject(i);
                recordData.add(diaryObject.getString("text"));
                recordData.add(diaryObject.getString("assigned_day"));
                allRecords.add(recordData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<ArrayList<String>> getTask() {
        List<ArrayList<String>> allTasks = new ArrayList<>();

        try {
            String json = getPostResult().getValue();
            JSONObject responseObject = new JSONObject(json);
            JSONArray plansArray = responseObject.getJSONArray("tasks");
            getTasksFromJSON(plansArray.toString(), allTasks);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("TASKS from SERVER " + allTasks);
        return allTasks;
    }


    public void getTasksFromJSON(String json, List<ArrayList<String>> allTasks) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                ArrayList<String> taskData = new ArrayList<>();
                JSONObject taskObject = jsonArray.getJSONObject(i);
                taskData.add(taskObject.getString("id"));
                taskData.add(taskObject.getString("name"));
                taskData.add(taskObject.getString("status"));

                allTasks.add(taskData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

