
/**
 * A Tag represents a single attribute that a track might have
 *
 */
public class Tag {
	
	private String uniqueIdentifier; 
	private String name;
	private String description;	
	
	public Tag(String name){
		this.name = name;
		uniqueIdentifier = this.generateUniqueIdentifier();
		description = "y u no change this";
	}
	
	/**creates a TrackList containing every Track listed under This in the database, 
	 * then for every child This has, merges with the getTracks of the child.
	 * @return 
	 */
	public TrackList getTracks(){
		return null;
	}
	
	/** see Track.addTag
	 * avoid calling this method
	 * @param trackBeingAdded  
	 */
	public void addTrack(Track trackBeingAdded){
		trackBeingAdded.addTag(this);
	}
	
	/** see Track.removeTag
	 * avoid calling this method
	 * @param trackBeingRemoved
	 */
	public void removeTrack(Track trackBeingRemoved){
		trackBeingRemoved.removeTag(this);
	}
	
	/**Searches the database to return the Tag whose name matches the passed String
	 * If there are multiple matches, return the first and throw exception
	 * @param name
	 * @return
	 */
	public static Tag getTagByName(String name){
		return null;
	}
	
	
	/**Searches the database to return the Tag whose uniqueIdentifier matches the passed String
	 * @param ID
	 * @return
	 */
	public static Tag getTagByID(String ID){
		return null;
	}	
	
	/**Creates tiered relationship between two Tags by adding the child to the list of the parent's children and vice versa in the database
	 * @param child is searched for whenever parent is
	 * @param parent is not searched for when child is
	 */
	public static void createRelationship(Tag child, Tag parent){
		
	}
	
	/**
	 * @return any Tags listed as children of This in the database.
	 */
	public Tag[] getChildren(){
		return null;		
	}
	
	/**
	 * @return any Tags listed as parents of This in the database.
	 */
	public Tag[] getParents(){
		return null;
	}
	
	/**Called upon Tag's creation. 
	 * @return a unique 8 character String used to identify this Tag.
	 */
	private static String generateUniqueIdentifier(){
		return null;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String newName){
		name = newName;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String newDescription){
		description = newDescription;
	}
}