package com.two.trial.yannick.todolist_2;

import com.two.trial.yannick.todolist_2.model.*;
import com.two.trial.yannick.todolist_2.model.impl.*;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OverviewActivity extends Activity {

    protected static String logger = "OverviewActivity";

    public OverviewActivity() {
//        Log.i(logger, "called: <constructor>");                 // i = info, d = debug, e = error
    }

    /*
     * declare instance attributes for the ui elements          // Bedienelemente erstellen, die verwendet werden sollen
     */
    // the textual "list view"
    private View itemlistView;
    // the add item button
    private Button addButton;

    /*
	 * the content view
	 */
    private ViewGroup contentView;

    /*
     * a progress dialog
     */
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialog;

    // for ListView: declare a list of DataItem objects that will collect the items created by the user
    private List<DataItem> itemsList = new ArrayList<DataItem>();

    private IDataItemCRUDOperations modelOperations;

    // for ListView: declare an adapter that mediates between the list of items and the listview
    // Instanzvariable
    private ArrayAdapter<DataItem> adapter;

	/*
	 * an attribute that holds the list of items that are created by this app (optionally)
	 */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call onCreate as super class !! important !! for all classes of the life cycle (that android framework can work as a framework)
        super.onCreate(savedInstanceState);

//        Log.i(logger, "called: onCreate()!");



        /* set the view -> choose the layout */
        setContentView(R.layout.layout_activity_overview);

        /* instantiate the ui elements */                                   // Bedienelemente auslesen, die verwendet werden sollen
        itemlistView = findViewById(R.id.itemlistView);                     // Konstante, um id innerhalb eines Layouts zu finden
        addButton    = (Button) findViewById(R.id.addButton);

        this.progressDialog = new ProgressDialog(this);

        this.alertDialog = new AlertDialog.Builder(this);

        //TODO: generischer umschreiben
        alertDialog.setMessage("Das ist ein AlertDialog")
                .setTitle("Offline Mode")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        /*
         * set an  onClick listener on the addButton
         *
         * create a handleAddAction() method on this class and call it from the listener
         *
         * show the alternative solution with the android:onClick attribute
         */

        // instantiate the model operations
        if(isOnline()) {
            Log.i(logger, "Network Log: Network available");
            isHostRechable();
        } else {
            Log.i(logger, "Network Log: No network available");
            modelOperations = new CRUDOperations(this);
        }

        /**
         *  Listener for button "Neues Todo anlegen"
         */
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddAction();
            }
        });

        //Bearbeitung 27.05. während Vorlesung
        adapter = new ArrayAdapter<DataItem>(this, R.layout.layout_listview_checkbox, R.id.itemName) {
            @Override
            public View getView(int position, View existingView, ViewGroup parent) {
                // existingView  = view die gerade an position ist und übergeben wird

                View listItemView;

                if(existingView != null) {
                    listItemView = existingView;
                    updateItemListView();
//                    Log.i(logger, "reusing existing view for position " + position + ": " + listItemView);
                } else {
                    // LayoutInflater => "Luftpumpe", die ein luftleeres XML Layout zu schönem Java Layout aufzublasen, mit Layout das hier erstellt wird
                    listItemView = getLayoutInflater().inflate(R.layout.layout_listview_checkbox, null);
                    updateItemListView();
                }

                TextView itemNameText = (TextView) listItemView.findViewById(R.id.itemName);
                final DataItem listItem = getItem(position);
                itemNameText.setText(listItem.getName());

                CheckBox itemChecked = (CheckBox) listItemView.findViewById(R.id.itemChecked);

                itemChecked.setOnCheckedChangeListener(null); // harte Methode, um beim Wiederverwenden der Ansicht nicht den alten Listener zu überschreiben

                // set checked, if COL_DONE == true
                itemChecked.setChecked(listItem.isDone());

                itemChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        listItem.setDone(isChecked);
                        updateAndShowNewItem(listItem);
                    }
                });

                final ImageView imageFav = (ImageView) listItemView.findViewById(R.id.imageFav);
                if(listItem.isFavourite()) {
                    imageFav.setImageResource(R.drawable.star);
                } else {
                    imageFav.setImageResource(R.drawable.star_grey);
                }

                imageFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listItem.isFavourite()) {
                            imageFav.setImageResource(R.drawable.star_grey);
                            listItem.setFavourite(false);
                        } else {
                            imageFav.setImageResource(R.drawable.star);
                            listItem.setFavourite(true);
                        }
//                        handleUpdateTodoAction(listItem.getId());
                        updateAndShowNewItem(listItem);
