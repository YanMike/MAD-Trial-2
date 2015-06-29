package com.two.trial.yannick.todolist_2.model.impl;

import android.accounts.NetworkErrorException;

import com.two.trial.yannick.todolist_2.model.DataItem;
import com.two.trial.yannick.todolist_2.model.IDataItemCRUDOperations;

import java.util.List;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;


public class RemoteDataItemCRUDOperationsImplRetrofit implements IDataItemCRUDOperations {

    private RemoteDataItemCRUDApiRetrofit serverSideApi;

    public RemoteDataItemCRUDOperationsImplRetrofit() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://192.168.178.20:8080/TodolistWebapp/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        serverSideApi = restAdapter.create(RemoteDataItemCRUDApiRetrofit.class);
    }

    @Override
    public DataItem createDataItem(DataItem item) {
        return serverSideApi.createDataItem(item);
    }

    @Override
    public List<DataItem> readAllDataItems() {
        return serverSideApi.readAllDataItems();
    }

    @Override
    public boolean deleteDataItem(long dataItemId) {
        return serverSideApi.deletaDataItem(dataItemId);
    }
}
