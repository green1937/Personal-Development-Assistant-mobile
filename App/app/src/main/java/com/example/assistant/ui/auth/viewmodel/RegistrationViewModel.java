package com.example.assistant.ui.auth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.repository.AuthRepository;
import com.example.assistant.ui.auth.model.Register;
import com.example.assistant.repository.NewObjectRepository;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class RegistrationViewModel extends ViewModel {

    private MutableLiveData<Register> register = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();
    private AuthRepository repository = new AuthRepository();
    private MutableLiveData<String> saveResult = new MutableLiveData<>();

    public LiveData<Register> getRegister() {
        return register;
    }

    public void setUrl(String urlRegister) {
        url.setValue(urlRegister);
    }

    public LiveData<String> getSaveResult() {
        return saveResult;
    }


    public RegistrationViewModel() {
        register.setValue(new Register("", "", "", ""));
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
        Register currentRegister = register.getValue();
        register.setValue(new Register(currentRegister.getEmail(), currentRegister.getUsername(), currentRegister.getPassword(), currentRegister.getRepeatPassword()));
        allJsonData.add(new Gson().toJson(register.getValue()));
        String jsonData = new Gson().toJson(register.getValue());
        repository.sendDataToServer(jsonData, url.getValue()).observeForever(result -> saveResult.setValue(result));
        System.out.println("REGISTER USER data = " + jsonData);
    }


    public boolean isInputDataValid() {
        Register currentRegister = register.getValue();
        return currentRegister != null &&
                !currentRegister.getUsername().isEmpty() &&
                !currentRegister.getEmail().isEmpty() &&
                !currentRegister.getPassword().isEmpty() &&
                !currentRegister.getRepeatPassword().isEmpty() &&
                currentRegister.getPassword().equals(currentRegister.getRepeatPassword()) &&
                isEmail(currentRegister.getEmail());
    }


    public static boolean isEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

}
