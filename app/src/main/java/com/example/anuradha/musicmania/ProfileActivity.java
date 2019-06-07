package com.example.anuradha.musicmania;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    String an3, wikiurl,alljson, age, imgurl, desc;
    TextView an3View, ageView, descView;
    ImageView profpic;
    JSONObject bigting, pting, obj1, obj2, obj3;
    JSONArray jarray, jarray2, jarray3;


    private static final String TAG = "searchApp";
    static String result3 = null;
    Integer responseCode = null;
    String responseMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        alljson = getIntent().getStringExtra("json");
        Log.d("ALLJSON", alljson);

        age = "";
        descView = findViewById(R.id.id_desc);
        an3 = getIntent().getStringExtra("an3");
        an3View = findViewById(R.id.id_an3);
        an3View.setText(an3);

        profpic = findViewById(R.id.id_profpic);
        ageView = findViewById(R.id.id_age);

        try {

            final String searchString = an3;

            // looking for
            String searchStringNoSpaces = searchString.replace(" ", "+");

            // Your API key
            // TODO replace with your value
            String key="AIzaSyBBx5K584L9tytoVu8FeGhQJp-N9W8nbLM";

            // Your Search Engine ID
            // TODO replace with your value
            String cx = "015987699959939710944:r-8zsw0mhga";

            String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchStringNoSpaces + "&key=" + key + "&cx=" + cx + "&alt=json";
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                Log.e(TAG, "ERROR converting String to URL " + e.toString());
            }
            ImgSearchAsyncTask searchTask3 = new ImgSearchAsyncTask();
            searchTask3.execute(url);

            bigting = new JSONObject(alljson);
            jarray = bigting.getJSONArray("items");
            pting = jarray.getJSONObject(0);
            wikiurl = pting.getString("link");
            new Task().execute();

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public class Task extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(wikiurl).get();
                int count = 0;

                for(Element row: doc.select("table.infobox.biography.vcard tr")){

                    if(row.text().contains("(age")) {
                        age = row.text();
                        age = age.substring(age.indexOf("(age") + 5, age.indexOf("(age") + 7);
                    }

                    count++;
                }

                if(age.equals("")){

                    for(Element row: doc.select("table.plainlist.vcard.infobox tr")){

                        if(row.text().contains("(age")) {
                            age = row.text();
                            age = age.substring(age.indexOf("(age") + 5, age.indexOf("(age") + 7);
                        }

                        count++;
                    }

                }

                for(Element p: doc.select("div.mw-parser-output p")){
                        desc += p.text();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
                ageView.setText("Age: " + age);
                desc.replace("null","");
                descView.setText(desc.substring(4));
                descView.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    public class ImgSearchAsyncTask extends AsyncTask<URL, Integer, String>{

        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(URL... urls) {

            URL url = urls[0];

            // Http connection
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                Log.e(TAG, "Http connection ERROR " + e.toString());
            }

            try {
                responseCode = conn.getResponseCode();
                responseMessage = conn.getResponseMessage();
            } catch (IOException e) {
                Log.e(TAG, "Http getting response code ERROR " + e.toString());
            }

            try {

                if(responseCode == 200) {

                    // response OK

                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = rd.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    rd.close();

                    conn.disconnect();

                    result3 = sb.toString();

                    return result3;

                }else{

                    // response problem

                    String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                    Log.e(TAG, errorMsg);
                    result3 = errorMsg;
                    return result3;

                }
            } catch (IOException e) {
                Log.e(TAG, "Http Response ERROR " + e.toString());
            }


            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "AsyncTask - onProgressUpdate, progress=" + progress);

        }

        protected void onPostExecute(String result3) {
            try {
                obj1 = new JSONObject(result3);
                jarray2 = obj1.getJSONArray("items");
                obj2 = jarray2.getJSONObject(0);
                jarray3 = obj2.getJSONObject("pagemap").getJSONArray("metatags");
                imgurl = jarray3.getJSONObject(0).getString("og:image");

                new SongActivity.DownloadImageTask((ImageView) findViewById(R.id.id_profpic))
                        .execute(imgurl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
