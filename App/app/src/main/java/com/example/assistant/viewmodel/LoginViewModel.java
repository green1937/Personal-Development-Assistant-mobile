package com.example.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.model.Login;
import com.example.assistant.repository.NewObjectRepository;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {


    private MutableLiveData<Login> login = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();
    private NewObjectRepository repository = new NewObjectRepository();
    private MutableLiveData<Boolean> saveResult = new MutableLiveData<>();

    public LiveData<Login> getLogin() {
        return login;
    }

    public void setUrl(String urlLogin) {
        url.setValue(urlLogin);
    }

    public LiveData<Boolean> getSaveResult() {
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
        login.setValue(new Login(currentLogin.getEmail(), currentLogin.getPassword()));
        allJsonData.add(new Gson().toJson(login.getValue()));

        //repository.sendDataObj(allJsonData, url.getValue(), "POST").observeForever(result -> saveResult.setValue(result));

        saveResult.setValue(true);
        System.out.println("---LOGIN--- USER data = " + allJsonData);
    }


    public boolean isInputDataValid() {
        Login currentLogin = login.getValue();
        return currentLogin != null &&
                !currentLogin.getEmail().isEmpty() &&
                !currentLogin.getPassword().isEmpty() &&
                isEmail(currentLogin.getEmail());
    }


    public  boolean isEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

}
