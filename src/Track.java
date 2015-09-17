//package muse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

/**
 * A Track references a single audio file in the Library.
 * Avoid duplicating metadata stored by the audio file itself.
 */
public class Track {
			
	private String Album;
	private String Artist;
	
	private MP3File mp3File;
	
	private String Genre;
	private String Location;//Absolute path
	
	private Tag tag;
	
	private String Title;
	
	public Track(String location){
		Location = location;
		
		//AudioFile
		try {
			
			AudioFile audioFile = AudioFileIO.read(new File(location) );
			mp3File = (MP3File) audioFile;
			
		}catch (CannotReadException e) {
			
			System.out.println("Cannot read the file");
			e.printStackTrace();
			
		}catch (IOException e) {
			
			e.printStackTrace();
			
		}catch (TagException e) {
			
			System.out.println("Jaudiotagger reports tag exception");
			e.printStackTrace();
			
		}catch (ReadOnlyFileException e) {
			
			System.out.println("Jaudiotagger reports read only exception");
			e.printStackTrace();
			
		}catch (InvalidAudioFrameException e) {
			
			System.out.println("Jaudiotagger reports invalid audio frame "
					            + "exception");
			
			e.printStackTrace();
		}
		
		
	}

	/**Creates a 'SNAP' frame in audioFile's ID3 tag if it does not already exist
	 * Appends the text field of the SNAP frame with the unique identifier of tagBeingAdded if it is not already present.
	 * Updates the database so This is found when searching for tagBeingAdded.
	 * @param tagBeingAdded the Tag whose unique identifier is added into ID3
	 */
	public void addTag(Tag tagBeingAdded){
		
	}
	
	/**Removes tagBeingRemoved's unique identifier from the This's SNAP ID3 Frame.
	 * Updates the database so This is not found when searching for tagBeingRemoved.
	 * @param tagBeingRemoved the Tag whose unique identifier is removed from ID3
	 */
	public void removeTag(Tag tagBeingRemoved){
		
	}
	
	/**
	 * Return Album of Track.
	 * Return empty string if no Track info frame.
	 * @return String
	 */
	public String getAlbum(){
		AbstractID3v2Tag ID3v2Tag = mp3File.getID3v2Tag();
		
		//ID3v2.3 or ID3v2.4 so use abstract
		String album = ID3v2Tag.getFirst(FieldKey.ALBUM);
		return album;
	}
	
	/**
	 * Return artist of track. 
	 * Return empty string if no artist info (i.e TOPE, TPE1 frame)
	 * @return String 
	 */
	public String getArtist(){
		AbstractID3v2Tag ID3v2Tag = mp3File.getID3v2Tag();
		
		//ID3v2.3 or ID3v2.4 so use abstract
		String artist = ID3v2Tag.getFirst(FieldKey.ARTIST);
		return artist;
	}
	
	/**
	 * Return Genre of Track.
	 * Return empty string if no genre frame (i.e TCON)
	 * @return String
	 */
	public String getGenre(){
		AbstractID3v2Tag ID3v2Tag = mp3File.getID3v2Tag();
		
		//ID3v2.3 or ID3v2.4 so use abstract
		String genre = ID3v2Tag.getFirst(FieldKey.GENRE);
		return genre;
	}
	
	/**
	 * Return absolute path of file
	 * @return String
	 */
	public String getTrackLocation(){
		return Location;
	}
	
	/**Parses through audioFile's SNAP ID3 Frame to retrieve unique identifiers, then compares those identifiers with Tags in the AllTagsList
	 * @return collection of all Tags found in audioFile's SNAP ID3 Frame. Returns an empty array if the Frame does not exist. 
	 */
	public Tag[] getTags(){
		return null;
	}
	
	/**
	 * Return Track title.
	 * Return relative file name if no title frame (i.e no TIT2)
	 * @return
	 */
	public String getTitle(){
		AbstractID3v2Tag ID3v2Tag = mp3File.getID3v2Tag();
		
		//ID3v2.3 or ID3v2.4 so use abstract
		String title = ID3v2Tag.getFirst(FieldKey.TITLE);
		if(title == ""){
			return MP3File.getBaseFilename( mp3File.getFile() );
		}
		return title;
	}
	/**I don't really know how we should do this. Maybe make a separate ID3 class? 	 * 
	 * @return
	 */
	public String[][] getID3(){
		return null;		
	}
	
	
	/** Need some sort of plan for doing this.
	 * @param args
	 */
	public void editID3(String[] args){
		
	}
}
