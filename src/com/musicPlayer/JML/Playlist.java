package com.musicPlayer.JML;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class Playlist {
	public ObservableList<Integer> playlist = FXCollections.observableArrayList();
	public String name=null;
	public final SimpleStringProperty titleName = new SimpleStringProperty(name);
	public int indexOfPlaying=0;
	public boolean shuffle=false;

	public Playlist(String nString) {
		name=nString;
		setTitleName(name);
		
		// TODO Auto-generated constructor stub
	}
    public void setTitleName(String fname) {
    		
        titleName.set(fname);
    }
    public void add(int s)
    {	
    		playlist.add(s);
    }
    public void remove(int s)
    {
    		playlist.remove(s);
    }
    public int size(){
    		return(playlist.size());
    }
    public void play(int i)
    {
    		stop(indexOfPlaying);
    		indexOfPlaying=i;
    		MusicPlayer.masterLibrary.get(indexOfPlaying).play();
    }
    public void stop(int i)
    {
    		if(MusicPlayer.masterLibrary.get(i).playing!=false)
    		{
        		MusicPlayer.masterLibrary.get(i).stop();
    		}
    }
    public void pause(int i)
    {
		if(MusicPlayer.masterLibrary.get(i).playing!=false)
		{
    			MusicPlayer.masterLibrary.get(i).pause();
		}
    }

}
