package com.example.imout;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReservationActivity extends AppCompatActivity {

    private Context context;
    public JSONObject receiveData;
    private int idObjekta;
    private int idDogadjaja;
    private TextView allTables;
    private TextView allLowTables;
    private TextView allHighTables;
    private TextView allBarChairs;
    private TextView availableTables;
    private TextView availableLowTables;
    private TextView availableHighTables;
    private TextView availableBarChairs;
    private EditText messageToOwner;
    private Button reserveButton;

    private int privileges = 0;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        context = getApplicationContext();

        allTables = findViewById(R.id.AllTables);
        allLowTables = findViewById(R.id.LowTables);
        allHighTables = findViewById(R.id.HighTables);
        allBarChairs = findViewById(R.id.BarChairs);
        availableTables = findViewById(R.id.AvailableTables);
        availableLowTables = findViewById(R.id.AvailableLowTables);
        availableHighTables = findViewById(R.id.AvailableHighTables);
        availableBarChairs = findViewById(R.id.AvailableBarChairs);
        messageToOwner = findViewById(R.id.PorukaVlasniku);
        reserveButton = findViewById(R.id.ReserveButton);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        privileges = loginPreferences.getInt("privileges", 0);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idObjekta = extras.getInt("ID");
            idDogadjaja = extras.getInt("IDD");
        }

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("username",loginPreferences.getString("username",""));
                    postData.put("tip", messageToOwner.getText().toString());
                    postData.put("idObjekta", idObjekta);
                    postData.put("idDogadjaja",idDogadjaja);
                    Requests task = new Requests(new Requests.AsyncResponse(){
                        @Override
                        public void processFinish(String output){ //Sta da se uradi sa povratnim podacima
                            try {
                                receiveData = new JSONObject(output);
                                String message = receiveData.getString("message");
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, message, duration);
                                toast.show();
                                if(message.equals("Success"))
                                    finish();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    try {
                        task.execute("reservation.php", postData.toString());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        setDataFromServer();
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
                            JSONArray reservationArray = receiveData.getJSONArray("reservation");
                            //allTables.setText(reservationArray.length());
                            int AllT = 0;
                            int AllHT = 0;
                            int AllLT = 0;
                            int AllBC = 0;
                            int AvaT = 0;
                            int AvaHT = 0;
                            int AvaLT = 0;
                            int AvaBC = 0;
                            for(int i=0; i<reservationArray.length(); i++)
                            {
                                JSONObject sto = reservationArray.getJSONObject(i);
                                AllT++;
                                if(sto.getString("Tip").equals("High"))
                                    AllHT++;
                                else if (sto.getString("Tip").equals("Low"))
                                    AllLT++;
                                else if (sto.getString("Tip").equals("Bar"))
                                    AllBC++;

                                if (sto.getString("StatusRezervacije").equals("No"))
                                    AvaT++;
                                if (sto.getString("Tip").equals("High") && sto.getString("StatusRezervacije").equals("No"))
                                    AvaHT++;
                                else if (sto.getString("Tip").equals("Low") && sto.getString("StatusRezervacije").equals("No"))
                                    AvaLT++;
                                else if (sto.getString("Tip").equals("Bar") && sto.getString("StatusRezervacije").equals("No"))
                                    AvaBC++;
                            }
                            allTables.setText(String.valueOf(AllT));
                            allLowTables.setText(String.valueOf(AllLT));
                            allHighTables.setText(String.valueOf(AllHT));
                            allBarChairs.setText(String.valueOf(AllBC));
                            availableTables.setText(String.valueOf(AvaT));
                            availableLowTables.setText(String.valueOf(AvaLT));
                            availableHighTables.setText(String.valueOf(AvaHT));
                            availableBarChairs.setText(String.valueOf(AvaBC));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                task.execute("reservationInformation.php", postData.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
