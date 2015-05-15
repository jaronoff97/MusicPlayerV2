package com.musicPlayer.JML;


import java.util.Collection;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaView;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

public class PlayerControl extends BorderPane {

    private MediaPlayer mp;
    private Song song;
    private MediaView mediaView;
    private final boolean repeat = false;
	private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private HBox mediaBar;
    private GridPane infoGrid = new GridPane();
    public Text title, artist, album;
    public int indexOfGrid=0;
    

	public HBox hForwardButton = new HBox(10);
	public Button forwardButton = new Button(">>");

	public HBox hReverseButton = new HBox(10);
	public Button reverseButton = new Button("<<");
    
	public  final Button playButton = new Button(">");
    
    public PlayerControl()
    {
    		setStyle(MusicPlayer.currentTheme);
    		 Pane mvPane = new Pane() {
    	        };
    		mvPane.setStyle("-fx-background-color: black;");
        setCenter(mvPane);
       
        mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);
        infoGrid.setAlignment(Pos.TOP_CENTER);
        infoGrid.setHgap(100);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(0, 0, 0, 0));
        
        setBottom(mediaBar);
    }
    public PlayerControl(final Song s) {
        song = s;
        song.play();
        song.setPlayCount(song.getPlayCount()+1);
        title=new Text(s.getName());
        artist=new Text(s.getArtist());
        album=new Text(s.getAlbum()); 
        
        System.out.println(s);
        duration = s.getSong().getMedia().getDuration();
        setStyle("-fx-background-color: #bfc2c7;");
        
        mediaView = new MediaView(song.getSong());
       
        Pane mvPane = new Pane() {
        };
        mvPane.getChildren().add(mediaView);
        mvPane.setMinSize(song.getSong().getMedia().getWidth(), song.getSong().getMedia().getWidth());
        
        mvPane.setStyle("-fx-background-color: black;");
        setCenter(mvPane);

        mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);
        infoGrid.setAlignment(Pos.TOP_CENTER);
        infoGrid.setHgap(100);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(0, 0, 0, 0));


		hReverseButton.setAlignment(Pos.TOP_LEFT);
        hReverseButton.getChildren().add(reverseButton);
        
		hForwardButton.setAlignment(Pos.TOP_LEFT);
        hForwardButton.getChildren().add(forwardButton);

        
        infoGrid.add(title,1,0);
        indexOfGrid++;
        infoGrid.add(artist,2,0);
        indexOfGrid++;
        infoGrid.add(album,3,0);
        indexOfGrid++;

        setTop(infoGrid);

        forwardButton.setOnAction(new EventHandler<ActionEvent>() {
        		public void handle(ActionEvent event)
        		{
        			MusicPlayer.play(MusicPlayer.masterLibrary.get(MusicPlayer.playlists.get(MusicPlayer.currentPlaylist).getNext()));
        			/*if(MusicPlayer.equalizerView.open==true)
        			{
        				MusicPlayer.equalizerView.setBottom(MusicPlayer.mediaControl);
        			}
        			else
        			{
        				MusicPlayer.musicBorder.setBottom(MusicPlayer.mediaControl);
        			}*/
        			
        		}
		});
        reverseButton.setOnAction(new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent event)
    			{
    				MusicPlayer.play(MusicPlayer.masterLibrary.get(MusicPlayer.playlists.get(MusicPlayer.currentPlaylist).getPrev()));
    				/*if(MusicPlayer.equalizerView.open==true)
        			{
        				MusicPlayer.equalizerView.setBottom(MusicPlayer.mediaControl);
        			}
        			else
        			{
        				MusicPlayer.musicBorder.setBottom(MusicPlayer.mediaControl);
        			}*/
    			}
        });
        
        
        
       

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = song.getSong().getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == Status.PAUSED
                        || status == Status.READY
                        || status == Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                    		forwardButton.fire();
                    		System.out.println("AT THE END!!!!");
                        atEndOfMedia = false;
                    }
                    song.play();
                } else {
                    song.pause();
                }
            }
        });
        song.getSong().setOnEndOfMedia(new Runnable() {
            public void run() {
             
                    
                    forwardButton.fire();
            }
        });
       
        song.getSong().currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                updateValues();
                //System.out.println("UPDATING VALUES FROM MP ChangeListener");
            }
        });
        
        song.getSong().setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    song.pause();
                    stopRequested = false;
                } else {
                    playButton.setText("||");
                }
            }
        });

        song.getSong().setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                playButton.setText(">");
            }
        });
    
        song.getSong().setOnReady(new Runnable() {

            public void run() {
                duration = song.getSong().getMedia().getDuration();
                updateValues();
                //System.out.println("UPDATING VALUES FROM MP SET ON READY");
                synchronized (song.getSong()) { //this is required since mp.setOnReady creates a new thread and our loopp  in the main thread
                	song.getSong().notify();// the loop has to wait unitl we are able to get the media metadata thats why use .wait() and .notify() to synce the two threads(main thread and MediaPlayer thread)
                }
            }
        });
        
        mediaBar.getChildren().add(hReverseButton);
        mediaBar.getChildren().add(playButton);
        mediaBar.getChildren().add(hForwardButton);
        // Add spacer
        Label spacer = new Label("   ");
        mediaBar.getChildren().add(spacer);

        // Add Time label
        Label timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    if (duration != null) {
                    	song.getSong().seek(duration.multiply(timeSlider.getValue() / 100.0));
                        //System.out.println("UPDATING VALUES FROM MP SEEK");
                    }
                    updateValues();
                }
            }
        });
        mediaBar.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);

        // Add the volume label
        Label volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                	song.getSong().setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });
        mediaBar.getChildren().add(volumeSlider);
        
        setBottom(mediaBar);
    }
    public void stop()
    {
    		song.stop();
    }
    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null && duration != null) {
            Platform.runLater(new Runnable() {
                @SuppressWarnings("deprecation")
				public void run() {
                    Duration currentTime = song.getSong().getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(song.getSong().getVolume() * 100));
                    }
                }
            });
        }
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
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                                     elapsedHours, elapsedMinutes, elapsedSeconds,
                                     durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                                     elapsedMinutes, elapsedSeconds, durationMinutes,
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
    public void addToGrid(Node child){
        infoGrid.add(child,indexOfGrid,0);
        indexOfGrid++;
    }
	public Song getSong() {
		// TODO Auto-generated method stub
		return song;
	}
}