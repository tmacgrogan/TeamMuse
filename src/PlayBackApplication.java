import java.io.File;

import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URI;
import java.util.ArrayList;
//import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
//import javafx.embed.swing.JFXPanel;
//import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
//import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Paint;
import javafx.scene.media.Media;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
//import javafx.scene.layout.Region;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;




final class PlayBackApplication{//Inherently package private	
	ImageView imageViewPlay;
	ImageView imageViewPlay_Hover;

	ImageView imageViewPause;
	ImageView imageViewPause_Hover;
	
	ImageView imageViewStop;
	ImageView imageViewStop_Hover;
	
	private BorderPane mediaBar = new BorderPane();
	private BorderPane innerMediaBar = new BorderPane(); 
	
	private MediaPlayer mediaPlayer;
	private Media media;
	private String currTrackLocation;
	
	private Track currTrack;
	private Track nextTrack;
	private Track selectedTrack;

	private Label trackTitle;
	private Label playTime;
	private Label timeLabel;
	private Label volumeLabel;
	
	StackPane trackTitleDisplay;
	
	private Duration duration;
	
	private Slider timeSlider;
	private Slider volumeSlider;

	Button playButton;
	Button pauseButton;
	Button stopButton;
	
	int songRow;
	
	Color sceneColor;
	
