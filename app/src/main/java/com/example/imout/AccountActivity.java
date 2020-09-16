package com.example.imout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class AccountActivity extends AppCompatActivity {

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private ImageView avatar;
    private static final int RESULT_LOAD_IMAGE = 1;//Za gallery
    private Bitmap picture;
    private Context context;

    private Button visited;
    private Button friends;
    private Button ratings;
    private Button editButton;
    private Button addFriend;
    private Button searchButton;
    private TextView name;
    private TextView surname;
    private TextView city;
    private TextView phoneNumber;
    private TextView birthday;
    private TextView username;
    private TextView email;
    private TextView search;
    private int activity = 4;
    private int state = 1;
    private boolean myprofile = true;
    private String showuser;
    private String statusRequest;
    private int rating;
    private int idToSend;


    private ListView listView;
    Korisnik[] users;
    Korisnik[] searchPpl;
    Rating[] ratingsArray;
    Objects[] objects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        context = getApplicationContext();
        avatar = findViewById(R.id.imageViewAvatar);
        name = findViewById(R.id.Name);
        surname = findViewById(R.id.Surname);
        city = findViewById(R.id.City);
        phoneNumber = findViewById(R.id.PhoneNumber);
        birthday = findViewById(R.id.Dateofbirth);
        username = findViewById(R.id.Username);
        email = findViewById(R.id.Email);
        editButton = findViewById(R.id.EditButton);
        listView = findViewById(R.id.ListViewAcc);
        visited = findViewById(R.id.buttonVisited);
        ratings = findViewById(R.id.buttonRatings);
        friends = findViewById(R.id.buttonFriends);
        addFriend = findViewById(R.id.buttonAddFriend);
        search = findViewById(R.id.editTextAddFriend);
        searchButton = findViewById(R.id.searchButton);


        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myprofile = extras.getBoolean("MyProfile");
            showuser = extras.getString("Username");
        }
        if(myprofile)
            state = 1;
        else
            state = 2;

        String pictureLink = loginPreferences.getString("picture", "");
        if(!pictureLink.equals("") && myprofile){
            Picasso.get().load(pictureLink)
                    //.resize(100,130)
                    .into(avatar);
        }
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button
                        obrisiKomentar(idToSend, rating);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button
                        break;
                }
            }
        };
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(state == 1) {
                    Intent intentObject = new Intent(AccountActivity.this , AccountActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean("MyProfile", false); //Your id za object
                    b.putString("Username", users[position].getUsername());
                    intentObject.putExtras(b);
                    startActivityForResult(intentObject, 3);
                }
                if(state == 2){
                    Intent intentObject = new Intent(AccountActivity.this , ObjectActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("ID", objects[position].getId()); //Your id za object
                    intentObject.putExtras(b);
                    startActivity(intentObject);
                }
                if(state == 3 &&  myprofile){
                    idToSend = ratingsArray[position].getIdLokalaRec();
                    rating = ratingsArray[position].getZvezdice();
                    AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                    builder.setMessage("Are you sure you want to delete your rating for this object?")
                            .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener)
                            .show();
                }
                if(state == 4){
                    Intent intentObject = new Intent(AccountActivity.this , AccountActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean("MyProfile", false); //Your id za object
                    b.putString("Username", searchPpl[position].getUsername());
                    intentObject.putExtras(b);
                    startActivityForResult(intentObject, 3);
                }


            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
                state = 4;
                setDataFromServer(false, search.getText().toString());
            }
        });
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 1;
                popuniPrijatelje(users);
            }
        });
        visited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 2;
                popuniIstoriju();
            }
        });
        ratings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 3;
                popuniRejting();
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myprofile){
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                }
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() { //Odlazi na edit activity
            @Override
            public void onClick(View v) {
                Intent intentObject = new Intent(AccountActivity.this , RegisterActivity.class);
                Bundle b = new Bundle();
                b.putBoolean("edit", true);
                intentObject.putExtras(b);
                startActivityForResult(intentObject, activity);
            }
        });


        setDataFromServer(true, "");
        if(myprofile)
            popuniPolja();


    }
    protected void sendPicture(){ //Slika upload
        final ImageThread ithread = new ImageThread(context, picture, loginPreferences.getString("username", ""), loginPreferences.getString("password", ""));
        ithread.start();
        try {
            Thread.sleep(1500);
            ithread.join();
            String link = ithread.getResult();
            if(link.equals("Error")){
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Error uploading picture", duration);
                toast.show();
                //loginPrefsEditor.putString("picture", "Not Found");
            }
            else{
                loginPrefsEditor.putString("picture", link);
                loginPrefsEditor.apply();
                Picasso.get().load(link)
                        //.resize(100,130)
                        .into(avatar);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //data.putExtra("image", ithread.getImage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) //Posle edita i izbora slike
    {
        if(requestCode == 3){
            finish();
            startActivity(getIntent());
        }
        if (requestCode == activity) {
                popuniPolja();
        }
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE  && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImageUri = data.getData();
            avatar.setImageURI(selectedImageUri);
            try {
                picture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                sendPicture();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    void popuniPolja(){ //Popunjavanje polja iz shared preferencesa
        email.setVisibility(View.VISIBLE);
        phoneNumber.setVisibility(View.VISIBLE);
        friends.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
        ratings.setVisibility(View.VISIBLE);
        visited.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        addFriend.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);

        name.setText("Name: " + loginPreferences.getString("ime", ""));
        surname.setText("Last name: " + loginPreferences.getString("prezime", ""));
        city.setText("City: " + loginPreferences.getString("mesto", ""));
        phoneNumber.setText("Phone Number: " + loginPreferences.getString("brojTelefona", ""));
        birthday.setText("Birthday: " + loginPreferences.getString("datumRodjenja", ""));
        username.setText("Username: " + loginPreferences.getString("username", ""));
        email.setText("Email: " + loginPreferences.getString("email", ""));
    }
    void popuniTudjiProfil(String n, String s, String c, String b, String u, String slika, final String status, boolean sender){
        name.setText("Name: " + n);
        surname.setText("Last name: " + s);
        city.setText("City: " + c);
        birthday.setText("Birthday: " + b);
        username.setText("Username: " + u);
        Picasso.get().load("https://imoutcodebullies.000webhostapp.com/Images/User/" + slika).into(avatar);

        email.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        phoneNumber.setVisibility(View.GONE);
        friends.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        addFriend.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.GONE);
        statusRequest = status;
        if(status.equals("Not Friends"))
            addFriend.setText("Add to friends");
        else if(status.equals("Complete"))
            addFriend.setText("Delete from friends");
        else if(sender){
            statusRequest = "Cancel request";
            addFriend.setText(statusRequest);
        }
        else{
            statusRequest = "Accept request";
            addFriend.setText(statusRequest);
        }

        if(!status.equals("Complete")){
            ratings.setVisibility(View.GONE);
            visited.setVisibility(View.GONE);
        }
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject postData = new JSONObject();
                try {

                    postData.put("password", loginPreferences.getString("password", ""));
                    postData.put("myusername", loginPreferences.getString("username", ""));
                    postData.put("username", showuser);
                    postData.put("status", statusRequest);

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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    try {
                        task.execute("requests.php", postData.toString());
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
    }
    void setDataFromServer(final Boolean u, String user){ //Request za podatke
        JSONObject postData = new JSONObject();
        try {
            if(!u)
                postData.put("username", user);
            if(!myprofile)
                postData.put("username", showuser);

            postData.put("myusername", loginPreferences.getString("username", ""));
            postData.put("password", loginPreferences.getString("password", ""));
            postData.put("ispitaj", u);
            Requests task = new Requests(new Requests.AsyncResponse(){
                @Override
                public void processFinish(String output){ //Sta da se uradi sa povratnim podacima
                    try {
                        JSONObject receiveData = new JSONObject(output);
                        String message = receiveData.getString("message");
                        if(message.equals("Success")){
                            if(!u){
                                JSONArray people = receiveData.getJSONArray("people");
                                searchPpl = new Korisnik[people.length()];
                                for(int i = 0; i < people.length(); i++) {
                                    JSONObject ppl = people.getJSONObject(i);
                                    Korisnik korisnik = new Korisnik(ppl.getString("Username"), ppl.getString("Ime")
                                            , ppl.getString("Prezime"), ppl.getString("Slika"));
                                    searchPpl[i] = korisnik;
                                    popuniPrijatelje(searchPpl);
                                }
                            }
                            else{
                                JSONArray friendsArray = null;
                                JSONArray objectsArray = null;
                                JSONArray ratingArray = null;
                                String status = null;
                                boolean sender = false;
                                if(myprofile){
                                    friendsArray = receiveData.getJSONArray("friends");
                                    users = new Korisnik[friendsArray.length()];
                                }
                                else{
                                    status = receiveData.getString("Status");
                                }
                                if(!myprofile && !status.equals("Not Friends"))
                                    sender = receiveData.getBoolean("Sender");


                                if(myprofile || status.equals("Complete")){
                                    objectsArray = receiveData.getJSONArray("istorija");
                                    ratingArray = receiveData.getJSONArray("rating");
                                    objects = new Objects[objectsArray.length()];
                                    ratingsArray = new Rating[ratingArray.length()];
                                }



                                if(!myprofile)
                                    popuniTudjiProfil(receiveData.getString("Ime"),receiveData.getString("Prezime")
                                            ,receiveData.getString("Mesto"),receiveData.getString("DatumRodjenja")
                                            ,receiveData.getString("Username"),receiveData.getString("Slika")
                                            ,status, sender);

                                if(myprofile)
                                    for(int i = 0; i < friendsArray.length(); i++) {
                                        JSONObject prijatelj = friendsArray.getJSONObject(i);
                                        Korisnik korisnik = new Korisnik(prijatelj.getString("Username"), prijatelj.getString("Ime")
                                                , prijatelj.getString("Prezime"), prijatelj.getString("Slika"));
                                        users[i] = korisnik;
                                        if(state == 1)
                                            popuniPrijatelje(users);
                                    }
                                if(myprofile || status.equals("Complete"))
                                for(int i = 0; i < objectsArray.length(); i++) {
                                    JSONObject object = objectsArray.getJSONObject(i);
                                    Objects objekat = new Objects(object.getInt("idObjekta"), object.getString("Datum")
                                            , object.getString("Logo"),object.getString("Ime"));
                                    objects[i] = objekat;
                                    if(state == 2)
                                        popuniIstoriju();
                                }
                                if(myprofile || status.equals("Complete"))
                                for(int i = 0; i < ratingArray.length(); i++) {
                                    JSONObject ob = ratingArray.getJSONObject(i);
                                    Rating r = new Rating(ob.getInt("Zvezdice"), ob.getString("Komentar")
                                            ,ob.getString("Logo"),ob.getString("Ime"), ob.getInt("idLokalaRec"));
                                    ratingsArray[i] = r;
                                    if(state == 3)
                                        popuniRejting();
                                }
                        }}
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                if(!u)
                    task.execute("searchUsers.php", postData.toString());
                else
                if(myprofile)
                    task.execute("profile.php", postData.toString());
                else
                    task.execute("returnProfile.php", postData.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    void popuniPrijatelje(Korisnik k[]){
        KorisnikAdapter korisnikAdapter = new KorisnikAdapter(this, k, R.layout.layout_friend);
        listView.setAdapter(korisnikAdapter);
    }
    void popuniIstoriju(){
        IstorijaAdapter istorijaAdapter = new IstorijaAdapter(this, objects, R.layout.layout_history);
        listView.setAdapter(istorijaAdapter);
    }
    void popuniRejting(){
        RecenzijaAdapter recenzijaAdapter = new RecenzijaAdapter(this, ratingsArray, R.layout.layout_rating);
        listView.setAdapter(recenzijaAdapter);
    }
    protected void obrisiKomentar(int id, int rating)
    {
        JSONObject postData = new JSONObject();
        try
        {
            postData.put("username", loginPreferences.getString("username", ""));
            postData.put("idLokalaRec", id);
            postData.put("rating", rating);
            Requests task = new Requests(new Requests.AsyncResponse(){
                @Override
                public void processFinish(String output){ //Sta da se uradi sa povratnim podacima
                    try {
                        JSONObject receiveData = new JSONObject(output);
                        String message = receiveData.getString("message");
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, message, duration);
                        toast.show();
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
                task.execute("deleteComment.php", postData.toString());
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
