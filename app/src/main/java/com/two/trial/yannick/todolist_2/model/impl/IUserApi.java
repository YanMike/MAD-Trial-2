package com.two.trial.yannick.todolist_2.model.impl;

import com.two.trial.yannick.todolist_2.model.User;

import retrofit.http.Body;
import retrofit.http.PUT;
import retrofit.http.Headers;
import retrofit.http.POST;

public interface IUserApi {
    @PUT("/users/auth")
    @Headers({"Content-Type:application/json"})
    public boolean authenticateUser(@Body User user);
}
