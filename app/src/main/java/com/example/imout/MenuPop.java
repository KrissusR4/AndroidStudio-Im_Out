package com.example.imout;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuPop extends Activity {

    public JSONObject receiveData;
    private int idObjekta;
    private Context context;

    private LinearLayout menuImages;
    ArrayList<String> listaMenu2 = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_pop);
        context = getApplicationContext();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.8));

        menuImages = findViewById(R.id.MenuListView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listaMenu2 = extras.getStringArrayList("ID");
        }


        LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i=0; i<listaMenu2.size(); i++)
        {
            View view = layoutInflater.inflate(R.layout.menu_images, menuImages, false);
            ImageView imageView = view.findViewById(R.id.MenuImage);
            Picasso.get().load(listaMenu2.get(i)).into(imageView);

            menuImages.addView(view);
        }

        //setDataFromServer1();

    }

    /*void setDataFromServer1(){
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
                            JSONArray images = receiveData.getJSONArray("images");
                            //objectImages = new String[images.length()];
                            List<String> listaObjects = new ArrayList<String>();
                            List<String> listaMenu = new ArrayList<String>();
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
                                    sajt.concat("/Menu/");
                                    sajt.concat(link);
                                    listaMenu.add(sajt);
                                }
                            }
                            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext()
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            for (int i=0; i<listaMenu.size(); i++)
                            {
                                View view = layoutInflater.inflate(R.layout.menu_images, menuImages, false);
                                ImageView imageView = view.findViewById(R.id.MenuImage);
                                Picasso.get().load(listaMenu.get(i)).into(imageView);

                                menuImages.addView(view);
                            }
                                for(int i=0; i<objectImages.length; i++)
                                {
                                    View view = layoutInflater.inflate(R.layout.object_images, objImages, false);
                                    ImageView imageView = view.findViewById(R.id.ObjectImageId);
                                    Picasso.get().load(objectImages[i]).into(imageView);

                                    objImages.addView(view);
                                }
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
    }*/

}

