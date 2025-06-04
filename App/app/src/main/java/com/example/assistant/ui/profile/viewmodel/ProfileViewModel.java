package com.example.assistant.ui.profile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.assistant.repository.GetDataRepository;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class ProfileViewModel extends ViewModel {
    private MutableLiveData<String> url = new MutableLiveData<>();
    private GetDataRepository repository = new GetDataRepository();
    private MutableLiveData<String> getResult = new MutableLiveData<>();



    public void setUrl(String urlProfile) {
        url.setValue(urlProfile);
    }


    public LiveData<String> getResult() {
        return getResult;
    }


    public void getDataProfile() {
        //repository.getDataObj(url.getValue()).observeForever(result -> getResult.setValue(result));
        getResult.setValue("{\n" +
                "    \"username\": \"userTest\", \n" +
                "    \"email\": \"usertesta3@test.com\"\n" +
                "}");
    }

    public ArrayList<String> setData() {
        String json = getResult.getValue();
        ArrayList<String> profileData = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);

            profileData.add(jsonObject.getString("username"));
            profileData.add(jsonObject.getString("email"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return profileData;

    }

}
