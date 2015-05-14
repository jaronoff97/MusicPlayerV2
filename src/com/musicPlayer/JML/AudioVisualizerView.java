package com.musicPlayer.JML;



import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AudioVisualizerView extends Application {
public static Song song;
public static Stage stage;
private GridPane grid = new GridPane();
private Scene scene;


	public AudioVisualizerView(Song song1, Stage s) {
		// TODO Auto-generated constructor stub
		song=song1;
		stage=s;
		try {
			start(stage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		scene=new Scene(grid,800,500);
		
		primaryStage.setScene(scene);
		primaryStage.show();

	}
	public static void main(String[] args)
	{
		
	}


}
