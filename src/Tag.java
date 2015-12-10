import java.util.ArrayList;

/**
 * The Tag class represents a particular user-defined attribute that may be used to 'describe' any Track.
 * Once a Tag is added to a Track, searching for the Tag will return the Track, as well as all others with the Tag.
 * Tags may have a hierarchical parent/child with other Tags, allowing Tags to exist as more specific subcategories of others.
 * Retrieving tracks of a Tag with children also retrieves the tracks of those Children. 
 * Tags may have any number of children or parents. 
 * 
 * Example: The user defines Tag 'rock' as parent of Tag 'metal' which in turn is a parent of 'thrash.'
 * Searching for 'metal' will retrieve any Tracks tagged with 'metal' or 'thrash' but will no retrieve Tracks tagged with only 'rock'
 *
 */
public class Tag {

	/** Unique ID that persists even if Tag is renamed */
	private int id;
	/** The name of the Tag must consist of characters without whitespace or '-' */
	private String name;
	/** User defined description of the Tag	 */
	private String description;	
	private boolean searched; 
	

	
	/**
	 * Creates an empty Tag object not linked to the Database. Avoid use out side of test applications.
	 */
	public Tag() {
		this.name = null;
		this.id = (Integer) null;
	}
	
	//Tag pulled from the database
	/**
	 * Creates a Tag object to reference a Tag already stored in the database. Avoid use outside of this class or DbManager
	 * @param name The String name of the Tag.
	 * @param id the unique identifier of the Tag
	 * @throws IllegalArgumentException Thrown if the specified name includes invalid characters
	 */
	public Tag(String name, int id) throws IllegalArgumentException{ 
		this.name = name;
		this.id = id;
		if(!nameIsValid(name)){
			throw new IllegalArgumentException("Tag name invalid: \"" + name + "\"");
		}
	}
	
	/**
	 * Creates a Tag object with a given name. If a Tag with this name already exists in the database, creates the Tag with the corresponding id,
	 * otherwise is added as a new Tag to the database and is assigned an id
	 * @param name The name of the Tag
	 * @throws IllegalArgumentException
	 */
	public Tag(String name) throws IllegalArgumentException{
		this(name, DbManager.getTagId(name));
	}
	
	/**creates a TrackList containing every Track listed under This in the database, 
	 * then for every child This has, merges with the getTracks of the child.
	 * @return List of all Tracks tagged with this Tag or any child Tags
	 */
	public ArrayList<Track> getTracks(){
		ArrayList<Integer> visitedTags = new ArrayList<Integer>();
		return this.getTracks(visitedTags);
	}
	
	/**
	 * Returns the tracks that have been tagged with this Tag
	 * @param visitedTags Who even knows?
	 * @return list of tracks tagged
	 */
	public ArrayList<Track> getTracks(ArrayList<Integer> visitedTags){
		ArrayList<ArrayList<Track>> allTrackLists = new ArrayList<ArrayList<Track>>();
		
		allTrackLists.add(DbManager.getTracks(this));
		visitedTags.add(this.getTagId());
		
		for(Tag child : this.getChildren()){
			if(!visitedTags.contains(child.getTagId())){
				allTrackLists.add(child.getTracks(visitedTags));
				visitedTags.add(child.getTagId());
			}						
		}
		
		return TrackListController.merge(allTrackLists);
	}
	
	/**
	 * Adds a reference to a child Tag in the database
	 * @param child Name of the child Tag
	 * @return True if successful, otherwise False
	 */
	public boolean addChild(String child){
		if( !nameIsValid(child) )
			return false;
		int childID = DbManager.getTagId(child);
		if( childID == -1 ){
			childID = (DbManager.insertTag(child)).getTagId();
		}
		boolean status = DbManager.insertParentTagLink(id, childID);	
		return status;
	}
	
	/**
	 * Add a parent tag to this tag. If tag not created. 
	 * 
	 * @param parent
	 * @return status
	 * 
	 */
	public boolean addParent(String parent){
		if( !nameIsValid(parent) )
			return false;
		int parentID = DbManager.getTagId(parent);
		if( parentID == -1 ){
			parentID = DbManager.insertTag(parent).getTagId();
		}
		boolean status = DbManager.insertParentTagLink(parentID, id);	
		return status;
	}
	
	/** 
	 * @see Track.addTag
	 * avoid calling this method
	 * adds tag to track
	 * @param trackBeingAdded  
	 */
	public void addTrack(Track trackBeingAdded){
		trackBeingAdded.addTag(this.name);
	}
	
