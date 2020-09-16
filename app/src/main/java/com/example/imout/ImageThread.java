package com.example.imout;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ImageThread extends Thread {

    private Bitmap image;
    private String res;
    private String encodedImage;
    private Context context;
    private String username;
    private String password;

    public ImageThread(Context context,Bitmap slika, String username, String password)
    {
        this.context = context;
        this.username = username;
        this.password = password;
        image = slika;
    }

    public String getResult(){
        return res;
    }
    /*public String getImage(){
        return encodedImage;
    }*/
    @Override

    public void run() {

        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
        encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        //Log.d("Unutra threda",encodedImage);

        try {
            URL url = new URL("https://imoutcodebullies.000webhostapp.com/PHP/images.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            String dataToSend= URLEncoder
                    .encode("image","UTF-8")+"="+URLEncoder
                    .encode(encodedImage,"UTF-8")+ "&" +URLEncoder
                    .encode("username","UTF-8")+ "=" +URLEncoder
                    .encode(username,"UTF-8")+ "&" +URLEncoder
                    .encode("password", "UTF-8")+ "=" + URLEncoder
                    .encode(password, "UTF-8");

            bufferedWriter.write(dataToSend);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result = "";
            String line;
            while( (line = bufferedReader.readLine()) != null){
                result += line;
            }
            res = result;
            bufferedReader.close();
            inputStream.close();

            httpURLConnection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}