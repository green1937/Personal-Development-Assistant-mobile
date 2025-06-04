package com.example.assistant.ui.wheel.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.repository.NewObjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;

public class CategoriesViewModel extends ViewModel {
    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<String> nameCtg = new MutableLiveData<>();
    private MutableLiveData<String> colorCtg = new MutableLiveData<>();
    private NewObjectRepository repository = new NewObjectRepository();
    private MutableLiveData<Boolean> saveResult = new MutableLiveData<>();


    public void setUrl(String urlCtg) {
        url.setValue(urlCtg);
    }


    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }
    public void saveData() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode()
                .put("user_id", 1)
                .put("title", nameCtg.getValue())
                .put("color", colorCtg.getValue())
                .put("active", true);

        String allJsonData = null;
        try {
            allJsonData = mapper.writeValueAsString(json);
            repository.sendDataObj(Collections.singletonList(allJsonData), url.getValue(), "POST").observeForever(result -> saveResult.setValue(result));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setNameCtg(String name) {
        nameCtg.setValue(name);
    }

    public void setColorCtg(String color) {
        colorCtg.setValue(color);
    }
}
