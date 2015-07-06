package com.two.trial.yannick.todolist_2;

import com.two.trial.yannick.todolist_2.model.*;
import com.two.trial.yannick.todolist_2.model.impl.*;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OverviewActivity extends Activity {

    protected static String logger = "OverviewActivity";

    public OverviewActivity() {
//        // Log.i(logger, "called: <constructor>");                 // i = info, d = debug, e = error
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
//    private List<DataItem> itemsList = new ArrayList<DataItem>();
    private List<DataItem> allItems = new ArrayList<DataItem>();

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

//        // Log.i(logger, "called: onCreate()!");



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
//        if(isOnline()) {
//            // Log.i(logger, "Network Log: Network available");
//            isHostRechable();
//        } else {
//            // Log.i(logger, "Network Log: No network available");
//            modelOperations = new CRUDOperations(this);
//        }

        final Bundle paramBundle = getIntent().getExtras();
        Log.i(logger, "Login Network Log: getParamBundle");
        boolean b = paramBundle.getBoolean("online");
        Log.i(logger, "Login Network Log: " + b);
        if(paramBundle != null) {
            Log.i(logger, "Login Network Log: paramBundle !null");
            if(paramBundle.getBoolean("online") == true ) {
                Log.i(logger, "Login Network Log: paramBundle == true");
                modelOperations = new SyncedDataItemCRUDOperationsImpl(OverviewActivity.this);
                // Log.i(logger, "Network Log: synced");
            } else {
                Log.i(logger, "Login Network Log: paramBundle == false");
                modelOperations = new CRUDOperations(OverviewActivity.this);
                alertDialog.setMessage(R.string.noHost_alert);
                alertDialog.show();
                // Log.i(logger, "Network Log: local");
            }
        } else {
            Log.i(logger, "Exception: " + "paramBundle empty");
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
//                        adapter.notifyDataSetChanged();
                        Log.i(logger, "getsCalled 1: readOutDataItemsAndPopulateView");
                        readOutDataItemsAndPopulateView();
                    }
                }
            }.execute();
        } else {
            Log.i(logger, "getsCalled 2: readOutDataItemsAndPopulateView");
            readOutDataItemsAndPopulateView();
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
                    // Log.i(logger,"getView");
                    Log.i(logger, "getsCalled 3: updateExistingListView");
                    updateExistingListView();
//                    // Log.i(logger, "reusing existing view for position " + position + ": " + listItemView);
                } else {
                    // LayoutInflater => "Luftpumpe", die ein luftleeres XML Layout zu schönem Java Layout aufzublasen, mit Layout das hier erstellt wird
                    listItemView = getLayoutInflater().inflate(R.layout.layout_listview_checkbox, null);
                    Log.i(logger, "getsCalled 4: updateItemListView");
                    updateItemListView();
                }

                TextView itemNameText = (TextView) listItemView.findViewById(R.id.itemName);
                final DataItem listItem = getItem(position);
                itemNameText.setText(listItem.getName());

                /*
                 *  Done CheckBox
                 */
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

                /*
                 *  Favourite ImageView
                 */
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
                        Log.i(logger, "getsCalled 5: updateAndShowNewItem(listItem)");
                        updateAndShowNewItem(listItem);
//                        Toast.makeText(OverviewActivity.this, ""+ listItem.getId(), Toast.LENGTH_SHORT).show();
                    }
                });

                /*
                 *  Date TextView
                 */
                // TODO: set "german" time format & show only date
                TextView dateView = (TextView) listItemView.findViewById(R.id.dateView);
                Date d = new Date(listItem.getExpiry());
                dateView.setText(d.toString());
                if(listItem.getExpiry() < System.currentTimeMillis()) {
                    dateView.setTextColor(Color.RED);
                } else {
                    dateView.setTextColor(Color.BLACK);
                }
                /*TextView textView = (TextView) getLayoutInflater().inflate(R.layout.layout_listview_highlighted, null);
                textView.setText(String.valueOf(System.currentTimeMillis()));

//                ((ViewGroup)view).addView(textView);*/

