import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;


public class TrackListController {
	
	/**
	 * @param toMerge
	 * @return a ArrayList<Track> that is the merge of all sets of toMerge
	 */
	public static ArrayList<Track> merge(ArrayList<ArrayList<Track>> toMerge){
		int hashSize = 0;
		
		for (ArrayList<Track> trackList : toMerge){ hashSize += trackList.size(); }
		
		Hashtable<Integer, Track> hashOfMerge = new Hashtable<Integer, Track>(hashSize, (float) 1.0);
		
		for (ArrayList<Track> trackList : toMerge){
			
			for(Track track : trackList){
				
				if (!hashOfMerge.containsKey(track.getTrackId())){
					hashOfMerge.put(track.getTrackId(), track);
				}
			}
		}
		
		return new ArrayList<Track>(hashOfMerge.values());
	}
	
	/**Returns 
	 * @param toIntersect
	 * @return the intersection of every set of toIntersect
	 */
	public static ArrayList<Track> intersect(ArrayList<ArrayList<Track>> toIntersect){
		ArrayList<Track> firstTrackList = new ArrayList<Track>();
		
		if(!toIntersect.isEmpty()){
			firstTrackList = toIntersect.remove(0);
		}
		
		ArrayList<Hashtable<Integer, Track>> trackListHashes = new ArrayList<Hashtable<Integer, Track>>();
		
		for (ArrayList<Track> trackList : toIntersect){
			Hashtable<Integer, Track> trackListHash = new Hashtable<Integer, Track>((int)(trackList.size() * 1.5));
			
			for (Track track : trackList){
				trackListHash.put(track.getTrackId(), track);
			}
			trackListHashes.add(trackListHash);
		}
		
		ArrayList<Track> toReturn = new ArrayList<Track>();
		
		for (Track track : firstTrackList){
			boolean foundInAll = true;
			
			for (Hashtable<Integer, Track> table : trackListHashes){
				foundInAll = foundInAll && table.containsKey(track.getTrackId());
			}
			if( foundInAll ){ toReturn.add(track); }
		}
		
		return toReturn;
	}
	
	/**
	 * @param mustBe 
	 * @param cantBe
	 * @return a ArrayList<Track> containing every member that is in mustBe and is not in cantBe
	 */
	public static ArrayList<Track> exclude(ArrayList<Track> mustBe, ArrayList<Track> cantBe){
		Hashtable<Integer, Track> excludeHash = new Hashtable<Integer, Track>((int) (cantBe.size() * 1.5));
		for(Track track : cantBe){
			excludeHash.put(track.getTrackId(), track);
		}
		
		
		ArrayList<Track> toReturn = new ArrayList<Track>();
		for(Track track : mustBe){
			if( !excludeHash.containsKey( track.getTrackId() ) ){
				toReturn.add(track);
			}
			
		}
		return toReturn;
	}
	
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Tag> getCommonTags(ArrayList<Track> tracksIn){
		if(tracksIn.size() == 0) return new ArrayList<Tag>();
		ArrayList<Track> tracks = (ArrayList<Track>) tracksIn.clone();
		
		System.out.println();
		System.out.println("TrackListController: getCommonTags: tracks.toString(): " + tracks.toString());
		System.out.println("TrackListController: getCommonTags: tracks.size(): " + tracks.size());
		System.out.println();
		
		ArrayList<Tag> firstTagList = (tracks.remove(0)).getTags();
		ArrayList<Hashtable<Integer,Tag>> tagListHashes = new ArrayList<Hashtable<Integer, Tag>>();
		
		for (Track track : tracks){
			
			
			ArrayList<Tag> tagList = track.getTags();
			
			Hashtable<Integer, Tag> tagListHash = new Hashtable<Integer, Tag>((int)(tagList.size() * 1.5));
			
			for (Tag tag : tagList){
				tagListHash.put(tag.getTagId(), tag);
			}
			tagListHashes.add(tagListHash);
		}
		
		ArrayList<Tag> toReturn = new ArrayList<Tag>();
		
		for (Tag tag : firstTagList){
			boolean foundInAll = true;
			
			for (Hashtable<Integer, Tag> table : tagListHashes){
				foundInAll = foundInAll && table.containsKey(tag.getTagId());
			}
			if( foundInAll ){ toReturn.add(tag); }
		}
		
		return toReturn;
	}
	
	public static ArrayList<Track> importM3UPlayList(){
		ArrayList<Track> trackList = new ArrayList<Track>();
		
		for(File file : chooseFiles(new FileNameExtensionFilter("M3U Files", "m3u"))){
			Scanner scan;
			
			try {
				scan = new Scanner(file);
				
				while (scan.hasNextLine()){
					String line = scan.nextLine();
					
					if(line.charAt(0) !='#'){ //line is a file location
						Track track;
						
						try {
							track = new Track(line);
						
						} catch (Exception e) {
							track = null;
							System.out.println("caught exception for: " + line);
							e.printStackTrace();
						}

						if(track != null){
							trackList.add(track);
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			DbManager.importTracks(trackList);
			
			for(Track track : trackList){
				System.out.println("title: " + track.getTitle());

				track.addTag("butt");
			}
			
			System.out.println("HOBO: " + file.getName());
			
		}
		
		return trackList;
	}
	
	public static ArrayList<Track> importToSnap()
	{	
		ArrayList<Track> trackList = new ArrayList<Track>();
		
		for(File file : chooseFiles(new FileNameExtensionFilter("MP3 Files", "mp3"))){
			trackList.add(new Track(file.getAbsolutePath()));
		}
		System.out.println(trackList.size());
		DbManager.importTracks(trackList);
		return trackList;
	}
	
	private static ArrayList<File> chooseFiles(FileNameExtensionFilter filter){
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		
		//default mode is FILES_ONLY. Changed it
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		
		JPanel panel = new JPanel( new BorderLayout() );
		int approval = chooser.showOpenDialog(panel);
		
		File[] files = null;
		ArrayList<File> allFiles = new ArrayList<File>();
		//make list of track objects from tracks user selects
		if(approval == JFileChooser.APPROVE_OPTION){//Click Open
			//a directory, list of tracks, or a track
			files = chooser.getSelectedFiles();			
			
			for(File file: files)
			{
				if(file.isDirectory())
				{
					FileSystemView fileSysView = chooser.getFileSystemView();
					for(File fileInDir: fileSysView.getFiles(file, false))
					{						 
						if( fileInDir.getAbsolutePath().contains("mp3")){
							allFiles.add(fileInDir);
						}
						
					}
					
				}else{//isFile
					allFiles.add(file);					
				}  				
			}			
		}
		return allFiles;
	}
}
