import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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
	private MediaPlayer mediaPlayer;
	private Media media;
	
	public PlayBackApplication(){
		
	}
	
	
	
	/**
	 * Sets up play, stop, pause buttons and actions.
	 * 
	 */
	public Scene snapPlayBackSetup(DefaultTableModel trackModel, JTable trackTable, ArrayList<Track> activeTrackList, ArrayList<Track> selectedTracks){
		//TODO change image loading to be platform independent
		
		//Set up buttons
		InputStream playImageInput = getClass().getResourceAsStream("Default_Play.png");
		Image playButtonImage = new Image(playImageInput);//new File("Default_Play.png").toURI().toString());
		
		InputStream pauseImageInput = getClass().getResourceAsStream("Default_Pause.png");
	    Image pauseButtonImage = new Image(pauseImageInput);//new File("Default_Pause.png").toURI().toString());
	    
	    InputStream stopImageInput = getClass().getResourceAsStream("Default_Stop.png");
	    Image stopButtonImage = new Image(stopImageInput);//new File("Default_Stop").toURI().toString());
	    
	    ImageView imageViewPlay = new ImageView(playButtonImage);
	    ImageView imageViewPause = new ImageView(pauseButtonImage);
	    ImageView imageViewStop = new ImageView(stopButtonImage);
	    
	    Button playButton = new Button(); //new Button("play",imageViewPlay);
	    playButton.setGraphic(imageViewPlay);
	    
	    Button pauseButton = new Button();//new Button("pause", imageViewPause);
	    pauseButton.setGraphic(imageViewPause);
	    
	    Button stopButton = new Button();//new Button("stop", imageViewStop);
	    stopButton.setGraphic(imageViewStop);
	    
	    HBox mediaBar = new HBox(5.0);
	    mediaBar.setPadding(new Insets(5, 10, 5, 10));
        mediaBar.setAlignment(Pos.CENTER_LEFT);
        
        
		//Add action listeners to button
        EventHandler<ActionEvent> evtHandler = (ActionEvent e) -> {
				int songRow = trackTable.getSelectedRow(); //first index selected if multiple selected
				
				System.out.println("PlayBackApplication: trackTable has selected row int: " + songRow);
				
				if(songRow == -1){//-1 returned by trackTable if no row selected
					return;//Nothing selected
				}
				String songAbsPath = selectedTracks.get(0).getTrackLocation();//(String)((Vector)trackModel.getDataVector().elementAt(songRow)).elementAt(0);
				System.out.println("PlayBackApplication: activeTrackList: " + activeTrackList.get(songRow).getTrackLocation());
				System.out.println("PlayBackApplication: selectedTracks: " + selectedTracks.get(0).getTrackLocation());
				System.out.println("PlayBackApplicaiton: trackModel got: " + songAbsPath);
				
				
				//Sanitize absolute path for compliance with JavaFX
				songAbsPath = new File(songAbsPath).toURI().toString();
				
				System.out.println("PlayBackApplication: Song being played after URI then toString (string from trackModel): " + songAbsPath);
				
				this.media = new Media(songAbsPath);
				this.mediaPlayer = new MediaPlayer(media);
				
				//if(){}
				mediaPlayer.setOnReady(new Runnable(){
					@Override
					public void run(){
						//System.out.println(media.getDuration());
						//System.out.println(media.getDuration().toMinutes());
					
				
						MediaPlayer.Status status = mediaPlayer.getStatus();
				
				
						switch(status){
							case UNKNOWN:
								System.out.println("PlayBackApplication: snapPlayBackSetup (source): " + e.getSource() + ": (status)" + status);
								
								//mediaPlayer.play();
								//mediaPlayer.stop();
								break;
								
							case READY:
								System.out.println("PlayBackApplication: snapPlayBackSetup (source): " + e.getSource() + ": (status)" + status);
								mediaPlayer.play();
								break;
								
							case PAUSED:
								System.out.println("The new status: in paused" + mediaPlayer.getStatus());
								System.out.println("PlayBackApplication: snapPlayBackSetup (source): " + e.getSource() + ": (status)" + status);
								if(e.getSource().equals(playButton))
									mediaPlayer.play();
								else if(e.getSource().equals(stopButton))
									mediaPlayer.stop();
								break;
								
							case PLAYING:
								System.out.println("The new status: in playing  " + mediaPlayer.getStatus());
								System.out.println("PlayBackApplication: snapPlayBackSetup (source): " + e.getSource() + ": (status)" + status);
								if(e.getSource().equals(pauseButton))
									mediaPlayer.pause();
								else if(e.getSource().equals(stopButton))
									mediaPlayer.stop();
								break;
								
							case STALLED:
								System.out.println("PlayBackApplication: snapPlayBackSetup (source): " + e.getSource() + ": (status)" + status);
								if(e.getSource().equals(pauseButton))
									mediaPlayer.pause();
								else if(e.getSource().equals(playButton))
									mediaPlayer.play();
								break;
								
							case STOPPED:
								System.out.println("PlayBackApplication: snapPlayBackSetup (source): " + e.getSource() + ": (status)" + status);
								if(e.getSource().equals(playButton))
									mediaPlayer.play();
								if(e.getSource().equals(pauseButton))
									mediaPlayer.pause();	
				}
				/*
				if(e.getSource().equals(playButton)){
					System.out.println("playButton");
					(this.mediaPlayer).play();
				}
				else if(e.getSource().equals(stopButton)){
					System.out.println("stopButton");
					(this.mediaPlayer).stop();
				}
				else if(e.getSource().equals(pauseButton)){
					System.out.println("pauseButton");
					(this.mediaPlayer).pause();
				}
				*/
					}
				});
				
		};//END lambda expression
        
		playButton.setOnAction(evtHandler);
		mediaBar.getChildren().add(playButton);
		
		stopButton.setOnAction(evtHandler);
		mediaBar.getChildren().add(stopButton);
		
		pauseButton.setOnAction(evtHandler);
		mediaBar.getChildren().add(pauseButton);
		
		BorderPane borderPane = new BorderPane(mediaBar);
		Scene scene = new Scene(borderPane);
			 
		return scene;
	}
	
}
