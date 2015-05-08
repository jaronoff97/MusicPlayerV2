package com.musicPlayer.JML;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.worldsworstsoftware.itunes.ItunesLibrary;
import com.worldsworstsoftware.itunes.ItunesTrack;
import com.worldsworstsoftware.itunes.parser.ItunesLibraryParser;
import com.worldsworstsoftware.itunes.parser.logging.DefaultParserStatusUpdateLogger;
import com.worldsworstsoftware.itunes.parser.logging.ParserStatusUpdateLogger;
import com.worldsworstsoftware.itunes.util.ItunesLibraryFinder;
import com.worldsworstsoftware.logging.DefaultStatusUpdateLogger;
import com.worldsworstsoftware.logging.StatusUpdateLogger;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * @author JEAcomputer
 *
 */
public class MusicPlayer extends Application {

	public static Library masterLibrary = new Library("Library");
	public static int index = 0;

	public static StatusUpdateLogger logger = new DefaultStatusUpdateLogger(true, System.out);
	public static String libraryLocation = ItunesLibraryFinder.findLibrary(logger);
	public static DefaultParserStatusUpdateLogger logger2 = new DefaultParserStatusUpdateLogger(true, System.out);
	public static ItunesLibrary itunesLibrary;
	public static BorderPane musicBorder =  new BorderPane();
	public static VBox musicVBox;
	public static PlayerControl mediaControl;
	public static ProgressBar progress = new ProgressBar();

	public static FileChooser addMP3Choose = new FileChooser();

	public static VBox topBar =  new VBox();

	public MenuBar menuBar = new MenuBar();
	public Menu menuFile = new Menu("File");
	public Menu menuEdit = new Menu("Edit");
	public Menu menuView = new Menu("View");

	public GridPane gridBar = new GridPane();

	public static ChangeListener<Duration> progressChangeListener;
	public static Text currentTime = new Text("");

	public static TableView<Song> musicTable = new TableView<Song>();
	/**
	 *
	 */
	public MusicPlayer() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		musicVBox = new VBox();
		logger2.setTrackParseUpdateFrequency(200);

		logger2.setPlaylistParseUpdateFrequency(ParserStatusUpdateLogger.UPDATE_FREQUENCY_ALWAYS); //we could also just do 1..
		itunesLibrary  = ItunesLibraryParser.parseLibrary(libraryLocation, logger2);

		Set<?> trackIds = itunesLibrary.getTracks().keySet();

		Integer trackId = null;
		Iterator<?> it = trackIds.iterator();
		for (int i = 0; i < (itunesLibrary.getTracks().size()); i++) {
			trackId = (Integer) it.next();
			ItunesTrack track = itunesLibrary.getTrackById(trackId.intValue());
			System.out.println(track.getGenre());
			if (track.getGenre() != "Podcast") {
				Song s = new Song(track, index);
				if (s != null) {
					if (s.getPointer() != null) {
						if (s.getGenre() != "Podcast") {
							masterLibrary.add(s);
						}

					}

				}
				index++;
			}
		}
		musicTable.setItems(masterLibrary.songList);
		Scene musicScene = new Scene(musicBorder, 0, 0);
		primaryStage.setScene(musicScene);
		musicBorder.setCenter(musicVBox);
		musicBorder.setBottom(mediaControl);
		musicBorder.setTop(topBar);
		musicVBox.setSpacing(5);
		musicVBox.setPadding(new Insets(10, 0, 0, 10));
		musicVBox.getChildren().addAll(new Label(""), musicTable);
		musicVBox.setLayoutX(0);
		musicVBox.setLayoutY(70);
		musicVBox.setVgrow(musicTable, Priority.ALWAYS);
		menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
		topBar.getChildren().addAll(menuBar, gridBar);
		gridBar.setVgap(4);
		gridBar.setHgap(10);
		gridBar.setPadding(new Insets(5, 5, 5, 5));
		final ComboBox searchComboBox = new ComboBox();
		searchComboBox.setEditable(true);
		searchComboBox.setPromptText("Search...");
		searchComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				searchComboBox.getItems().clear();
				searchComboBox.getItems().addAll(search(ov.getValue().toString()));

