package com.example.imout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

   //Neophodno za GPS
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import android.location.LocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StartingActivity extends AppCompatActivity {

    private ImageButton mapImageButton;
    private ImageButton userAccountImageButton;
    private Button logOutButton;
    private Button notifButton;
    Notification[] notifications;
    Notification[] manageNotifications;
    private int length;
    private int privileges;
    private Button createObjectButton;
    private Context context;
    private Button removeObjectButton;
    private int idd;
    private ListView lw;

    private boolean mLocationPermissionGranted = false;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        context = getApplicationContext();

        mapImageButton = findViewById(R.id.mapImageButton);
        userAccountImageButton = findViewById(R.id.userAccountImageButton);
        logOutButton = findViewById(R.id.logOutButton);
        notifButton = findViewById(R.id.buttonNotifications);
        createObjectButton = findViewById(R.id.createObject);
        removeObjectButton = findViewById(R.id.removeObject);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        privileges = loginPreferences.getInt("privileges", 0);
        notifButton.setText("Notifications");
        if(privileges == 2 || privileges == 3){
            createObjectButton.setVisibility(View.VISIBLE);
        }
        else{
            createObjectButton.setVisibility(View.GONE);
        }

        mapImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(StartingActivity.this, MapActivity.class);
                startActivity(intent2);
            }
        });
        notifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (privileges != 0) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(StartingActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.layout_notifications, null);
                    mBuilder.setTitle("Notifications");

                    final ListView listView = mView.findViewById(R.id.listViewNotifications);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Proveri tip notifikacije
                            if (notifications[position].getType().equals("Korisnik")) {
                                Intent intentObject = new Intent(StartingActivity.this, AccountActivity.class);
                                Bundle b = new Bundle();
                                b.putBoolean("MyProfile", false);
                                b.putString("Username", notifications[position].getText1());
                                intentObject.putExtras(b);
                                startActivityForResult(intentObject, 3);
                            } else if (notifications[position].getType().equals("Objekat")) {
                                Intent intentObject = new Intent(StartingActivity.this, ObjectActivity.class);
                                Bundle b = new Bundle();
                                b.putInt("ID", notifications[position].getId());
                                intentObject.putExtras(b);
                                startActivityForResult(intentObject, 5);
                            }
                        }
                    });

                    notifications();
                    setNotifications(listView);

                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                } else {
                    Toast.makeText(context, "You must log in first", Toast.LENGTH_SHORT).show();
                }
            }
        });


        userAccountImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (privileges != 0) {
                    Intent intentObject = new Intent(StartingActivity.this, AccountActivity.class);
                    startActivityForResult(intentObject, 4);
                } else {
                    Toast.makeText(context, "You must log in first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPrefsEditor.clear().commit();
                Intent intentLogOut = new Intent(StartingActivity.this, MainActivity.class);
                startActivity(intentLogOut);
                finish();
            }
        });

        createObjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentComments = new Intent(StartingActivity.this, CreateObjectActivity.class);
                Bundle b = new Bundle();
                b.putBoolean("MyProfile", true); //Your id za object
                intentComments.putExtras(b);
                startActivity(intentComments);
            }
        });


        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button
                        manageObjects("deleteObject", idd, lw);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button
                        break;
                }
            }
        };

        removeObjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                manageObjects("myObjects",0);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(StartingActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.layout_notifications, null);
                mBuilder.setTitle("Select object to remove:");

//                ListView listView = mView.findViewById(R.id.listViewNotifications);

                lw = mView.findViewById(R.id.listViewNotifications);
                manageObjects("myObjects",0, lw);

                lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        idd = manageNotifications[position].getId();

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(StartingActivity.this);
                        builder2.setMessage("Are you sure you want to delete this object?")
                                .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener)
                                .show();


                    }
                });

