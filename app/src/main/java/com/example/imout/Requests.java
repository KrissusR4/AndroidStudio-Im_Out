package com.example.imout;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//import android.app.AlertDialog;


public class Requests extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate;
    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(String output);
    }


    public Requests(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override


    protected String doInBackground(String... params) {

        String data = "";
        final String conn = "https://imoutcodebullies.000webhostapp.com/PHP/";

        HttpURLConnection httpURLConnection = null;
        try {

            httpURLConnection = (HttpURLConnection) new URL(conn + params[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes("postData=" + params[1]);
            wr.flush();
            wr.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }


        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
        //super.onPostExecute(result);
        //alertDialog.setMessage(result);
        //alertDialog.show();

    }


    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
        //alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setTitle(" Log inned:");

    }
}