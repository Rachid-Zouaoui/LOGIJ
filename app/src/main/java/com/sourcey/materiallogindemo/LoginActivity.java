package com.sourcey.materiallogindemo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.Bind;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private String name;
    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        saveUsername();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

         name = _nameText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        BackGround b = new BackGround();
        b.execute(name,password);

    }

    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String username = params[0];
            String password = params[1];
            String data = "";
            int tmp;


            try {
                //URL url = new URL("http://192.168.1.76/GuessImage/register.php"); //ho messo il mio ip (ipv4) perché il db è in locale
                URL url = new URL("http://evene.altervista.org/login.php");
                String urlParams = "username=" +username+"&password=" + password ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();
                InputStream is = httpURLConnection.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data += (char) tmp;
                }
                is.close();
                httpURLConnection.disconnect();

                return data;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            //Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Verification...");
            progressDialog.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            System.out.println("s é:"+s);
                            if (s.equals("{\"message\":\"ok.\"}")) {
                                // Toast.makeText(getBaseContext(), "Dati salvati correttamente", Toast.LENGTH_LONG).show();
                                //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                //intent.putExtra("username", user); //PASSO LO USER AD HOME ACTIVITY, MI SERVE IN QUESTO MODO PER GESTIRE LE PREF
                                //startActivity(intent);
                                onLoginSuccess();
                            } else if (s.equals("{\"message\":\"errato.\"}")) {
                                progressDialog.cancel();
                                //Toast.makeText(getBaseContext(), "Username già esistente", Toast.LENGTH_LONG).show();
                                onLoginFailed();
                                //utente.setError("Inserire un username diverso!");
                            } else if (s.equals("Exception: Unable to resolve host \"guessimage.altervista.org\": No address associated with hostname")) {
                                progressDialog.cancel();
                                Toast.makeText(getBaseContext(), "Connessione Fallita, assicurati di essere connesso", Toast.LENGTH_LONG).show();
                                //startActivity(getIntent());
                            }
                        }
                    }, 1500);
        }
    }


    /**public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        System.out.println("Ra"+name);
        intent.putExtra("username", name); //PASSO LO USER AD HOME ACTIVITY, MI SERVE IN QUESTO MODO PER GESTIRE LE PREF
        startActivity(intent);
        finish();
    }**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        intent.putExtra("username", name);//PASSO LO USER AD HOME ACTIVITY, MI SERVE IN QUESTO MODO PER GESTIRE LE PREF
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }


        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
    private void saveUsername() { //controllo se lo username é salvato

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getString(HomeActivity.NOME_UTENTE, "").isEmpty()) {
            //se lo username e' stato RICEVUTO correttamente da HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("username", prefs.getString(HomeActivity.NOME_UTENTE, ""));
            //Prendo il valore di default di NOME_UTENTE lo assegno a "username" lo passo ad Home*
            startActivity(intent);
        }


    }
}