				System.out.println(ov.getValue().toString());
				System.out.println(t);
				System.out.println(t1);
			}
		});

		searchComboBox.setOnAction((Event ev) -> {
			try
			{
				musicTable.scrollTo(masterLibrary.indexOf((Song) searchComboBox.getSelectionModel().getSelectedItem()));
				searchComboBox.getItems().removeAll();
			}
			catch(ClassCastException e)
			{
				
			}
        });
		gridBar.add(searchComboBox, 0, 0);

		musicTable.setEditable(true);
		TableColumn<Song, String> songCol = new TableColumn<Song, String>("Song Name");
		songCol.setCellValueFactory(new PropertyValueFactory<Song, String>("titleName"));
		songCol.setPrefWidth(300);
		TableColumn<Song, String> artistCol = new TableColumn<Song, String>("Artist Name");
		artistCol.setCellValueFactory(new PropertyValueFactory<Song, String>("artistName"));
		artistCol.setPrefWidth(200);
		TableColumn<Song, String> albumCol = new TableColumn<Song, String>("Album Name");
		albumCol.setCellValueFactory(new PropertyValueFactory<Song, String>("albumName"));
		albumCol.setPrefWidth(300);
		TableColumn<Song, String> genreCol = new TableColumn<Song, String>("Genre Name");
		genreCol.setCellValueFactory(new PropertyValueFactory<Song, String>("genreName"));
		genreCol.setPrefWidth(150);
		TableColumn<Song, String> playsCol = new TableColumn<Song, String>("Plays name");
		playsCol.setCellValueFactory(new PropertyValueFactory<Song, String>("playsName"));
		playsCol.setPrefWidth(100);

		TableColumn<Song, String> durationCol = new TableColumn<Song, String>("Duration");
		durationCol.setCellValueFactory(new PropertyValueFactory<Song, String>("durationName"));
		TableColumn<Song, String> orderCol = new TableColumn<Song, String>("Order");
		orderCol.setCellValueFactory(new PropertyValueFactory<Song, String>("orderName"));

		Menu menuEffect = new Menu("Theme");

		MenuItem lightSkin = new MenuItem("Light Skin");
		lightSkin.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {

			}
		});

		//No Effects menu
		MenuItem noEffects = new MenuItem("Default Theme");

		noEffects.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
			}
		});
		menuEffect.getItems().addAll(lightSkin, noEffects);
		MenuItem menuAddFiles = new MenuItem("Import MP3's");
		menuAddFiles.setAccelerator(KeyCombination.keyCombination("Meta+O"));
		menuAddFiles.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				browseFiles(primaryStage, masterLibrary);

			}
		});
		MenuItem makeNewPlaylist = new MenuItem("Make a new playlist");
		makeNewPlaylist.setAccelerator(KeyCombination.keyCombination("Meta+N"));
		makeNewPlaylist.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {

			}
		});
		MenuItem exit = new MenuItem("Exit");
		exit.setAccelerator(KeyCombination.keyCombination("Meta+Q"));
		exit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				System.out.println("EXITING!");
				System.exit(0);
			}
		});
		menuFile.getItems().addAll(menuAddFiles, makeNewPlaylist, exit);
		menuView.getItems().addAll(menuEffect);

		musicTable.getColumns().addAll(orderCol, songCol, artistCol, albumCol, genreCol, playsCol);
		musicTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		musicScene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
				musicTable.setPrefWidth((double)newSceneWidth);
				musicTable.setMaxWidth((double)newSceneWidth);
			}
		});
		musicScene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
				musicTable.setPrefHeight((double)newSceneHeight);
				musicTable.setMaxHeight((double)newSceneHeight);

				musicTable.setPrefHeight((double)newSceneHeight);
				musicTable.setMaxHeight((double)newSceneHeight);
			}
		});
		musicTable.setRowFactory(cb -> {
			TableRow<Song> row = new TableRow<>();
			row.setOnMouseClicked(ev -> {
				final ContextMenu rightClickEvents = new ContextMenu();
				MenuItem rightClickPlay = new MenuItem("Play Song");
				rightClickPlay.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {

					}
				});
				MenuItem rightClickPause = new MenuItem("Pause Song");
				rightClickPause.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {

					}
				});
				MenuItem remove = new MenuItem("Remove Song");
				remove.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {

					}
				});
				Menu rightClickAddToPlaylist = new Menu("Add to a playlist");
				rightClickAddToPlaylist.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {
						//rightClickAddToPlaylist.getItems().add(queue.pMenuItem);

					}

				});
				rightClickEvents.getItems().add(rightClickPlay);
				rightClickEvents.getItems().add(rightClickPause);
				rightClickEvents.getItems().add(remove);
				rightClickEvents.getItems().add(rightClickAddToPlaylist);
				row.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new EventHandler<MouseEvent>() {
					@Override public void handle(MouseEvent e) {
						if (e.getButton() == MouseButton.SECONDARY)
							rightClickEvents.show(row, e.getScreenX(), e.getScreenY());
					}
				});

				if (musicTable.getSelectionModel().getSelectionMode() == SelectionMode.MULTIPLE) {
					final ObservableList<Song> selection = musicTable.getSelectionModel().getSelectedItems();

				}
				if (ev.getClickCount() > 1) {
					if (masterLibrary.contains(row.getItem())) {
						row.getItem().setURL(row.getItem().getPath());
						play(row.getItem());
						System.out.println("PRINTING");
					}
				}
			});
			return row;
		});
        
		
		

		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		// TODO Auto-generated method stub



		primaryStage.setX(0);
		primaryStage.setY(0);
		primaryStage.setWidth(bounds.getWidth());
		primaryStage.setHeight(bounds.getHeight());

		primaryStage.show();

	}

	/**
	 * @param args
	 */
	public static void play(Song s) {
		setCurrentlyPlaying(s);
		setMediaControl(s);
		masterLibrary.play(masterLibrary.indexOf(s));

	}
	public static ObservableList<Song> search(String searchString) {
		ObservableList<Song> options = FXCollections.observableArrayList();
		for (Song song : masterLibrary.songList) {
			if (song != null) {
				try {

					if (song.getGenre().contains(searchString)) {
						options.add(song);
						continue;
					}
					if (song.getName().contains(searchString)) {
						options.add(song);
						continue;
					}
					if (song.getAlbum().contains(searchString)) {
						options.add(song);
						continue;
					}
					if (song.getArtist().contains(searchString)) {
						options.add(song);
						continue;
					}


				} catch (NullPointerException exception) {
					//exception.printStackTrace();
				}


			}
		}

		return (options);


	}
	public static void browseFiles(Stage primaryStage, Library library) {
		configureFileChooser(addMP3Choose);
		List<File> list =
		    addMP3Choose.showOpenMultipleDialog(primaryStage);
		if (list != null) {
			for (File file : list) {
				library.add(new Song(file.toURI(), index));

				addToLibrary(file);
			}
		}
	}
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.setTitle("Add MP3's");
		fileChooser.setInitialDirectory(
		    new File(System.getProperty("user.home"))
		);
		fileChooser.getExtensionFilters().addAll(
		    new FileChooser.ExtensionFilter("MP3", "*.mp3")
		);
	}
	public static void addToLibrary(File f) {

		String fileName = "addToLibrary.txt";

		try {
			FileWriter filewriter = new FileWriter(fileName, true);
			BufferedWriter bufferedWriter = new BufferedWriter(filewriter);
			bufferedWriter.newLine();
			bufferedWriter.write(f.toURI().toString());
			System.out.println("Your file has been written");
			bufferedWriter.close();
		} catch (IOException ex) {
			System.out.println(
			    "Error writing to file '"
			    + fileName + "'");
		} finally {
		}
	}
	public static void setCurrentlyPlaying(final Song s) {
		//removeAllListeners();
		MediaPlayer newPlayer = s.getSong();
		//newPlayer.seek(Duration.ZERO);

		progress.setProgress(0);
		progressChangeListener = new ChangeListener<Duration>() {
			@Override public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
				s.setProgressListener(progressChangeListener);
				progress.setProgress(1.0 * newPlayer.getCurrentTime().toMillis() / newPlayer.getTotalDuration().toMillis());
				currentTime.setText("0:0 " + makeProperTime(newPlayer.getCurrentTime()) + " " + makeProperTime(newPlayer.getTotalDuration()));
			}
		};
		newPlayer.currentTimeProperty().addListener(progressChangeListener);
	}
	private static void setMediaControl(Song s) {
		System.out.println("Setting Media Viewer");
		mediaControl = new PlayerControl(s);
		musicBorder.setBottom(mediaControl);
	}
	public static String makeProperTime(Duration d) {
		int minTime = (int) d.toMinutes();
		int secTime = (int) d.toSeconds();
		String secTimeString = "";
		if (secTime / 60 >= 1) {
			secTime %= 60;
		}
		if (minTime / 60 >= 1) {
			minTime %= 60;
		}
		if (secTime < 10) {
			secTimeString = "0" + secTime;
			return (minTime + ":" + secTimeString);
		} else {
			return (minTime + ":" + secTime);
		}


	}
	public static void main(String[] args) {
		launch(args);
		libraryLocation = args[0];
		// TODO Auto-generated method stub

	}

}
