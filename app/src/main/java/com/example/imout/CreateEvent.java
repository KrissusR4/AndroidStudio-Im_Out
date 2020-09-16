package com.example.imout;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateEvent extends AppCompatActivity {

    private Context context;
    public JSONObject receiveData;
    private int idObjekta;

    private int privileges = 0;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    private EditText eventName;
    private EditText eventTime;
    private EditText eventDate;
    private EditText eventBand;
    private EditText eventPrice;
    private Button newEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        context = getApplicationContext();

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        privileges = loginPreferences.getInt("privileges", 0);

        eventName = findViewById(R.id.eventName);
        eventTime = findViewById(R.id.eventTime);
        eventDate = findViewById(R.id.eventDate);
        eventBand = findViewById(R.id.eventBand);
        eventPrice = findViewById(R.id.eventPrice);
        newEventButton = findViewById(R.id.eventCreateButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idObjekta = extras.getInt("ID");
        }

        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewEventF();
            }
        });

    }

    protected void createNewEventF(){
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
                task.execute("eventCreate.php", packJson().toString());
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
            postData.put("id", idObjekta);
            postData.put("name", eventName.getText().toString());
            postData.put("time", eventTime.getText().toString());
            postData.put("date", eventDate.getText().toString());
            postData.put("band", eventBand.getText().toString());
            postData.put("price", eventPrice.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    protected boolean verifyFields()
    {
        boolean name = Validate.hasText(eventName,true);
        boolean time = Validate.hasText(eventTime,true);
        boolean date = Validate.hasText(eventDate,true);
        boolean band = Validate.hasText(eventBand,true);
        boolean price = Validate.hasText(eventPrice,true);

        return(name && time && date && band && price);
    }
}
