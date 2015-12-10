import java.awt.Dimension;

import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import javafx.scene.Scene;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import javafx.util.Duration;

/**
 * Sets up control buttons and field components for audio playback of music. 
 * Note: does not extend JavaFX Application because meant for combination with Java Swing from a driver class
 * @author Bim
 *
 */
final class PlayBackApplication{//Inherently package private	
	private Button playButton;
	private Button stopButton;
	
	private BorderPane innerMediaBar = new BorderPane();
	private BorderPane mediaBar = new BorderPane();
	
	private Dimension parentPanelSize;
	private Duration duration;
	
	private ImageView imageViewPause;
	private ImageView imageViewPause_Hover;
	private ImageView imageViewPlay;
	private ImageView imageViewPlay_Hover;
	private ImageView imageViewStop;
	private ImageView imageViewStop_Hover;
	
	private Label playTimeLabel;
	private Label timeLabel;
	private Label trackTitleLabel;
	private Label volumeLabel;
	private Label artistTitleLabel;
	
	/**
	 * Media object that wraps track via its absolute path
	 */
	private Media media;
	
	/**
	 * MediaPlayer object that wraps the Media object for playback
	 */
	private MediaPlayer mediaPlayer;

	private Slider timeSlider;
	private Slider volumeSlider;
	
	private String currTrackLocation;
	
	private final Timeline trackLabel_timeline = new Timeline();
	private final Timeline artistLabel_timeline = new Timeline();
	
	private Track currTrack;
	private Track nextTrack;
	private Track selectedTrack;

