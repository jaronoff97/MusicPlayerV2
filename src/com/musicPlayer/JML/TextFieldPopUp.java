package com.musicPlayer.JML;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TextFieldPopUp extends Application{

    public static String password = "";
    public static GridPane textFieldGrid = new GridPane();
    public static Scene textFieldScene = new Scene(textFieldGrid, 300, 275);
    public static Button textFieldButton = new Button("Done?");
    public static Text textFieldTitle = new Text("Text field");
    public static Label textFieldLabel = new Label("Text field");
    public static TextField textField = new TextField();
    public static final Text loginTarget = new Text();
    public static String returnString = "";
    private static boolean completed = false;


    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Make a playlist");
        makeTextBox("Name your playlist!");
        textFieldGrid.setAlignment(Pos.CENTER);
        textFieldGrid.setHgap(10);
        textFieldGrid.setVgap(10);
        textFieldGrid.setPadding(new Insets(25, 25, 25, 25));
        textFieldGrid.add(textField,1,1);
        textFieldGrid.add(textFieldButton,2,1);
        textFieldTitle.setId("welcome-text");
        
        textFieldButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
            	returnString = makeString(textField.getCharacters());
            	MusicPlayer.playlists.add(new Playlist(returnString));
            	completed=true;
            	//System.out.println(returnString);
            	quit(primaryStage);


        }
    });
        textFieldGrid.add(textFieldTitle, 0, 0, 2, 1);
       
        primaryStage.setScene(textFieldScene);

        //textFieldScene.getStylesheets().add(LoginWindow.class.getResource("CSS_Skins/Styling.css").toExternalForm());
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.setWidth(400);
        primaryStage.setHeight(200);
        primaryStage.show();
    }
    public void makeTextBox(String t){
    	textFieldTitle=new Text(t);
    	textFieldLabel=new Label(t);


    }
    public static String makeString(CharSequence r) {
        final StringBuilder sb = new StringBuilder(r.length());
        sb.append(r);
        //System.out.println("String is: " + sb.toString());
        return sb.toString();
    }
    public static void quit(Stage s){
    	s.close();
    	//System.exit(0);
    }
    public static boolean complete(){
    	return(completed);
    }

}
