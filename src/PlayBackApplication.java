import java.io.File;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.JTable;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.Media;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.geometry.Insets;
import javafx.geometry.Pos;



final class PlayBackApplication{//Inherently package private	
	
	
	/**
	 * Sets up play, stop, pause buttons and actions.
	 * 
	 */
	public static Scene snapPlayBackSetup(ArrayList<Track> activeTrackList, JTable trackTable){
		//TODO change image loading to be platform independent
		
		//Set up buttons
		Image playButtonImage = new Image(new File("Default_Play.png").toURI().toString());
	    Image pauseButtonImage = new Image(new File("Default_Pause.png").toURI().toString());
	    Image stopButtonImage = new Image(new File("Default_Stop").toURI().toString());
	    
	    ImageView imageViewPlay = new ImageView(playButtonImage);
	    ImageView imageViewPause = new ImageView(pauseButtonImage);
	    ImageView imageViewStop = new ImageView(stopButtonImage);
	    
	    Button playButton = new Button("play",imageViewPlay);
	    Button pauseButton = new Button("pause", imageViewPause);
	    Button stopButton = new Button("stop", imageViewStop);
	    
	    HBox mediaBar = new HBox(5.0);
	    mediaBar.setPadding(new Insets(5, 10, 5, 10));
        mediaBar.setAlignment(Pos.CENTER_LEFT);
        
        
		//Add action listeners to button
       // MediaPlayer mediaPlayer;
        
        EventHandler<ActionEvent> evtHandler = (ActionEvent) -> {
			//EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>(){
			//@Override
			//public void handle(ActionEvent event){
				int songRow = trackTable.getSelectedRow(); //first index selected if multiple selected
				if(songRow == -1){
					return;//Nothing selected
				}
				String songAbsPath = activeTrackList.get(songRow).getTrackLocation();
				
				//Sanitize absolute path for compliance with JavaFX
				songAbsPath = new File(songAbsPath).toURI().toString();
				
				Media media = new Media(songAbsPath);
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				
				MediaPlayer.Status status = mediaPlayer.getStatus();
	            if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED){ 

	            	System.out.println("PlayBackApplication: snapPlayBackSetup: (play)MediaPlayer.Status before play= " + status.toString());
	            	mediaPlayer.play();
	            	System.out.println("PlayBackApplication: snapPlayBackSetup: (play)MediaPlayer.Status after play = " + status.toString());
	                //return;
	            }
	            else if(status == MediaPlayer.Status.READY)
	            	System.out.println("PlayBackApplication: snapPlayBackSetup: (play)MediaPlayer.Status = " + status.toString());
	            	//mediaPlayer.play();
			//}
					
						
		//};	
		//return eventHandler;
		};
        
		playButton.setOnAction( evtHandler);
		mediaBar.getChildren().add(playButton);
		
		stopButton.setOnAction( new EventHandler<ActionEvent>(){
			
			@Override
			public void handle(ActionEvent event){
				int songRow = trackTable.getSelectedRow(); //first index selected if multiple selected
				if(songRow == -1){
					return;//Nothing selected
				}
				String songAbsPath = activeTrackList.get(songRow).getTrackLocation();
				
				//Sanitize absolute path for compliance with JavaFX
				songAbsPath = new File(songAbsPath).toURI().toString();
				
				Media media = new Media(songAbsPath);
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				
				MediaPlayer.Status status = mediaPlayer.getStatus();
	            if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED){ 

	            	System.out.println("PlayBackApplication: snapPlayBackSetup: (stop)MediaPlayer.Status before stop = " + status.toString());
	            	mediaPlayer.stop();
	            	System.out.println("PlayBackApplication: snapPlayBackSetup: (stop)MediaPlayer.Status after stop = " + status.toString());
	                //return;
	            }
	            else if(status == MediaPlayer.Status.PLAYING || status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.STALLED)
	            	System.out.println("PlayBackApplication: snapPlayBackSetup: (stop)MediaPlayer.Status = " + status.toString());
	            	//mediaPlayer.stop();
			}
						
		});
		mediaBar.getChildren().add(stopButton);
		
		pauseButton.setOnAction( new EventHandler<ActionEvent>(){
			
			@Override
			public void handle(ActionEvent event){
				int songRow = trackTable.getSelectedRow(); //first index selected if multiple selected
				if(songRow == -1){
					return;//Nothing selected
				}
				String songAbsPath = activeTrackList.get(songRow).getTrackLocation();
				
				//Sanitize absolute path for compliance with JavaFX
				songAbsPath = new File(songAbsPath).toURI().toString();
				
				Media media = new Media(songAbsPath);
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				
				MediaPlayer.Status status = mediaPlayer.getStatus();
	            if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED){ 

	            	System.out.println("PlayBackApplication: snapPlayBackSetup: (stop)MediaPlayer.Status before pause= " + status.toString());
	            	mediaPlayer.pause();
	            	System.out.println("PlayBackApplication: snapPlayBackSetup: (stop)MediaPlayer.Status after pause = " + status.toString());
	                //return;
	            }
	            else if(status == MediaPlayer.Status.PLAYING || status == MediaPlayer.Status.STOPPED || status == MediaPlayer.Status.STALLED)
	            	System.out.println("PlayBackApplication: snapPlayBackSetup: (stop)MediaPlayer.Status = " + status.toString());
	            	//mediaPlayer.pause();
			}
						
		});
		mediaBar.getChildren().add(pauseButton);
		
		BorderPane borderPane = new BorderPane(mediaBar);
		Scene scene = new Scene(borderPane);
			 
		return scene;
	}
	
	/**
	 * JavaFX doesn't work with backslash.
	 * Strips FILE protocol. Replaces back-slash with forward-slash
	 */
	//TODO make unicode safe 
	private static String sanitizePath(String absPath){
		String newAbsPath = "";
		for(int i=3; i < absPath.length(); i++){
			char c = absPath.charAt(i);
			if(c == 92){//"\"
				newAbsPath += "/";
			}else{
				newAbsPath += c;
			}
			
		}
		System.out.println("PlayBackApplication: sanitizePath call: " + newAbsPath);
		return newAbsPath;	
	}
	
}
