package com.two.trial.yannick.todolist_2;

/**
 * Created by yannick on 30.04.15.
 */
    import com.two.trial.yannick.todolist_2.model.DataItem;

    import android.app.Activity;
    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.Menu;
    import android.view.View;
    import android.widget.Button;
    import android.widget.CheckBox;
    import android.widget.EditText;
    import android.widget.Toast;

public class DetailviewActivity extends Activity {

    protected static String logger = "DetailActivity";

	/*
	 * declare attributes for the three ui elements (and later for the createAction MenuItem) - Instanzattribute
	 */

    private CheckBox doneCheckBox;
    private EditText itemnameText;
    private EditText descriptionText;
    private Button createButton;
    private Button deleteButton;
    private long latency;

    private AlertDialog.Builder alertDialog;

	/* an attribute that holds the latency between calling us and receiving the call (like any functionality implemented here this has only didactic purpose) */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Detailview", "onCreate");
        setContentView(R.layout.layout_activity_detailview);

		/* we read out the argument from the intent that contains the calltime and instantiate the latency attribute */
        latency = System.currentTimeMillis() - getIntent().getLongExtra("callTime", 0);

		/* instantiate the three ui elements */
        doneCheckBox    = (CheckBox) findViewById(R.id.doneCheckBox);
        itemnameText    = (EditText) findViewById(R.id.itemnameText);
        descriptionText = (EditText) findViewById(R.id.descriptionText);
        createButton    = (Button) findViewById(R.id.createButton);
        deleteButton    = (Button) findViewById(R.id.deleteButton);

        this.alertDialog = new AlertDialog.Builder(this);

        //TODO: /* set the createButton as not enabled as long as no text has been input */

		/* set a listener on the itemnameText that enables the createButton on done */

		/* use this check in OnEditorActionListener:
		 * actionId == EditorInfo.IME_ACTION_DONE
						|| KeyEvent.KEYCODE_ENTER == event.getKeyCode() */

        final Bundle paramBundle = getIntent().getExtras();
        if(paramBundle != null) {
            itemnameText.setText(paramBundle.getString("name"));
            descriptionText.setText(paramBundle.getString("description"));
            createButton.setText("Update Item");

            /* set an onClick listener on the createButton that calls the createItem action */
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCreateAction("update", paramBundle.getLong("paramItemId"));
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.setMessage("Do you want to delete this item?")
                            .setTitle("Confirm Deletion")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handleDeleteAction(paramBundle.getLong("paramItemId"));
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                    .show();
                }
            });
        } else {
            createButton.setText("Create Item");
            /* set an onClick listener on the createButton that calls the createItem action */
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCreateAction("create", 0);
                }
            });
        }
		/*TODO set the latency value as text on the latency textview */
//        latencyLabel.setText(String.valueOf(latency));
    }

    private void handleCreateAction(String type, long passedItemId) {
        /* create an item, using the text from the edit text and the latency attribute */
        DataItem item = new DataItem(String.valueOf(itemnameText.getText()), latency, String.valueOf(descriptionText.getText()), doneCheckBox.isChecked(), false);
        item.setId(passedItemId);

		/* create a return intent and pass the item (back to the activity) */
        Intent returnIntent = new Intent();

        if(type == "create") {
            returnIntent.putExtra("createdItem", item);
        } else if(type == "update") {
            returnIntent.putExtra("updatedItem", item);
        }

		/* set the result passing RESULT_OK from Activity */
        setResult(Activity.RESULT_OK, returnIntent);

		/* finish the activity */
        finish(); // die zuvor aufgerufene Activity wird wieder aufgerufen
    }

    private void handleDeleteAction(long itemId) {
        Log.i(logger, "loeschen: handleDeleteAction");

        Intent returnIntent = new Intent();
        returnIntent.putExtra("deletedItem", itemId);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("Detailview","onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_detailview, menu);

		/* we instantiate a create action once it is available */

        return true;
    }

	/* add boolean onOptionsItemSelected(MenuItem item) */

}
