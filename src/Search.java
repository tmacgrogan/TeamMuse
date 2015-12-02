import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

/**
 * The Search class represents a set of Tracks composed of other sets specified via Tags that are combined via various set operations.
 * This set of Tracks is any Tracks in the Library that are described with *all* of the specified positive tags and *none* of the specified negative tags. 
 * Tags are specified via a String which is parsed to determine which Tags are to be used and how the sets of Tracks those Tags describe are to be combined.
 * A Search is stored as the collection of Tags, but can return the correct set of Tracks by querying the database when prompted with executeSearch()
 * 
 * When creating a Search via a search String, terms separated by spaces indicate the names of a Tags. 
 * 
 * Every Tag in the search String that is not preceded by a '-' represents an positive (intersected) Tag.
 * For each positive Tag when the Search is executed, the set of Tracks that Tag describes is queried from the database and then the intersection of all of these Tracks is found.
 * 
 * Every Tag in the search String that is preceded by a '-' represents a negative (excluded) Tag.
 * For each negative Tag when the Search is executed, the set of Tracks that Tag describes is queried from the database and the difference of the intersected Tracks and this set is found.
 * 
 * The keyword 'or' can be used to combine multiple searches. The search String is split at every instance of 'or' and these are used to create sub-Searches.
 * When a Search is executed, the union of the sets of every sub Search is found.
 * 
 * @Author Scott Beale
 *
 */
/**
 * @author Akolyte01
 *
 */
/**
 * @author Akolyte01
 *
 */
public class Search {
	//default search terms
	
	/** List of Positive Tags */
	private ArrayList<Tag> tagsToIntersect;
	/** List of Negative Tags */
	private ArrayList<Tag> tagsToExclude;
	/** Searches comprised of multiple subSearches separated by the 'or' keyword are stored and recalled recursively as a linked list of Search objects.
	 * If there are no subSearches, subSearch is null */
	private Search subSearch;
	/** The Search as represented by a single String */
	private String searchString;
	
//	public Search(ArrayList<Tag> tagsToIntersect, ArrayList<Tag> tagsToExclude){
//		this.tagsToIntersect = tagsToIntersect;
//		this.tagsToExclude = tagsToExclude;
//	}
	
	
	/**
	 * Creates a search object by parsing the searchString to populate the lists of Tags that describe the set of Tracks represented by the Search.
	 * @param searchString Specifies what combination of Tags that describes the set of Tracks this Search represents. 
	 * Tags are separated by spaces. Negative Tags are preceded by a '-'. SubSearches are separated by ' or '.
	 */
	public Search(String searchString){
		this.searchString = searchString;
		tagsToIntersect = new ArrayList<Tag>();
		tagsToExclude = new ArrayList<Tag>();
		subSearch = null;
		parse();
	}
	
	/**Creates a search object 'backwards' by passing in lists of Tag objects. Used to reconstruct saved Search objects from how they are stored in the database.
	 * @param tagsToIntersect positive Tags
	 * @param tagsToExclude negative Tags
	 * @param subSearch next Search in the linked list of sub Searches
	 */
	public Search(ArrayList<Tag> tagsToIntersect, ArrayList<Tag> tagsToExclude, Search subSearch){
		this.tagsToIntersect = tagsToIntersect;
		this.tagsToExclude = tagsToExclude;
		this.subSearch = subSearch; 
		this.searchString = "";
		for(Tag tag : tagsToIntersect){
			this.searchString += tag.getName() + " ";
		}
		for(Tag tag : tagsToExclude){
			this.searchString += "-" + tag.getName() + " ";
		}
		if(this.subSearch != null){
			this.searchString += "or " + subSearch.getSearchText();
		}		
	}
	
