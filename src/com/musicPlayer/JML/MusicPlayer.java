package com.musicPlayer.JML;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import com.thoughtworks.xstream.XStream;
import com.worldsworstsoftware.itunes.ItunesLibrary;
import com.worldsworstsoftware.itunes.ItunesTrack;
import com.worldsworstsoftware.itunes.parser.ItunesLibraryParser;
import com.worldsworstsoftware.itunes.parser.logging.DefaultParserStatusUpdateLogger;
import com.worldsworstsoftware.itunes.parser.logging.ParserStatusUpdateLogger;
import com.worldsworstsoftware.itunes.util.ItunesLibraryFinder;
import com.worldsworstsoftware.logging.DefaultStatusUpdateLogger;
import com.worldsworstsoftware.logging.StatusUpdateLogger;

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
	public static PlayerControl mediaControl = new PlayerControl();
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
	public static EqualizerView equalizerView;
	
	public static ObservableList<Integer> dragged = FXCollections.observableArrayList();
	
	public static ListView<Playlist> playlistTable = new ListView<Playlist>();
	public static ObservableList<Playlist> playlists;
	public static Playlist queue = new Playlist("Queue");
	
	public static int currentPlaylist=0;
	
	public static String darkMetro;
	public static String lightMetro;
	public static String flatTheme;
	public static String currentTheme;
	public static String mistSilver;
	public static String windowsTheme;
	
	public Stage eqStage = new Stage();
	public Stage playlistStage = new Stage();
	/**
	 *
	 */
	public static void main(String[] args) {
		launch(args);
		libraryLocation = args[0];

	}
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
		darkMetro = getClass().getResource("/CSS_skins/DefaultSkin.css").toExternalForm();
		flatTheme = getClass().getResource("/CSS_skins/flatterFX.css").toExternalForm();
        lightMetro = getClass().getResource("/CSS_skins/LightMetro.css").toExternalForm();
        mistSilver = getClass().getResource("/CSS_skins/MistSilver.css").toExternalForm();
        windowsTheme = getClass().getResource("/CSS_skins/WindowsGlass.css").toExternalForm();
        currentTheme = darkMetro;
        
        
		equalizerView = new EqualizerView(masterLibrary.get(0), mediaControl, eqStage);
		equalizerView.hide(eqStage);
		equalizerView.open=false;
		final VBox sidebar = new VBox();
        sidebar.setSpacing(5);
        sidebar.setPadding(new Insets(10, 0, 0, 10));
        sidebar.getChildren().addAll(playlistTable);
		playlistTable.setItems(playlists);
		musicTable.setItems(masterLibrary.songList);
		Scene musicScene = new Scene(musicBorder, 0, 0);
		musicScene.getStylesheets().add(currentTheme);
		primaryStage.setScene(musicScene);
		musicBorder.setCenter(musicVBox);
		musicBorder.setBottom(mediaControl);
		musicBorder.setTop(topBar);
		musicBorder.setLeft(sidebar);
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
		searchComboBox.setPrefWidth(200);
		searchComboBox.valueProperty().addListener(new ChangeListener<String>() {
			String pastString="";
			@Override
			public void changed(ObservableValue ov, String oldValue, String newValue) {
				if(oldValue!=newValue)
				{
					searchComboBox.getItems().clear();
				}
				searchComboBox.getItems().addAll(search(ov.getValue().toString()));
				System.out.println("<------------------------->");
				System.out.println(ov.getValue().toString());
				System.out.println(oldValue);
				System.out.println(newValue);
				
			}
		});
		
		Button testButton = new Button("Open visualizer");
		testButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				AudioVisualizerView avView = new AudioVisualizerView(masterLibrary.currentlyPlaying(), new Stage());
				
			}
			
		});
		//gridBar.add(testButton, 4, 0);
		searchComboBox.setOnAction((Event ev) -> {
			try
			{
				musicTable.scrollTo(masterLibrary.indexOf((Song) searchComboBox.getSelectionModel().getSelectedItem()));
				//searchComboBox.getItems().clear();
			}
			catch(ClassCastException e)
			{
				
			}
        });
		
		Button makePlaylist = new Button("Make new playlist");
		 makePlaylist.setOnAction(new EventHandler<ActionEvent>() {

	            @Override
	            public void handle(ActionEvent e) {
	            		TextFieldPopUp playlistMaker = new TextFieldPopUp();
	            		playlistMaker.start(playlistStage);
	            }
	        });
		 primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				saveData();
			}
			 
		});
		
		gridBar.add(searchComboBox, 0, 0);
		gridBar.add(makePlaylist, 1, 0);


		playlists = FXCollections.observableArrayList();
		playlists.add(masterLibrary);
		getData();
		
		//TableColumn<Playlist, String> playlistCol = new TableColumn<Playlist, String>("Playlists");
		//playlistCol.setCellValueFactory(new PropertyValueFactory<Playlist, String>("titleName"));
		
		playlistTable.setItems(playlists);
		playlistTable.setEditable(true);
        playlistTable.setPrefWidth(200);
        playlistTable.setPlaceholder(new Label("No Content In List"));
        playlistTable.setCellFactory(cb -> {
            ListCell<Playlist> cell = new TextFieldListCell<>();
            final ContextMenu rightClickPlaylist = new ContextMenu();
			MenuItem deleteList = new MenuItem("Delete Playlist");
			deleteList.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					
					if(cell.getItem()==queue)
					{
						deleteList.setText("Clear Queue");
						queue.intList.clear();
						queue.playlist.clear();
						
					}
					if(cell.getItem()!=masterLibrary && cell.getItem()!=queue)
					{
						
						deleteList.setText("Delete Playlist");
						playlists.remove(playlists.indexOf(cell.getItem()));
						musicTable.setItems(masterLibrary.songList);
						playlistTable.getSelectionModel().select(0);
					}
					
				}
			});
			MenuItem newPlaylist = new MenuItem("New Playlist");
			newPlaylist.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					TextFieldPopUp playlistMaker = new TextFieldPopUp();
            			playlistMaker.start(playlistStage);
				}
			});
			MenuItem editName = new MenuItem("Edit the playlist");
			editName.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					//rightClickAddToPlaylist.getItems().add(queue.pMenuItem);

				}

			});
			rightClickPlaylist.getItems().addAll(deleteList, newPlaylist, editName);
			cell.addEventHandler(MouseEvent.MOUSE_CLICKED,
					new EventHandler<MouseEvent>() {
						@Override public void handle(MouseEvent e) {
							if (e.getButton() == MouseButton.SECONDARY)
								rightClickPlaylist.show(cell, e.getScreenX(), e.getScreenY());
						}
					});
            cell.setOnDragOver(ev -> {
                if (ev.getGestureSource() != cell
                && ev.getDragboard().hasString()) {
                    ev.acceptTransferModes(TransferMode.COPY);
                }
                ev.consume();
            });

            cell.setOnMouseClicked(ev -> {
                try{

                    if (ev.getClickCount() == 1) {
                    	if(cell.getItem()!=masterLibrary)
                    	{
                    		ObservableList<Song> tempPlaylist = FXCollections.observableArrayList();
                    		for(Integer song : cell.getItem().playlist)
                    		{
                    			tempPlaylist.add(masterLibrary.get(song));
                    		}
                    		musicTable.setItems(tempPlaylist);
                    		currentPlaylist=playlists.indexOf(cell.getItem());
                    	}
                    	else{
                    		musicTable.setItems(masterLibrary.songList);
                    		currentPlaylist=0;
                    	}
                    		
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            cell.setOnDragDropped(ev -> {
                Dragboard db = ev.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    //cell.setText("Got Item: " + db.getString());
                    success = true;
                }
                cell.getItem().addAll(dragged);
                ev.setDropCompleted(success);
                ev.consume();
                System.out.println(cell.getItem().playlist);
                saveData();
            });
            return cell;

        });
        playlistTable.setOnEditCommit(new EventHandler<ListView.EditEvent<Playlist>>() {
            @Override
            public void handle(ListView.EditEvent<Playlist> t) {
                playlistTable.getItems().get(t.getIndex()).setTitleName(t.getNewValue().name);
                System.out.println("setOnEditCommit");
            }

        });

        musicTable.setOnMouseClicked(new EventHandler<MouseEvent>() { //click
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) { // double click

                    ObservableList<Integer> selected = FXCollections.observableArrayList();
                    for(Song song : musicTable.getSelectionModel().getSelectedItems())
                    {
                    		selected.add(song.getOrder());
                    }
                    dragged = selected;
                    if (selected != null) {
                        System.out.println("select : " + selected);

                    }
                }
            }
        });

        musicTable.setOnDragDetected(new EventHandler<MouseEvent>() { //drag
            @Override
            public void handle(MouseEvent event) {
                // drag was detected, start drag-and-drop gesture

                ObservableList<Integer> selected = FXCollections.observableArrayList();
                for(Song song : musicTable.getSelectionModel().getSelectedItems())
                {
                		selected.add(song.getOrderName());
                }
                dragged = selected;
                if (dragged != null) {

                    Dragboard db = musicTable.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString("a Song");
                    db.setContent(content);
                    event.consume();
                }
            }
        });

        musicTable.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                // data is dragged over the target
                Dragboard db = event.getDragboard();
                if (event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });
		
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
		TableColumn<Song, String> timeCol = new TableColumn<Song, String>("Total time");
		timeCol.setCellValueFactory(new PropertyValueFactory<Song, String>("timeName"));
		timeCol.setPrefWidth(100);
		TableColumn<Song, String> kindCol = new TableColumn<Song, String>("Kind");
		kindCol.setCellValueFactory(new PropertyValueFactory<Song, String>("kindName"));
		kindCol.setPrefWidth(100);

		TableColumn<Song, String> orderCol = new TableColumn<Song, String>("Order");
		orderCol.setCellValueFactory(new PropertyValueFactory<Song, String>("orderName"));

		Menu menuEffect = new Menu("Theme");

		MenuItem lightSkin = new MenuItem("Light Theme");
		lightSkin.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				 musicScene.getStylesheets().remove(currentTheme);
	                musicScene.getStylesheets().add(lightMetro);
	                currentTheme = lightMetro;
	                EqualizerView.setTheme(currentTheme);
			}
		});
		MenuItem flatThemeMenu = new MenuItem("Flat Theme");
		flatThemeMenu.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				 musicScene.getStylesheets().remove(currentTheme);
	                musicScene.getStylesheets().add(flatTheme);
	                currentTheme = flatTheme;
	                EqualizerView.setTheme(currentTheme);
			}
		});
		MenuItem mistThemeMenu = new MenuItem("Mist Theme");
		mistThemeMenu.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				 musicScene.getStylesheets().remove(currentTheme);
	                musicScene.getStylesheets().add(mistSilver);
	                currentTheme = mistSilver;
	                EqualizerView.setTheme(currentTheme);
			}
		});
		MenuItem windowsThemeMenu = new MenuItem("Windows Theme");
		windowsThemeMenu.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				 musicScene.getStylesheets().remove(currentTheme);
	                musicScene.getStylesheets().add(windowsTheme);
	                currentTheme = windowsTheme;
	                EqualizerView.setTheme(currentTheme);
			}
		});

		//No Effects menu
		MenuItem noEffects = new MenuItem("Dark Theme");

		noEffects.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
                musicScene.getStylesheets().remove(currentTheme);
                musicScene.getStylesheets().add(darkMetro);
                currentTheme = darkMetro;
                EqualizerView.setTheme(currentTheme);
			}
		});
		menuEffect.getItems().addAll(lightSkin, windowsThemeMenu, mistThemeMenu, flatThemeMenu, noEffects);
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
		
		MenuItem showEQ = new MenuItem("Equalizer");
		showEQ.setAccelerator(KeyCombination.keyCombination("Meta+E"));
		showEQ.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				if(equalizerView.initialized==false)
				{
					equalizerView = new EqualizerView(masterLibrary.currentlyPlaying(), mediaControl, eqStage);
					
					eqStage.show();
				}
				else
				{
					try
					{
						equalizerView.setBottom(mediaControl);
						equalizerView.show(eqStage);
						
					}
					catch(IllegalStateException e)
					{
						e.printStackTrace();
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		});
		MenuItem exit = new MenuItem("Exit");
		exit.setAccelerator(KeyCombination.keyCombination("Meta+Q"));
		exit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				
				saveData();
				System.exit(0);
			}
		});
		menuFile.getItems().addAll(menuAddFiles, makeNewPlaylist, exit);
		menuView.getItems().addAll(showEQ,menuEffect);

		musicTable.getColumns().addAll(orderCol, songCol, artistCol, albumCol, genreCol, timeCol, playsCol, kindCol);
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
						play(row.getItem());
					}
				});
				MenuItem rightClickPause = new MenuItem("Pause Song");
				rightClickPause.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {
						masterLibrary.songList.get(masterLibrary.indexOfPlaying);
					}
				});
				MenuItem remove = new MenuItem("Remove Song");
				remove.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {
						masterLibrary.remove(row.getItem());
					}
				});
				MenuItem showInfo = new MenuItem("Show song info");
				showInfo.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						// TODO Auto-generated method stub
						GetInfoPopUp getInfo = new GetInfoPopUp(row.getItem());
						try {
							getInfo.start(new Stage());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				});
				Menu rightClickAddToPlaylist = new Menu("Add to a playlist");
				rightClickAddToPlaylist.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {
						for(Playlist playlist : playlists)
						{
							MenuItem playlistMenu = new MenuItem(playlist.getName());
							playlistMenu.setOnAction(new EventHandler<ActionEvent>() {
								public void handle(ActionEvent e) {
									playlist.add(row.getItem().getOrder());
								}
							});
							rightClickAddToPlaylist.getItems().add(playlistMenu);
						}
						

					}

				});
				rightClickEvents.getItems().add(rightClickPlay);
				rightClickEvents.getItems().add(rightClickPause);
				rightClickEvents.getItems().add(remove);
				rightClickEvents.getItems().add(showInfo);
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
						
						play(row.getItem());
						equalizerView.show(eqStage);
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
	public static void stopAll()
	{
		for(Song song : masterLibrary.songList)
		{
			song.stop();
		}
	}
	public static void play(Song s) {
		//setCurrentlyPlaying(s);
		stopAll();
		masterLibrary.indexOfPlaying=s.getOrder();
		setMediaControl(s);
		equalizerView.setSong(s);
		equalizerView.setBottom(mediaControl);
			System.out.println("Set the bottom!");
		
		//masterLibrary.play(masterLibrary.indexOf(s));

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
	private static void setMediaControl(Song s) {
		if(mediaControl.getSong()!=null)
		{
			mediaControl.stop();
		}
		
		mediaControl = new PlayerControl();
		musicBorder.setBottom(mediaControl);
		System.out.println("Setting Media Viewer");
		mediaControl = new PlayerControl(s);
		musicBorder.setBottom(mediaControl);
	}
	public void saveData()
	{
		File file = new File("library.xml");
		XStream xStream = new XStream();
		xStream.alias("playlist", Playlist.class);
		ObjectOutputStream objectOutputStream;
		xStream.omitField(Playlist.class, "titleName");
		xStream.omitField(Playlist.class, "playlist");
		try {
			objectOutputStream = xStream.createObjectOutputStream(new FileOutputStream(file));

			for(Playlist playlist :playlists)
			{	
				if(playlist!=masterLibrary)
				{
					playlist.prepData();
					objectOutputStream.writeObject(playlist);
				}
			
			}
			objectOutputStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}
		
	}
	public void getData()
	{
		File file = new File("library.xml");
		XStream xStream = new XStream();
		xStream.alias("playlist", Playlist.class);
		try {
			ObjectInputStream objectInputStream = xStream.createObjectInputStream(new FileInputStream(file));
			Object newPlaylist = null;
            
            while ((newPlaylist = objectInputStream.readObject()) != null) {
                
                if (newPlaylist instanceof Playlist) {
                
                    System.out.println(((Playlist)newPlaylist).toString());
                   Playlist tempPlaylist = new Playlist((Playlist)newPlaylist);
                   if(isAlreadyThere(tempPlaylist)!=true)
                   {
                	   	playlists.add(tempPlaylist);
                   }
                    
                }
                
            }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean isAlreadyThere(Playlist playlist)
	{
		boolean returned=false;
			for(Playlist playlist2 : playlists)
			{
				if(playlist.getName()==playlist2.getName())
				{
					returned=true;
				}
			}
		return(returned);
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
}