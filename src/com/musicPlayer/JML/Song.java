package com.musicPlayer.JML;

import java.net.URI;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.worldsworstsoftware.itunes.ItunesTrack;



public class Song extends ItunesTrack{
	private int originalOrder = 0;
	private MediaPlayer song;
	private URI pointer;
	private String path;
	public boolean songAtEnd=true;
	public boolean playing=false;
	public boolean setup=false;
	public Image albumCover;
    public final SimpleIntegerProperty orderName = new SimpleIntegerProperty(originalOrder);
	@XStreamAlias("TitleName")
    public final SimpleStringProperty titleName = new SimpleStringProperty(name);
	@XStreamAlias("artistName")
    public final SimpleStringProperty artistName = new SimpleStringProperty(artist);
	@XStreamAlias("albumName")
    public final SimpleStringProperty albumName = new SimpleStringProperty(album);
	@XStreamAlias("genreName")
    public final SimpleStringProperty genreName = new SimpleStringProperty(genre);
	@XStreamAlias("playsName")
    public final SimpleIntegerProperty playsName = new SimpleIntegerProperty(playCount);
	@XStreamAlias("totalTime")
    public final SimpleStringProperty timeName = new SimpleStringProperty(""+totalTime);
	@XStreamAlias("kindName")
    public final SimpleStringProperty kindName = new SimpleStringProperty(kind);
	
	
	
    public String toString() {
        return ("Name: " + name + "\n Artist: " + artist + "\n Album: " + album +"\n Number: "+originalOrder);
        //return ("--");
    }
    public ChangeListener<Duration> progresschangelistener;
	
   