	//TODO manage resources to avoid memory leak
	//TODO Adjust components to move with resizing
	public PlayBackApplication(){
	    //mediaBar.setPadding(new Insets(5, 10, 5, 10));
        
		/**********************************Set up buttons*****************************************/
		InputStream playImageInput = getClass().getResourceAsStream("Default_Play.png");
		Image playButtonImage = new Image(playImageInput);
		InputStream playImageHover = getClass().getResourceAsStream("Default_Play_Hover.png");
		Image playButtonHover = new Image(playImageHover);
		
		InputStream pauseImageInput = getClass().getResourceAsStream("Default_Pause.png");
		Image pauseButtonImage = new Image(pauseImageInput);
		InputStream pauseImageHover = getClass().getResourceAsStream("Default_Pause_Hover.png");
		Image pauseButtonHover = new Image(pauseImageHover);
		
		InputStream stopImageInput = getClass().getResourceAsStream("Default_Stop.png");
		Image stopButtonImage = new Image(stopImageInput);
		InputStream stopImageHover = getClass().getResourceAsStream("Default_Stop_Hover.png");
		Image stopButtonHover = new Image(stopImageHover);
		/*****************************************************************************************/
		imageViewPlay = new ImageView(playButtonImage);
		imageViewPlay_Hover = new ImageView(playButtonHover);
		
		imageViewPause = new ImageView(pauseButtonImage);
		imageViewPause_Hover = new ImageView(pauseButtonHover);
		
		imageViewStop = new ImageView(stopButtonImage);
		imageViewStop_Hover = new ImageView(stopButtonHover);
		
		
		//Circle buttonCircle = new Circle();
		double size = 30;
		playButton = new Button(); 
		playButton.setGraphic(imageViewPlay);
		playButton.setPadding(Insets.EMPTY);
		playButton.setShape( new Circle() );
		playButton.setPrefSize(size, size);
		playButton.setMaxHeight(size);
		playButton.setMaxWidth(size);
		playButton.setMinHeight(size);
		playButton.setMinWidth(size);
		
		//For highlighting of play button on start-up prior to track play
		playButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
			if(newValue.booleanValue())
				playButton.setGraphic(imageViewPlay_Hover);
			else
				playButton.setGraphic(imageViewPlay);	
		});
		////////////////////////////////////////////////////////////////////
		
		stopButton = new Button();
		stopButton.setGraphic(imageViewStop);
		stopButton.setPadding(Insets.EMPTY);
		stopButton.setShape( new Circle() );
		//Change button, title display, highlight effects when button hovered
		stopButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
			if(newValue.booleanValue())
				stopButton.setGraphic(imageViewStop_Hover);
			else
				stopButton.setGraphic(imageViewStop);
		});

		//////////////////////////////////////////////////////////////////////
		HBox buttons = new HBox(playButton, stopButton);
		buttons.setAlignment(Pos.BOTTOM_LEFT);
		innerMediaBar.setLeft(buttons);
		
		
		playTime = new Label();
		playTime.setMinWidth(Control.USE_PREF_SIZE);
        timeLabel = new Label("Time");
        timeSlider = new Slider();
		timeSlider.setMinWidth(Control.USE_PREF_SIZE);
		HBox timeSliderHBox = new HBox();
        timeSliderHBox.getChildren().addAll(timeLabel,timeSlider, playTime);
        timeSliderHBox.setAlignment(Pos.CENTER);
		trackTitle = new Label();
		VBox trackAndTimeDisplay = new VBox();
		trackAndTimeDisplay.getChildren().addAll(trackTitle, timeSliderHBox);
		trackAndTimeDisplay.setAlignment(Pos.CENTER);
		innerMediaBar.setCenter(trackAndTimeDisplay);
		

        volumeLabel = new Label("Volume");
        volumeLabel.setMinWidth(Control.USE_PREF_SIZE);
        volumeSlider = new Slider();
        volumeSlider.setMinWidth(Control.USE_PREF_SIZE);
        volumeSlider.setMaxWidth(70);
        VBox volumeSliderVBox = new VBox();
		volumeSliderVBox.getChildren().addAll(volumeLabel,volumeSlider);
		volumeSliderVBox.setAlignment(Pos.BOTTOM_LEFT);
		innerMediaBar.setRight(volumeSliderVBox);
        
        //Set volume of media player to what user sets
        volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (volumeSlider.isValueChanging()) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
            }
        });
        
        mediaBar.setBottom(innerMediaBar);
        mediaBar.setPrefSize(10, 10);
		sceneColor = Color.ALICEBLUE;
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
        	//String trackSelected = (String)trackTable.getModel().getValueAt(songRow, 0);
        	//System.out.println("\n PlayBackApplication: trackSelected using JTable: " + trackSelected);
        	
        	selectedTrack = selectedTracks.get(0);
        	System.out.println("\nPlayBackApplicaiton: selectedTrack: " + selectedTrack.getTitle());
        	
        	//nextTrack filled in endOfMedia callback
        	if(nextTrack != null) {
        		selectedTrack = nextTrack;//if media ends by itself nextTrack is set
        		System.out.println("\nPlayBackApplication: selectedTrack is switched to nextTrack: " + selectedTrack.getTitle() );
        		
        	   	nextTrack = null;//will be set-up onEndOfMedia if track ends by itself	
        	}
        	
        	String selectedTrackLocation = selectedTrack.getTrackLocation();
        	String selectedTrackURI = (new File(selectedTrackLocation)).toURI().toString();
        	
        	if(mediaPlayer == null){//First Ever Playback
        		System.out.println("\nPlayBackApplication: First Ever Playback");
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
						if(playButton.isHover()) playButton.setGraphic(imageViewPause_Hover);
						else playButton.setGraphic(imageViewPause);
						
						//this.playButton.setGraphic(imageViewPause);
						this.trackTitle.setText("Now Playing: " + currTrack.getTitle());
						//Change button, title display, highlight effects when button hovered
						playButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
							if(newValue.booleanValue())
								playButton.setGraphic(imageViewPause_Hover);
							else
								playButton.setGraphic(imageViewPause);
						});
						//////////////////////////////////////////////////////////////////////
				        //TODO Use above block when changing button symbols so they highlight
				        
						System.out.println("\nPlayBackApplication: MediaControl: mediaPlayer READY");
        			}
        		);
            	

                //Allows user to move time slider to seek on track. Only need to set once for duration of global timeSlider
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
        		
        		case PLAYING://Symbol shows pause
        			//Applicable action here is user wanting to play a different song. Not what's currently playing 
        			//If status is PLAYING, button has pause symbol
        			//pause currently playing music
        			mediaPlayer.pause();
        			if(playButton.isHover()) playButton.setGraphic(imageViewPlay_Hover);
        			else playButton.setGraphic(imageViewPlay);
        			
        			//this.playButton.setGraphic(imageViewPlay);
        			//Change button, title display, highlight effects when button hovered
					playButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
						if(newValue.booleanValue())
							playButton.setGraphic(imageViewPlay_Hover);
						else
							playButton.setGraphic(imageViewPlay);
					});
					//////////////////////////////////////////////////////////////////////
					
        			System.out.println();
        			System.out.println("PlayBackApplication: mediaPlayer's current time: " + mediaPlayer.getCurrentTime());
        			System.out.println("PlayBackApplication: mediaPlayer's stop time: " + mediaPlayer.getStopTime());
        			System.out.println("PlayBackApplicaiton: currTrackLocation: " + currTrackLocation);
        			System.out.println("PlayBackApplication: selectedTrackLocation: " + selectedTrackLocation);
        			System.out.println("PlayBackApplication: currTrackLocation equals selectedTrackLocaiton: " + currTrackLocation.equals(selectedTrackLocation));
        			System.out.println();
        			
        			if( (mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) ){
        				
        				System.out.println("\n PlayBackApplication: PLAYING: Song ended and next track should play: " + selectedTrack.getTitle() );
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
	            				if(playButton.isHover()) playButton.setGraphic(imageViewPause_Hover);
	            				else playButton.setGraphic(imageViewPause);
	            					
	            				//this.playButton.setGraphic(imageViewPause);
	    						this.trackTitle.setText("Now Playing: " + currTrack.getTitle());
	    						//Change button, title display, highlight effects when button hovered
	    						playButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
	    							if(newValue.booleanValue())
	    								playButton.setGraphic(imageViewPause_Hover);
	    							else
	    								playButton.setGraphic(imageViewPause);
	    						});
	    						//////////////////////////////////////////////////////////////////////
        					}
        				);

        	            
                		mediaPlayer.setOnEndOfMedia( ()->{
            					setNextTrackPlay(activeTrackList);  
            					playButton.fire();
            				}
                		);//END setOnEndOfMedia
        			}
        			
        			//Reached if play pressed on song currently playing 
        			System.out.println("PlayBackApplication: in PLAYING: paused pressedon song currently playing");
        			break;
        			
        		case STOPPED://symbols are play, stop
        			
        			//User playing a different song than previous song
        			if( !(currTrackLocation.equals(selectedTrackLocation)) ){
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
	            				if(playButton.isHover()) playButton.setGraphic(imageViewPause_Hover);
	            				else playButton.setGraphic(imageViewPause); 
	            					
	        					//this.playButton.setGraphic(imageViewPause);
	        					this.trackTitle.setText("Now Playing: " + currTrack.getTitle());
	        					//Change button, title display, highlight effects when button hovered
	    						playButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
	    							if(newValue.booleanValue())
	    								playButton.setGraphic(imageViewPause_Hover);
	    							else
	    								playButton.setGraphic(imageViewPause);
	    						});
	    						//////////////////////////////////////////////////////////////////////
            				}
        				);//END setOnReady()
                		
                		mediaPlayer.setOnEndOfMedia( ()->{
	            				setNextTrackPlay(activeTrackList);
	            				playButton.fire();
            				}
                		);//END setOnEndOfMedia

        				System.out.println("\nPlayBackApplication: MediaControl: STOPPED 1");
        				System.out.println("\nPlayBackApplication: playEvent: mediaPlayer status after poll: " + mediaPlayer.getStatus());
        				return;
        			}
        			
        			//User wanting to replay same song
        			mediaPlayer.seek(mediaPlayer.getStartTime());
        			updateValues();
        			
        			mediaPlayer.play();
        			if(playButton.isHover()) playButton.setGraphic(imageViewPause_Hover);
        			else playButton.setGraphic(imageViewPause);
        			
					//this.playButton.setGraphic(imageViewPause);
					this.trackTitle.setText("Now Playing: " + currTrack.getTitle());
					//Change button, title display, highlight effects when button hovered
					playButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
						if(newValue.booleanValue())
							playButton.setGraphic(imageViewPause_Hover);
						else
							playButton.setGraphic(imageViewPause);
					});
					//////////////////////////////////////////////////////////////////////
					
        			System.out.println("\nPlayBackApplication: MediaControl: STOPPED 2: User want to replay same song");
        			break;
        			
        		case PAUSED://Symbol is set to play
        			//User wants to resume && there's time left on the song
        			//if( currTrackLocation.equals(selectedTrackLocation) ){//&& !(mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) ){
        				mediaPlayer.play();
        				if(playButton.isHover()) playButton.setGraphic(imageViewPause_Hover);
        				else playButton.setGraphic(imageViewPause);
        			
						//this.playButton.setGraphic(imageViewPause);
						//Change button, title display, highlight effects when button hovered
						playButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
							if(newValue.booleanValue())
								playButton.setGraphic(imageViewPause_Hover);
							else
								playButton.setGraphic(imageViewPause);
						});
						//////////////////////////////////////////////////////////////////////
						
						mediaPlayer.setOnEndOfMedia( ()->{
								System.out.println("\n PlayBackApplicaiton: PAUSED: On end of media and status is: " +  mediaPlayer.getStatus());
            					setNextTrackPlay(activeTrackList);
            					playButton.fire();
        					}
						);//END setOnEndOfMedia
						
						
        				System.out.println();
        				System.out.println("PlayBackApplication: PAUSED: User wants to resume && there's time left on the song");
        				System.out.println("PlayBackApplication: PAUSED: mediaPlayer status after poll: " + mediaPlayer.getStatus());
        				System.out.println();

    				System.out.println("PlayBackApplication: MediaControl: PAUSED 2");
        			break;
        					
        			
        	}//END Switch stmt
        	System.out.println("PlayBackApplication: playEvent: mediaPlayer status after poll: " + mediaPlayer.getStatus());

        	
        };//END PlayEvent handler
        
        EventHandler<ActionEvent> stopEvent = (ActionEvent e)->{
        	mediaPlayer.stop();//Stop playing currently playing track
        	
        	//this.playButton.setGraphic(imageViewPlay);
			this.trackTitle.setText("");
			playButton.setGraphic(imageViewPlay);
			//Change button, title display, highlight effects when button hovered
			playButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
				if(newValue.booleanValue())
					playButton.setGraphic(imageViewPlay_Hover);
				else
					playButton.setGraphic(imageViewPlay);
			});
			//////////////////////////////////////////////////////////////////////
			
			
        	mediaPlayer.seek(mediaPlayer.getStartTime());
        	        	
        	updateValues();
        	System.out.println("PlayBackApplication: MediaControl: stopEvent: UpdateValues running");
		};

		playButton.setOnAction(playEvent);
		stopButton.setOnAction(stopEvent);
		
		Scene scene = new Scene(mediaBar, sceneColor);
		//scene.setFill(Paint.valueOf("#202020"));
		return scene;
	}
	
	/**
	 * Sets currTrack and currTrackLocation
	 * @param currentTrack
	 */
	private void setCurrentTrack(Track currentTrack){
		this.currTrack = currentTrack;
		this.currTrackLocation = currentTrack.getTrackLocation();
	}
	

	/**
	 * Updates nextTrack
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
