import java.sql.SQLException;
import java.util.ArrayList;

public class Tag {

	
	private int id;
	private String name;
	private String description;	
	
	//Tag pulled from the database
	public Tag(String name, int id){
		//TODO Sanitize tag names
		this(name);
		this.id = id;
	}
	
	public Tag(String name){
		this.name = name;
		this.id = DbManager.getTagId(name);
	}
	
	/**creates a TrackList containing every Track listed under This in the database, 
	 * then for every child This has, merges with the getTracks of the child.
	 * @return 
	 */
	public ArrayList<Track> getTracks(){
		return null;
	}
	
	/**
	 * Add a parent tag to this tag. If tag not created. 
	 * 
	 * @param parent
	 * @throws SQLException 
	 */
	//Catch SQLException in caller and prompt user that parent already attached to this child
	public void addParent(String parent) throws SQLException{

		Tag parentTag;
		if( DbManager.getTagId(parent) == -1 ){
			parentTag = DbManager.insertTag(parent);
			DbManager.insertParentTagLink(parentTag.id, id);
		
		}else{ 
			parentTag = new Tag(parent);
			DbManager.insertParentTagLink(parentTag.id, id);			
		}
		
		
	}
	
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
	public Tag[] getChildren(){
		return null;		
	}
	
	/**
	 * @return any Tags listed as parents of This in the database.
	 */
	public Tag[] getParents(){
		return null;
	}
	
	public int getTagId(){
		return id;
	}
	
	public void setTagId(int id){
		this.id = id;
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
