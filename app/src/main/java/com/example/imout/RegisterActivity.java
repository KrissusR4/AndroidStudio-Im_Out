package com.example.imout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private Context context;
    private EditText username;
    private EditText password;
    private Button register;
    private EditText dateOfBirth;
    private EditText email;
    private EditText passwordConfirm;
    public JSONObject receiveData;
    private EditText name;
    private EditText surname;
    private EditText phoneNumber;
    private EditText city;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    boolean editData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = getApplicationContext();

        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        register = findViewById(R.id.buttonRegister);
        dateOfBirth = findViewById(R.id.dateOfBirthEditText);
        email = findViewById(R.id.email);
        passwordConfirm = findViewById(R.id.confirmedPassword);
        name = findViewById(R.id.Name);
        surname = findViewById(R.id.Surname);
        phoneNumber = findViewById(R.id.phoneNumber);
        city = findViewById(R.id.city);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            editData = extras.getBoolean("edit");
        }
        if(editData){
            register.setText("Edit");
            username.setHint("");
            username.setHint("Type your old password");
            setEditValues();
            username.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        else{
            register.setText("Register");
            username.setHint("Username");
            username.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_NORMAL);
        }
        //OnClicks
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!editData) {
                    register();
                }
                else{ //Ako treba da se edituje
                    sendEditedData();
                }
            }
        });
    }
    protected void register(){
            if (verifyFields()) { // Provera da li su sva potrebna polja popunjena
                Requests task = new Requests(new Requests.AsyncResponse() {
                    @Override
                    public void processFinish(String output) { //Sta da se uradi sa povratnim podacima
                        try {
                            receiveData = new JSONObject(output);
                            String message = receiveData.getString("message");

                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, message, duration);
                            toast.show();
                            if (message.equals("Success")) {
                                Intent data = new Intent();
                                data.putExtra("username", username.getText().toString());
                                data.putExtra("password", password.getText().toString());

                                setResult(RESULT_OK, data);

                                Intent successIntent = new Intent(RegisterActivity.this, StartingActivity.class);
                                startActivity(successIntent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                try {
                    task.execute("register.php", packJson().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {   //Ne poklapaju se lozinke
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Failed to validate fields", duration);
                toast.show();
            }
    }
    protected void sendEditedData(){
        if (verifyFields()) { // Provera da li su sva potrebna polja popunjena
            Requests task = new Requests(new Requests.AsyncResponse() {
                @Override
                public void processFinish(String output) { //Sta da se uradi sa povratnim podacima
                    try {
                        receiveData = new JSONObject(output);
                        String message = receiveData.getString("message");

                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, message, duration);
                        toast.show();
                        if (message.equals("Success")) {
                            addDataToPref(receiveData.getString("newpassword"));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                task.execute("register.php", packJson().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {   //Ne poklapaju se lozinke
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "Failed to validate fields", duration);
            toast.show();
        }
    }
    protected boolean verifyFields(){
        boolean nm = Validate.hasText(name, true);
        boolean lnm = Validate.hasText(surname, true);
        boolean us = true;
        boolean pw;
        boolean oldpw = true;
        if(!editData){
            us = Validate.hasText(username, true);
            pw = Validate.isPassword(password,passwordConfirm);
        }
        else{
            pw = (!Validate.hasText(password, false) && !Validate.hasText(passwordConfirm, false)) || Validate.isPassword(password,passwordConfirm);
            oldpw = Validate.isPassword(username, username);
        }

        boolean em = Validate.isEmailAddress(email,true);
        boolean ph = Validate.isPhoneNumber(phoneNumber,false);
        return(nm && lnm && us && pw && em && ph && oldpw);
    }
    protected void setEditValues(){
        dateOfBirth.setText(loginPreferences.getString("datumRodjenja",""));
        email.setText(loginPreferences.getString("email",""));
        name.setText(loginPreferences.getString("ime",""));
        surname.setText(loginPreferences.getString("prezime",""));
        phoneNumber.setText(loginPreferences.getString("brojTelefona",""));
        city.setText(loginPreferences.getString("mesto",""));
    }
    protected JSONObject packJson(){
        JSONObject postData = new JSONObject();
        try {
            if(editData){
                if((!Validate.hasText(password, false) && !Validate.hasText(passwordConfirm, false))){
                    postData.put("newPassword", username.getText().toString());
                }else
                    postData.put("newPassword", password.getText().toString());
                postData.put("password", username.getText().toString());
                postData.put("md5", true);
            }
            else{
                postData.put("password", password.getText().toString());
            }
            postData.put("username", loginPreferences.getString("username", ""));
            postData.put("date", dateOfBirth.getText().toString());
            postData.put("email", email.getText().toString());
            postData.put("name", name.getText().toString());
            postData.put("surname", surname.getText().toString());
            postData.put("phoneNumber", phoneNumber.getText().toString());
            postData.put("city", city.getText().toString());
            postData.put("edit", editData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }
    protected void addDataToPref(String pw){
        loginPrefsEditor.putString("password", pw);
        loginPrefsEditor.putString("email", email.getText().toString());
        loginPrefsEditor.putString("ime", name.getText().toString());
        loginPrefsEditor.putString("prezime", surname.getText().toString());
        loginPrefsEditor.putString("brojTelefona", phoneNumber.getText().toString());
        loginPrefsEditor.putString("datumRodjenja", dateOfBirth.getText().toString());
        loginPrefsEditor.putString("mesto", city.getText().toString());
        loginPrefsEditor.apply();
    }
}
