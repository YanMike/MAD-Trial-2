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

        import android.os.Bundle;
        import android.app.Activity;
        import android.content.Intent;
        import android.util.Log;
        import android.view.Menu;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

public class OverviewActivity extends Activity {

    protected static String logger = "OverviewActivity";

    public OverviewActivity() {
        Log.i(logger, "called: <constructor>");                 // i = info, d = debug, e = error
    }

    /*
     * declare instance attributes for the ui elements          // Bedienelemente erstellen, die verwendet werden sollen
     */
    private TextView itemlistView;
    private Button addButton;

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
        itemlistView = (TextView) findViewById(R.id.itemlistView);          // Konstante, um id innerhalb eines Layouts zu finden
        addButton = (Button) findViewById(R.id.addButton);

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

        /* reset the listview removing the lorem ipsum using setText() */
        itemlistView.setText("ToDo List");
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
        Toast.makeText(this, "onActivityResult", Toast.LENGTH_SHORT).show();

        if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            DataItem item =(DataItem) data.getSerializableExtra("createdItem");
            itemlistView.setText(itemlistView.getText().toString() + "\n" + item.getName() + " -- " + item.getLatency());
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
