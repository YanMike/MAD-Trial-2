package com.two.trial.yannick.todolist_2.model.impl;

import com.two.trial.yannick.todolist_2.model.DataItem;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;


public interface RemoteDataItemCRUDApiRetrofit {

    @POST("/todos")
    public DataItem createDataItem(@Body DataItem item);

    @GET("/todos")
    public List<DataItem> readAllDataItems();

    @DELETE("todos/{id}")
    public boolean deletaDataItem(@Path("id") long dataItemId);
}
