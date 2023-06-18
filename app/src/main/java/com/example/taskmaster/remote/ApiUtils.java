package com.example.taskmaster.remote;

public class ApiUtils {
    // REST API server URL
    public static final String BASE_URL = "https://csc557-radhi.000webhostapp.com/taskmaster/api/";

    //https://www.md5hashgenerator.com/
    // - use this link to convert plain text to MD5 hash
    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

}
