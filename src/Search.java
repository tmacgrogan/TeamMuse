import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

/**
 * The Search class represents a set of Tracks composed of other sets specified via Tags that are combined via various set operations.
 * Searches are created with a String which specifies which Tags are to be used in the search and how the sets of Tracks those Tags describe are to be combined.
 * In memory, the Search is stored as the collection of those Tags, but can return the correct set of Tracks by querying the database when prompted with executeSearch()
 * 
 * When creating a Search, terms separated by spaces indicate the name of a Tag. 
 *
 */
public class Search {
	//default search terms
	private ArrayList<Tag> tagsToIntersect;
	private ArrayList<Tag> tagsToExclude;
	private Search subSearch;
	private String searchString;
	
//	public Search(ArrayList<Tag> tagsToIntersect, ArrayList<Tag> tagsToExclude){
//		this.tagsToIntersect = tagsToIntersect;
//		this.tagsToExclude = tagsToExclude;
//	}
	
	public Search(String searchString){
		this.searchString = searchString;
		tagsToIntersect = new ArrayList<Tag>();
		tagsToExclude = new ArrayList<Tag>();
		subSearch = null;
		parse();
	}
	
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
	
	public void print(){
		String toPrint = "";
		for(Tag curr : tagsToIntersect){
			toPrint += curr.getName() + " ";
		}
		System.out.println("Tags to intersect: "+toPrint);
		
		toPrint = "";
		for(Tag curr : tagsToExclude){
			toPrint += curr.getName() + " ";
		}
		System.out.println("Tags to exclude: "+toPrint);
	}
	
	public static void test(){
		Search testSearch = new Search("cat bat scat rat fat mat");
		testSearch.print();
		testSearch = new Search("cat bat scat -rat fat -mat");
		testSearch.print();
		testSearch = new Search(" cat bat scat -rat fat -mat");
		testSearch.print();
		testSearch = new Search(" cat bat scAT 	       -rat fat -mat");
		testSearch.print();
		testSearch = new Search(" cat bat scAT 	 afsdfak; elj489qy2pt5huo    fa  -rat fat -mat");
		testSearch.print();

//		testSearch = new Search("  -cat	 bat scAT  RAT   FAT -mat");
//		testSearch.print();
//		System.out.println("reached");
	}
		
	/**Creates the intersection of all of the TrackLists returned by getTracks for every Tag in tagsToIntersect
	 * Merges all of the TrackLists returned by getTracks for every Tag in tagsToExclude
	 * @return a TrackList containing every Track found in all of the tagsToIntersect and in none of the tagsToExclude
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
	
	/**exports the results of performSearch by writing to a document to be read as a playlist by compatible programs
	 * 
	 */
	public void export(){
		
	}
	
	/**Saves this Search so it can be accessed later--saves to database so it can be found after closing and reopening
	 * 
	 */
	public void favoriteSearch(){
		DbManager.saveSearch(this);
	}
	
	public ArrayList<Tag> getTagsToIntersect(){
		return tagsToIntersect;
	}
	
	public void setTagsToIntersect(ArrayList<Tag> tagsToIntersect){
		this.tagsToIntersect = tagsToIntersect;
	}
	
	public ArrayList<Tag> getTagsToExclude(){
		return tagsToExclude;
	}
	
	public void setTagsToExclude(ArrayList<Tag> tagsToExclude){
		this.tagsToIntersect = tagsToExclude;
	}
	
	public static ArrayList<Search> getAllSearches(){
		return DbManager.getSavedSearches();
	}
	
	public Search getSubSearch(){
		return subSearch;
	}
	
	public String getSearchText(){
		return searchString;
	}
	
	public String toString(){
		return searchString;
	}
}
