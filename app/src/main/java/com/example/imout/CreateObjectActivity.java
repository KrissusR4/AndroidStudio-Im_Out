package com.example.imout;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateObjectActivity extends AppCompatActivity {

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private int privileges = 0;

    private Context context;

    public JSONObject receiveData;

    private EditText objectName;
    private EditText objectAddress;
    private EditText objectType;
    private EditText objectLocationX;
    private EditText objectLocationY;
    private EditText objectContact;
    private EditText objectTimeOpened;
    private EditText objectMusicPreference;
    private EditText objectHighTables;
    private EditText objectLowTables;
    private EditText objectBarChairs;

    //private ImageView objectLogo;
    //private Button objectOpenEditorButton;
    private Button objectCreateButton;

    //boolean editData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_object);
        context = getApplicationContext();

        objectName = findViewById(R.id.objectCreateName);
        objectAddress = findViewById(R.id.objectCreateAddress);
        objectType = findViewById(R.id.objectCreateType);
        objectLocationX = findViewById(R.id.objectCreateLocationX);
        objectLocationY = findViewById(R.id.objectCreateLocationY);
        objectContact = findViewById(R.id.objectCreateContact);
        objectTimeOpened = findViewById(R.id.objectCreateTimeOpened);
        objectMusicPreference = findViewById(R.id.objectCreateMusicPreference);
        objectHighTables = findViewById(R.id.objectCreateHighTables);
        objectLowTables = findViewById(R.id.objectCreateLowTables);
        objectBarChairs = findViewById(R.id.objectCreateBarChairs);

        //objectLogo = findViewById(R.id.objectCreateLogoPic);
        //objectOpenEditorButton = findViewById(R.id.objectCreateOpenEditorButton);
        objectCreateButton = findViewById(R.id.objectCreateButton);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        privileges = loginPreferences.getInt("privileges", 0);

        objectCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInformation();
            }
        });
    }

    protected void sendInformation(){
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

                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                task.execute("objectCreate.php", packJson().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "Failed to validate fields", duration);
            toast.show();
        }
    }

    protected JSONObject packJson(){
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", loginPreferences.getString("username", ""));
            postData.put("name", objectName.getText().toString());
            postData.put("address", objectAddress.getText().toString());
            postData.put("type", objectType.getText().toString());
            postData.put("locX", objectLocationX.getText().toString());
            postData.put("locY", objectLocationY.getText().toString());
            postData.put("contact", objectContact.getText().toString());
            postData.put("time", objectTimeOpened.getText().toString());
            postData.put("music", objectMusicPreference.getText().toString());
            postData.put("high", objectHighTables.getText().toString());
            postData.put("low", objectLowTables.getText().toString());
            postData.put("bar", objectBarChairs.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    protected boolean verifyFields()
    {
        boolean name = Validate.hasText(objectName,true);
        boolean address = Validate.hasText(objectAddress,true);
        boolean type = Validate.hasText(objectType,true);
        boolean locX = Validate.hasText(objectLocationX,true);
        boolean locY = Validate.hasText(objectLocationY,true);
        boolean contact = Validate.hasText(objectContact,true);
        boolean time = Validate.hasText(objectTimeOpened,true);
        boolean music = Validate.hasText(objectMusicPreference,true);
        boolean high = Validate.hasText(objectHighTables,true);
        boolean low = Validate.hasText(objectLowTables,true);
        boolean bar = Validate.hasText(objectBarChairs,true);

        return(name && address && type && locX && locY && contact && time && music && high && low && bar);
    }


}
