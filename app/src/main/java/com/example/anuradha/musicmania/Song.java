package com.example.anuradha.musicmania;

public class Song {

    private String songName, artist, year, id;

    public Song(){
        //default constructor for the boys
    }

    public Song(String songName, String artist, String year, String id){
        this.songName = songName;
        this.artist = artist;
        this.year = year;
        this.id = id;
    }

    public String getSongName(){
        return songName;
    }

    public String getArtist(){
        return artist;
    }

    public String getYear(){
        return year;
    }

    public String toString() {
        return songName + " by " + artist + " in " + year;
    }

    public String getId() {
        return id;
    }
}
