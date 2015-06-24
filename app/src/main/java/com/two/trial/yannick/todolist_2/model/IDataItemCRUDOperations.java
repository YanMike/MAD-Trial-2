package com.two.trial.yannick.todolist_2.model;

import android.content.Context;

import java.util.List;

public interface IDataItemCRUDOperations {

    public DataItem createDataItem(DataItem item);

    public List<DataItem> readAllDataItems();

    boolean deleteDataItem(long dataItemId);

    void deleteSQLiteDatabase(Context context);
}