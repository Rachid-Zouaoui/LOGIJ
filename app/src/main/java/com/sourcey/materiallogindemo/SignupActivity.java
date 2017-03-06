package com.sourcey.materiallogindemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private String name;
    private String emails;
    private String passwords;
    private String surname;

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);

        name = _nameText.getText().toString();
        emails = _emailText.getText().toString();
        passwords = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        //chiamata al server
        BackGround b = new BackGround();
        b.execute(name,emails,passwords);

    }
    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String username = params[0];
            String email = params[1];
            String password = params[2];
            String data = "";
            int tmp;


            try {
                //URL url = new URL("http://192.168.1.76/GuessImage/register.php"); //ho messo il mio ip (ipv4) perché il db è in locale
                URL url = new URL("http://evene.altervista.org/register.php");
                String urlParams = "username=" + username +"&email=" + email + "&password=" + password;
                System.out.println("parametri"+urlParams);
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
            final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            System.out.println("s è:"+s);
                            if (s.equals("{\"message\":\"connected to the database.\"}{\"message\":\"Dati salvati correttamente.\"}")) {
                               // Toast.makeText(getBaseContext(), "Dati salvati correttamente", Toast.LENGTH_LONG).show();
                                //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                //intent.putExtra("username", user); //PASSO LO USER AD HOME ACTIVITY, MI SERVE IN QUESTO MODO PER GESTIRE LE PREF
                                //startActivity(intent);
                                onSignupSuccess();
                            } else if (s.equals("{\"message\":\"connected to the database.\"}{\"message\":\"Unable to save the data to the database.\"}")) {
                                progressDialog.cancel();
                                //Toast.makeText(getBaseContext(), "Username già esistente", Toast.LENGTH_LONG).show();
                                onSignupFailed();
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


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        System.out.println("Ra"+name);
        intent.putExtra("username", name); //PASSO LO USER AD HOME ACTIVITY, MI SERVE IN QUESTO MODO PER GESTIRE LE PREF
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }



        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }



        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

}
