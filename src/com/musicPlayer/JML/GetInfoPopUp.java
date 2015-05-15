package com.musicPlayer.JML;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GetInfoPopUp extends Application {
public static Song song;
private GridPane gridPane = new GridPane();
private TextField name, artist, album, genre, year, kind, duration, size, bitRate, dateModified;
private Text nameText, artistText, albumText, genreText, yearText, kindText, durationText, sizeText, bitRateText, dateModifiedText;


		public GetInfoPopUp(Song s) {
			song=s;
			name = new TextField(song.getName());
			artist = new TextField(song.getArtist());
			album = new TextField(song.getAlbum());
			genre = new TextField(song.getGenre());
			year = new TextField(""+song.getYear());
			kind = new TextField(song.getKind());
			duration = new TextField(""+song.getTotalTime());
			size = new TextField(""+song.getSize());
			bitRate = new TextField(""+song.getBitRate());
			dateModified = new TextField(song.getDateModified());
			nameText=new Text("Name");
			artistText=new Text("Artist");
			albumText=new Text("Album");
			genreText=new Text("Genre");
			yearText=new Text("Year");
			kindText=new Text("Kind");
			durationText=new Text("Duration");
			sizeText=new Text("Size");
			bitRateText=new Text("Bit Rate");
			dateModifiedText=new Text("Date Modified");
			gridPane.add(nameText, 0, 0);
			gridPane.add(artistText, 0, 1);
			gridPane.add(albumText, 0, 2);
			gridPane.add(genreText, 0, 3);
			gridPane.add(yearText, 0, 4);
			gridPane.add(kindText, 0, 5);
			gridPane.add(durationText, 0, 6);
			gridPane.add(sizeText, 0, 7);
			gridPane.add(bitRateText, 0, 8);
			gridPane.add(dateModifiedText, 0, 9);
			gridPane.add(name, 1, 0);
			gridPane.add(artist, 1, 1);
			gridPane.add(album, 1, 2);
			gridPane.add(genre, 1, 3);
			gridPane.add(year, 1, 4);
			gridPane.add(kind, 1, 5);
			gridPane.add(duration, 1, 6);
			gridPane.add(size, 1, 7);
			gridPane.add(bitRate, 1, 8);
			gridPane.add(dateModified, 1, 9);
				// TODO Auto-generated constructor stub
			}
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Scene scene = new Scene(gridPane,300,500);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}
