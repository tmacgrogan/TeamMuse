import java.util.ArrayList;

/**
 * The Search class holds a group of Tags that correspond to the sets of songs associated with those Tag either intersected, excluded, or merged.
 *
 */
public class Search {
	//default search terms
	private Tag[] tagsToIntersect;
	private Tag[] tagsToExclude;
	
	public Search(Tag[] tagsToIntersect, Tag[] tagsToExclude){
		this.tagsToIntersect = tagsToIntersect;
		this.tagsToExclude = tagsToExclude;
	}
	
	/**Creates the intersection of all of the TrackLists returned by getTracks for every Tag in tagsToIntersect
	 * Merges all of the TrackLists returned by getTracks for every Tag in tagsToExclude
	 * @return a TrackList containing every Track found in all of the tagsToIntersect and in none of the tagsToExclude
	 */
	public ArrayList<Track> executeSearch(){
		return null;
	}
	
	/**exports the results of performSearch by writing to a document to be read as a playlist by compatible programs
	 * 
	 */
	public void export(){
		
	}
	
	/**Saves this Search so it can be accessed later--saves to database so it can be found after closing and reopening
	 * 
	 */
	public void favoriteThisSearch(){
		
	}
	
	public Tag[] getTagsToIntersect(){
		return tagsToIntersect;
	}
	
	public void setTagsToIntersect(Tag[] tagsToIntersect){
		this.tagsToIntersect = tagsToIntersect;
	}
	
	public Tag[] getTagsToExclude(){
		return tagsToExclude;
	}
	
	public void setTagsToExclude(Tag[] tagsToExclude){
		this.tagsToIntersect = tagsToExclude;
	}
}
