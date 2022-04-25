package com.example.gotitapplication.util;

import com.example.gotitapplication.gson.NewsList;
import com.google.gson.Gson;

public class Utility {
    public static NewsList parseJsonWithGson(final String requestText){
        Gson gson = new Gson();
        return gson.fromJson(requestText, NewsList.class);
    }

}