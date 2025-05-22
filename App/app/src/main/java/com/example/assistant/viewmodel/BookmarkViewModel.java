package com.example.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.repository.GetDataRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookmarkViewModel extends ViewModel {

    private MutableLiveData<String> url = new MutableLiveData<>();

    private GetDataRepository getDataRepository = new GetDataRepository();
    private MutableLiveData<List<ArrayList<String>>> bookmark = new MutableLiveData<>();
    private MutableLiveData<String> getResult = new MutableLiveData<>();


    public List<ArrayList<String>> getBookmark() {
        return bookmark.getValue();
    }


    public BookmarkViewModel() { }


    public void setUrl(String urlBookmarks) {
        url.setValue(urlBookmarks);
    }


    public LiveData<String> getResult() {
        return getResult;
    }


    /*
        Загрузка данных
     */
    public void loadBookmarkData() {
        getDataRepository.getDataObj(url.getValue()).observeForever(result -> {
            getResult.setValue(result);
        });
    }


    /*
        Передача данных плана в модель
     */
    public void setPlans() {
        String json = getResult().getValue();
        List<ArrayList<String>> allBookmarks = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                ArrayList<String> markData = new ArrayList<>();
                JSONObject planObject = jsonArray.getJSONObject(i);

                //ТУТ ИЗМЕНИТЬ!
                markData.add(planObject.getString("id"));
                markData.add(planObject.getString("name"));
                markData.add(planObject.getString("tags"));

                allBookmarks.add(markData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bookmark.setValue(allBookmarks);

    }
}
