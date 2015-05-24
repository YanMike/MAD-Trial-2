package com.two.trial.yannick.todolist_2;

/**
 * Created by yannick on 30.04.15.
 */
    import com.two.trial.yannick.todolist_2.model.ToDoData;

    import android.app.Activity;
    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.Menu;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

public class DetailviewActivity extends Activity {

	/*
	 * declare attributes for the three ui elements (and later for the createAction MenuItem) - Instanzattribute
	 */

    private EditText itemnameText;
    private EditText descriptionText;
    private Button createButton;
    private long latency;

	/* an attribute that holds the latency between calling us and receiving the call (like any functionality implemented here this has only didactic purpose) */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Detailview", "onCreate");
        setContentView(R.layout.layout_activity_detailview);

		/* we read out the argument from the intent that contains the calltime and instantiate the latency attribute */
        latency = System.currentTimeMillis() - getIntent().getLongExtra("callTime", 0);

		/* instantiate the three ui elements */
        itemnameText    = (EditText) findViewById(R.id.itemnameText);
        descriptionText = (EditText) findViewById(R.id.descriptionText);
        createButton    = (Button) findViewById(R.id.createButton);

		/* set the createButton as not enabled as long as no text has been input */

		/* set a listener on the itemnameText that enables the createButton on done */

		/* use this check in OnEditorActionListener:
		 * actionId == EditorInfo.IME_ACTION_DONE
						|| KeyEvent.KEYCODE_ENTER == event.getKeyCode() */

		/* set an onClick listener on the createButton that calls the createItem action */
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateAction();
            }
        });

		/* set the latency value as text on the latency textview */
//        latencyLabel.setText(String.valueOf(latency));
    }

    private void handleCreateAction() {
		/* create an item, using the text from the edit text and the latency attribute */
        ToDoData item = new ToDoData(String.valueOf(itemnameText.getText()), latency, String.valueOf(descriptionText.getText()));

		/* create a return intent and pass the item (back to the activity) */
        Intent returnIntent = new Intent();
        returnIntent.putExtra("createdItem", item);

		/* set the result passing RESULT_OK from Activity */
        setResult(Activity.RESULT_OK, returnIntent);

		/* finish the activity */
        finish(); // die zuvor aufgerufene Activity wird wieder aufgerufen
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
