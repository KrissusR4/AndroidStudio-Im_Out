package com.example.imout;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CommentsPop extends Activity {

    public JSONObject receiveData;
    private int idObjekta;
    private ListView komentariListView;
    Komentar[] komentari;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_pop);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.8));

        komentariListView = findViewById(R.id.CommentsListView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idObjekta = extras.getInt("ID");
        }

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

                            JSONArray commentsArray = receiveData.getJSONArray("comments");
                            komentari = new Komentar[commentsArray.length()];
                            for(int i=0; i<commentsArray.length(); i++)
                            {
                                JSONObject koment = commentsArray.getJSONObject(i);
                                Komentar komentar = new Komentar(koment.getString("idKorisnikaRec"), koment.getInt("Zvezdice")
                                        , koment.getString("Komentar"));
                                komentari[i] = komentar;
                            }

                            popuniKomentare();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                task.execute("commentsPop.php", postData.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    void popuniKomentare(){
        KomentarAdapter komentarAdapter = new KomentarAdapter(this, komentari, R.layout.layout_comments_pop);
        komentariListView.setAdapter(komentarAdapter);
    }
}