	/**
	 * Parses the searchString and populates the lists of Tags that describe the set of Tracks represented by the Search.
	 * If the 'or' is keyword is present, splits the String at the first instance and sets the subSearch to a new Search created from the remainder of searchString.
	 */
	private void parse(){
		
		searchString = searchString.trim();
		
		//ArrayList<String> includeTagStrings = new ArrayList<String>();
		//ArrayList<String> excludeTagStrings = new ArrayList<String>();
		String[] subStrings = searchString.split("\\sor\\s", 2);
		if(subStrings.length == 2){
			subSearch = new Search(subStrings[1]);
		}
		String[] splitStrings = subStrings[0].split("\\s");
		
		for(String curr : splitStrings){
			if(curr.length() > 0){
				if(curr.charAt(0)=='-'){
					curr = curr.substring(1,(curr.length()));
					
					Tag currTag = new Tag(curr);
					
					if(currTag.getTagId() < 0 ){
						System.out.println("Search term \""+ curr + "\" is not an existing tag");
					}
					else{
						tagsToExclude.add(currTag);
					}				
				}else{
					Tag currTag = new Tag(curr);
	
					if(currTag.getTagId() < 0 ){
						System.out.println("Search term \""+ curr + "\" is not an existing tag");
						JOptionPane.showMessageDialog(null, "Search term \""+ curr + "\" is not an existing tag");
					}
					else{
						tagsToIntersect.add(currTag);
					}				
				}
			}
		}
	}
	
		
	/**Creates the intersection of all of the TrackLists returned by getTracks for every Tag in tagsToIntersect. If tagsToIntersect is empty, finds the entire library.
	 * Merges all of the TrackLists returned by getTracks for every Tag in tagsToExclude
	 * Then finds the difference of the intersected Tracks and excluded Tracks.
	 * Finally finds the union of this difference and executeSearch() of any subSearch;
	 * @return a TrackList containing every Track found in all of the tagsToIntersect and in none of the tagsToExclude, as well as any returned by executing the subSearch;
	 */
	public ArrayList<Track> executeSearch(){
		ArrayList<ArrayList<Track>> intersectTrackLists = new ArrayList<ArrayList<Track>>();
		
		ArrayList<Track> mustBe;
		
		if(tagsToIntersect.isEmpty()){
			mustBe = DbManager.getLibrary();
		}
		else{
			for(Tag tag : tagsToIntersect){
				intersectTrackLists.add(tag.getTracks());
				System.out.println(""+tag.getName());
			}
			mustBe = TrackListController.intersect(intersectTrackLists);
		}
		
		
		ArrayList<ArrayList<Track>> excludeTrackLists = new ArrayList<ArrayList<Track>>();
		
		for(Tag tag : tagsToExclude){
			excludeTrackLists.add(tag.getTracks());
		}
		
		ArrayList<Track> cantBe = TrackListController.merge(excludeTrackLists);
		
		ArrayList<Track> finalList = TrackListController.exclude(mustBe, cantBe);
		
		if(subSearch != null){
			ArrayList<ArrayList<Track>> toMerge = new ArrayList<ArrayList<Track>>();
			toMerge.add(finalList);
			toMerge.add(subSearch.executeSearch());
			finalList = TrackListController.merge(toMerge);
		}
		
		
//		Collections.swap(finalList, 3, 9);
//		Collections.swap(finalList, 0, 10);
//		Collections.swap(finalList, 1, 9);
//		Collections.swap(finalList, 2, 8);
		
//		Collections.reverse(finalList);
//		
//		Collections.sort(finalList, new MetadataComparator("Name"));
			
		return finalList;		
	}
	
	
	/**Saves this Search so it can be accessed later--saves to database so it can be found after closing and reopening
	 * 
	 */
	public void favoriteSearch(){
		DbManager.saveSearch(this);
	}
	
	
	/**
	 * Gets the tagsToIntersect
	 * @return tagsToIntersect The positive Tags
	 */
	public ArrayList<Tag> getTagsToIntersect(){
		return tagsToIntersect;
	}
	
	/**
	 * Sets tagsToIntersect
	 * @param tagsToIntersect The positive Tags
	 */
	public void setTagsToIntersect(ArrayList<Tag> tagsToIntersect){
		this.tagsToIntersect = tagsToIntersect;
	}
	
	/**
	 * Gets the tagsToExclude
	 * @return tagsToExclude The negative Tags
	 */
	public ArrayList<Tag> getTagsToExclude(){
		return tagsToExclude;
	}
	
	/**
	 * Sets tagsToExclude
	 * @param tagsToExclude The negative Tags
	 */
	public void setTagsToExclude(ArrayList<Tag> tagsToExclude){
		this.tagsToIntersect = tagsToExclude;
	}
	
	/**
	 * Gets all of the Search objects saved in the database.
	 * @return ArrayList of all Search objects saved in the database.
	 */
	public static ArrayList<Search> getAllSearches(){
		return DbManager.getSavedSearches();
	}
	
	/**
	 * Gets any subSearch
	 * @return the subSearch
	 */
	public Search getSubSearch(){
		return subSearch;
	}
	
	/**
	 * Gets the searchText
	 * @return the searchText
	 */
	public String getSearchText(){
		return searchString;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return searchString;
	}
}
