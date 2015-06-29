package com.two.trial.yannick.todolist_2.model.impl;

import android.content.Context;

import com.two.trial.yannick.todolist_2.model.DataItem;
import com.two.trial.yannick.todolist_2.model.IDataItemCRUDOperations;

import java.util.List;

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    private RemoteDataItemCRUDOperationsImpl remoteCRUD;

    private CRUDOperations localCRUD;

    public SyncedDataItemCRUDOperationsImpl(Context context) {
        localCRUD = new CRUDOperations(context);
        remoteCRUD = new RemoteDataItemCRUDOperationsImpl();
    }

    @Override
    public DataItem createDataItem(DataItem item) {

        // ID kommt mit zurück
        DataItem localCreated= localCRUD.createDataItem(item);

        // zweite Zuweisung eigentlich überflüssig, da nichts mehr hinzugefügt werden soll
        DataItem remoteCreated = remoteCRUD.createDataItem(localCreated);

        return remoteCreated;
    }

    // zugriff auf locale db
    @Override
    public List<DataItem> readAllDataItems() {
        return localCRUD.readAllDataItems();
    }

    @Override
    public boolean deleteDataItem(long dataItemId) {
        boolean localDeleted = localCRUD.deleteDataItem(dataItemId);
        if(localDeleted) {
            boolean remoteDeleted = remoteCRUD.deleteDataItem(dataItemId);
            return remoteDeleted;
        } else {
            return false;
        }
    }
}
