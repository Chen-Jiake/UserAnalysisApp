package com.example.useranalysisapp.utils;

import org.json.JSONObject;

public interface ResultListener<T> {
    public void onSuccess(String message, T data);

    public void onFailure(String message, T data);
}
