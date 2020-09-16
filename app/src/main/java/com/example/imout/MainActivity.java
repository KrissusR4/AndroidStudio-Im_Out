package com.example.imout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private int activity = 2;
    private Context context;
    private EditText username;
    private EditText password;
    private Button logIn;
    private Button register;
    private CheckBox saveLoginCheckBox;
    JSONObject receiveData;
    private Button guest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        logIn = findViewById(R.id.logInButton);
        register = findViewById(R.id.registerButton);
        guest = findViewById(R.id.guestButton);
        saveLoginCheckBox = findViewById(R.id.saveLoginCheckBox);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        loginPrefsEditor.putString("picture", "https://imoutcodebullies.000webhostapp.com/Images/User/default.jpg");
        loginPrefsEditor.putInt("privileges", 0);

        boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (!saveLogin)
            loginPrefsEditor.clear().commit();
        if (saveLogin == true) {
            login(loginPreferences.getString("username", ""), loginPreferences.getString("password", ""), true);
            saveLoginCheckBox.setChecked(true);
        }

        logIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login(username.getText().toString(), password.getText().toString(), false);
            }
        });



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intentRegister = new Intent(MainActivity.this , RegisterActivity.class);
                startActivityForResult(intentRegister,activity);
                //startActivity(intentRegister);

            }
        });


        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent guestIntent = new Intent(MainActivity.this, StartingActivity.class);
                startActivity(guestIntent);
                finish();
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == activity) {
            if (resultCode == RESULT_OK) {
                username.setText(data.getStringExtra("username"));
                password.setText(data.getStringExtra("password"));
            }
        }
    }
    protected void login(String u, String p, boolean y){

        JSONObject postData = new JSONObject();
        try {
            postData.put("username", u);
            postData.put("password", p);
            postData.put("md5", y);

            Requests task = new Requests(new Requests.AsyncResponse(){

                @Override
                public void processFinish(String output){ //Sta da se uradi sa povratnim podacima
                    try {
                        receiveData = new JSONObject(output);
                        String message = receiveData.getString("message");

                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, message, duration);
                        toast.show();

                        if (message.equals("Success")){

                            loginPrefsEditor.putString("username", receiveData.getString("Username"));
                            loginPrefsEditor.putString("password", receiveData.getString("Password"));
                            loginPrefsEditor.putString("email", receiveData.getString("Email"));
                            loginPrefsEditor.putString("picture", receiveData.getString("Slika"));
                            loginPrefsEditor.putInt("privileges", receiveData.getInt("TipKorisnika"));
                            loginPrefsEditor.putString("ime", receiveData.getString("Ime"));
                            loginPrefsEditor.putString("prezime", receiveData.getString("Prezime"));
                            loginPrefsEditor.putString("brojTelefona", receiveData.getString("BrojTelefona"));
                            loginPrefsEditor.putString("datumRodjenja", receiveData.getString("DatumRodjenja"));
                            loginPrefsEditor.putString("mesto", receiveData.getString("Mesto"));

                            if (saveLoginCheckBox.isChecked() || loginPreferences.getBoolean("saveLogin", false)) {
                                loginPrefsEditor.putBoolean("saveLogin", true);
                            } else {
                                loginPrefsEditor.putBoolean("saveLogin", false);
                                //loginPrefsEditor.clear();
                            }
                            loginPrefsEditor.apply();

                            Intent intentSuccess = new Intent(MainActivity.this , StartingActivity.class);
                            Bundle b = new Bundle();
                            b.putBoolean("edit", false);
                            startActivity(intentSuccess);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            try {
                task.execute("login.php", postData.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
