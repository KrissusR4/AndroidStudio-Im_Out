package com.example.imout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;


import static java.text.DateFormat.getDateInstance;

public class ObjectActivity extends AppCompatActivity {

    private Context context;
    private ImageView logo;
    private TextView contact;
    private TextView grade;
    private TextView name;
    private TextView location;
    private TextView openTime;
    private TextView kind;
    private TextView music;
    private Button scheme;
    private Button menu;
    private Button events;
    private LinearLayout objImages;
    private Button approve;
    private Button decline;
    private Button visited;
    //String [] objectImages;
    ArrayList<String> listaMenu1 = new ArrayList<String>();
    private RatingBar ratings;
    private Button seeAllComments;
    private EditText enterComm;
    private Button comm;
    public JSONObject receiveData;
    private int idObjekta;
    private int privileges = 0;
    private String vlasnik;

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);
        context = getApplicationContext();

        logo = findViewById(R.id.Logo);
        name = findViewById(R.id.Name);
        contact = findViewById(R.id.Contact);
        grade = findViewById(R.id.Grade);
        location = findViewById(R.id.Location);
        openTime = findViewById(R.id.OpenTime);
        kind = findViewById(R.id.Kind);
        music = findViewById(R.id.Music);
        scheme = findViewById(R.id.Scheme);
        menu = findViewById(R.id.Menu);
        events = findViewById(R.id.Events);
        objImages = findViewById(R.id.ObjImages);
        ratings = findViewById(R.id.Ratings);
        seeAllComments = findViewById(R.id.SeeAllComm);
        enterComm = findViewById(R.id.EnterComm);
        comm = findViewById(R.id.Comm);
        decline = findViewById(R.id.declineButton);
        approve = findViewById(R.id.approveButton);
        visited = findViewById(R.id.buttonVisited);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        privileges = loginPreferences.getInt("privileges", 0);

        visited.setVisibility(View.VISIBLE);
        if(privileges == 3){
            decline.setVisibility(View.VISIBLE);
            approve.setVisibility(View.VISIBLE);
        }
        else{
            decline.setVisibility(View.GONE);
            approve.setVisibility(View.GONE);
        }
        if(privileges == 0)
            visited.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idObjekta = extras.getInt("ID");
        }

        setDataFromServer();


        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approve("approveObject");
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approve("deleteObject");
            }
        });

        //On Click Scheme
        scheme.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intentComments = new Intent(ObjectActivity.this,SchemeActivity.class);
                Bundle b = new Bundle();
                b.putInt("ID", idObjekta); //Your id za object
                intentComments.putExtras(b);
                startActivity(intentComments);
            }
        });

        //On Click Menu
        menu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentMenu = new Intent(ObjectActivity.this,MenuPop.class);
                Bundle b = new Bundle();
                ArrayList<String> strings = new ArrayList<String>();
                strings = listaMenu1 ;
                b.putStringArrayList("ID", strings); //Your id za object
                intentMenu.putExtras(b);
                startActivity(intentMenu);
            }
        });

        visited.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JSONObject postData = new JSONObject();
                Date c = Calendar.getInstance().getTime();
                DateFormat df = getDateInstance();
                String formattedDate = df.format(c);
                try {

                    postData.put("idObjekta", idObjekta);
                    postData.put("Datum", formattedDate);
                    postData.put("idKorisnika", loginPreferences.getString("username",""));

                    Requests task = new Requests(new Requests.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {

                            try {
                                receiveData = new JSONObject(output);
                                String message = receiveData.getString("message");
                                if (message.equals("Success")) {

                                    String s = "Visited";
                                    visited.setText(s);
                                    visited.setClickable(false);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    try {
                        task.execute("objectVisited.php", postData.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        //On Click Events
        events.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intentComments = new Intent(ObjectActivity.this,EventActivity.class);
                Bundle b = new Bundle();
                b.putInt("ID", idObjekta); //Your id za object
                intentComments.putExtras(b);
                startActivity(intentComments);
            }
        });

        //On Click SeeAllComments
        seeAllComments.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentComments = new Intent(ObjectActivity.this,CommentsPop.class);
                Bundle b = new Bundle();
                b.putInt("ID", idObjekta); //Your id za object
                intentComments.putExtras(b);
                startActivity(intentComments);
            }
        });

        //On Click Comm
        comm.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(privileges != 0)
                    posaljiKomentar();
                else
                    Toast.makeText(context, "You must log in first", Toast.LENGTH_SHORT).show();
            }
        });
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
                                Picasso.get().load("https://imoutcodebullies.000webhostapp.com/Images/Logo/".concat(receiveData.getString("Logo")))
                                        .into(logo);
                                name.setText(receiveData.getString("Ime"));
                                contact.setText(receiveData.getString("Kontakt"));
                                int sumaOcena = receiveData.getInt("SumaOcena");
                                int brojRecenzija = receiveData.getInt("BrojRecenzija");

                                if(privileges == 3 && receiveData.getString("Status").equals("Pending")){
                                    decline.setVisibility(View.VISIBLE);
                                    approve.setVisibility(View.VISIBLE);
                                }
                                else{
                                    decline.setVisibility(View.GONE);
                                    approve.setVisibility(View.GONE);
                                }
                                float gradee = 0;
                                if(brojRecenzija != 0)
                                    gradee = (float)sumaOcena / brojRecenzija;
                                String gradeee = String.valueOf(gradee);
                                grade.setText(gradeee);
                                location.setText(receiveData.getString("Lokacija"));
                                openTime.setText(receiveData.getString("RadnoVreme"));
                                kind.setText(receiveData.getString("Vrsta"));
                                music.setText(receiveData.getString("Muzika"));
                                vlasnik = receiveData.getString("idVlasnikaObj");

                                JSONArray images = receiveData.getJSONArray("images");
                                //objectImages = new String[images.length()];
                                List<String> listaObjects = new ArrayList<String>();
                                ArrayList<String> listaMenu = new ArrayList<String>();
                                for(int i = 0; i < images.length(); i++){
                                    JSONObject slika = images.getJSONObject(i);

                                    String sajt = "https://imoutcodebullies.000webhostapp.com/Images";
                                    String link = slika.getString("Link");
                                    String type = slika.getString("Tip");
                                    //Imageview pravis
                                    if(type.equals("Object")){
                                        sajt = sajt.concat("/Object/");
                                        sajt = sajt.concat(link);
                                        //objectImages[i] = sajt;
                                        listaObjects.add(sajt);
                                    }
                                    else if(type.equals("Menu"))
                                    {
                                        sajt = sajt.concat("/Menu/");
                                        sajt = sajt.concat(link);
                                        listaMenu.add(sajt);
                                        listaMenu1 = listaMenu;
                                    }
                                }
                                LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext()
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                for (int i=0; i<listaObjects.size(); i++)
                                {
                                    View view = layoutInflater.inflate(R.layout.object_images, objImages, false);
                                    ImageView imageView = view.findViewById(R.id.ObjectImageId);
                                    Picasso.get().load(listaObjects.get(i)).into(imageView);

                                    objImages.addView(view);
                                }
                                /*for(int i=0; i<objectImages.length; i++)
                                {
                                    View view = layoutInflater.inflate(R.layout.object_images, objImages, false);
                                    ImageView imageView = view.findViewById(R.id.ObjectImageId);
                                    Picasso.get().load(objectImages[i]).into(imageView);

                                    objImages.addView(view);
                                }*/
                                //popuniSlike();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                try {
                    task.execute("objectData.php", postData.toString());
                }
                catch (Exception e){
                    e.printStackTrace();
                }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /*void popuniSlike(){
        ObjectActivity.CustomAdapter customAdapter = new ObjectActivity.CustomAdapter(this, objectImages);
        objImages.setAdapter(customAdapter);
    }
    class CustomAdapter extends ArrayAdapter<String> {
        Context context;
        String images[];

        CustomAdapter(Context c, String i[]){
            super(c, R.layout.object_images, i);
            this.context = c;
            this.images = i;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.object_images, parent, false);
            ImageView imageView = row.findViewById(R.id.ObjectImageId);

            Picasso.get().load(images[position]).into(imageView);

            return row;

        }
    }*/

    protected void posaljiKomentar()
    {
        JSONObject postData = new JSONObject();
        try
        {
            postData.put("username", loginPreferences.getString("username", ""));
            postData.put("id", idObjekta);
            postData.put("rating", ratings.getRating());
            postData.put("comment", enterComm.getText().toString());
            postData.put("password", loginPreferences.getString("password", ""));
            Requests task = new Requests(new Requests.AsyncResponse(){
                @Override
                public void processFinish(String output){ //Sta da se uradi sa povratnim podacima
                    try {
                        JSONObject receiveData = new JSONObject(output);
                        String message = receiveData.getString("message");
                        if(message.equals("Success")){
                            finish();
                            startActivity(getIntent());
                        }
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, message, duration);
                        toast.show();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            try
            {
                    task.execute("postComment.php", postData.toString());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    protected void approve(String s){
        JSONObject postData = new JSONObject();
        try
        {
            postData.put("username", loginPreferences.getString("username", ""));
            postData.put("ownerUsername", vlasnik);
            postData.put("password", loginPreferences.getString("password", ""));
            postData.put("id", idObjekta);
            postData.put("action", s);

            Requests task = new Requests(new Requests.AsyncResponse(){
                @Override
                public void processFinish(String output){ //Sta da se uradi sa povratnim podacima
                    try {
                        JSONObject receiveData = new JSONObject(output);
                        String message = receiveData.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        if(message.equals("Success")){
                            finish();
                            startActivity(getIntent());
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            try
            {
                task.execute("objectManage.php", postData.toString());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

