package com.two.trial.yannick.todolist_2.model;

import java.util.List;

public interface IDataItemCRUDOperations {

    public DataItem createDataItem(DataItem item);

    public List<DataItem> readAllDataItems();

    boolean deleteDataItem(long dataItemId);
}