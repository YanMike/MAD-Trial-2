package com.two.trial.yannick.todolist_2.model;

import android.content.Context;

import java.util.List;



public interface IDataItemCRUDOperations {

    public DataItem createDataItem(DataItem item);

    public List<DataItem> readAllDataItems();

    public DataItem updateDataItem(DataItem item);

    boolean deleteDataItem(long dataItemId);

    public DataItem readDataItem(long id);
}