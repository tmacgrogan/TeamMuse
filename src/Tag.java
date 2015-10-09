import java.sql.SQLException;
import java.util.ArrayList;

public class Tag {

	//TODO possible structures to hold children and parents
	private int id;
	private String name;
	private String description;	
	
	//Tag pulled from the database
	public Tag(String name, int id) throws IllegalArgumentException{ 
		//TODO Sanitize tag names
		this.name = name;
		this.id = id;
		if(!nameIsValid()){
			throw new IllegalArgumentException("Tag name invalid: \"" + name + "\"");
		}
	}
	
	public Tag(String name) throws IllegalArgumentException{
		this(name, DbManager.getTagId(name));
	}
	
	/**creates a TrackList containing every Track listed under This in the database, 
	 * then for every child This has, merges with the getTracks of the child.
	 * @return 
	 */
	public ArrayList<Track> getTracks(){
		return null;
	}
	//TODO addChild method
	public boolean addChild(String child){
		int childID = DbManager.getTagId(child);
		if( childID == -1 ){
			DbManager.insertTag(child);
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

		int parentID = DbManager.getTagId(parent);
		if( parentID == -1 ){
			DbManager.insertTag(parent);
		}
		boolean status = DbManager.insertParentTagLink(parentID, id);	
		return status;
	}
	
	//TODO delete Parent and child tags
	
	/** see Track.addTag
	 * avoid calling this method
	 * @param trackBeingAdded  
	 */
	public void addTrack(Track trackBeingAdded){
		trackBeingAdded.addTag(this.name);
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
	public ArrayList<Tag> getChildren(){
		return DbManager.getChildren(id);		
	}
	
	/**
	 * @return any Tags listed as parents of This in the database.
	 */
	public ArrayList<Tag> getParents(){
		return DbManager.getParents(id);
	}
	
	public int getTagId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean setName(String newName){
		//TODO implement validation
		if(nameIsValid(newName)){
			name = newName;
			DbManager.setTagName(newName, id);
			return true;
		}
		else
			return false;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String newDescription){
		description = newDescription;
	}
	

	private boolean nameIsValid(String name){
		//space, comma, dash, "not", parentheses, empty/whitespace
		return true;
	}
	
	private boolean nameIsValid(){
		return nameIsValid(this.name);
	}
	
	/*
	public static void main(String[] args){
		DbManager.setupConnection();
		
		Tag childTag = new Tag("Krump");
		
		//Tag parenTag = new Tag("HipHop");
		try{
		childTag.addParent("parentTag");
		}catch(SQLException e){
			System.out.println("Parent and child assoc. already in database");
			
		}
	}
	*/

}