	public Song(ItunesTrack obj, int oO) {
		super(obj);
		originalOrder=oO;
		
		try {
			setPointer(new URI(location));
			setPath(getPointer().toASCIIString());
			System.out.println(toString());
		} catch (Exception e) {
			
		}

        setTitleName(name);
        setArtistName(artist);
        setAlbumName(album);
        setOrderName(originalOrder);
        setPlaysName(playCount);
        setGenreName(genre);
        setTimeName(formatTime(new Duration(0), new Duration(totalTime)));
        setKindName(kind);
        //setDurationName(""+totalTime);
	}
	 public Song( URI f, int oO) {
	        resetProperties();
	        originalOrder = oO;
	        setPath(f.toASCIIString());
	        setPointer(f);
	        System.out.println("Made Song");
	        setURL(getPath());
	        setOnEndOfMedia();
	        startHandling(getPath());
	        setOrderName(originalOrder);
	        setPlaysName(playCount);
	        setGenreName(genre);
	        


	        //System.out.println(this);
	    }
	 public int getOrder()
	 {
		 return(originalOrder);
	 }
	 public String returnData()
	 {
		 return ("Name: " + name + "\n Artist: " + artist + "\n Album: " + album);
	 }
	 public void play() {
		 System.out.println("PLAYING!!!! "+toString());
		 	if(setup==false)
		 	{
		 		startHandling(getPath());
		 	}
	        song.play();
	        playing = true;
	    }
	    public void stop() {
	    		if(setup==true)
		 	{
	    			  song.stop();
	    		        playing = false;
		 	}
	      
	    }
	    public void pause() {
	        song.pause();
	        playing = false;
	    }
	    public void mute() {
	        if (this.song.isMute() == false) {
	            this.song.setMute(true);
	        }
	        if (this.song.isMute() == true) {
	            this.song.setMute(false);
	        }
	    }
	 	public MediaPlayer getSong() {
	 		if(song==null)
	 		{
	 			startHandling(getPath());
	 		}
			return song;
		}
	    public void setOnEndOfMedia() {
	        song.setOnEndOfMedia(new Runnable() {
	            @Override
	            public void run() {
	                songAtEnd = true;
	            }
	        });
	    }
	    private static String formatTime(Duration elapsed, Duration duration) {
	        int intElapsed = (int) Math.floor(elapsed.toSeconds());
	        int elapsedHours = intElapsed / (60 * 60);
	        if (elapsedHours > 0) {
	            intElapsed -= elapsedHours * 60 * 60;
	        }
	        int elapsedMinutes = intElapsed / 60;
	        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
	                             - elapsedMinutes * 60;

	        if (duration.greaterThan(Duration.ZERO)) {
	            int intDuration = (int) Math.floor(duration.toSeconds());
	            int durationHours = intDuration / (60 * 60);
	            if (durationHours > 0) {
	                intDuration -= durationHours * 60 * 60;
	            }
	            int durationMinutes = intDuration / 60;
	            int durationSeconds = intDuration - durationHours * 60 * 60
	                                  - durationMinutes * 60;
	           durationSeconds-=1;
	            if (durationHours > 0) {
	                return String.format("%d:%02d:%02d",
	                                     durationHours, durationMinutes, durationSeconds);
	            } else {
	                return String.format("%02d:%02d",durationMinutes,
	                                     durationSeconds);
	            }
	        } else {
	            if (elapsedHours > 0) {
	                return String.format("%d:%02d:%02d", elapsedHours,
	                                     elapsedMinutes, elapsedSeconds);
	            } else {
	                return String.format("%02d:%02d", elapsedMinutes,
	                                     elapsedSeconds);
	            }
	        }
	    }
	    public void startHandling(String url)
	    {
	    	  try {
	              final Media media = new Media(url);
	              song = new MediaPlayer(media);
	              setup=true;
	              if(name==null)
	              {
	            	  media.getMetadata().addListener(new MapChangeListener<String, Object>() {
		                  @Override
		                  public void onChanged(Change<? extends String, ? extends Object> ch) {
		                      if (ch.wasAdded()) {

		                          handleMetadata(ch.getKey(), ch.getValueAdded());
		                          

		                      }
		                  }
		              });
	              }
	              
	             
	          } catch (RuntimeException re) {
	              // Handle construction errors
	              System.out.println("Caught Exception: " + re.getMessage());
	          }
	    }
	    public void handleMetadata(String key, Object value) {
	        if (key.equals("album")) {
	            album = value.toString();
	            //System.out.println(album);
	        }
	        if (key.equals("artist")) {
	            artist = value.toString();
	            //System.out.println(artist);
	        }
	        if (key.equals("title")) {
	            name = value.toString();
	            //System.out.println(title);
	            //System.out.println("-------------------");
	        }
	        if (key.equals("genre")) {
	            genre = value.toString();
	            //System.out.println(title);
	            //System.out.println("-------------------");
	        }
	        if (key.equals("image")) {
	        		albumCover=(Image)(value);
	        }
	        //setDurationName("");
	        setTitleName(name);
	        setArtistName(artist);
	        setAlbumName(album);
	        setOrderName(originalOrder);
	        setGenre(genre);
	        
	        //System.out.println(getOrderName());
	    }
	    public void setURL(String url) {

	        initializeMedia(url);
	    }
	    private void initializeMedia(String url) {
	        resetProperties();
	        try {
	            final Media media = new Media(url);
	            
	            song = new MediaPlayer(media);
	            
	        } catch (RuntimeException re) {
	            // Handle construction errors
	            System.out.println("Caught Exception: " + re.getMessage());
	        }
	    }
	    private void resetProperties() {
	        setArtistName("");
	        setAlbumName("");
	        setTitleName("");
	    }
	 public void setTitleName(String fname) {
	        titleName.set(fname);
	    }
	    public void setArtistName(String fname) {
	        artistName.set(fname);
	    }
	    public void setAlbumName(String fname) {
	        albumName.set(fname);
	    }
	    public void setPlaysName(int playCount) {
	        playsName.set(playCount);
	    }
	    public void setGenreName(String fname) {
	        genreName.set(fname);
	    }
	    public void setOrderName(int oO) {
	        orderName.set(oO);
	    }
		public URI getPointer() {
			return pointer;
		}
		public void setPointer(URI pointer) {
			this.pointer = pointer;
		}
		public String getTimeName(){
			return timeName.get();
		}
		public void setTimeName(String int1){
			timeName.set(int1);
		}
		public void setKindName(String n)
		{
			kindName.set(n);
		}
		public String getKindName()
		{
			return kindName.get();
		}
		 public String getTitleName() {
		        return titleName.get();
		    }
		    public String getArtistName() {
		        return artistName.get();
		    }
		    public String getAlbumName() {
		        return albumName.get();
		    }
		    public String getGenreName() {
		        return genreName.get();
		    }
		    public int getOrderName() {
		        return orderName.get();
		    }
		    public int getPlaysName() {
		        return playsName.get();
		    }
			public String getPath() {
				return path;
			}
			public void setPath(String path) {
				this.path = path;
			}
		    public void setProgressListener(ChangeListener<Duration> pCL){
		        progresschangelistener=pCL;
		    }
}
