package com.two.trial.yannick.todolist_2;

import com.two.trial.yannick.todolist_2.model.*;
import com.two.trial.yannick.todolist_2.model.impl.*;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends Activity {

    protected static String logger = "OverviewActivity";

    public OverviewActivity() {
        Log.i(logger, "called: <constructor>");                 // i = info, d = debug, e = error
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

    // for ListView: declare a list of DataItem objects that will collect the items created by the user
    private List<DataItem> dataItems = new ArrayList<DataItem>();

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

        Log.i(logger, "called: onCreate()!");

        /* set the view -> choose the layout */
        setContentView(R.layout.layout_activity_overview);

        /* instantiate the ui elements */                                   // Bedienelemente auslesen, die verwendet werden sollen
        itemlistView = findViewById(R.id.itemlistView);                     // Konstante, um id innerhalb eines Layouts zu finden
        addButton    = (Button) findViewById(R.id.addButton);

        this.progressDialog = new ProgressDialog(this);

        /*
         * set an  onClick listener on the addButton
         *
         * create a handleAddAction() method on this class and call it from the listener
         *
         * show the alternative solution with the android:onClick attribute
         */

        // instantiate the model operations
//        modelOperations = new RemoteDataItemCRUDOperationsImpl();
        modelOperations = new CRUDOperations(this);       // Klasse muss übergeben werden als Context

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddAction();

                /**
                 * only for development - to delete database to add new columns or make any other basical changes
                */
                /*
                try {
                    modelOperations.deleteSQLiteDatabase(OverviewActivity.this);
                    Log.i(logger, "database deleted");
                } catch(Exception e) {
                    Log.i(logger, "database deletion failed: " + e);
                    e.printStackTrace();
                }
                */
                /* *** */
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
                    Log.i(logger, "reusing existing view for position " + position + ": " + listItemView);
                } else {
                    // LayoutInflater => "Luftpumpe", die ein luftleeres XML Layout zu schönem Java Layout aufzublasen, mit Layout das hier erstellt wird
                    listItemView = getLayoutInflater().inflate(R.layout.layout_listview_checkbox, null);
                    Log.i(logger, "creating new view for position " + position + ": " + listItemView);

                    //für fragments: getActivity().getLayoutInflater().inflate(R.layout.layout_listview_checkbox, null); // null = Elternelement

                }

                TextView itemNameText = (TextView) listItemView.findViewById(R.id.itemName);
                CheckBox itemChecked = (CheckBox) listItemView.findViewById(R.id.itemChecked);

                itemChecked.setOnCheckedChangeListener(null); // harte Methode, um beim Wiederverwenden der Ansicht nicht den alten Listener zu überschreiben

                final DataItem listItem = getItem(position);

                itemNameText.setText(listItem.getName()); // + " - " + listItem.getDescription());
                itemChecked.setChecked(listItem.isDone());

                itemChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        listItem.setDone(isChecked);
                        handleUpdateAction(listItem);

//                        deleteDataItemAndUpdateListView(listItem);
                    }
                });

                return listItemView;
                // erwartet einen View als return -> den wir für ein einzelnes Listenelement über int position bestimmen
            }
        };
        adapter.setNotifyOnChange(true);
        ((ListView)itemlistView).setAdapter(adapter);

        ((ListView) itemlistView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO REAL TODO : UMSETZEN -  ÜBERGANG IN DETAILVIEW

                // test
                /*
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.layout_textview_simple, null);
                textView.setText(String.valueOf(System.currentTimeMillis()));

                ((ViewGroup)view).addView(textView);
                */
                // siehe Vorlesung 27.05. um auch andere Layouts einzubinden (evtl. Lösung für Favourite/ un-favourite?
                /* *** */


            }
        });



        // read out all items and populate the view
        new AsyncTask<Void, Void, List<DataItem>>() {

            @Override
            protected List<DataItem> doInBackground(Void... params) {
                return modelOperations.readAllDataItems();
            }

            // Aufruf von onPostExecute erfolgt auf UI Thread -> Schreibzugriff gegeben
            @Override
            protected void onPostExecute(List<DataItem> result) {
                adapter.addAll(result);
            }
        }.execute();
    }

    private void deleteDataItemAndUpdateListView(final DataItem selectedItem) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                // wenn löschen erfolgreich -> Methode returns true
                return modelOperations.deleteDataItem(selectedItem.getId());
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    // Um Ansicht zu aktualisieren
                    adapter.remove(selectedItem);
                } else {
                    // falls löschen fehl schlägt
                    Toast.makeText(OverviewActivity.this, "DataItem could not be deleted!", Toast.LENGTH_SHORT).show();
                    Log.i(logger, "Deletion failed!");
                }
            }
        }.execute();
    }

    /*
     * deal with adding a new element to the list
     */
    private void handleAddAction() {
        /* first simply use a toast to show feedback of the onclick action */
        Toast.makeText(this, "handleAddAction()", Toast.LENGTH_LONG).show();

    	/* then call the Detailview Activity */
        Intent callDetailIntent = new Intent(this, DetailviewActivity.class);

    	/* create an intent expressing what we want to DO, i.e. using the action SHOW_DETAILS */

    	/* pass arguments ("extras") to the detailview - we show it using the current time as an example */
        callDetailIntent.putExtra("callTime", System.currentTimeMillis());
    	/* actually send the intent triggering the display of the activity - use startActivityForResult */
        startActivityForResult(callDetailIntent, 0);
    }

    private void handleUpdateAction(DataItem item) {
        new AsyncTask<DataItem, Void, DataItem>() {

            @Override
            protected DataItem doInBackground(DataItem... params) {
                if(modelOperations instanceof CRUDOperations) {
                    return ((CRUDOperations)modelOperations).updateDataItem(params[0]);
                }
                return params[0];
            };

            @Override
            protected void onPostExecute(DataItem result) {
                if(result != null)
                Toast.makeText(OverviewActivity.this, result != null ? "Successfully updated item" + result.getId() : "Update failed!", Toast.LENGTH_LONG).show();
            };

        }.execute(item);
    }

    /* implement onActivityResult(): read out result and update the listview using setText() */ // muss implementiert sein, um auf das Resultat reagieren zu können
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether we have a result object
        if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            DataItem item =(DataItem) data.getSerializableExtra("createdItem");
            createAndShowNewItem(item);
        } else {
            Log.i(logger, "no newItem contained in result");
        }
    }

    private void createAndShowNewItem(final DataItem newItem) {

        new AsyncTask<DataItem, Void, DataItem>() {
            // Aufruf von onPreExecute erfolgt auf UI Thread
            /*@Override
            protected void onPreExecute() {
                progressDialog.show();
            };*/

            @Override
            protected DataItem doInBackground(DataItem... params) {
                return modelOperations.createDataItem(params[0]);
            }

            // Aufruf von onPostExecute erfolgt auf UI Thread -> Schreibzugriff gegeben
            @Override
            protected void onPostExecute(DataItem result) {
                //adapter.addAll(result);
                updateItemListView(result);
                //progressDialog.hide();
            };

        }.execute(newItem);
    }

    /*
	 * update the view
	 */
    private void updateItemListView(DataItem item) {
        /* add the item to the adapter */
        adapter.add(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.i(logger, "onResume()!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(logger, "onDestroy()!");
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
}
