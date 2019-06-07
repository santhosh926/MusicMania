package com.example.anuradha.musicmania;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class SongListAdapter extends ArrayAdapter<Song> {
    private Activity context;
    private List<Song> songList;

    public SongListAdapter(Activity context, List<Song> songList) {
        super(context, R.layout.list_layout, songList);
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.list_layout, null, true);

        final TextView showName = item.findViewById(R.id.id_showName);
        TextView showArtist = item.findViewById(R.id.id_showArtist);
        TextView showYear = item.findViewById(R.id.id_showYear);
        Button remove = item.findViewById(R.id.id_remove);

        final Song song = songList.get(position);

        showName.setText(song.getSongName());
        showArtist.setText(song.getArtist());
        showYear.setText(song.getYear());

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongActivity.databaseSongs.child(song.getId()).removeValue();
            }
        });

        return item;
    }

}
