package com.two.trial.yannick.todolist_2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;


public class Login extends Activity {

    protected static String logger = "LoginActivity";

    private EditText loginEmail;
    private EditText loginPassword;
            boolean validMail;
            boolean validPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_login);

        loginEmail                  = (EditText) findViewById(R.id.loginEmail);
        loginPassword               = (EditText) findViewById(R.id.loginPassword);
        final Button loginButton    = (Button) findViewById(R.id.loginButton);

        loginButton.setEnabled(false);

        loginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (isValidEmail(loginEmail.getText().toString())) {
                    validMail = true;
                } else {
                    loginEmail.setError("Please insert a valid email address");
                }

                if(loginPassword.getText().toString().length() != 6) {
                    validPassword = false;
                    loginPassword.setError("Please insert a valid password (length of 6, only digits are allowed");
                } else {
                    validPassword = true;
                }

                if(validMail == true && validPassword == true){
                    loginButton.setEnabled(true);
                }
            }
        });

        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(validMail == true && validPassword == true){
                    loginButton.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        loginPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(validMail == true && validPassword == true){
//                    loginButton.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    private ProgressDialog testDialog = null;

                    @Override
                    protected void onPreExecute() {
                        testDialog = ProgressDialog.show(Login.this, "Bitte warten Sie...", "waehrend des Ladevorgangs.");
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
//                        Log.i(FrameLayout.class.getName(), "onClick(): ");
                        Log.i(logger, "NEUSTART NEUSTART NEUSTART NEUSTART NEUSTART NEUSTART NEUSTART NEUSTART NEUSTART NEUSTART ###########################################");
                        // TODO not with intent !?
                        startActivity(new Intent(Login.this, OverviewActivity.class));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void o) {
//                        Log.i(Login.this.getClass().getName(),
//                                "onPostExecute()...");
                        testDialog.cancel();
                    }

                }.execute();



            }
        });
    }

    public static final int ALERT_DIALOG = 0;
    public int dialogCount = 0;
    /**
     * display a dialog passing arguments
     */
    public void runDialog() {
        Bundle args = new Bundle();
        args.putInt("dialogCount", dialogCount++);

        showDialog(ALERT_DIALOG, args);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if(target == null)
            return false;
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
