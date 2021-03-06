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
 * A Track references a single audio file in the Library. Avoid duplicating
 * metadata stored by the audio file itself.
 */
public class Track {

	/** The unique identifier of the Track as stored in the database */
	private int id;

	private AudioFile audioFile;
	private Date importDate;
	/** Absolute path */
	private String Location;// Absolute path

	// private String Title;

	/**
	 * Constructor for Track already in the database (assigned an id)
	 * @param location The absolute path to the Tracks file
	 * @param trackId The unique identifier
	 */
	public Track(String location, int trackId) {
		this(location);
		this.id = trackId;
	}

	/**
	 * Creates a Track from a given location to an .mp3 file. 
	 * @param location 	Absolute path to an .mp3 file
	 */
	public Track(String location) {
		Location = location;
		id = -1;
		// AudioFile
		try {

			audioFile = AudioFileIO.read(new File(location));
			//mp3File = (MP3File) audioFile;

		} catch (CannotReadException e) {

			System.out.println("Cannot read the file");
			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (TagException e) {

			System.out.println("Jaudiotagger reports tag exception");
			e.printStackTrace();

		} catch (ReadOnlyFileException e) {

			System.out.println("Jaudiotagger reports read only exception");
			e.printStackTrace();

		} catch (InvalidAudioFrameException e) {

			System.out.println("Jaudiotagger reports invalid audio frame "
					+ "exception");

			e.printStackTrace();
		}

	}

	/**
	 * Adds a Tag to the Track
	 * @param name Name of the added Tag
	 */
	public void addTag(String name) {
		DbManager.addTagToTrack(name, this);
	}

	/**
	 * Removes tagBeingRemoved's unique identifier from the This's SNAP ID3
	 * Frame. Updates the database so This is not found when searching for
	 * tagBeingRemoved.
	 * 
	 * @param tagBeingRemoved
	 *            the Tag whose unique identifier is removed from ID3
	 */
	public void removeTag(Tag tagBeingRemoved) {
		DbManager.removeTagFromTrack(tagBeingRemoved, this);
	}

	/**
	 * Return Album of Track. Return empty string if no Track info frame.
	 * 
	 * @return String
	 */
	public String getAlbum() {
		//AbstractID3v2Tag ID3v2Tag = mp3File.getID3v2Tag();
		String album = audioFile.getTag().getFirst(FieldKey.ALBUM);
		
		// ID3v2.3 or ID3v2.4 so use abstract
		//String album = ID3v2Tag.getFirst(FieldKey.ALBUM);
		return album;
	}

	/**
	 * Return artist of track. Return empty string if no artist info (i.e TOPE,
	 * TPE1 frame)
	 * 
	 * @return String
	 */
	public String getArtist() {
		//AbstractID3v2Tag ID3v2Tag = mp3File.getID3v2Tag();
		String artist = audioFile.getTag().getFirst(FieldKey.ARTIST);
		
		// ID3v2.3 or ID3v2.4 so use abstract
		//String artist = ID3v2Tag.getFirst(FieldKey.ARTIST);
		return artist;
	}

	/**
	 * Return Genre of Track. Return empty string if no genre frame (i.e TCON)
	 * 
	 * @return String
	 */
	public String getGenre() {
		//AbstractID3v2Tag ID3v2Tag = mp3File.getID3v2Tag();
		String genre = audioFile.getTag().getFirst(FieldKey.GENRE);
		
		// ID3v2.3 or ID3v2.4 so use abstract
		//String genre = ID3v2Tag.getFirst(FieldKey.GENRE);
		return genre;
	}

	/**
	 * Return absolute path of file
	 * 
	 * @return String
	 */
	public String getTrackLocation() {
		return Location;
	}
	
	/**
	 * Set track location to this absolute path.
	 * 
	 * @param trackLocation Absolute path
	 */
	public void setTrackLocation(String trackLocation){
		Location = trackLocation;
	}

	/**
	 * Parses through audioFile's SNAP ID3 Frame to retrieve unique identifiers,
	 * then compares those identifiers with Tags in the AllTagsList
	 * 
	 * @return collection of all Tags found in audioFile's SNAP ID3 Frame.
	 *         Returns an empty array if the Frame does not exist.
	 */
	public ArrayList<Tag> getTags() {
		return DbManager.getTags(this);
	}
	
	/**
	 * Return Track title. Return relative file name if no title frame (i.e no
	 * TIT2)
	 * 
	 * @return String	Title of track
	 */
	public String getTitle() {
		//AbstractID3v2Tag ID3v2Tag = mp3File.getID3v2Tag();
		String title = audioFile.getTag().getFirst(FieldKey.TITLE);
		
		// ID3v2.3 or ID3v2.4 so use abstract
		
		//String title = ID3v2Tag.getFirst(FieldKey.TITLE);
		if (title == "" || title == null) {
			//return MP3File.getBaseFilename(mp3File.getFile());
			return AudioFile.getBaseFilename(audioFile.getFile());
		}
		return title;
	}

	/**
	 * Return Track runtime
	 * 
	 * @return String Length of the music track
	 */
	public String getRuntime() {
		//return ((int)mp3File.getMP3AudioHeader().getPreciseTrackLength()) + "";
		return ((int)(audioFile.getAudioHeader().getTrackLength())) + "";//getPreciseTrackLength()) + "";
	}
	
	/**
	 * Returns the Date on which the Track was imported
	 * 
	 * @return Date Date at which track was imported
	 */
	public Date getImportDate(){
		if(importDate == null){
			importDate = DbManager.getTrackCreatedDate(id);
		}
		return importDate;
	}
	
	/**
	 * sets the Date on which the Track was imported
	 * 
	 * @param date 
	 */
	public void setImportDate(Date date){
		importDate = date;
	}
	
	/**
	 * Sets the identifier of the Track
	 * 
	 * @param id the Track identifier
	 */
	public void setTrackId(int id) {
		this.id = id;
	}

	/**
	 * Returns the identifier of the Track
	 * 
	 * @return int The trackID
	 */
	public int getTrackId() {
		if(id < 0){
			id = DbManager.getTrackId(Location);
		}
		return id;
	}
}
