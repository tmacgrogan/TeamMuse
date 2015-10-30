import java.util.ArrayList;
import java.util.Collections;

/**
 * The Search class holds a group of Tags that correspond to the sets of songs associated with those Tag either intersected, excluded, or merged.
 *
 */
public class Search {
	//default search terms
	private ArrayList<Tag> tagsToIntersect;
	private ArrayList<Tag> tagsToExclude;
	
	public Search(ArrayList<Tag> tagsToIntersect, ArrayList<Tag> tagsToExclude){
		this.tagsToIntersect = tagsToIntersect;
		this.tagsToExclude = tagsToExclude;
	}
	
	public Search(String searchString){
		tagsToIntersect = new ArrayList<Tag>();
		tagsToExclude = new ArrayList<Tag>();
		parse(searchString);
	}
	
	private void parse(String searchString){
		
		searchString = searchString.trim();
		
		//ArrayList<String> includeTagStrings = new ArrayList<String>();
		//ArrayList<String> excludeTagStrings = new ArrayList<String>();
		
		String[] splitStrings = searchString.split("\\s");
		
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
	public void favoriteThisSearch(){
		
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
}
