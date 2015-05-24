package com.two.trial.yannick.todolist_2.model;

import android.content.Context;

import java.util.List;

public interface IToDoDataCRUDOperations {

    public ToDoData createDataItem(ToDoData item);

    public List<ToDoData> readAllDataItems();

    boolean deleteDataItem(long dataItemId);

    void deleteSQLiteDatabase(Context context);
}