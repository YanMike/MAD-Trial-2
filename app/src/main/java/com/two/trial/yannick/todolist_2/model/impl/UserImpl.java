package com.two.trial.yannick.todolist_2.model.impl;

import com.two.trial.yannick.todolist_2.model.User;

import retrofit.RestAdapter;

public class UserImpl {

    private IUserApi serverSideApi;

    public UserImpl() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://192.168.178.20:8080/TodolistWebapp/")    //@Home
//                .setEndpoint("http://192.168.178.32:8080/TodolistWebapp/")      //@Eltern
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        serverSideApi = restAdapter.create(IUserApi.class);
    }

//    @Override
    public boolean authenticateUser(User user) {
        return serverSideApi.authenticateUser(user);
    }
}
