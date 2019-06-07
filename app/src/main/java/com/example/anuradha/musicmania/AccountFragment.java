package com.example.anuradha.musicmania;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    ListView listView;
    List<Song> songs;
    SongListAdapter adapter;
    TextView edisplay;
    Button signoutBtn;

    private FirebaseAuth sAuth;
    private static final String TAG = "searchApp";
    static String result = null;
    Integer responseCode = null;
    String responseMessage = "";
    String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

            listView = view.findViewById(R.id.id_listView);
            edisplay = view.findViewById(R.id.id_edisplay);
            signoutBtn = view.findViewById(R.id.id_signout);
            sAuth = FirebaseAuth.getInstance();
            songs = new ArrayList<>();
            adapter = new SongListAdapter(getActivity(), songs);
            listView.setAdapter(adapter);
            edisplay.setText(sAuth.getCurrentUser().getEmail());
            uid = sAuth.getCurrentUser().getUid();
            SongActivity.databaseSongs = FirebaseDatabase.getInstance().getReference(uid).child("songs");

           SongActivity.databaseSongs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    songs.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Song song = ds.getValue(Song.class);
                        songs.add(song);
                    }

                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("CLICKED", "yes");
                    final String searchString = songs.get(position).getSongName();

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
                    GoogleSearchAsyncTask2 searchTask = new GoogleSearchAsyncTask2();
                    searchTask.execute(url);
            }
        });
        return view;
    }

    public class GoogleSearchAsyncTask2 extends AsyncTask<URL, Integer, String> {
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

                if (responseCode == 200) {

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

                } else {

                    // response problem

                    String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                    Log.e(TAG, errorMsg);
                    result = errorMsg;
                    return result;

                }
            } catch (IOException e) {
                Log.e(TAG, "Http Response ERROR " + e.toString());
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Intent intent = new Intent(getActivity(), SongActivity.class);
            intent.putExtra("JSONFILE", result);
            startActivity(intent);
        }
    }
}

