package com.example.anuradha.musicmania;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SongActivity extends AppCompatActivity {

    TextView artist, song, year;
    Button  artistInfo, add;
    String title, jsonfile, an, sn, yr, snippet, vidurl;
    static JSONArray jsonArray, innerArray;
    static JSONObject mainobj, ting, innerobj;
    int dash, dash2, comma;
    ImageView img;

    private static final String TAG = "searchApp";
    static String result2 = null;
    Integer responseCode = null;
    String responseMessage = "";

    public static DatabaseReference databaseSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        //TextViews
        artist = findViewById(R.id.id_artist);
        song = findViewById(R.id.id_song);
        year = findViewById(R.id.id_year);

        //Buttons
        artistInfo = findViewById(R.id.id_artistInfo);
        add = findViewById(R.id.id_add);

        //ImageView
        img = findViewById(R.id.id_img);

        jsonfile = getIntent().getStringExtra("JSONFILE");

        if(jsonfile==null) {
            Toast.makeText(this, "Song Not Found", Toast.LENGTH_SHORT).show();
            finish();
        }

        try {
            mainobj = new JSONObject(jsonfile);

            if(mainobj==null) {
                Toast.makeText(this, "Song Not Found", Toast.LENGTH_SHORT).show();
                finish();
            }

            jsonArray = mainobj.getJSONArray("items");
            title = jsonArray.getJSONObject(0).getString("title");
            dash = title.indexOf("-");
            an = title.substring(0, dash);

            Log.d("TITLE", title);

            if (title.contains("("))
                dash2 = title.indexOf("(");
            else if (title.contains("["))
                dash2 = title.indexOf("[");
            else if (title.contains("|"))
                dash2 = title.indexOf("|");
            else
                dash2 = title.indexOf("- YouTube");

            if(dash2>0 && dash>0)
                sn = title.substring(dash + 1, dash2);
            else{
                Toast.makeText(this, "Song Not Found", Toast.LENGTH_SHORT).show();
                finish();
            }

            artist.setText("Artist: " + an);
            song.setText("Song:" + sn);

            snippet = jsonArray.getJSONObject(0).getString("snippet");
            comma = snippet.indexOf(",");
            yr = snippet.substring(comma+2, comma+6);
            year.setText("Year: " + yr);

            ting = jsonArray.getJSONObject(0).getJSONObject("pagemap");
            innerArray = ting.getJSONArray("imageobject");
            innerobj = innerArray.getJSONObject(0);

            new DownloadImageTask((ImageView) findViewById(R.id.id_img))
                    .execute(innerobj.getString("url"));

            vidurl = jsonArray.getJSONObject(0).getString("link");
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent vidIntent = new Intent(SongActivity.this, MusicPlayerActivity.class);
                    vidIntent.putExtra("vidurl", vidurl);
                    vidIntent.putExtra("an", an);
                    vidIntent.putExtra("sn", sn);
                    vidIntent.putExtra("yr", yr);
                    startActivity(vidIntent);
                }
            });

            artistInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String searchString = an;

                    // looking for
                    String searchStringNoSpaces = searchString.replace(" ", "+");

                    // Your API key
                    // TODO replace with your value
                    String key="AIzaSyBBx5K584L9tytoVu8FeGhQJp-N9W8nbLM";

                    // Your Search Engine ID
                    // TODO replace with your value
                    String cx = "015987699959939710944:f1fpggr5300";

                    String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchStringNoSpaces + "&key=" + key + "&cx=" + cx + "&alt=json";
                    URL url = null;
                    try {
                        url = new URL(urlString);
                    } catch (MalformedURLException e) {
                        Log.e(TAG, "ERROR converting String to URL " + e.toString());
                    }

                    // start AsyncTask
                    WikiSearchAsyncTask searchTask2 = new WikiSearchAsyncTask();
                    searchTask2.execute(url);

                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addSong();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    private void addSong() {
        String id = databaseSongs.push().getKey();
        Song song = new Song(sn, an, yr, id);
        databaseSongs.child(id).setValue(song);
        Toast.makeText(this, "Song Added to Your Playlist", Toast.LENGTH_SHORT).show();
    }

    public class WikiSearchAsyncTask extends AsyncTask<URL, Integer, String>{

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

                    result2 = sb.toString();

                    return result2;

                }else{

                    // response problem

                    String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                    Log.e(TAG, errorMsg);
                    result2 = errorMsg;
                    return result2;

                }
            } catch (IOException e) {
                Log.e(TAG, "Http Response ERROR " + e.toString());
            }


            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "AsyncTask - onProgressUpdate, progress=" + progress);

        }

        protected void onPostExecute(String result2) {
            Intent profIntent = new Intent(SongActivity.this, ProfileActivity.class);
            profIntent.putExtra("json", result2);
            profIntent.putExtra("an3",an);
            startActivity(profIntent);
        }

    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
