package com.example.assistant.utils;

import java.util.ArrayList;
import java.util.List;

public interface Callback {
    void onSuccess(List<List<ArrayList<String>>> data);
    void onFailure(String errorMessage);
}