	private int songRow;
	
	
	//TODO manage resources to avoid memory leak
	//TODO Adjust components to move with resizing
	/**
	 * Sets up controls in play back application panel.
	 * @param parentPanelSize 
	 */
	public PlayBackApplication(Dimension parentPanelSize){
		this.parentPanelSize = parentPanelSize;

		BackgroundFill bgFill = new BackgroundFill(Color.rgb(64,64,64), CornerRadii.EMPTY, Insets.EMPTY);
		Background bg = new Background(bgFill);

		/**********************************Set up button images************************************/
		InputStream playImageInput = getClass().getResourceAsStream("playOn.png");
		Image playButtonImage = new Image(playImageInput);
		InputStream playImageHover = getClass().getResourceAsStream("playOn_Hover.png");
		Image playButtonHover = new Image(playImageHover);
		
		InputStream pauseImageInput = getClass().getResourceAsStream("pauseOn.png");
		Image pauseButtonImage = new Image(pauseImageInput);
		InputStream pauseImageHover = getClass().getResourceAsStream("pauseOn_Hover.png");
		Image pauseButtonHover = new Image(pauseImageHover);

		InputStream stopImageInput = getClass().getResourceAsStream("stopOn.png");
		Image stopButtonImage = new Image(stopImageInput);
		InputStream stopImageHover = getClass().getResourceAsStream("stopOn_Hover.png");
		Image stopButtonHover = new Image(stopImageHover);
		
		imageViewPlay = new ImageView(playButtonImage);
		imageViewPlay_Hover = new ImageView(playButtonHover);
		
		imageViewPause = new ImageView(pauseButtonImage);
		imageViewPause_Hover = new ImageView(pauseButtonHover);
		
		imageViewStop = new ImageView(stopButtonImage);
		imageViewStop_Hover = new ImageView(stopButtonHover);
		/*****************************************************************************************/
		
		double maxSize = 5;
		double minSize  = 1;
		playButton = new Button(); 
		playButton.setGraphic(imageViewPlay);
		playButton.setBackground(bg);
		playButton.setPadding(Insets.EMPTY);
		playButton.setShape( new Circle() );
		//playButton.setPrefSize(5, 5);
		playButton.setMaxHeight(maxSize);
		playButton.setMaxWidth(maxSize);
		playButton.setMinHeight(minSize);
		playButton.setMinWidth(minSize);
		
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
		//stopButton.setPadding(Insets.EMPTY);
		//stopButton.setShape( new Circle() );
		stopButton.setMaxHeight(maxSize);
		stopButton.setMaxWidth(maxSize);
		stopButton.setMinHeight(minSize);
		stopButton.setMinWidth(minSize);
		
		//Change button, title display, highlight effects when button hovered
		stopButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
			if(newValue.booleanValue())
				stopButton.setGraphic(imageViewStop_Hover);
			else
				stopButton.setGraphic(imageViewStop);
		});
		//////////////////////////////////////////////////////////////////////

		playTimeLabel = new Label();
		playTimeLabel.setMinWidth(Control.USE_PREF_SIZE);
		playTimeLabel.setTextFill(Color.DODGERBLUE);
		
        timeLabel = new Label("Time");
        timeSlider = new Slider();
        timeSlider.setPrefWidth(Control.USE_COMPUTED_SIZE);
		timeSlider.setMinWidth(Control.USE_PREF_SIZE);

		artistTitleLabel = new Label();
		artistTitleLabel.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		artistTitleLabel.setMaxWidth(Control.USE_PREF_SIZE);
		artistTitleLabel.setTextFill(Color.WHITE);
		
		trackTitleLabel = new Label();
		trackTitleLabel.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		trackTitleLabel.setTextFill(Color.WHITE);
        trackTitleLabel.setOnMouseEntered((MouseEvent e ) ->{
        	this.stopLabelAnimation(artistTitleLabel, trackTitleLabel);
        });
        trackTitleLabel.setOnMouseExited((MouseEvent e) ->{
        	if(this.mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)){
        		this.setLabelAnimation(artistTitleLabel, trackTitleLabel);
        	}
        });
		
		
        volumeLabel = new Label("Volume");
        volumeLabel.setMinWidth(Control.USE_PREF_SIZE);
        
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(Control.USE_COMPUTED_SIZE);
        volumeSlider.setMinWidth(Control.USE_PREF_SIZE);
        volumeSlider.setOnMouseEntered((MouseEvent e ) ->{
        	this.stopLabelAnimation(artistTitleLabel, trackTitleLabel);
        });
        volumeSlider.setOnMouseExited((MouseEvent e) ->{
        	if(this.mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)){
        		this.setLabelAnimation(artistTitleLabel, trackTitleLabel);
        	}
        });
        //Set volume of media player to what user sets
        volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (volumeSlider.isValueChanging()) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
            }
        });
        
		layoutComponents(timeSlider,volumeSlider,
				  playButton,stopButton,artistTitleLabel,trackTitleLabel,
				  playTimeLabel,timeLabel,volumeLabel,innerMediaBar);
		
		this.innerMediaBar.setBackground(bg);
	}//END Constructor
	
	/**
	 * Retrieve the Media object that represents the current Track object
	 * @return Media 
	 */
	public Media getMedia(){
		return media;
	}
	
	/**
	 * 
	 * Retrieve the MediaPlayer object that wraps the Media object and controls playback
	 * @return MediaPlayer
	 */
	public MediaPlayer getMediaPlayer(){
		return mediaPlayer;
	}
	

	/**
	 * Set the Media object within play back application
	 * @param media
	 * @return void
	 */
	public void setMedia(Media media){
		this.media = media;
	}
	
	/**
	 * Set the MediaPlayer controller within play back application
	 * @param mediaPlayer
	 * @return void
	 */
	public void setMediaPlayer(MediaPlayer mediaPlayer){
		this.mediaPlayer = mediaPlayer;
	}
	

	
	/**
	 * Sets up play, stop, pause buttons and actions.
	 * 
	 * Note: trackTable table model assumed to be: "Name", "Artist", "Album", "Date Added"
	 * @param trackTable container for music tracks
	 * @param selectedTracks holds currently selected music tracks at any given time
	 * @param activeTrackList music tracks that user sees currently in the trackTable
	 * 
	 * @return Scene Visual representation of components and controls in play back application
	 */
	public Scene snapPlayBackSetup(JTable trackTable, ArrayList<Track> selectedTracks, ArrayList<Track> activeTrackList){
		
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
						
						this.playButton.setGraphic(imageViewPause);
        				this.artistTitleLabel.setText(currTrack.getArtist());
        				
						this.trackTitleLabel.setText(currTrack.getTitle());
						
						this.setLabelAnimation(this.artistTitleLabel,this.trackTitleLabel);
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
        			
        			this.playButton.setGraphic(imageViewPlay);
        			//Change button, title display, highlight effects when button hovered
					playButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
						if(newValue.booleanValue())
							playButton.setGraphic(imageViewPlay_Hover);
						else
							playButton.setGraphic(imageViewPlay);
					});
					//////////////////////////////////////////////////////////////////////
					
					/*
        			System.out.println();
        			System.out.println("PlayBackApplication: mediaPlayer's current time: " + mediaPlayer.getCurrentTime());
        			System.out.println("PlayBackApplication: mediaPlayer's stop time: " + mediaPlayer.getStopTime());
        			System.out.println("PlayBackApplicaiton: currTrackLocation: " + currTrackLocation);
        			System.out.println("PlayBackApplication: selectedTrackLocation: " + selectedTrackLocation);
        			System.out.println("PlayBackApplication: currTrackLocation equals selectedTrackLocaiton: " + currTrackLocation.equals(selectedTrackLocation));
        			System.out.println();
        			*/
					
        			if( (mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) ){
        				
        				//System.out.println("\n PlayBackApplication: PLAYING: Song ended and next track should play: " + selectedTrack.getTitle() );
        				media = new Media(selectedTrackURI);
        				mediaPlayer = new MediaPlayer(media);
        				
        				currTrack = selectedTrack;
        				currTrackLocation = selectedTrackLocation;
        				
        				//System.out.println("PlayBackApplication: Playing new song: MediaPlayer status: " + mediaPlayer.getStatus());
        				
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
	            					
	            				this.playButton.setGraphic(imageViewPause);
	            				this.artistTitleLabel.setText(currTrack.getArtist());
	    						this.trackTitleLabel.setText(currTrack.getTitle());
	    						this.setLabelAnimation(this.artistTitleLabel,this.trackTitleLabel);
	    						
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
        			//System.out.println("PlayBackApplication: in PLAYING: paused pressedon song currently playing");
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
	            					
	        					this.playButton.setGraphic(imageViewPause);
	            				this.artistTitleLabel.setText(currTrack.getArtist());
	    						this.trackTitleLabel.setText(currTrack.getTitle());
	    						this.setLabelAnimation(this.artistTitleLabel,this.trackTitleLabel);
	    						
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

//        				System.out.println("\nPlayBackApplication: MediaControl: STOPPED 1");
//        				System.out.println("\nPlayBackApplication: playEvent: mediaPlayer status after poll: " + mediaPlayer.getStatus());
        				return;
        			}
        			
        			//User wanting to replay same song
        			mediaPlayer.seek(mediaPlayer.getStartTime());
        			updateValues();
        			
        			mediaPlayer.play();
        			if(playButton.isHover()) playButton.setGraphic(imageViewPause_Hover);
        			else playButton.setGraphic(imageViewPause);
        			
					this.playButton.setGraphic(imageViewPause);
    				this.artistTitleLabel.setText(currTrack.getArtist());
					this.trackTitleLabel.setText(currTrack.getTitle());
					this.setLabelAnimation(this.artistTitleLabel,this.trackTitleLabel);
					
					//Change button, title display, highlight effects when button hovered
					playButton.hoverProperty().addListener( (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
						if(newValue.booleanValue())
							playButton.setGraphic(imageViewPause_Hover);
						else
							playButton.setGraphic(imageViewPause);
					});
					//////////////////////////////////////////////////////////////////////
					
        			//System.out.println("\nPlayBackApplication: MediaControl: STOPPED 2: User want to replay same song");
        			break;
        			
        		case PAUSED://Symbol is set to play
        			//User wants to resume && there's time left on the song
        			//if( currTrackLocation.equals(selectedTrackLocation) ){//&& !(mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime())) ){
        				mediaPlayer.play();
        				if(playButton.isHover()) playButton.setGraphic(imageViewPause_Hover);
        				else playButton.setGraphic(imageViewPause);
        			
						this.playButton.setGraphic(imageViewPause);
						
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
						
						/*
        				System.out.println();
        				System.out.println("PlayBackApplication: PAUSED: User wants to resume && there's time left on the song");
        				System.out.println("PlayBackApplication: PAUSED: mediaPlayer status after poll: " + mediaPlayer.getStatus());
        				System.out.println();

    				System.out.println("PlayBackApplication: MediaControl: PAUSED 2");
    				*/
        			break;
        					
        			
        	}//END Switch stmt
        	//System.out.println("PlayBackApplication: playEvent: mediaPlayer status after poll: " + mediaPlayer.getStatus());

        	
        };//END PlayEvent handler
        
        EventHandler<ActionEvent> stopEvent = (ActionEvent e)->{
        	mediaPlayer.stop();//Stop playing currently playing track
        	
        	this.playButton.setGraphic(imageViewPlay);
			this.artistTitleLabel.setText("");
			this.trackTitleLabel.setText("");
			this.stopLabelAnimation(this.artistTitleLabel,this.trackTitleLabel);
			this.playButton.setGraphic(imageViewPlay);
			
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
        	//System.out.println("PlayBackApplication: MediaControl: stopEvent: UpdateValues running");
		};

		playButton.setOnAction(playEvent);
		stopButton.setOnAction(stopEvent);
		
		Color sceneColor = Color.DARKGRAY;
		Scene scene = new Scene(this.innerMediaBar);
		scene.setFill(sceneColor);

		return scene;
	}
	
	
	/*
	 * Orients components on BorderPane. Volume slider is vertical and to left.
	 * In middle vertical box, artist then track and (left-right) play/pause, stop, time slider
	 * leftPanel in MainView is 180x556
	 * 
	 * @param timeSlider, volumeSlider
	 * @param playButton, stopButton
	 * @param artistTitleLabel,trackTitleLabelk
	 * @param playTimeLabel, timeLabel, 
	 * @param volumeLabel
	 * @param innerMediaBar The BorderPane where the above components will be laid out in 
	 * 
	 */
	private void layoutComponents(Slider timeSlider, Slider volumeSlider,
								  Button playButton,Button stopButton,Label artistTitleLabel,
								  Label trackTitleLabel, Label playTimeLabel,Label timeLabel, 
								  Label volumeLabel, BorderPane innerMediaBar)
	{
		VBox artistAndTrack = new VBox();
		artistTitleLabel.setText("Artist");
		trackTitleLabel.setText("Track");
		artistAndTrack.getChildren().addAll(artistTitleLabel, trackTitleLabel );
		
		HBox playBtnContainer = new HBox(playButton);
		HBox stopBtnContainer = new HBox(stopButton);
		HBox buttons = new HBox(20, playButton, stopButton);

		HBox timeControls = new HBox(timeSlider, buttons);//, playTimeLabel);
		

        VBox volumeVBox = new VBox();
        volumeSlider.setOrientation(Orientation.VERTICAL);
        volumeSlider.setShowTickMarks(true);
        volumeVBox.getChildren().addAll(volumeSlider);
        volumeVBox.setMaxSize(10, 50);

        HBox every1 = new HBox();
        every1.getChildren().addAll(volumeVBox, artistAndTrack);
        
        HBox buttons_playTimeLabel = new HBox();
        buttons_playTimeLabel.getChildren().addAll(playTimeLabel);
        playTimeLabel.setAlignment(Pos.TOP_LEFT);
        buttons.setAlignment(Pos.TOP_RIGHT);
        
        VBox control_space_play = new VBox();
        control_space_play.getChildren().addAll(timeControls, buttons_playTimeLabel);
        VBox every1Else = new VBox();
        every1Else.getChildren().addAll(every1,control_space_play);//timeControls,buttons_playTimeLabel);
        

		innerMediaBar.setTop(every1Else);
		int mediaView_LeftPanelHeigth = 556; 
		innerMediaBar.setMaxSize(100, 350);
		//(175, ((1/5)*(1/3)) * mediaView_LeftPanelHeigth );
	}
	
	/**
	 * Sets currTrack and currTrackLocation
	 * @param currentTrack
	 */
	private void setCurrentTrack(Track currentTrack){
		this.currTrack = currentTrack;
		this.currTrackLocation = currentTrack.getTrackLocation();
	}
	
	//Accesses trackTitleLabel and animates that
	private void setLabelAnimation(Label artistLabel,Label trackLabel ){

		this.artistLabel_timeline.setCycleCount(Timeline.INDEFINITE);
		final KeyValue artistKV = new KeyValue(artistLabel.translateXProperty(), -(trackLabel.getText().length()) * 3 );
		final KeyFrame artistKF = new KeyFrame(Duration.millis(10000), artistKV);
		artistLabel_timeline.getKeyFrames().add(artistKF);
		artistLabel_timeline.play();
		
		this.trackLabel_timeline.setCycleCount(Timeline.INDEFINITE);
		final KeyValue tracKV = new KeyValue(trackLabel.translateXProperty(), -(trackLabel.getText().length()) * 3 );
		final KeyFrame tracKF = new KeyFrame(Duration.millis(10000), tracKV);
		trackLabel_timeline.getKeyFrames().add(tracKF);
		trackLabel_timeline.play();
	}
	
	/*
	 * Accesses trackLabel_timeline and stops it. Avoids memory leak.
	 * Should be called upon "Stop" button press of media application
	 */
	private void stopLabelAnimation(Label artistLabel, Label trackLabel){
		artistLabel_timeline.stop();
		artistLabel.setTranslateX(0);

		trackLabel_timeline.stop();
		trackLabel.setTranslateX(0);
	}
	/*
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

	/*
	 * Makes time slider change while track plays back.
	 * Updates time display of track playback.
	 * Sets volume slider to represent current audio volume of track.
	 */
	private void updateValues() {
        if (playTimeLabel != null && timeSlider != null && volumeSlider != null && duration != null) {
        	//System.out.println();
			
			Duration currentTime = mediaPlayer.getCurrentTime();//returns duration in milliseconds 
			
			playTimeLabel.setText(formatTime(currentTime, duration));
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
