package com.example.assistant.ui.plans.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.repository.GetDataRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlansViewModel extends ViewModel {

    private MutableLiveData<String> url = new MutableLiveData<>();
    private GetDataRepository getDataRepository = new GetDataRepository();
    private MutableLiveData<List<ArrayList<String>>> plans = new MutableLiveData<>();
    private MutableLiveData<String> getResultActive = new MutableLiveData<>();
    private MutableLiveData<String> getResultArchive = new MutableLiveData<>();
    private MutableLiveData<String> token = new MutableLiveData<>();


    public void setUrl(String urlPlan) {
        url.setValue(urlPlan);
    }

    public PlansViewModel() { }


    public LiveData<String> getResultActive() {
        return getResultActive;
    }


    public LiveData<String> getResultArchive() {
        return getResultArchive;
    }

    public  List<ArrayList<String>> getPlans() {
        return plans.getValue();
    }

    public void setToken(String t) {
        token.setValue(t);
    }

    /*
        Загрузка данных
     */
    public void loadPlanData() {
        String urlActive = url.getValue() + "/full?status=0";
        getDataRepository.getDataObj(urlActive, token.getValue()).observeForever(result -> {
            getResultActive.setValue(result);
        });

        String urlArchive = url.getValue() + "/full?status=1";
        getDataRepository.getDataObj(urlArchive, token.getValue()).observeForever(result -> {
            getResultArchive.setValue(result);
        });
    }


    /*
        Передача данных плана в модель
     */
    public void setPlans() {
        String jsonActive = getResultActive().getValue();
        String jsonArchive = getResultArchive().getValue();

        List<ArrayList<String>> allPlans = new ArrayList<>();

        getPlansFromJSON(jsonActive, allPlans);
        allPlans.add(new ArrayList<>(Collections.singleton("Архивные планы")));
        getPlansFromJSON(jsonArchive, allPlans);

        plans.setValue(allPlans);

    }


    public static void getPlansFromJSON(String json, List<ArrayList<String>> allPlans) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                ArrayList<String> planData = new ArrayList<>();
                JSONObject planObject = jsonArray.getJSONObject(i);
                planData.add(planObject.getString("id"));
                planData.add(planObject.getString("name"));
                planData.add(planObject.getString("status"));

                allPlans.add(planData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

