package com.musicPlayer.JML;


import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.EqualizerBand;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EqualizerView extends Application{
private static final double START_FREQ = 250.0;
private static final int BAND_COUNT = 10;
private SpectrumBar[] spectrumBars;
private SpectrumListener spectrumListener;
private Song song;
private BorderPane borderPane = new BorderPane();
private GridPane grid = new GridPane();
private static Scene scene;
private static String currentTheme;
private PlayerControl playerControl;
public boolean initialized=false;
public boolean open=false;

		@Override
		public void start(Stage primaryStage) throws Exception {
	// TODO Auto-generated method stub
			borderPane.setCenter(grid);
			borderPane.setBottom(playerControl);
			scene = new Scene(borderPane, 800,500);
			currentTheme=MusicPlayer.currentTheme;
			setTheme(currentTheme);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){

				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					MusicPlayer.musicBorder.setBottom(playerControl);
					primaryStage.hide();
					open=false;
					
				}
				
			}
			
					);
			
			primaryStage.setScene(scene);
			open=true;
			primaryStage.show();
	
		}
		public EqualizerView()
		{
			
		}
	public EqualizerView(Song s, PlayerControl p, Stage stage) {
		
		initialized=true;
		song=s;
		
		playerControl = p;
		setSong(song);

		try {
			start(stage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}
	public void setSong(Song song1)
	{
		
		grid.getChildren().clear();
		createEQInterface(song1);
		createSpectrumBars(song1);
		
		spectrumListener = new SpectrumListener(START_FREQ, song1.getSong(), spectrumBars);
		song1.getSong().setAudioSpectrumListener(spectrumListener);
	}
	 private void createSpectrumBars(Song s) {
         spectrumBars = new SpectrumBar[BAND_COUNT];

         for (int i = 0; i < spectrumBars.length; i++) {
           spectrumBars[i] = new SpectrumBar(100, 20);
           spectrumBars[i].setMaxWidth(44);
           GridPane.setHalignment(spectrumBars[i], HPos.CENTER);
           grid.add(spectrumBars[i], i, 0);
         }
       }
	 public void setBottom(Node n)
	 {
		 playerControl = (PlayerControl) n;
		 borderPane.setBottom(playerControl);
	 }
	private void createEQInterface(Song s)
	{
		if(s.setup==false)
		{
			s.startHandling(s.getPath());
		}
		final ObservableList<EqualizerBand> bands =
                s.getSong().getAudioEqualizer().getBands();

        bands.clear();
        
        double min = EqualizerBand.MIN_GAIN;
        double max = EqualizerBand.MAX_GAIN;
        double mid = (max - min) / 2;
        double freq = START_FREQ;

        // Create the equalizer bands with the gains preset to
        // a nice cosine wave pattern.
        for (int j = 0; j < BAND_COUNT; j++) {
          // Use j and BAND_COUNT to calculate a value between 0 and 2*pi
          double theta = (double)j / (double)(BAND_COUNT-1) * (2*Math.PI);
          
          // The cos function calculates a scale value between 0 and 0.4
          double scale = 0.4 * (1 + Math.cos(theta));
          
          // Set the gain to be a value between the midpoint and 0.9*max.
          double gain = min + mid + (mid * scale);
          
          bands.add(new EqualizerBand(freq, freq/2, gain));
          freq *= 2;
        }
        
        for (int i = 0; i < bands.size(); ++i) {
          EqualizerBand eb = bands.get(i);
          Slider slider = createEQSlider(eb, min, max);

          final Label l = new Label(formatFrequency(eb.getCenterFrequency()));
          l.getStyleClass().addAll("mediaText", "eqLabel");

          GridPane.setHalignment(l, HPos.CENTER);
          GridPane.setHalignment(slider, HPos.CENTER);
          GridPane.setHgrow(slider, Priority.ALWAYS);

          grid.add(l, i, 1);
          grid.add(slider, i, 2);
        }
	}
	 private Slider createEQSlider(EqualizerBand eb, double min, double max) {
         final Slider s = new Slider(min, max, eb.getGain());
         s.getStyleClass().add("eqSlider");
         s.setOrientation(Orientation.VERTICAL);
         s.valueProperty().bindBidirectional(eb.gainProperty());
         s.setPrefWidth(44);
         return s;
       }
	 private String formatFrequency(double centerFrequency) {
         if (centerFrequency < 1000) {
           return String.format("%.0f Hz", centerFrequency);
         } else {
           return String.format("%.1f kHz", centerFrequency / 1000);
         }
       }

	public GridPane getGrid() {
		return grid;
	}

	public void setGrid(GridPane grid) {
		this.grid = grid;
	}
	public void show(Stage s)
	{
		s.show();
	}
	public void hide(Stage s)
	{
		s.hide();
	}
    public static void quit(Stage s){
    	s.close();
    	//System.exit(0);
    }
    public static void setTheme(String string)
    {
    		scene.getStylesheets().remove(currentTheme);
    		currentTheme=string;
    		scene.getStylesheets().add(currentTheme);
    }

	
}
