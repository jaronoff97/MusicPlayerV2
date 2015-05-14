package com.musicPlayer.JML;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Library extends Playlist{
	public ObservableList<Song> songList = FXCollections.observableArrayList();
	
	public boolean shuffle=false;
	
	public Library(String nString) {
		// TODO Auto-generated constructor stub
		super(nString);
	}
	public void add(Song s)
	{
		songList.add(s);
		playlist.add(s.getOrder());
	}
	public void remove(Song s)
	{
		songList.remove(s);
		playlist.remove(s.getOrder());
	}

    public int indexOf(Song s) {
        return (songList.indexOf(s));
    }
	public boolean contains(Song item) {
		if(songList.contains(item))
				{
				return(true);
				}
		else{

			return false;
		}
	}
	public Song get(int i) {
		// TODO Auto-generated method stub
		for(Song song : songList)
		{
			if(song.getOrder()==i)
			{
				return(song);
			}
		}
		
		return songList.get(i);
	}
	public Song currentlyPlaying()
	{
		return(songList.get(indexOfPlaying));
	}


}
