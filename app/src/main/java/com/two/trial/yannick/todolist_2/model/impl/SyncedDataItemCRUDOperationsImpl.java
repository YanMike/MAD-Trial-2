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
                        Toast.makeText(context, "An error occurred during sync.", Toast.LENGTH_LONG).show();
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
                        currentItem.setId(0);
                        this.createDataItem(currentItem);
                    } else {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "An error occurred during sync.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }
    }

    public boolean deleteAllRemoteDataItems() {
        return remoteCRUD.deleteAllRemoteDataItems();
    }

    @Override
    public DataItem createDataItem(DataItem item) {
        // returns ID
        DataItem localCreated= localCRUD.createDataItem(item);

        try {
            /**
             * ONLY FOR DEBUGGING
             */
//                isHostReachable();
            /**
             * END
             */
            DataItem remoteCreated = remoteCRUD.createDataItem(localCreated);
            return remoteCreated;
        } catch(Exception e) {
            e.printStackTrace();
            return localCreated;
        }

    }

    // Only local access required
    @Override
    public List<DataItem> readAllDataItems() {
        if(!syncDone) {
            exchangeTodos();
            syncDone = true;
        }
        return localCRUD.readAllDataItems();
    }

    @Override
    public DataItem readDataItem(long itemId) {
        return localCRUD.readDataItem(itemId);
    }

    /**
     * Contains a WORKAROUND
     * after a while not touching the app, the app crashes during the next action. Updates are done locally only. If I restart my app and do another sync,
     * everything is fine, therefore I did this workaround: catching exception of first try, update data and view locally and call remote action a second time.
     *
     * @param item
     * @return
     */
    @Override
    public DataItem updateDataItem(DataItem item) {
        /**
         * ONLY FOR DEBUGGING
         */
//        isHostReachable();
        /**
         * END
         */

        DataItem localCreated = localCRUD.updateDataItem(item);
        try {
            DataItem remoteCreated = remoteCRUD.updateDataItem(localCreated);
            return remoteCreated;
        } catch(Exception e) {
            e.printStackTrace();
            try {
                DataItem remoteCreated = remoteCRUD.updateDataItem(localCreated);
            } catch(Exception f) {
                f.printStackTrace();
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


    /**
     * for debugging only
     */
    private void isHostReachable() {
        AsyncTask hostTask = new AsyncTask<Void, Void, Boolean>() {
            private ProgressDialog hostDialog = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean hostOnline = false;
                try {
                    URL url = new URL("http://192.168.178.20:8080/TodolistWebapp/");  //@Home
//                    URL url = new URL("http://192.168.178.32:8080/TodolistWebapp/");    //@Eltern
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
