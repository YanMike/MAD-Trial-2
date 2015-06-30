package com.two.trial.yannick.todolist_2.model.impl;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.two.trial.yannick.todolist_2.model.DataItem;
import com.two.trial.yannick.todolist_2.model.IDataItemCRUDOperations;

import java.util.List;

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    private boolean syncDone = false;

    private RemoteDataItemCRUDOperationsImplRetrofit remoteCRUD;
    private CRUDOperations localCRUD;

    private Context context;

    public SyncedDataItemCRUDOperationsImpl(Context context) {
        localCRUD = new CRUDOperations(context);
        remoteCRUD = new RemoteDataItemCRUDOperationsImplRetrofit();
        this.context = context;
    }

    public void exchangeTodos() {
        List<DataItem> localData = localCRUD.readAllDataItems();

        if(localData.size() > 0) {
            /*
             * Case: todos are stored locally
             * Requirement: delete all remotely stored todos & copy local todos to WebApp
             */
            if( deleteAllRemoteDataItems() ) {
                for(DataItem currentItem : localData) {
                    remoteCRUD.createDataItem(currentItem);
                }
            } else {
                //todo: give feedback
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "remote deletion crashed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else {
            /*
             * Case: no todos are stored locally
             * Requirement: copy all remote todos to local db
             */
            List<DataItem> remoteData = remoteCRUD.readAllDataItems();
            if(remoteData.size() > 0) {
                for(DataItem currentItem : remoteData) {
                    if(remoteCRUD.deleteDataItem(currentItem.getId())) {
                        currentItem.setId(0); // zuweisen nicht vergessen
                        this.createDataItem(currentItem);
                    } else {
                        // todo: give feedback that some problem occured
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "sync is running", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }
    }



    // TODO: Write my own function / find my own way
    /*public void sync() {
        List<DataItem> localData = localCRUD.readAllDataItems();
        if(localData.size() > 0) {
            // todo: remove all existing data items from server and add the local ones

            for(DataItem localItem : localData) {
                remoteCRUD.createDataItem(localItem);
            }
        }
        else {
            List<DataItem> remoteData = remoteCRUD.readAllDataItems();
            if(remoteData.size() > 0) {
                // todo: give feedback that sync is running
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "sync is running", Toast.LENGTH_SHORT).show();
                    }
                });

                for(DataItem currentItem : remoteData) {
                    // delete item from server
                    if(remoteCRUD.deleteDataItem(currentItem.getId())) {
                        currentItem.setId(0); // zuweisen nicht vergessen
                        this.createDataItem(currentItem);
                    }
                    else {
                        // todo: give feedback that some problem occured
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "sync is running", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }
    }*/

    public boolean deleteAllLocalDataItems() {
        List<DataItem> localItems = localCRUD.readAllDataItems();
        for(DataItem item : localItems) {
            if(!localCRUD.deleteDataItem(item.getId())) {
                // todo: give feedback that an error occured
                return false;
            }
        }
        return true;
    }

    public boolean deleteAllRemoteDataItems() {
        return remoteCRUD.deleteAllRemoteDataItems();
    }

    @Override
    public DataItem createDataItem(DataItem item) {

        // ID kommt mit zurück
        DataItem localCreated= localCRUD.createDataItem(item);

        // zweite Zuweisung eigentlich überflüssig, da nichts mehr hinzugefügt werden soll
        DataItem remoteCreated = remoteCRUD.createDataItem(localCreated);

        return remoteCreated;
    }

    // zugriff auf lokale db
    @Override
    public List<DataItem> readAllDataItems() {
        if(!syncDone) {
//            sync();
            exchangeTodos();
            syncDone = true;
        }
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
