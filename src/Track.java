import java.io.File;
import java.util.*;

/**
 * A Track references a single audio file in the Library.
 * Avoid duplicating metadata stored by the audio file itself.
 */
public class Track {
	private File audioFile;
	

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
	
	/**Parses through audioFile's SNAP ID3 Frame to retrieve unique identifiers, then compares those identifiers with Tags in the AllTagsList
	 * @return collection of all Tags found in audioFile's SNAP ID3 Frame. Returns an empty array if the Frame does not exist. 
	 */
	public Tag[] getTags(){
		return null;
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
