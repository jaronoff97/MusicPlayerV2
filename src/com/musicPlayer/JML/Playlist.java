package com.musicPlayer.JML;

import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public class Playlist {
	public String name=null;
	public ObservableList<Integer> playlist = FXCollections.observableArrayList();
	public final SimpleStringProperty titleName = new SimpleStringProperty(name);
	
	public ArrayList<Integer> intList = new ArrayList<Integer>();
	public int indexOfPlaying=0;
	public boolean shuffle=false;
	
	public String toString()
	{
		return name;
	}
	public Playlist(Playlist p)
	{
		name=p.getName();
		playlist=FXCollections.observableArrayList(p.intList);
		setTitleName(name);
		
	}
	public Playlist(String nString) {
		name=nString;
		setTitleName(name);
		
		
		// TODO Auto-generated constructor stub
	}
    public void setTitleName(String fname) {
    		
        titleName.set(fname);
    }
    public String getTitleName() {
    		
    		return titleName.get();
    }
    public String getName()
    {
    		return name;
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
	public void addAll(ObservableList<Integer> dragged) {
		// TODO Auto-generated method stub
		playlist.addAll(dragged);
	}
	public ArrayList<Integer> intArrayList()
	{
		ArrayList<Integer> intArrayList = new ArrayList<>(playlist);
		return(intArrayList);
	}
	public void prepData()
	{
		intList=intArrayList();
		
		
	}
	public int getNext() {
		  if (indexOfPlaying < playlist.size() - 1) {
	            indexOfPlaying ++;

	        } else {
	            indexOfPlaying = 0;
	        }
		System.out.println("Next song: "+indexOfPlaying);
		return(playlist.get(indexOfPlaying));
	}
	public int getPrev() {
		if (indexOfPlaying - 1 == -1) {
            indexOfPlaying = this.playlist.size() - 1;
        } else {
            indexOfPlaying--;
        }
		return(playlist.get(indexOfPlaying));
	}

}