//                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
//                dateView.setText(dateFormat.format(d));

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
        // Log.i(logger, "ID: " + id);
    	/* then call the Detailview Activity */

    	/* create an intent expressing what we want to DO, i.e. using the action SHOW_DETAILS */
        Intent callDetailIntent = new Intent(this, DetailviewActivity.class);
        Bundle paramBundle = new Bundle();

        paramBundle.putLong("paramItemId", id);
        // TODO: decide, either handover all params or call readDataItem within DetailViewActivity(id)
        DataItem paramItem = modelOperations.readDataItem(id);

        paramBundle.putString("name", paramItem.getName());
        paramBundle.putString("description", paramItem.getDescription());
        paramBundle.putLong("expiry", paramItem.getExpiry());
        paramBundle.putBoolean("done", paramItem.isDone());
        paramBundle.putBoolean("favourite", paramItem.isFavourite());

        callDetailIntent.putExtras(paramBundle);

    	/* actually send the intent triggering the display of the activity - use startActivityForResult */
        startActivityForResult(callDetailIntent, 1);
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
//                adapter.notifyDataSetChanged();
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
                    // Log.i(logger,"deleteDataItemAndUpdateListView");
                    updateItemListView();
                    //adapter.remove(selectedItem);
                } else {
                    // falls löschen fehl schlägt
                    Toast.makeText(OverviewActivity.this, "DataItem could not be deleted!", Toast.LENGTH_SHORT).show();
                    // Log.i(logger, "Deletion failed!");
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
                DataItem item = (DataItem) data.getSerializableExtra("updatedItem");
                updateAndShowNewItem(item);
            } else {
                long itemId = data.getLongExtra("deletedItem", 0);
                deleteDataItemAndUpdateListView(itemId);
            }
        } else {
            // Log.i(logger, "no newItem contained in result");
        }
    }

    private void updateAndShowNewItem(DataItem item) {
        new AsyncTask<DataItem, Void, DataItem>() {

            @Override
            protected DataItem doInBackground(DataItem... params) {
                modelOperations.updateDataItem(params[0]);
                return params[0];
            };

            @Override
            protected void onPostExecute(DataItem result) {
                if(result != null) {
                    // Log.i(logger,"updateAndShowNewItem");
                    updateItemListView();
                }
                Toast.makeText(OverviewActivity.this, result != null ? "Successfully updated item" + result.getId() : "Update failed!", Toast.LENGTH_SHORT).show();
                Log.i(logger, "-create/update: " + result != null ? "Successfully updated item" + result.getId() : "Update failed!");
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
                // Log.i(logger,"createAndShowNewItem");
                updateItemListView();
//                progressDialog.hide();
            };
        }.execute(newItem);
    }

    /*
	 * update the view
	 */
    private void updateItemListView() {
        // Log.i(logger, "updateItemListView - itself");
        /* add the item to the adapter */
        //TODO: to read out all data from db is not the nicest solution ;) but it works
        allItems = modelOperations.readAllDataItems();

        List<DataItem> doneItems  = new ArrayList<>();
        List<DataItem> falseItems = new ArrayList<>();
        for(DataItem item : allItems) {
            if(item.isDone()) {
                doneItems.add(item);
            } else {
                falseItems.add(item);
            }
        }

        allItems.clear();
        adapter.clear();

        for(DataItem item : falseItems) {
            allItems.add(item);
        }
        for(DataItem item : doneItems) {
            allItems.add(item);
        }

        adapter.addAll(allItems);
//        adapter.notifyDataSetChanged();
    }
    private void updateExistingListView() {
        // Log.i(logger, "updateExistingListView");
        /* add the item to the adapter */
        //TODO: to read out all data from db is not the nicest solution ;) but it works
//        List<DataItem> allItems = new ArrayList<DataItem>();
//        allItems = modelOperations.readAllDataItems();

        List<DataItem> doneItems  = new ArrayList<>();
        List<DataItem> falseItems = new ArrayList<>();
        for(DataItem item : allItems) {
            if(item.isDone()) {
                doneItems.add(item);
            } else {
                falseItems.add(item);
            }
        }

        allItems.clear();

        for(DataItem item : falseItems) {
            allItems.add(item);
        }
        for(DataItem item : doneItems) {
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
//        // Log.i(logger, "onDestroy()!");
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
        } else if (item.getItemId() == R.id.optionSortByDate) {
            sortByDate();
            // Log.i(logger, "SortByDate: done");
        } else if(item.getItemId() == R.id.optionSortByPrio) {
            sortByFav();
            // Log.i(logger, "SortByFav: done - " + allItems.size());
        }
        return super.onOptionsItemSelected(item);
    }

    class BooleanComparator implements Comparator<DataItem> {
        @Override
        public int compare(DataItem item1, DataItem item2) {
            String fav1 = String.valueOf(item2.isFavourite());
            String fav2 = String.valueOf(item1.isFavourite());
            return fav1.compareTo(fav2);
        }
    }
    class DateComparator implements Comparator<DataItem> {
        @Override
        public int compare(DataItem item1, DataItem item2) {
            Date date1 = new Date(item1.getExpiry());
            Date date2 = new Date(item2.getExpiry());

            if(date1.equals(date2)) {
//                if(item1.isFavourite() == true && item2.isFavourite() == true) {
//                    return date1.compareTo(date2);
//                } else if(item1.isFavourite() == true) {
//                    return -1;
//                } else if(item2.isFavourite() == true) {
//                    return 1;
//                }
                if(item1.isFavourite() == true && item2.isFavourite() == true) {
                    return date1.compareTo(date2);
                } else if(item1.isFavourite() == true) {
                    return -1;
                } else if(item2.isFavourite() == true) {
                    return 1;
                }
            }
            return date1.compareTo(date2);
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
    }

    public void sortByDate() {
        for(DataItem item : allItems) {
            Date date = new Date(item.getExpiry());
            // Log.i(logger, "sortbydate: "+item.getName() + " | " + date);
        }

        DateComparator dateComp = new DateComparator();
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

        if(doneItems.size()>0) {
            Collections.sort(doneItems, dateComp);
        }
        if(falseItems.size()>0) {
            Collections.sort(falseItems, dateComp);
        }

        allItems.clear();
        for(DataItem item : doneItems) {
            allItems.add(item);
        }
        for(DataItem item : falseItems) {
            allItems.add(item);
        }
        // Log.i(logger, "SortByDate allItems after clear: " + allItems.size());

        adapter.clear();
        adapter.addAll(allItems);
//        adapter.notifyDataSetChanged();
    }
    public void sortByFav() {
        BooleanComparator boolComp = new BooleanComparator();
        DateComparator dateComp = new DateComparator();

        allItems = modelOperations.readAllDataItems();

        List<DataItem> doneFavItems  = new ArrayList<>();
        List<DataItem> doneItems  = new ArrayList<>();
        List<DataItem> falseFavItems = new ArrayList<>();
        List<DataItem> falseItems = new ArrayList<>();
        for(DataItem item : allItems) {
            // done, fav
            if(item.isDone() && item.isFavourite()) {
                doneFavItems.add(item);
            }
            // done, not fav
            else if(item.isDone()) {
                doneItems.add(item);
            }
            // not done, fav
            else if(item.isFavourite()) {
                falseFavItems.add(item);
            }
            // not done, not fav
            else {
                falseItems.add(item);
            }
        }

        if(falseFavItems.size()>0)
        {
            Collections.sort(falseFavItems, dateComp);
        }
        if(falseItems.size()>0) {
            Collections.sort(falseItems, dateComp);
        }
        if(doneFavItems.size()>0) {
            Collections.sort(doneFavItems, dateComp);
        }
        if(doneItems.size()>0) {
            Collections.sort(doneItems, dateComp);
        }

        allItems.clear();
        allItems.addAll(falseFavItems);
        allItems.addAll(falseItems);
        allItems.addAll(doneFavItems);
        allItems.addAll(doneItems);

        adapter.clear();
        adapter.addAll(allItems);
//        adapter.notifyDataSetChanged();
    }
    public void sortByFavOld() {
        BooleanComparator boolComp = new BooleanComparator();
        allItems = modelOperations.readAllDataItems();

        List<DataItem> doneItems  = new ArrayList<>();
        List<DataItem> falseItems = new ArrayList<>();
        for(DataItem item : allItems) {
            if(item.isDone()) {
                doneItems.add(item);
            } else {
                falseItems.add(item);
            }
        }

        if(doneItems.size()>0) {
            Collections.sort(doneItems, boolComp);
        }
        if(falseItems.size()>0) {
            Collections.sort(falseItems, boolComp);
        }

        allItems.clear();
        for(DataItem item : doneItems) {
            allItems.add(item);
        }
        for(DataItem item : falseItems) {
            allItems.add(item);
        }
        // Log.i(logger, "SortByFav allItems after clear: " + allItems.size());

        adapter.clear();
        adapter.addAll(allItems);
//        adapter.notifyDataSetChanged();
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
                    URL url = new URL("http://192.168.178.20:8080/TodolistWebapp/");  //@Home
//                    URL url = new URL("http://192.168.178.32:8080/TodolistWebapp/");    //@KathisEltern
                    final HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setRequestProperty("User-Agent", "Android Application");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(10 * 1000);
                    urlc.connect();

                    if (urlc.getResponseCode() == 200) {
                        // Log.i(logger, "Network Log: Host reachable");
                        testing = true;
                    }
//                    // Log.i(logger, "Network Log: Code: " + urlc.getResponseCode());
                } catch (Throwable e) {
                    // Log.i(logger, "Network Log: Host not reachable");
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
//            // Log.i(logger, "Network Log - true or false: " + String.valueOf(hostTask.get()));
            if( hostTask.get() == true) {
                modelOperations = new SyncedDataItemCRUDOperationsImpl(OverviewActivity.this);
                // Log.i(logger, "Network Log: synced");
            } else {
                modelOperations = new CRUDOperations(OverviewActivity.this);
                alertDialog.setMessage(R.string.noHost_alert);
                alertDialog.show();
                // Log.i(logger, "Network Log: local");
            }
        } catch (InterruptedException e) {
            // Log.i(logger, "Network Log: interrupted");
//            e.printStackTrace();
        } catch (ExecutionException e) {
            // Log.i(logger, "Network Log: execution");
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
//                        adapter.notifyDataSetChanged();
                        readOutDataItemsAndPopulateView();
                    }
                }
            }.execute();
        }
    }

}