//                        Toast.makeText(OverviewActivity.this, ""+ listItem.getId(), Toast.LENGTH_SHORT).show();
                    }
                });

                return listItemView;
                // erwartet einen View als return -> den wir für ein einzelnes Listenelement über int position bestimmen
            }
        };

        /**
         * click on item
         */
        adapter.setNotifyOnChange(true);
        ((ListView)itemlistView).setAdapter(adapter);

        ((ListView) itemlistView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleUpdateTodoAction(adapter.getItem(position).getId());
            }
        });



        // read out all items and populate the view
//        readOutDataItemsAndPopulateView();
    }

    /**
     * deal with adding a new element to the list
     */
    private void handleAddAction() {
    	/* then call the Detailview Activity */

    	/* create an intent expressing what we want to DO, i.e. using the action SHOW_DETAILS */
        Intent callDetailIntent = new Intent(this, DetailviewActivity.class);

    	/* actually send the intent triggering the display of the activity - use startActivityForResult */
        startActivityForResult(callDetailIntent, 0);
    }

    /**
     * gets called, when clicked on item
     * @param id
     */
    private void handleUpdateTodoAction(long id) {
        Log.i(logger, "ID: " + id);
    	/* then call the Detailview Activity */

    	/* create an intent expressing what we want to DO, i.e. using the action SHOW_DETAILS */
        Intent callDetailIntent = new Intent(this, DetailviewActivity.class);
        Bundle paramBundle = new Bundle();

        paramBundle.putLong("paramItemId", id);
        // TODO: decide, either handover all params or call readDataItem within DetailViewActivity(id)
        DataItem paramItem = modelOperations.readDataItem(id);

        paramBundle.putString("name", paramItem.getName());
        paramBundle.putString("description", paramItem.getDescription());
        paramBundle.putString("done", String.valueOf(paramItem.isDone()));
        paramBundle.putString("favourite", String.valueOf(paramItem.isFavourite()));

        callDetailIntent.putExtras(paramBundle);

    	/* actually send the intent triggering the display of the activity - use startActivityForResult */
        startActivityForResult(callDetailIntent, 1);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void readOutDataItemsAndPopulateView() {
        new AsyncTask<Void, Void, List<DataItem>>() {

            @Override
            protected List<DataItem> doInBackground(Void... params) {
                return modelOperations.readAllDataItems();
            }

            // Aufruf von onPostExecute erfolgt auf UI Thread -> Schreibzugriff gegeben
            @Override
            protected void onPostExecute(List<DataItem> result) {
//               todo: delete
//               itemsList.addAll(result);
                adapter.addAll(result);
            }
        }.execute();
    }


    private void deleteDataItemAndUpdateListView(final long itemId) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                // wenn löschen erfolgreich -> Methode returns true
                return modelOperations.deleteDataItem(itemId);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    // Um Ansicht zu aktualisieren
                    updateItemListView();
                    //adapter.remove(selectedItem);
                } else {
                    // falls löschen fehl schlägt
                    Toast.makeText(OverviewActivity.this, "DataItem could not be deleted!", Toast.LENGTH_SHORT).show();
                    Log.i(logger, "Deletion failed!");
                }
            }
        }.execute();
    }

    /* implement onActivityResult(): read out result and update the listview using setText() */ // muss implementiert sein, um auf das Resultat reagieren zu können
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether we have a result object
        /* requestCodes:
         *  0 -> create
         *  1 -> update
         */
        if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            DataItem item = (DataItem) data.getSerializableExtra("createdItem");
            createAndShowNewItem(item);
        } else if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if(data.getSerializableExtra("updatedItem") != null) {
                Log.i(logger, "loeschen: requestCode1 & updatedItem");
                DataItem item = (DataItem) data.getSerializableExtra("updatedItem");
                updateAndShowNewItem(item);
            } else {
                Log.i(logger, "loeschen: requestCode1 & deletedItem");
                long itemId = data.getLongExtra("deletedItem", 0);
                deleteDataItemAndUpdateListView(itemId);
            }
        } else {
            Log.i(logger, "no newItem contained in result");
        }
    }

    private void updateAndShowNewItem(DataItem item) {
        new AsyncTask<DataItem, Void, DataItem>() {

            @Override
            protected DataItem doInBackground(DataItem... params) {
                Log.i(logger, "update: " + String.valueOf(params[0].isDone()));
                modelOperations.updateDataItem(params[0]);
                return params[0];
            };

            @Override
            protected void onPostExecute(DataItem result) {
                if(result != null) {
                    updateItemListView();
                }

                Toast.makeText(OverviewActivity.this, result != null ? "Successfully updated item" + result.getId() : "Update failed!", Toast.LENGTH_SHORT).show();
            };

        }.execute(item);
    }

    private void createAndShowNewItem(final DataItem newItem) {
        new AsyncTask<DataItem, Void, DataItem>() {
            // Aufruf von onPreExecute erfolgt auf UI Thread
            @Override
            protected void onPreExecute() {
//                progressDialog.show();
            };

            @Override
            protected DataItem doInBackground(DataItem... params) {
                return modelOperations.createDataItem(params[0]);
            }

            // Aufruf von onPostExecute erfolgt auf UI Thread -> Schreibzugriff gegeben
            @Override
            protected void onPostExecute(DataItem result) {
                updateItemListView();
//                progressDialog.hide();
            };
        }.execute(newItem);
    }

    /*
	 * update the view
	 */
    private void updateItemListView() {
        Log.i(logger, "updateItemListView");
        /* add the item to the adapter */
        //TODO: to read out all data from db is not the nicest solution ;) but it works
        List<DataItem> allItems = new ArrayList<DataItem>();
        allItems = modelOperations.readAllDataItems();

        List<DataItem> doneItems  = new ArrayList<>();
        List<DataItem> falseItems = new ArrayList<>();
        for(DataItem item : allItems) {
            if(item.isDone()) {
                doneItems.add(item);
            } else {
                falseItems.add(item);
            }
//            boolean result = doneItems.add(item) ? item.isDone() : falseItems.add(item);
        }

        allItems.clear();
        for(DataItem item : doneItems) {
            allItems.add(item);
        }
        for(DataItem item : falseItems) {
            allItems.add(item);
        }
        adapter.clear();
        adapter.addAll(allItems);
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        List<DataItem> allItems = modelOperations.readAllDataItems();
//        adapter.addAll(allItems);
//        adapter.clear();
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
//        Log.i(logger, "onDestroy()!");
    }

