package com.example.assistant.ui.auth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.repository.AuthRepository;
import com.example.assistant.repository.GetRequestRepository;
import com.example.assistant.ui.auth.model.Login;
import com.example.assistant.repository.NewObjectRepository;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {


    private MutableLiveData<Login> login = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();
    private AuthRepository repository = new AuthRepository();
    private MutableLiveData<String> saveResult = new MutableLiveData<>();

    public LiveData<Login> getLogin() {
        return login;
    }

    public void setUrl(String urlLogin) {
        url.setValue(urlLogin);
    }

    public LiveData<String> getSaveResult() {
        return saveResult;
    }


    public LoginViewModel() {
        login.setValue(new Login("", ""));
    }


    public void onSaveBtnClicked() {
        if (isInputDataValid()) {
            System.out.println("SUCCESS!");
            saveNewUser();
        }
        else
            System.out.println("NO :(");
    }


    public void saveNewUser() {
        ArrayList<String> allJsonData = new ArrayList<>();
        Login currentLogin = login.getValue();
        login.setValue(new Login(currentLogin.getUserName(), currentLogin.getPassword()));
        allJsonData.add(new Gson().toJson(login.getValue()));
        String jsonData = new Gson().toJson(login.getValue());
        repository.sendDataToServer(jsonData, url.getValue()).observeForever(result -> saveResult.setValue(result));

        System.out.println("---LOGIN--- USER data = " + jsonData);
    }

    public String getTokenFromJSON(String json) {
        String token = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            // Получаем значение поля "token"
            token = jsonObject.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
       return token;
    }

    public boolean isInputDataValid() {
        Login currentLogin = login.getValue();
        return currentLogin != null &&
                !currentLogin.getUserName().isEmpty() &&
                !currentLogin.getPassword().isEmpty();
                //isEmail(currentLogin.getEmail());
    }


    public  boolean isEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

}
