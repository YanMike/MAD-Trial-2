package com.two.trial.yannick.todolist_2;

import com.two.trial.yannick.todolist_2.model.*;
import com.two.trial.yannick.todolist_2.model.impl.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OverviewActivity extends Activity {

    protected static String logger = "OverviewActivity";

    public OverviewActivity() {
//        // Log.i(logger, "called: <constructor>");                 // i = info, d = debug, e = error
    }

    private View itemlistView;
    private Button addButton;

    private AlertDialog.Builder alertDialog;

    private List<DataItem> allItems = new ArrayList<DataItem>();

    private IDataItemCRUDOperations modelOperations;

    private ArrayAdapter<DataItem> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call onCreate as super class !! important !! for all classes of the life cycle (that android framework can work as a framework)
        super.onCreate(savedInstanceState);

        /* set the view -> choose the layout */
        setContentView(R.layout.layout_activity_overview);

        /* instantiate the ui elements */
        itemlistView = findViewById(R.id.itemlistView);
        addButton    = (Button) findViewById(R.id.addButton);

        this.alertDialog = new AlertDialog.Builder(this);

        //TO-DO: code as method
        alertDialog.setMessage("Das ist ein AlertDialog")
                .setTitle("Offline Mode")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        final Bundle paramBundle = getIntent().getExtras();
        if(paramBundle != null) {
            if(paramBundle.getBoolean("online") == true ) {
                modelOperations = new SyncedDataItemCRUDOperationsImpl(OverviewActivity.this);
            } else {
                modelOperations = new CRUDOperations(OverviewActivity.this);
                alertDialog.setMessage(R.string.noHost_alert);
                alertDialog.show();
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
                        readOutDataItemsAndPopulateView();
                    }
                }
            }.execute();
        } else {
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

        adapter = new ArrayAdapter<DataItem>(this, R.layout.layout_listview_checkbox, R.id.itemName) {
            @Override
            public View getView(int position, View existingView, ViewGroup parent) {
                View listItemView;

                if(existingView != null) {
                    listItemView = existingView;
                    updateExistingListView();
                } else {
                    listItemView = getLayoutInflater().inflate(R.layout.layout_listview_checkbox, null);
                    updateItemListView();
                }

                /*
                 *  Name TextView
                 */
                TextView itemNameText = (TextView) listItemView.findViewById(R.id.itemName);
                final DataItem listItem = getItem(position);
                itemNameText.setText(listItem.getName());

                /*
                 *  Done CheckBox
                 */
                CheckBox itemChecked = (CheckBox) listItemView.findViewById(R.id.itemChecked);
                itemChecked.setOnCheckedChangeListener(null);
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
                        updateAndShowNewItem(listItem);
                    }
                });

                /*
                 *  Date TextView
                 */
                TextView dateView = (TextView) listItemView.findViewById(R.id.dateView);
                Date d = new Date(listItem.getExpiry());
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                dateView.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + cal.get(Calendar.DAY_OF_MONTH) + ", " + cal.get(Calendar.YEAR));
                if(listItem.getExpiry() < System.currentTimeMillis()) {
                    dateView.setTextColor(Color.RED);
                } else {
                    dateView.setTextColor(Color.BLACK);
                }

                // requires a View as return value -> list element gets identified by position (int)
                return listItemView;
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
    }

    /**
     * deal with adding a new element to the list
     */
    private void handleAddAction() {
        Intent callDetailIntent = new Intent(this, DetailviewActivity.class);
        startActivityForResult(callDetailIntent, 0);
    }

    /**
     * gets called, when clicked on item
     * @param id
     */
    private void handleUpdateTodoAction(long id) {
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

        startActivityForResult(callDetailIntent, 1);
    }

    private void readOutDataItemsAndPopulateView() {
        new AsyncTask<Void, Void, List<DataItem>>() {

            @Override
            protected List<DataItem> doInBackground(Void... params) {
                return modelOperations.readAllDataItems();
            }

            @Override
            protected void onPostExecute(List<DataItem> result) {
                adapter.addAll(result);
//                adapter.notifyDataSetChanged();
            }
        }.execute();
    }


    private void deleteDataItemAndUpdateListView(final long itemId) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return modelOperations.deleteDataItem(itemId);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    updateItemListView();
                } else {
                    Toast.makeText(OverviewActivity.this, "Error occurred: Todo could not be deleted!", Toast.LENGTH_SHORT).show();
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
         *  1 -> update/ delete ('all' actions in detailview activity)
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
            Log.i(logger, "Unspecified Error occured during onActivityResult().");
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
                    updateItemListView();
                }
                Toast.makeText(OverviewActivity.this, result != null ? "Successfully updated item" + result.getId() : "Update failed!", Toast.LENGTH_SHORT).show();
            };

        }.execute(item);
    }

    private void createAndShowNewItem(final DataItem newItem) {
        new AsyncTask<DataItem, Void, DataItem>() {
            @Override
            protected DataItem doInBackground(DataItem... params) {
                return modelOperations.createDataItem(params[0]);
            }
            @Override
            protected void onPostExecute(DataItem result) {
                updateItemListView();
            };
        }.execute(newItem);
    }

    /*
	 * update the view
	 */
    private void updateItemListView() {
        // to read out all data from db is not the nicest solution ;) but it works
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
        // to read out all data from db is not the nicest solution ;) but it works

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


    /**
     * Options menu
     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.optionAdd) {
            handleAddAction();
            return true;
        } else if (item.getItemId() == R.id.optionSortByDate) {
            sortByDate();
        } else if(item.getItemId() == R.id.optionSortByPrio) {
            sortByFav();
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

        adapter.clear();
        adapter.addAll(allItems);
//        adapter.notifyDataSetChanged();
    }
    public void sortByFav() {
        DateComparator dateComp = new DateComparator();

        allItems = modelOperations.readAllDataItems();

        List<DataItem> doneFavItems  = new ArrayList<>();
        List<DataItem> doneItems  = new ArrayList<>();
        List<DataItem> falseFavItems = new ArrayList<>();
        List<DataItem> falseItems = new ArrayList<>();
        for(DataItem item : allItems) {
            // done & fav
            if(item.isDone() && item.isFavourite()) {
                doneFavItems.add(item);
            }
            // done & not fav
            else if(item.isDone()) {
                doneItems.add(item);
            }
            // not done & fav
            else if(item.isFavourite()) {
                falseFavItems.add(item);
            }
            // not done & not fav
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

    /**
     * old
     */
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

        adapter.clear();
        adapter.addAll(allItems);
//        adapter.notifyDataSetChanged();
    }
}
