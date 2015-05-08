package com.musicPlayer.JML;


import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.EqualizerBand;
import javafx.stage.PopupWindow;

public class EqualizerView extends PopupWindow {
private static final double START_FREQ = 250.0;
private static final int BAND_COUNT = 7;
private Song song;
private GridPane grid = new GridPane();


	public EqualizerView(Song s) {
		song=s;
		createEQInterface(grid,song);
		createSpectrumBars(grid, song);
		spectrumListener = new SpectrumListener(START_FREQ, song.getSong(), spectrumBars);
		// TODO Auto-generated constructor stub
	}
	
	private void createSpectrumBars(GridPane grid2, Song song2) {
		// TODO Auto-generated method stub
		spectrumBars = new SpectrumBar[BAND_COUNT];
		
		for(int i=0; i<spectrumBars.length; i++)
		{
			spectrumBars[i] = new SpectrumBars(100,20);
			spectrumBars[i].setMaxWidth(44);
			
		}
	}

	private void createEQInterface(GridPane gPane, Song s)
	{
		final ObservableList<EqualizerBand> bands = s.getSong().getAudioEqualizer().getBands();
		bands.clear();
		
		double min = EqualizerBand.MIN_GAIN;
		double max = EqualizerBand.MAX_GAIN;
		double mid = (max-min)/2;
		double freq = START_FREQ;
		
		for(int j=0; j<BAND_COUNT;j++)
		{
			double theta = (double)j/ (double)(BAND_COUNT-1)*(2*Math.PI);
			double scale = 0.4 * (1+Math.cos(theta));
			double gain = min +mid + (mid*scale);
			
			bands.add(new EqualizerBand(freq, freq/2, gain));
		}
		for(int i=0;i<bands.size(); ++i)
		{
			EqualizerBand eb = bands.get(i);
			Slider s= createEQSlider(eb,min,max);
			
			final Label l= new Label(formatFrequency(eb.getCenterFrequency()));
			l.getStyleClass().addAll("mediaText","eqLabel");
			
			GridPane.setHalignment(l, HPos.CENTER);
			GridPane.setHalignment(s, HPos.CENTER);
			GridPane.setHgrow(s,  Priority.ALWAYS);
			
			gPane.add(l,i,1);
			gPane.add(s,i,2);
			
		}
	}
	private Slider createEQSlider(EqualizerBand eb, double min, double max)
	{
		final Slider s = new Slider(min,max,eb.getGain());
		s.getStyleClass().add("eqSlider");
		s.setOrientation(Orientation.VERTICAL);
		s.valueProperty().bindBidirectional(eb.gainProperty());
		s.setPrefWidth(44);
		return s;
	}
	private String formatFrequency(double centerFrequency)
	{
		if(centerFrequency<1000)
		{
			return String.format("%.0f Hz", centerFrequency);
		}
		else
		{
			return String.format("%.1f Hz", centerFrequency/1000);
		}
	}
}