//                setManageObjectNotifications(listView);

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
          }
        });



        if(privileges != 0)
            notifications();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(checkMapServices())
        {
            if(!mLocationPermissionGranted)
                getLocationPermission();

        }
    }


    private boolean checkMapServices()
    {
        if(isServicesOK()) {                            //Google service available?
            if (isMapsEnabled()) {                      //Google maps available?
                return true;
            }
        }
        return false;
    }


    //    is Google services installed
    public boolean isServicesOK()
    {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(StartingActivity.this);

        if (available == ConnectionResult.SUCCESS)
        {
            Log.d(TAG,"isServicesOK: Google Play is ok");
            return true;
        }
        else {
            Toast.makeText(this, "you cant make app req", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private void buildAlertMessageNoGps()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("App requires GPS. Do You want to enable it?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,@SuppressWarnings("unused") final int which)
                    {
                        Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);   //GPS enabled or not
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d(TAG, "onActivityResult: Called");
        switch(requestCode)
        {
            case(PERMISSIONS_REQUEST_ENABLE_GPS):{
                if(!mLocationPermissionGranted)
                {
                    getLocationPermission();
                }
                break;
            }
            case(3): //Notifikacije korisnik
                finish();
                startActivity(getIntent());
                break;
            case(4): //Profil
                notifications();
            case(5): //Objekat
                finish();
                startActivity(getIntent());
                break;
        }
    }



    private void getLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;

        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);                      //ask to use location permission
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int grantResults[])
    {
        mLocationPermissionGranted = false;
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }



    public boolean isMapsEnabled()
    {
        final LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if( !manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    protected void setManageObjectNotifications(ListView l){
        NotificationAdapter notificationAdapter = new NotificationAdapter(this, manageNotifications, R.layout.layout_friend);
        l.setAdapter(notificationAdapter);
    }

    protected void setNotifications(ListView l){
        NotificationAdapter notificationAdapter = new NotificationAdapter(this, notifications, R.layout.layout_friend);
        l.setAdapter(notificationAdapter);
    }

    protected void notifications(){
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", loginPreferences.getString("username", ""));
            postData.put("password", loginPreferences.getString("password", ""));
            Requests task = new Requests(new Requests.AsyncResponse(){
                @Override
                public void processFinish(String output){ //Sta da se uradi sa povratnim podacima
                    try {
                        JSONObject receiveData = new JSONObject(output);
                        String message = receiveData.getString("message");
                        if(message.equals("Success")){
                            int privileges = loginPreferences.getInt("privileges", 0);
                            JSONArray people = receiveData.getJSONArray("friendRequests");
                            JSONArray objects = null;
                            length = people.length();
                            int objlen = 0;
                            if(privileges == 3){
                                objects = receiveData.getJSONArray("objectRequests");
                                length += objects.length();
                                objlen = objects.length();
                            }
                            notifications = new Notification[length];
                            int i;
                            for(i = 0; i < people.length(); i++) {
                                JSONObject ppl = people.getJSONObject(i);
                                Notification notification = new Notification(ppl.getString("Username")
                                        , ppl.getString("Ime").concat(" ").concat(ppl.getString("Prezime"))
                                        , ppl.getString("Slika"), "Korisnik", 0);
                                notifications[i] = notification;
                            }
                            for(int j = 0; j < objlen; j++) {
                                JSONObject obj = objects.getJSONObject(j);
                                Notification notification = new Notification(obj.getString("Ime")
                                        , obj.getString("Lokacija"), obj.getString("Logo")
                                        , "Objekat", obj.getInt("idObjekta"));
                                notifications[i] = notification;
                                i++;
                            }
                            //final ListView listView = findViewById(R.id.listViewNotifications);
                            //setFriends(listView);
                            notifButton.setText("Notifications: ".concat(String.valueOf(length)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                task.execute("notifications.php", postData.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    private void manageObjects(String s, int id, final ListView l) {
        final JSONObject postData = new JSONObject();
        try {
            postData.put("username", loginPreferences.getString("username", ""));
            postData.put("password", loginPreferences.getString("password", ""));
            postData.put("ownerUsername", loginPreferences.getString("username", ""));
            if (s.equals("deleteObject"))
                postData.put("id", id);
            else postData.put("id", 0);
            postData.put("action", s);

            Requests task = new Requests(new Requests.AsyncResponse() {
                @Override
                public void processFinish(String output) { //Sta da se uradi sa povratnim podacima
                    try {
                        JSONObject receiveData = new JSONObject(output);
                        String message = receiveData.getString("message");
                        if (message.equals("Success")) {
                            JSONArray objects = null;
                            int objlen = 0;
                            objects = receiveData.getJSONArray("objects");
                            objlen = objects.length();

                            manageNotifications = new Notification[objlen];
                            for (int j = 0; j < objlen; j++) {
                                JSONObject obj = objects.getJSONObject(j);
                                Notification notification = new Notification(obj.getString("Ime")
                                        , obj.getString("Lokacija"), obj.getString("Logo")
                                        , "Objekat", obj.getInt("idObjekta"));
                                manageNotifications[j] = notification;
                            }
                            setManageObjectNotifications(l);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                task.execute("objectManage.php", postData.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
