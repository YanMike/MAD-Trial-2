package com.two.trial.yannick.todolist_2.model.impl;

import android.content.Context;
import android.util.Log;

import com.two.trial.yannick.todolist_2.model.DataItem;
import com.two.trial.yannick.todolist_2.model.IDataItemCRUDOperations;

import org.jboss.resteasy.client.ProxyFactory;

import java.util.List;


public class RemoteDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    private RemoteDataItemCRUDApi serverSideApi;
    protected static String logger = "remote";

    public RemoteDataItemCRUDOperationsImpl() {
        serverSideApi = ProxyFactory.create(RemoteDataItemCRUDApi.class, "http://192.168.178.20:8080/TodolistWebapp/");
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

    @Override
    public void deleteSQLiteDatabase(Context context) {

    }
}
