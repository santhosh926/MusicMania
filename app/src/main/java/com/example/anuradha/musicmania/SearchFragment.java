package com.example.anuradha.musicmania;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    EditText eText;
    Button btn;
    ProgressBar progressBar;

    private static final String TAG = "searchApp";
    static String result = null;
    Integer responseCode = null;
    String responseMessage = "";

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // GUI init
        eText = view.findViewById(R.id.id_edittext);
        btn = view.findViewById(R.id.id_search);
        progressBar = view.findViewById(R.id.pb_loading_indicator);

        // button onClick
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String searchString = eText.getText().toString();

                // hide keyboard
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // looking for
                String searchStringNoSpaces = searchString.replace(" ", "+");

                // Your API key
                // TODO replace with your value
                String key = "AIzaSyBBx5K584L9tytoVu8FeGhQJp-N9W8nbLM";

                // Your Search Engine ID
                // TODO replace with your value
                String cx = "015987699959939710944:ahx3ggidcbq";

                String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchStringNoSpaces + "&key=" + key + "&cx=" + cx + "&alt=json";
                URL url = null;
                try {
                    url = new URL(urlString);
                } catch (MalformedURLException e) {
                    Log.e(TAG, "ERROR converting String to URL " + e.toString());
                }


                // start AsyncTask
                GoogleSearchAsyncTask searchTask = new GoogleSearchAsyncTask();
                searchTask.execute(url);
            }

        });
        return view;
    }


    public class GoogleSearchAsyncTask extends AsyncTask<URL, Integer, String> {

            protected void onPreExecute(){
                // show progressbar
                progressBar.setVisibility(View.VISIBLE);
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

                        result = sb.toString();

                        return result;

                    }else{

                        // response problem

                        String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                        Log.e(TAG, errorMsg);
                        result = errorMsg;
                        return  result;

                    }
                } catch (IOException e) {
                    Log.e(TAG, "Http Response ERROR " + e.toString());
                }


                return null;
            }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "AsyncTask - onProgressUpdate, progress=" + progress);

        }

        protected void onPostExecute(String result) {

            // hide progressbar
            progressBar.setVisibility(View.GONE);

            Intent intent = new Intent(getActivity(), SongActivity.class);
            intent.putExtra("JSONFILE", result);
            startActivity(intent);

        }


    }
}