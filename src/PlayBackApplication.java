import java.io.File;
import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URI;
import java.util.ArrayList;
//import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

//import javafx.application.Application;
//import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
//import javafx.embed.swing.JFXPanel;
//import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.media.MediaPlayer;
//import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
//import javafx.scene.paint.Paint;
import javafx.scene.media.Media;

//import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
//import javafx.scene.layout.Region;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;



final class PlayBackApplication{//Inherently package private	
	
	private HBox mediaBar = new HBox(5.0);
	
	private MediaPlayer mediaPlayer;
	private Media media;
	private String currTrackLocation;
	
	private Label playTime;
	private Label timeLabel;
	private Label volumeLabel; 
	private Duration duration;
	private Slider timeSlider;
	private Slider volumeSlider;

	Button playButton;
	Button pauseButton;
	Button stopButton;
	
	int songRow;
	
	Color sceneColor;
	//private boolean firstPlay;
	
	//TODO manage resources to avoid memory leak
	//TODO Adjust components on Hbox to move with resizing
	public PlayBackApplication(){
	    mediaBar.setPadding(new Insets(5, 10, 5, 10));
        mediaBar.setAlignment(Pos.CENTER_LEFT);
        
		//Set up buttons
		InputStream playImageInput = getClass().getResourceAsStream("Default_Play.png");
		Image playButtonImage = new Image(playImageInput);
		InputStream pauseImageInput = getClass().getResourceAsStream("Default_Pause.png");
		Image pauseButtonImage = new Image(pauseImageInput);
		InputStream stopImageInput = getClass().getResourceAsStream("Default_Stop.png");
		Image stopButtonImage = new Image(stopImageInput);

		ImageView imageViewPlay = new ImageView(playButtonImage);
		ImageView imageViewPause = new ImageView(pauseButtonImage);
		ImageView imageViewStop = new ImageView(stopButtonImage);

		playButton = new Button(); 
		playButton.setGraphic(imageViewPlay);
		pauseButton = new Button();
		pauseButton.setGraphic(imageViewPause);
		stopButton = new Button();
		stopButton.setGraphic(imageViewStop);
		
		playTime = new Label();
		playTime.setMinWidth(Control.USE_PREF_SIZE);
        timeLabel = new Label("Time");
        timeLabel.setMinWidth(Control.USE_PREF_SIZE);
		timeSlider = new Slider();
		timeSlider.setMinWidth(Control.USE_PREF_SIZE);
		timeSlider.setMaxWidth(100);
		HBox.setHgrow(timeSlider, Priority.ALWAYS);

        volumeLabel = new Label("Volume");
        volumeLabel.setMinWidth(Control.USE_PREF_SIZE);
        volumeSlider = new Slider();
        volumeSlider.setMinWidth(Control.USE_PREF_SIZE);
        //volumeSlider.setMaxWidth(100);//Control.USE_PREF_SIZE);
        HBox.setHgrow(volumeSlider, Priority.SOMETIMES);
        //volumeSlider.valueProperty().addListener((Observable ov) -> {
        //});
        
        //Set volume of media player to what user sets
        volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (volumeSlider.isValueChanging()) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
            }
        });
        
      
        
		mediaBar.getChildren().add(timeLabel);
		mediaBar.getChildren().add(timeSlider);
		mediaBar.getChildren().add(playTime);
		mediaBar.getChildren().add(playButton);
		mediaBar.getChildren().add(pauseButton);
		mediaBar.getChildren().add(stopButton);
		mediaBar.getChildren().add(volumeLabel);
		mediaBar.getChildren().add(volumeSlider);
		
		//firstPlay = true;
		sceneColor = Color.DIMGRAY;
	}//END Constructor
	
	
	
	/**
	 * Sets up play, stop, pause buttons and actions.
	 * 
	 */
	public Scene snapPlayBackSetup(DefaultTableModel trackModel, JTable trackTable, ArrayList<Track> selectedTracks){
        
        /*****************Play Even Handler*************************/
		//Listener attached to time slider for when user seeks. Track time gets updated too.
		//Listener attached to volume slider for when user sets it. Audio set to user specified volume.
        EventHandler<ActionEvent> playEvent = (ActionEvent e) -> {
        	songRow = trackTable.getSelectedRow();
        	if(songRow < 0)//-1 Value if nothing selected from trackTable
    			return;
        	String selectedTrackLocation = selectedTracks.get(0).getTrackLocation();
        	String selectedTrackURI = (new File(selectedTrackLocation)).toURI().toString();
        	
        	if(mediaPlayer == null){//First Ever Playback
        		
        		media = new Media(selectedTrackURI);
        		mediaPlayer = new MediaPlayer(media);
        		
        		System.out.println("playbackapplication: mediacontrol: first playback set-up");
        		
        		//This is only time mediaplyer will be at unknown state.
        		//Make sure player is at ready state before leaving this conditional
        		mediaPlayer.setOnReady( () ->{
					duration = mediaPlayer.getMedia().getDuration();
					updateValues();
					mediaPlayer.play();

					updateValues();
					System.out.println("PlayBackApplication: MediaControl: setOnReady call: UpdateValues running");
        			}
        		);
        		
        		/*
        		mediaPlayer.setOnEndOfMedia( ()->{
        			//Play next song in track list currently displayed
        			String currTrackSelected = selectedTracks.get(0).getTitle();
        			//String nexTracktoSelect = trackTable.getSelectionModel()
        			
        			
        			}
        				
        				
        		);
        		*/
        		currTrackLocation = selectedTrackLocation;
            	
            	//For time slider
                mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
                	updateValues();//Update time slider to represent current time of track playback and volume
                	
                });
                
                timeSlider.valueProperty().addListener((Observable ov) -> {
                	System.out.println("PlayBackApplication: timeSlider change event");
                    if (timeSlider.isValueChanging()) {
                    	System.out.println("PlayBackApplication: timeSlider value is changing");
                        // multiply duration by percentage calculated by slider position
                        if (duration != null) {
                            mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                        }
                        updateValues();//Update time slider to represent current time of track playback and volume
                        System.out.println("PlayBackApplication: MediaControl: timeSlider set-up: UpdateValues running");
                        
                    }
                });
        	}//END if

        	
        	MediaPlayer.Status status = mediaPlayer.getStatus();
        	System.out.println("PlayBackApplication: playEvent: mediaPlayer status poll: " + status);
        	switch(status){
        	
        		case PLAYING://Applicable action here is user wanting to play a different song. Not what's currently playing since it's what's currently playing
        			
        			
        			System.out.println("PlayBackApplication: mediaPlayer's current time: " + mediaPlayer.getCurrentTime());
        			System.out.println("PlayBackApplication: mediaPlayer's stop time: " + mediaPlayer.getStopTime());
        			System.out.println("PlayBackApplicaiton: currTrackLocation: " + currTrackLocation);
        			System.out.println("PlayBackApplication: selectedTrackLocation: " + selectedTrackLocation);
        			System.out.println("PlayBackApplication: currTrackLocation equals selectedTrackLocaiton: " + currTrackLocation.equals(selectedTrackLocation));
        			
        			//When song finishes status is still PLAYING. If user hits play again on same song. It should be played again since its finished.
        			if(currTrackLocation.equals(selectedTrackLocation) && (mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) ){
        				//mediaPlayer.seek(mediaPlayer.getStartTime());
        				System.out.println();
        				System.out.println("PlayBackApplication: User playing current song that ended");
        				
        				mediaPlayer.stop();//PLAYING state doesn't transfer into PLAYING state. Have to stop first to then transfer to PLAYING state.
        				mediaPlayer.seek(mediaPlayer.getStartTime());
        				updateValues();
        				
        				mediaPlayer.play();
        				updateValues();
        				System.out.println("PlayBackApplication: MediaControl: PLAYING 1: UpdateValues running");
        			}
        			
        			if( !(currTrackLocation.equals(selectedTrackLocation)) ){
        				mediaPlayer.stop();//current song
        				mediaPlayer.seek(mediaPlayer.getStartTime());
        				duration = mediaPlayer.getMedia().getDuration();
        				updateValues();
        				
        				media = new Media(selectedTrackURI);
        				mediaPlayer = new MediaPlayer(media);
        				System.out.println("PlayBackApplication: Playing new song: MediaPlayer status: " + mediaPlayer.getStatus());
        				mediaPlayer.setOnReady( () ->{
        					duration = mediaPlayer.getMedia().getDuration();
        					updateValues();
            				mediaPlayer.play();
            				updateValues();
            		        //System.out.println("PlayBackApplication: MediaControl: setOnReady call: UpdateValues running");
            				}
        				);
        				//duration = mediaPlayer.getMedia().getDuration();
                		//mediaPlayer = new MediaPlayer(media);
                		
                		
        				currTrackLocation = selectedTrackLocation;
        				//mediaPlayer.play();
        				//updateValues();
        				System.out.println("PlayBackApplication: MediaControl: PLAYING 2 UpdateValues running");
        				return;
        			}
        			//Reached if play pressed on song currently playing 
        			//updateValues();
        			break;
        			
        		case STOPPED:
        			//TODO Optimize by consolidating repetitive code
        			//User playing a different song than previous song
        			if( !(currTrackLocation.equals(selectedTrackLocation)) ){
        				media = new Media(selectedTrackURI);
        				//duration = mediaPlayer.getMedia().getDuration();
                		mediaPlayer = new MediaPlayer(media);
                		
                		mediaPlayer.setOnReady( () ->{
                			duration = mediaPlayer.getMedia().getDuration();
                			updateValues();
            				mediaPlayer.play();
            		        updateValues();
            		        //System.out.println("PlayBackApplication: MediaControl: setOnReady call: UpdateValues running");
            				}
        				);
                		
                		
        				currTrackLocation = selectedTrackLocation;
        				//mediaPlayer.play();
        				//updateValues();
        				System.out.println("PlayBackApplication: MediaControl: STOPPED 1: UpdateValues running");
        				System.out.println("PlayBackApplication: playEvent: mediaPlayer status after poll: " + mediaPlayer.getStatus());
        				return;
        			}
        			//User wanting to replay same song
        			mediaPlayer.seek(mediaPlayer.getStartTime());
        			updateValues();
        			mediaPlayer.play();
        			updateValues();
        			System.out.println("PlayBackApplication: MediaControl: STOPPED 2: UpdateValues running");
        			break;
        			
        		case PAUSED:
        			//User wants to resume && there's time left on the song
        			if( currTrackLocation.equals(selectedTrackLocation) && !(mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) ){
        				mediaPlayer.play();
        				updateValues();
        				System.out.println("PlayBackApplication: MediaControl: PAUSED 1: UpdateValues running");
        				System.out.println("PlayBackApplication: playEvent: mediaPlayer status after poll: " + mediaPlayer.getStatus());
        				return;
        			}
        			//User wants to play a different song
        			//else if( !(mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) )
        			media = new Media(selectedTrackURI);
        			mediaPlayer = new MediaPlayer(media);
        			
        			mediaPlayer.setOnReady( () ->{
        				duration = mediaPlayer.getMedia().getDuration();
        				updateValues();
        				mediaPlayer.play();
        		        updateValues();
        		        System.out.println("PlayBackApplication: MediaControl: setOnReady call: UpdateValues running");
        				}
    				);
        			//duration = mediaPlayer.getMedia().getDuration();
            		
            		
    				currTrackLocation = selectedTrackLocation;
    				//mediaPlayer.play();
    				//updateValues();
    				System.out.println("PlayBackApplication: MediaControl: PAUSED 2: UpdateValues running");
        			break;
        					
        			
        	}
        	System.out.println("PlayBackApplication: playEvent: mediaPlayer status after poll: " + mediaPlayer.getStatus());
        	//For time slider
            mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            	updateValues();//Update time slider to represent current time of track playback and volume
            	
            });
        	
        };//END PlayEvent handler
        
        EventHandler<ActionEvent> stopEvent = (ActionEvent e)->{
        	mediaPlayer.stop();
        	mediaPlayer.seek(mediaPlayer.getStartTime());
        	        	
        	updateValues();
        	System.out.println("PlayBackApplication: MediaControl: stopEvent: UpdateValues running");
		};
		
		EventHandler<ActionEvent> pauseEvent = (ActionEvent e)->{
        	mediaPlayer.pause();
        	updateValues();
        	System.out.println("PlayBackApplication: MediaControl: pauseEvent: UpdateValues running");
		};

		playButton.setOnAction(playEvent);
		stopButton.setOnAction(stopEvent);
		pauseButton.setOnAction(pauseEvent);
		
		Scene scene = new Scene(mediaBar, sceneColor);
		//scene.setFill(Paint.valueOf("#202020"));
		return scene;
	}
	
	
	
	//TODO optimize
	private String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,
                        durationMinutes, durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",
                        elapsedMinutes, elapsedSeconds);
            }
        }
    }

	/**
	 * Makes time slider change with progressive track playback.
	 * Updates time display of track playback.
	 * Sets volume slider to represent current audio volume of track.
	 */
	private void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null && duration != null) {
			//System.out.println("PlayBackApplication: MediaControl: UpdateValues running");
			
			Duration currentTime = mediaPlayer.getCurrentTime();
			playTime.setText(formatTime(currentTime, duration));
			timeSlider.setDisable(duration.isUnknown());
			if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
				timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
			}
			if (!volumeSlider.isValueChanging()) {
				volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume() * 100));
			}
       }
   }
	
}
