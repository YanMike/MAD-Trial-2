package com.two.trial.yannick.todolist_2.model.impl;

import com.two.trial.yannick.todolist_2.model.DataItem;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;


public interface RemoteDataItemCRUDApiRetrofit {
    @POST("/todos")
    @Headers({"Content-Type:application/json"})
    public DataItem createDataItem(@Body DataItem item);

    @GET("/todos")
    @Headers({"Content-Type:application/json"})
    public List<DataItem> readAllDataItems();

    @GET("/todos/{id}")
    @Headers({"Content-Type:application/json"})
    public DataItem readDataItem(@Path("id") long dataItemId);

    @PUT("/todos")
    @Headers({"Content-Type:application/json"})
    public DataItem updateDataItem(@Body DataItem item);

    @DELETE("/todos/{id}")
    @Headers({"Content-Type:application/json"})
    public boolean deletaDataItem(@Path("id") long dataItemId);

    @DELETE("/todos")
    @Headers({"Content-Type:application/json"})
    public boolean deleteAllRemoteDataItems();
}
