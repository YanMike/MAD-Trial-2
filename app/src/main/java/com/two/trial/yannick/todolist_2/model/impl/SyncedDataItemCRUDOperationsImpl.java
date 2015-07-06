package com.two.trial.yannick.todolist_2.model.impl;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.two.trial.yannick.todolist_2.model.DataItem;
import com.two.trial.yannick.todolist_2.model.IDataItemCRUDOperations;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    protected static String logger = "syncedCRUDOps";

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
            //TODO: does not cover, if remote does not have any todos
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
        try {
            /**
             * ONLY FOR DEBUGGING
             */
                isHostReachable();
            /**
             * END
             */
            DataItem remoteCreated = remoteCRUD.createDataItem(localCreated);
            Log.i(logger, "remote -create/update: " + item.getName());
            return remoteCreated;
        } catch(Exception e) {
            Log.i(logger, "-create/update: remote creation crashed");
            e.printStackTrace();
            return localCreated;
        }

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
    public DataItem readDataItem(long itemId) {
        return localCRUD.readDataItem(itemId);
    }

    @Override
    public DataItem updateDataItem(DataItem item) {
        /**
         * ONLY FOR DEBUGGING
         */
        isHostReachable();
        /**
         * END
         */

        DataItem localCreated = localCRUD.updateDataItem(item);
        Log.i(logger, "local -create/update: " + item.getName());

        // zweite Zuweisung eigentlich überflüssig, da nichts mehr hinzugefügt werden soll
        try {
            DataItem remoteCreated = remoteCRUD.updateDataItem(localCreated);
            Log.i(logger, "remote -create/update: " + item.getName());
            return remoteCreated;
        } catch(Exception e) {
            Log.i(logger, "-create/update: remote creation crashed");
            e.printStackTrace();
            try {
                DataItem remoteCreated = remoteCRUD.updateDataItem(localCreated);
                Log.i(logger, "-create/update: remote creation worked 2nd time");
            } catch(Exception f) {
                Log.i(logger, "-create/update: remote creation crashed 2nd time");
            }
            return localCreated;
        }
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


    private void isHostReachable() {
        AsyncTask hostTask = new AsyncTask<Void, Void, Boolean>() {
            private ProgressDialog hostDialog = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean hostOnline = false;
                try {
                    URL url = new URL("http://192.168.178.20:8080/TodolistWebapp/");  //@Home
//                    URL url = new URL("http://192.168.178.32:8080/TodolistWebapp/");    //@KathisEltern
                    final HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setRequestProperty("User-Agent", "Android Application");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(10 * 1000);
                    urlc.connect();

                    if (urlc.getResponseCode() == 200) {
                        Log.i(logger, "-create/update  Login Network Log: Host reachable");
                        hostOnline = true;
                    }
//                    // Log.i(logger, "Network Log: Code: " + urlc.getResponseCode());
                } catch (Throwable e) {
                    Log.i(logger, "-create/update  Login Network Log: Host not reachable");
//                    e.printStackTrace();
                    hostOnline = false;
                }
                return hostOnline;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                Log.i(logger, "-create/update Host reachable: " + aBoolean);
            }
        }.execute();
    }
}
