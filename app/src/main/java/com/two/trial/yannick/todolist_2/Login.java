package com.two.trial.yannick.todolist_2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.two.trial.yannick.todolist_2.model.IDataItemCRUDOperations;
import com.two.trial.yannick.todolist_2.model.User;
import com.two.trial.yannick.todolist_2.model.impl.CRUDOperations;
import com.two.trial.yannick.todolist_2.model.impl.SyncedDataItemCRUDOperationsImpl;
import com.two.trial.yannick.todolist_2.model.impl.UserImpl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class Login extends Activity {

    protected static String logger = "LoginActivity";

    private IDataItemCRUDOperations modelOperations;
    private UserImpl userOps = new UserImpl();

    private boolean hostOnline;

    private EditText loginEmail;
    private EditText loginPassword;
            boolean validMail;
            boolean validPassword;

    private AlertDialog.Builder alertDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.alertDialog = new AlertDialog.Builder(this);

        if(isOnline()) {
            isHostReachableForLogin();

            if(hostOnline == true) {
                Log.i(logger, "Login Network Log: Network available");
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
//                Intent callOverviewIntent = new Intent(Login.this, OverviewActivity.class);

                    @Override
                    public void onClick(View v) {
                        new AsyncTask<Void, Void, Boolean>() {
                            private ProgressDialog testDialog = null;

                            @Override
                            protected void onPreExecute() {
//                                testDialog = ProgressDialog.show(Login.this, "Bitte warten Sie...", "waehrend des Ladevorgangs.");
                            }

                            @Override
                            protected Boolean doInBackground(Void... params) {
                                User user = new User(String.valueOf(loginEmail.getText()), String.valueOf(loginPassword.getText()));
                                boolean validatedUser = userOps.authenticateUser(user);

                                if(validatedUser == true) {
                                    Intent callOverviewIntent = new Intent(Login.this, OverviewActivity.class);
                                    callOverviewIntent.putExtra("online", true);
                                    startActivity(callOverviewIntent);
                                }
                                return validatedUser;
                            }

                            @Override
                            protected void onPostExecute(Boolean validatedUser) {
//                        Log.i(Login.this.getClass().getName(),
//                                "onPostExecute()...");
//                                testDialog.cancel();
                                if(validatedUser == false) {

                                    alertDialog.setMessage("Email or password wrong. Please try again!")
                                            .setTitle("Authentication Error")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
//                                                    finish();
//                                                    startActivity(getIntent());
                                                }
                                            })
                                            .show();
                                }
                            }

                        }.execute();
                    }
                });
            } else {
                Log.i(logger, "Login Network Log: Host not available");
                Intent callOverviewIntent = new Intent(Login.this, OverviewActivity.class);
                callOverviewIntent.putExtra("online", false);
                startActivity(callOverviewIntent);
            }

        } else {
            Log.i(logger, "Login Network Log: Network not available");
            Intent callOverviewIntent = new Intent(Login.this, OverviewActivity.class);
            callOverviewIntent.putExtra("online", false);
            startActivity(callOverviewIntent);
        }
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public final static boolean isValidEmail(CharSequence target) {
        if(target == null)
            return false;
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void isHostReachableForLogin() {

        AsyncTask hostTask = new AsyncTask<Void, Void, Boolean>() {
            private ProgressDialog hostDialog = null;

            @Override
            protected void onPreExecute() {
                hostDialog = ProgressDialog.show(Login.this, "Bitte warten Sie...", "waehrend des Ladevorgangs.");
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
                        Log.i(logger, "Login Network Log: Host reachable");
                        hostOnline = true;
                    }
//                    // Log.i(logger, "Network Log: Code: " + urlc.getResponseCode());
                } catch (Throwable e) {
                     Log.i(logger, "Login Network Log: Host not reachable");
//                    e.printStackTrace();
                    hostOnline = false;
                }
                return hostOnline;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                hostDialog.cancel();
            }
        }.execute();

        try {
//            // Log.i(logger, "Network Log - true or false: " + String.valueOf(hostTask.get()));
            if( hostTask.get() == true) {
                modelOperations = new SyncedDataItemCRUDOperationsImpl(Login.this);
                // Log.i(logger, "Network Log: synced");
            } else {
                modelOperations = new CRUDOperations(Login.this);
                // Log.i(logger, "Network Log: local");
            }
        } catch (InterruptedException e) {
            // Log.i(logger, "Network Log: interrupted");
//            e.printStackTrace();
        } catch (ExecutionException e) {
            // Log.i(logger, "Network Log: execution");
//            e.printStackTrace();
        }

//        if(modelOperations instanceof SyncedDataItemCRUDOperationsImpl) {
//            new AsyncTask<Void, Void, Boolean>() {
//
//                @Override
//                protected Boolean doInBackground(Void... params) {
//                    try{
//                        ((SyncedDataItemCRUDOperationsImpl) modelOperations).exchangeTodos();
//                        return true;
//                    }catch (Exception e) {
//                        e.printStackTrace();
//                        return false;
//                    }
//                };
//
//                @Override
//                protected void onPostExecute(Boolean result) {
//                    if(result) {
//                        // if sync has been run successfully, we update the view
//                        adapter.clear();    // view gets cleared
////                        adapter.notifyDataSetChanged();
//                        readOutDataItemsAndPopulateView();
//                    }
//                }
//            }.execute();
//        }
    }
}