	/** 
	 * @see Track.removeTag
	 * avoid calling this method
	 * removes tag from track
	 * @param trackBeingRemoved
	 */
	public void removeTrack(Track trackBeingRemoved){
		trackBeingRemoved.removeTag(this);
	}
	
	/**
	 * Removes the parent-child relationship between this and a parent Tag
	 * @param parent The former parent Tag
	 * @param parent
	 */
	public void removeParent(Tag parent){
		DbManager.removeParentTagLink(parent.getTagId(), this.id);
	}
	
	/**
	 * Removes the parent-child relationship between this and a parent TAg
	 * @param child The former child Tag
	 */
	public void removeChild(Tag child){
		DbManager.removeParentTagLink(this.id, child.getTagId());
	}
	
	
	
	/**
	 * Retrieve any child Tags of this Tag
	 * @return any Tags listed as children of This in the database.
	 */
	public ArrayList<Tag> getChildren(){
		return DbManager.getChildren(id);		
	}
	
	/**
	 * Retrieve any parent Tags of this Tag
	 * @return any Tags listed as parents of This in the database.
	 */
	public ArrayList<Tag> getParents(){
		return DbManager.getParents(id);
	}
	
	/**
	 * Get the unique identifier
	 * @return the unique identifier
	 */
	public int getTagId(){
		return id;
	}
	
	/**
	 * Get the name
	 * @return the Tag's name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Set the name to a new String if it is valid.
	 * @param newName the new name for the tag
	 * @return Boolean whether the newName was valid and Tag updated or not.
	 */
	public boolean setName(String newName){
		//TODO implement validation
		if(nameIsValid(newName)){
			name = newName;
			boolean success = DbManager.setTagName(newName, id);
			return success;
		}
		else
			return false;
	}
	
	/**
	 * Get the description of the Tag
	 * @return the description
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * Set the description of the Tag
	 * @param newDescription the new description
	 */
	public void setDescription(String newDescription){
		description = newDescription;
	}
	
	/**
	 * Checks whether a given String is valid for use as a name for a Tag. To be valid it must not include any whitespace or '-' chars
	 * @param name The String being validated
	 * @return Boolean of whether the name is valid or not
	 */
	private boolean nameIsValid(String name){
		//space, comma, dash, "not", parentheses, empty/whitespace
		String trimName = name.trim();
		if(name.equalsIgnoreCase("not") || name.equalsIgnoreCase("") )
			return false;
		
		String regex_begin ="[\\(,\\-\\s\\)].+"; 
		String regex_middle = ".+[\\s\\,\\(\\)].+";
		String regex_end = ".+[\\,\\(\\)]$";
		
		if(trimName.matches(regex_begin))
			return false;
		else if(trimName.matches(regex_middle))
			return false;
		else if(trimName.matches(regex_end))
			return false;
		
		return true;
	}
	
	/*
	public static void main(String[] args){
		//DbManager.setupConnection();
		
		//rock, fast, pants, pop-punk, 
		System.out.println("nameisValid: " + "rock" + "=" + nameIsValid("rock"));
		
		System.out.println("nameisValid: " + "fast" + "=" + nameIsValid("fast"));
		
		System.out.println("nameisValid: " + "pop-punk" + "=" + nameIsValid("pop-punk"));
		
		System.out.println("nameisValid: " + "rock" + "=" + nameIsValid("rock"));
		
		System.out.println("nameisValid: " + "<empty-String>" + "=" + nameIsValid(""));
		
		System.out.println("nameisValid: " + "not" + "=" + nameIsValid("not"));
		
		System.out.println("nameisValid: " + "trip" + "=" + nameIsValid("trip"));
		
		System.out.println("nameisValid: " + "NoT" + "=" + nameIsValid("NoT"));
		
		System.out.println("nameisValid: " + "-EMO" + "=" + nameIsValid("-EMO"));
		
		System.out.println("nameisValid: " + ",me" + "=" + nameIsValid(",me"));
		
		System.out.println("nameisValid: " + "my,name" + "=" + nameIsValid("my,name"));
		
		System.out.println("nameisValid: " + "---Hi-me" + "=" + nameIsValid("---Hi-me"));
		
		System.out.println("nameisValid: " + "()" + "=" + nameIsValid("()"));
		
		System.out.println("nameisValid: " + "pop--" + "=" + nameIsValid("pop--"));	
		
		System.out.println("nameisValid: " + "pot-luck" + "=" + nameIsValid("pot-luck"));
		
		System.out.println("nameisValid: " + "(lookhere" + "=" + nameIsValid("(lookhere"));
		
		System.out.println("nameisValid: " + "lookhere)" + "=" + nameIsValid("lookhere)"));
		
		
	}
	*/

}
