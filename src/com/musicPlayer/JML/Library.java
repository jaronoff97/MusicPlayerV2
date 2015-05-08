package com.musicPlayer.JML;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Library extends Playlist{
	public ObservableList<Song> songList = FXCollections.observableArrayList();
	public String name=null;
	public final SimpleStringProperty titleName = new SimpleStringProperty(name);
	public int indexOfPlaying=0;
	public boolean shuffle=false;
	
	public Library(String nString) {
		// TODO Auto-generated constructor stub
		super(nString);
	}
	public void add(Song s)
	{
		songList.add(s);
	}
	public void remove(Song s)
	{
		songList.remove(s);
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
		return songList.get(i);
	}


}
