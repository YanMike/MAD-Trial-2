/*
package com.two.trial.yannick.todolist_2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class OverviewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
*/


package com.two.trial.yannick.todolist_2;

        import com.two.trial.yannick.todolist_2.model.DataItem;

        import android.app.ProgressDialog;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.app.Activity;
        import android.content.Intent;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

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
//    private TextView itemlistView;
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

    // for ListView: declare a list of DataItem objects that will collect the
    // items created by the user
    private List<DataItem> dataItems = new ArrayList<DataItem>();

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
//x        itemlistView = (TextView) findViewById(R.id.itemlistView);          // Konstante, um id innerhalb eines Layouts zu finden
        itemlistView = findViewById(R.id.itemlistView);
        addButton = (Button) findViewById(R.id.addButton);

        this.progressDialog = new ProgressDialog(this);

        /*
         * set an onClick listener on the addButton
         *
         * create a handleAddAction() method on this class and call it from the listener
         *
         * show the alternative solution with the android:onClick attribute
         */

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(OverviewActivity.this, "onClick!", Toast.LENGTH_LONG).show();        // zu Testzwecken; alert, der alleine verschwindet
                handleAddAction();
            }
        });

        adapter = new ArrayAdapter<DataItem>(this, R.layout.layout_listview_checkbox, R.id.itemName);
        adapter.setNotifyOnChange(true);

        if (itemlistView instanceof TextView) {
            ((TextView) itemlistView).setText("");
        } else {
            ((ListView)itemlistView).setAdapter(adapter);
        }

        // read out all items and populate the view
        new AsyncTask<Void, Void, List<DataItem>>() {

            // Aufruf von onPreExecute erfolgt auf UI Thread
            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected List<DataItem> doInBackground(Void... params) {
                return readAllDataItems();
            }

            // Aufruf von onPostExecute erfolgt auf UI Thread -> Schreibzugriff gegeben
            @Override
            protected void onPostExecute(List<DataItem> result) {
                adapter.addAll(result);
                progressDialog.hide();
            }
        }.execute();



        /* reset the listview removing the lorem ipsum using setText() */
//x        itemlistView.setText("ToDo List");

    }

    private List<DataItem> readAllDataItems() {
        try {
            // to slow done UI Thread
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // create a list of two items
        List<DataItem> dataItems = new ArrayList<DataItem>();
        dataItems.add(new DataItem("i1", System.currentTimeMillis()));
        dataItems.add(new DataItem("i2", System.currentTimeMillis()));
        dataItems.add(new DataItem("i3", System.currentTimeMillis()));
        dataItems.add(new DataItem("i4", System.currentTimeMillis()));
        dataItems.add(new DataItem("i5", System.currentTimeMillis()));

        return dataItems;
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


    /* implement onActivityResult(): read out result and update the listview using setText() */ // muss implementiert sein, um auf das Resultat reagieren zu können
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(this, "onActivityResult", Toast.LENGTH_SHORT).show();
//
//        if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
//            DataItem item =(DataItem) data.getSerializableExtra("createdItem");
//            itemlistView.setText(itemlistView.getText().toString() + "\n" + item.getName() + " -- " + item.getLatency());
//        }

        // check whether we have a result object
        if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            DataItem item =(DataItem) data.getSerializableExtra("createdItem");
            createAndShowNewItem(item);
        } else {
            Log.i(logger, "no newItem contained in result");
        }
    }

    private void createAndShowNewItem(final DataItem newItem) {

        // show the progress dialog
//        progressDialog.show();
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // call the createDataItemMethod, passing newItem
//                final DataItem createdItem = createDataItem(newItem);
//
//                this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // update the listview with the return value of
//                        // createDataItem
//                        updateItemlistView(createdItem);
//                        // close the progress dialog
//                        progressDialog.hide();
//                    }
//                });
//            }
//
//        }).start();

        new AsyncTask<DataItem, Void, DataItem>() {

            // Aufruf von onPreExecute erfolgt auf UI Thread
            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected DataItem doInBackground(DataItem... params) {
                return createDataItem(newItem);
            }

            // Aufruf von onPostExecute erfolgt auf UI Thread -> Schreibzugriff gegeben
            @Override
            protected void onPostExecute(DataItem result) {
//                adapter.addAll(result);
//                progressDialog.hide();
                updateItemlistView(result);
                progressDialog.hide();
            }
        }.execute();

    }

    /*
	 * some action that is performed and takes some time...
	 */
    private DataItem createDataItem(DataItem item) {
        try {
            Thread.sleep(1500);
            // we add the item to the list
            dataItems.add(item);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return item;
    }

    /*
	 * update the view
	 */
    private void updateItemlistView(DataItem item) {

        if (this.itemlistView instanceof TextView) {
            ((TextView) itemlistView).setText(((TextView) itemlistView).getText() + "\n" + item.getName() + " -- "
                    + item.getLatency());
            progressDialog.hide();
        }
		/* for ListView: here we deal with the case that we have a listview */
        else {
			/* add the item to the adapter */
            adapter.add(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_overview, menu);
        return true;
    }

    /* implement boolean onOptionsItemSelected(MenuItem item)  */

}