//    private MenuItem sortDoneOptionItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_overview, menu);

        /*for(int i = 0; i < menu.size(); i++) {
            MenuItem currentItem = menu.getItem(i);
            if(currentItem.getItemId() == R.id.optionSortByDone) {
                currentItem.setEnabled(false);
                sortDoneOptionItem = currentItem;
            }

        }*/

        return true;
    }

    /* implement boolean onOptionsItemSelected(MenuItem item)  */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.optionAdd) {
            handleAddAction();
            return true;
        }

//        else if (item.getItemId() == R.id.optionSortByDate) {
//            sortDoneOptionItem.setEnabled(true);
//        }
        return super.onOptionsItemSelected(item);
    }

    //
    public boolean testing = false;

    private void isHostRechable() {

        AsyncTask hostTask = new AsyncTask<Void, Void, Boolean>() {
            private ProgressDialog hostDialog = null;

           @Override
           protected void onPreExecute() {
               hostDialog = ProgressDialog.show(OverviewActivity.this, "Bitte warten Sie...", "waehrend des Ladevorgangs.");
           }

           @Override
            protected Boolean doInBackground(Void... params) {
                try {
//                    URL url = new URL("http://192.168.178.20:8080/TodolistWebapp/");  //@Home
                    URL url = new URL("http://192.168.178.32:8080/TodolistWebapp/");    //@KathisEltern
                    final HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setRequestProperty("User-Agent", "Android Application");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(10 * 1000);
                    urlc.connect();

                    if (urlc.getResponseCode() == 200) {
                        Log.i(logger, "Network Log: Host reachable");
                        testing = true;
                    }
//                    Log.i(logger, "Network Log: Code: " + urlc.getResponseCode());
                } catch (Throwable e) {
                    Log.i(logger, "Network Log: Host not reachable");
//                    e.printStackTrace();
                    testing = false;
                }
                return testing;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                hostDialog.cancel();
            }
        }.execute();

        try {
//            Log.i(logger, "Network Log - true or false: " + String.valueOf(hostTask.get()));
            if( hostTask.get() == true) {
                modelOperations = new SyncedDataItemCRUDOperationsImpl(OverviewActivity.this);
                Log.i(logger, "Network Log: synced");
            } else {
                modelOperations = new CRUDOperations(OverviewActivity.this);
                alertDialog.setMessage("Access to web application is not possible. App will try to synchronize Todos on next restart.");
                alertDialog.show();
                Log.i(logger, "Network Log: local");
            }
        } catch (InterruptedException e) {
            Log.i(logger, "Network Log: interrupted");
//            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.i(logger, "Network Log: execution");
//            e.printStackTrace();
        }

        if(modelOperations instanceof SyncedDataItemCRUDOperationsImpl) {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    try{
                        ((SyncedDataItemCRUDOperationsImpl) modelOperations).exchangeTodos();
                        return true;
                    }catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                };

                @Override
                protected void onPostExecute(Boolean result) {
                    if(result) {
                        // if sync has been run successfully, we update the view
                        adapter.clear();    // view gets cleared
                        readOutDataItemsAndPopulateView();
                    }
                }
            }.execute();
        }
    }

}
