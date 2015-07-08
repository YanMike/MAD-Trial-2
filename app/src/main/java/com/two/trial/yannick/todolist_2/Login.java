package com.two.trial.yannick.todolist_2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.two.trial.yannick.todolist_2.model.User;
import com.two.trial.yannick.todolist_2.model.impl.UserImpl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class Login extends Activity {

    protected static String logger = "LoginActivity";

    private UserImpl userOps = new UserImpl();

    private boolean hostOnline;

    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;

//    private AlertDialog.Builder alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        this.alertDialog = new AlertDialog.Builder(this);

        if(isOnline()) {
            isHostReachableForLogin();

            if(hostOnline == true) {
                Log.i(logger, "Login Network Log: Network available");
                setContentView(R.layout.layout_login);

                loginEmail     = (EditText) findViewById(R.id.loginEmail);
                loginPassword  = (EditText) findViewById(R.id.loginPassword);
                loginButton    = (Button) findViewById(R.id.loginButton);

                // to set error message on App start
                isLoginValid();
                loginButton.setEnabled(false);

                loginEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        loginPassword.setError(null);
                        if (isLoginValid()) {
                            loginButton.setEnabled(true);
                        } else {
                            loginButton.setEnabled(false);
                        }
                    }
                });
                loginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        loginPassword.setError(null);
                        if (isLoginValid()) {
                            loginButton.setEnabled(true);
                        } else {
                            loginButton.setEnabled(false);
                        }
                    }
                });

                loginEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        loginPassword.setError(null);
                        if (isLoginValid()) {
                            loginButton.setEnabled(true);
                        } else {
                            loginButton.setEnabled(false);
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        loginEmail.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(isEmailValid(loginEmail.getText().toString()) && isPasswordValid(loginPassword.getText().toString())) {
                            loginButton.setEnabled(true);
                        } else {
                            loginButton.setEnabled(false);
                        }
                    }
                });
                loginPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        loginPassword.setError(null);
                        if (isLoginValid()) {
                            loginButton.setEnabled(true);
                        } else {
                            loginButton.setEnabled(false);
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        loginPassword.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(isEmailValid(loginEmail.getText().toString()) && isPasswordValid(loginPassword.getText().toString())) {
                            loginButton.setEnabled(true);
                        } else {
                            loginButton.setEnabled(false);
                        }
                    }
                });

                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AsyncTask<Void, Void, Boolean>() {
                            private ProgressDialog authDialog = null;

                            @Override
                            protected void onPreExecute() {
                                authDialog = ProgressDialog.show(Login.this, "Bitte warten Sie...", "waehrend des Ladevorgangs.");
                            }

                            @Override
                            protected Boolean doInBackground(Void... params) {
                                try {
                                    // for presentation, to see ProgressDialog
                                    Thread.sleep(1000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return false;
                                }
                                return loginAttempt();
                            }

                            @Override
                            protected void onPostExecute(Boolean validatedUser) {
                                authDialog.cancel();
                                if(validatedUser == false) {
                                    loginPassword.setError("Email or password wrong. Please try again!");

//                                    alertDialog.setMessage("Email or password wrong. Please try again!")
//                                            .setTitle("Authentication Error")
//                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.cancel();
////                                                    finish();
////                                                    startActivity(getIntent());
//                                                }
//                                            })
//                                            .show();
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

    public boolean isLoginValid() {
        boolean cancel = false;
        String mail    = loginEmail.getText().toString();
        String pwd = loginPassword.getText().toString();

        if(isEmailValid(mail) && isPasswordValid(pwd)) {
            return true;
        } else if(isEmailValid(mail) && !isPasswordValid(pwd)) {
            loginEmail.setError(null);
            loginPassword.setError("Please insert a valid password.");
            return false;
        } else if(!isEmailValid(mail) && isPasswordValid(pwd)) {
            loginEmail.setError("Please insert a valid email.");
            loginPassword.setError(null);
            return false;
        } else {
            loginEmail.setError("Please insert a valid email.");
            loginPassword.setError("Please insert a valid password.");
            return false;
        }
    }

    public boolean isPasswordValid(String pwd) {
        return pwd.length() == 6;
    }

    public static boolean isEmailValid(String mail) {
        if(mail == null)
            return false;
        return Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    public boolean loginAttempt() {
        User user = new User(String.valueOf(loginEmail.getText()), String.valueOf(loginPassword.getText()));
        boolean validatedUser = userOps.authenticateUser(user);
        if(validatedUser == true) {
            Intent callOverviewIntent = new Intent(Login.this, OverviewActivity.class);
            callOverviewIntent.putExtra("online", true);
            startActivity(callOverviewIntent);
        }
        return validatedUser;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void isHostReachableForLogin() {

        AsyncTask hostTask = new AsyncTask<Void, Void, Boolean>() {
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
        }.execute();

        try {
            hostTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}