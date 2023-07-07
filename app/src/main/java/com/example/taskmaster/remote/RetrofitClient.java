package com.example.taskmaster.remote;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClientV2(String URL) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,true);

        if(retrofit == null) {
            // initialize retrofit
             retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(JacksonConverterFactory.create(mapper))
                    .client(new OkHttpClient())
                    .build();
        }

        return retrofit;
    }

    public static Retrofit getClient(String URL) {

        // first API call, no retrofit instance yet?
        if (retrofit == null) {
            // initialize retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        // return instance of retrofit
        return retrofit;
    }
}
