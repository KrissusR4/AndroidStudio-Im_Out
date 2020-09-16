package com.example.imout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class EventActivity extends AppCompatActivity {

    private Context context;
    public JSONObject receiveData;
    private int idObjekta;
    private int idDogadjaja;
    private ScrollView scrollView;
    private ListView eventList;
    Dogadjaj [] dogadjaji;

    private int privileges = 0;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    private Button newEventButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        context = getApplicationContext();
        eventList = findViewById(R.id.listaDogadjaja);
        newEventButton = findViewById(R.id.newEventButton);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        privileges = loginPreferences.getInt("privileges", 0);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idObjekta = extras.getInt("ID");
        }

        if(privileges == 2 || privileges == 3){
            newEventButton.setVisibility(View.VISIBLE);
        }
        else{
            newEventButton.setVisibility(View.GONE);
        }

        setDataFromServer();

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentComments = new Intent(EventActivity.this,ReservationActivity.class);
                Bundle b = new Bundle();
                b.putInt("ID", idObjekta); //Your id za object
                b.putInt("IDD", dogadjaji[position].getIdDog());
                intentComments.putExtras(b);
                startActivity(intentComments);
            }
        });

        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentComments = new Intent(EventActivity.this,CreateEvent.class);
                Bundle b = new Bundle();
                b.putInt("ID", idObjekta); //Your id za object
                intentComments.putExtras(b);
                startActivityForResult(intentComments, 1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) //Posle edita i izbora slike
    {
        if(requestCode == 1){
            finish();
            startActivity(getIntent());
        }
    }
    void setDataFromServer(){
        JSONObject postData = new JSONObject();
        try {
            postData.put("id", idObjekta);
            Requests task = new Requests(new Requests.AsyncResponse(){
                @Override
                public void processFinish(String output){ //Sta da se uradi sa povratnim podacima
                    try {
                        receiveData = new JSONObject(output);
                        String message = receiveData.getString("message");
                        if(message.equals("Success")){
                            JSONArray eventsArray = receiveData.getJSONArray("events");
                            dogadjaji = new Dogadjaj[eventsArray.length()];
                            for(int i=0; i<eventsArray.length(); i++)
                            {
                                JSONObject event = eventsArray.getJSONObject(i);
                                Dogadjaj dogadjaj = new Dogadjaj(event.getString("ImeDogadjaja"), event.getString("Vreme")
                                        , event.getString("Datum"), event.getString("Bend"),event.getString("Cena"),event.getInt("idDogadjaja"));
                                dogadjaji[i] = dogadjaj;
                            }
                            popuniDogadjaje();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                task.execute("events.php", postData.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    void popuniDogadjaje(){
        DogadjajAdapter dogadjajAdapter = new DogadjajAdapter(this, dogadjaji, R.layout.layout_event);
        eventList.setAdapter(dogadjajAdapter);
    }
}
