package com.two.trial.yannick.todolist_2.model.jaxrs_impl;

import com.two.trial.yannick.todolist_2.model.DataItem;
import com.two.trial.yannick.todolist_2.model.IDataItemCRUDOperations;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

import java.util.List;


public class RemoteDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    private RemoteDataItemCRUDApi serverSideApi;
    private String baseUrl = "http://192.168.178.20:8080/TodolistWebapp/";

    public RemoteDataItemCRUDOperationsImpl() {
        serverSideApi = ProxyFactory.create(RemoteDataItemCRUDApi.class, baseUrl, new ApacheHttpClient4Executor());
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
