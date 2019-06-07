package com.example.anuradha.musicmania;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MusicPlayerActivity extends YouTubeBaseActivity{

    TextView songinfo;
    YouTubePlayerView playerView;
    String whole, vidid, an2, sn2, yr2;
    int startIndex;
    YouTubePlayer.OnInitializedListener onInitializedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        songinfo = findViewById(R.id.id_songinfo);

        an2 = getIntent().getStringExtra("an");
        sn2 = getIntent().getStringExtra("sn");
        yr2 = getIntent().getStringExtra("yr");
        songinfo.setText(an2 + "-" + sn2 + "(" + yr2 + ")");

        whole = getIntent().getStringExtra("vidurl");
        startIndex = whole.indexOf("=");
        vidid = whole.substring(startIndex+1);

        playerView = findViewById(R.id.id_playerView);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(vidid);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        playerView.initialize("AIzaSyBBx5K584L9tytoVu8FeGhQJp-N9W8nbLM", onInitializedListener);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(MusicPlayerActivity.this, SongActivity.class);
        startActivity(myIntent);

        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }

        return true;
    }

}
