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
	private String autoPlay_NextTrackLocation;
	
	private Track currTrack;
	private Track nextTrack;
	private Track selectedTrack;

	
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
        //TODO remove buttons, Thao has better buttons
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
	 * Note: trackTable table model assumed to be: "Name", "Artist", "Album", "Date Added"
	 * 
	 */
	public Scene snapPlayBackSetup(DefaultTableModel trackModel, JTable trackTable, ArrayList<Track> selectedTracks, ArrayList<Track> activeTrackList){
        
        /*****************Play Even Handler*************************/
		//Listener attached to time slider for when user seeks. Track time gets updated too.
		//Listener attached to volume slider for when user sets it. Audio set to user specified volume.
        EventHandler<ActionEvent> playEvent = (ActionEvent e) -> {
        	songRow = trackTable.getSelectedRow();
        	if(songRow < 0)//-1 Value if nothing selected from trackTable
    			return;
        	
        	//Need to make sure to get column at "Name"
        	//Returns the title
        	String trackSelected = (String)trackTable.getModel().getValueAt(songRow, 0);
        	System.out.println("\n PlayBackApplication: trackSelected using JTable: " + trackSelected + "\n");
        	
        	selectedTrack = selectedTracks.get(0);
        	System.out.println();
        	System.out.println("PlayBackApplicaiton: selectedTrack: " + selectedTrack.getTitle());
        	
        	if(nextTrack != null) {
        		selectedTrack = nextTrack;//if media ends by itself nextTrack is set
        		System.out.println("PlayBackApplication: selectedTrack is switched to nextTrack: " + selectedTrack.getTitle() + "\n" );
        		
        	   	nextTrack = null;//will be set-up onEndOfMedia if track ends by itself	
        	}
        	
        	String selectedTrackLocation = selectedTrack.getTrackLocation();
        	String selectedTrackURI = (new File(selectedTrackLocation)).toURI().toString();
        	
        	if(mediaPlayer == null){//First Ever Playback
        		System.out.println("PlayBackApplication: First Ever Playback");
        		media = new Media(selectedTrackURI);
        		mediaPlayer = new MediaPlayer(media);
        		
        		//Set below two lines after each media creation operation
        		currTrack = selectedTrack;
        		currTrackLocation = selectedTrackLocation;
        		
        		mediaPlayer.setOnReady( () ->{
					duration = mediaPlayer.getMedia().getDuration();//returns duration in seconds
					updateValues();//duration must be set before updateValues can be called
					
					
	            	//Makes time slider move during track playback
	        		//Add this block anywhere a new media object created in order to monitor its current playing time
	                mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
	                	updateValues();//Update time slider to represent current time of track playback and volume
	                	
	                });
	                
	                
					mediaPlayer.play();

					//updateValues();
					System.out.println("PlayBackApplication: MediaControl: mediaPlayer READY");
        			}
        		);
            	

                
                //Allows user to move time slider to seek on track 
                timeSlider.valueProperty().addListener((Observable ov) -> {
                	//System.out.println("PlayBackApplication: timeSlider change event");
                    if (timeSlider.isValueChanging()) {
                    	System.out.println("PlayBackApplication: timeSlider value is changing");
                        // multiply duration by percentage calculated by slider position
                        if (duration != null) {
                            mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                        }
                        updateValues();//Update time slider to represent current time of track playback and volume
                        System.out.println("PlayBackApplication: MediaControl: timeSlider manually set: after seek call updateValues is called");
                        
                    }
                });
                
        		mediaPlayer.setOnEndOfMedia( ()->{
        				setNextTrackPlay(activeTrackList); 
        				playButton.fire();
        			
        			}
        		);//END setOnEndOfMedia
        		
               
        		
        	}//END if(mediaPlayer == null){First Ever Playback

        	
        	MediaPlayer.Status status = mediaPlayer.getStatus();
        	System.out.println("PlayBackApplication: playEvent: mediaPlayer status poll: " + status);
        	switch(status){
        	
        		case PLAYING://Applicable action here is user wanting to play a different song. Not what's currently playing since it's what's currently playing
        			
        			System.out.println();
        			System.out.println("PlayBackApplication: mediaPlayer's current time: " + mediaPlayer.getCurrentTime());
        			System.out.println("PlayBackApplication: mediaPlayer's stop time: " + mediaPlayer.getStopTime());
        			System.out.println("PlayBackApplicaiton: currTrackLocation: " + currTrackLocation);
        			System.out.println("PlayBackApplication: selectedTrackLocation: " + selectedTrackLocation);
        			System.out.println("PlayBackApplication: currTrackLocation equals selectedTrackLocaiton: " + currTrackLocation.equals(selectedTrackLocation));
        			System.out.println();
        			
        			//When song finishes status is still PLAYING. If user hits play again on same song. It should be played again since its finished.
        			if(currTrackLocation.equals(selectedTrackLocation) && (mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) ){
        				//mediaPlayer.seek(mediaPlayer.getStartTime());
        				System.out.println();
        				System.out.println("PlayBackApplication: User playing current song that ended");
        				
        				mediaPlayer.stop();//PLAYING state doesn't transfer into PLAYING state. Have to stop first to then transfer to PLAYING state.
        				mediaPlayer.seek(mediaPlayer.getStartTime());
        				updateValues();
        				
        				mediaPlayer.play();
        				//updateValues();
        				System.out.println("PlayBackApplication: MediaControl: PLAYING 1: UpdateValues running");
        			}
        			
        			//User selects a new song and hits play button while a current song is playing which isn't the selected song 
        			if( !(currTrackLocation.equals(selectedTrackLocation)) ){
        				System.out.println("\nPlayBackApplication: MediaControl: PLAYING 2 UpdateValues running\n");
        				mediaPlayer.stop();//current song
        				System.out.println("PlayBackApplication: selected new track. Status of previous after stop call: " + mediaPlayer.getStatus() + "\n");
        				
        				mediaPlayer.seek(mediaPlayer.getStartTime());
        				updateValues();
        				
        				media = new Media(selectedTrackURI);
        				mediaPlayer = new MediaPlayer(media);
        				
        				currTrack = selectedTrack;
        				currTrackLocation = selectedTrackLocation;
        				
        				System.out.println("PlayBackApplication: Playing new song: MediaPlayer status: " + mediaPlayer.getStatus());
        				
        				mediaPlayer.setOnReady( () ->{
	        					duration = mediaPlayer.getMedia().getDuration();
	        					updateValues();
	        					
	            	        	//For time slider
	            	            mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
	            	            	updateValues();//Update time slider to represent current time of track playback and volume
	            	            	
	            	            });

	            				mediaPlayer.play();
	            				//updateValues();
	            		        //System.out.println("PlayBackApplication: MediaControl: setOnReady call: UpdateValues running");
        					}
        				);

        	            
                		mediaPlayer.setOnEndOfMedia( ()->{
            				setNextTrackPlay(activeTrackList);  
            				playButton.fire();

            			}
            		);//END setOnEndOfMedia

        				
        				//return;
        			}
        			
        			//Reached if play pressed on song currently playing 
        			System.out.println("PlayBackApplication: in PLAYING: outside of conditionals: at locatio before break");
        			break;
        			
        		case STOPPED:
        			
        			//User playing a different song than previous song
        			if( !(currTrackLocation.equals(selectedTrackLocation)) ){
        				media = new Media(selectedTrackURI);
        				//duration = mediaPlayer.getMedia().getDuration();
                		mediaPlayer = new MediaPlayer(media);
                		
                		currTrack = selectedTrack;
                		currTrackLocation = selectedTrackLocation;
                		
                		mediaPlayer.setOnReady( () ->{
                			duration = mediaPlayer.getMedia().getDuration();
                			updateValues();
                			
            	        	//For time slider
            	            mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            	            	updateValues();//Update time slider to represent current time of track playback and volume
            	            	
            	            });
            	            
            				mediaPlayer.play();
            				}
        				);//END setOnReady()
                		
                		mediaPlayer.setOnEndOfMedia( ()->{
            				setNextTrackPlay(activeTrackList);
            				playButton.fire();

            			}
            		);//END setOnEndOfMedia

        				System.out.println("PlayBackApplication: MediaControl: STOPPED 1: UpdateValues running");
        				System.out.println("PlayBackApplication: playEvent: mediaPlayer status after poll: " + mediaPlayer.getStatus());
        				return;
        			}
        			//User wanting to replay same song
        			mediaPlayer.seek(mediaPlayer.getStartTime());
        			updateValues();
        			mediaPlayer.play();
        			System.out.println("PlayBackApplication: MediaControl: STOPPED 2: UpdateValues running");
        			break;
        			
        		case PAUSED:
        			//User wants to resume && there's time left on the song
        			if( currTrackLocation.equals(selectedTrackLocation) && !(mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) ){
        				mediaPlayer.play();
        				//updateValues();
        				System.out.println("PlayBackApplication: MediaControl: PAUSED 1: UpdateValues running");
        				System.out.println("PlayBackApplication: playEvent: mediaPlayer status after poll: " + mediaPlayer.getStatus());
        				return;
        			}
        			//User wants to play a different song
        			//else if( !(mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) )
        			media = new Media(selectedTrackURI);
        			mediaPlayer = new MediaPlayer(media);
        			
        			currTrack = selectedTrack;
        			currTrackLocation = selectedTrackLocation;
        			
        			mediaPlayer.setOnReady( () ->{
        				duration = mediaPlayer.getMedia().getDuration();
        				updateValues();
        				
        	        	//For time slider
        	            mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
        	            	updateValues();//Update time slider to represent current time of track playback and volume
        	            	
        	            });
        	            
        				mediaPlayer.play();
        		        //updateValues();
        	            
        		        System.out.println("PlayBackApplication: MediaControl: setOnReady call: UpdateValues running");
        				}
    				);
        			
            		mediaPlayer.setOnEndOfMedia( ()->{
        				setNextTrackPlay(activeTrackList);  
        				playButton.fire();

        			}
        		);//END setOnEndOfMedia
            		

    				System.out.println("PlayBackApplication: MediaControl: PAUSED 2: UpdateValues running");
        			break;
        					
        			
        	}
        	System.out.println("PlayBackApplication: playEvent: mediaPlayer status after poll: " + mediaPlayer.getStatus());

        	
        };//END PlayEvent handler
        
        EventHandler<ActionEvent> stopEvent = (ActionEvent e)->{
        	mediaPlayer.stop();//Stop playing currently playing track
        	mediaPlayer.seek(mediaPlayer.getStartTime());
        	        	
        	updateValues();
        	System.out.println("PlayBackApplication: MediaControl: stopEvent: UpdateValues running");
		};
		
		EventHandler<ActionEvent> pauseEvent = (ActionEvent e)->{
        	mediaPlayer.pause();
        	//updateValues();
        	System.out.println("PlayBackApplication: MediaControl: pauseEvent: UpdateValues running");
		};

		playButton.setOnAction(playEvent);
		stopButton.setOnAction(stopEvent);
		pauseButton.setOnAction(pauseEvent);
		
		Scene scene = new Scene(mediaBar, sceneColor);
		//scene.setFill(Paint.valueOf("#202020"));
		return scene;
	}
	
	
	/*
	private MediaPlayer mediaPlayer;
	private Media media;
	private String currTrackLocation;
	private String autoPlay_NextTrackLocation;
	*/
	/**
	 * Updates nextTrack, currTrack fields
	 * @param window.getActiveTrackList()
	 */
	private void setNextTrackPlay(ArrayList<Track> activeTrackList){
		
		//Needs to account for wrap arounds
		//Set currTrack, nextTrack
		int currTrackIndex = activeTrackList.indexOf(currTrack);
		int nextTrackIndex = ++currTrackIndex;
		
		if((nextTrackIndex) >= activeTrackList.size()){ 
			System.out.println("\n PlayBackApplication: setNextTrackPlay: wrapping around \n");
			nextTrackIndex = 0;
		}
		
		nextTrack = activeTrackList.get(nextTrackIndex);
		System.out.println("\n PlayBackApplication: new and improved activeTrackList logs next Track as: " + activeTrackList.get(nextTrackIndex).getTitle() + "\n");
	}
	
	
	
	//TODO optimize
	private String formatTime(Duration elapsed, Duration duration) {
		//System.out.println();
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        //System.out.println("PlayBackApplication: formatTime: intElapsed: " + intElapsed);
        int elapsedHours = intElapsed / (60 * 60);
        //System.out.println("PlayBackApplication: formatTime: elapsedHours: " + elapsedHours);
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
	 * Makes time slider change while track plays back.
	 * Updates time display of track playback.
	 * Sets volume slider to represent current audio volume of track.
	 */
	private void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null && duration != null) {
        	//System.out.println();
			
			Duration currentTime = mediaPlayer.getCurrentTime();//returns duration in milliseconds 
			
			playTime.setText(formatTime(currentTime, duration));
			timeSlider.setDisable(duration.isUnknown());						//Below: allows user to change time slider to seek on track
			if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
				
				//Setting value of slider to a proportional value depending on current time out of total duration
				//Multiplies that with 100 to get proportion and sets value of slider to that porportion with 100% being the total duration
				timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
			}
			if (!volumeSlider.isValueChanging()) {
				volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume() * 100));
			}
       }
   }
	
}
