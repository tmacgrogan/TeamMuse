import java.awt.BorderLayout;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;


/**
 * Helper class for performing operations on or generating ArrayLists of Tracks for other parts in the application. 
 * In particular, performing set operations used by the Search class as well as importing and exporting files.
 * @author Scott Beale
 *
 */
public class TrackListController {
	
	/**
	 * Finds the union of each ArrayList of Tracks passed in.
	 * @param toMerge Contains ArrayLists of Tracks that are merged (union).
	 * @return The union of every set of toMerge
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
	
	/** 
	 * Finds the intersection of each ArrayList of Tracks passed in.
	 * @param toIntersect Contains ArrayLists of Tracks that are intersected.
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
	 * Finds the difference of mustBe and cantBe
	 * @param mustBe set of Tracks whose presence is required for inclusion in returned set
	 * @param cantBe set of Tracks whose presence is forbidden from inclusion in returned set
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
	
	
	/**
	 * Finds all Tags shared by every Track in the passed ArrayList
	 * @param tracksIn the Tracks whose common Tags are found.
	 * @return Every Tag shared by every Track of tracksIn.
	 */
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
	
	/**
	 * Opens a file chooser to import a M3U playlist. 
	 * Parses the .m3U file and imports every .mp3 file not already present into the Library.
	 * Adds a tag with name 'list:[FileName]' to every Track in the .m3u file.
	 * Saves a search for that tag in Saved searches
	 * @return Search for new Tag created and added to every Track of playlist
	 */
	public static Search importM3UPlayList(){
		ArrayList<Track> trackList = new ArrayList<Track>();
		String playListTag = new String();
		
		for(File file : chooseFiles(new FileNameExtensionFilter("M3U Files", "m3u"))){
			//Scanner scan;
			BufferedReader read;
			
			try {
				read = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF8")));
				
				//scan = new Scanner(file);
				
				for(String line = read.readLine(); line != null; line = read.readLine()){
					if( (line.length() > 0)&& line.charAt(0) !='#'){ //line is a file location
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
			} catch (IOException e) {
				e.printStackTrace();
			}
			DbManager.importTracks(trackList);
			
			playListTag = "list:" + file.getName().substring(0, file.getName().length() - 4);
			
			for(Track track : trackList){
				System.out.println("title: " + track.getTitle());

				track.addTag(playListTag);
			}
			
		}
		return new Search(playListTag);
	}
	
	/**
	 * Opens a file chooser to select a collection of .mp3 file or a folder. 
	 * If files are selected, adds those Tracks to the database.
	 * If a folder is selected, adds any .mp3 files contained in that folder or lower as Tracks to the database.
	 * @return List of new Tracks successfully added to the database
	 */
	public static ArrayList<Track> importToSnap()
	{	
		ArrayList<Track> trackList = new ArrayList<Track>();
		
		for(File file : chooseFiles(new FileNameExtensionFilter("MP3 Files", "mp3"))){
			trackList.add(new Track(file.getAbsolutePath()));
		}
		System.out.println("TrackListController: trackList size: " + trackList.size());
		DbManager.importTracks(trackList);
		return trackList;
	}
	
	/**
	 * Opens a file chooser for a particular kind of file specified by the filter.
	 * @param filter The filter to indicate what type of file is to be chosen.
	 * @return List of files user chooses
	 */
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
	
	/**
	 * Writes a List of tracks into an .m3u file which can be imported into Virtual DJ or iTunes
	 * If Virtual DJ is present on the user's computer, defaults to placing new playlists into Virtual DJs playlist folder, allowing them to be quickly accessed.
	 * @param playlist
	 */
	public static void exportM3u(ArrayList<Track> playlist){
		FileNameExtensionFilter filter = new FileNameExtensionFilter("M3U Playlist", "m3u");
		String defaultPath;
		
		//System.out.println(System.getProperty("os.name").substring(0, 3).equals("Win"));
		if(System.getProperty("os.name").substring(0, 3).equals("Win")){
			System.out.println("reached");
			defaultPath = System.getProperty("user.home")+"/Documents/VirtualDJ/Playlists";
		}else{
			defaultPath = System.getProperty("user.home")+"Library/VirtualDJ/Playlists";
		}		

		JFileChooser chooser = new JFileChooser(defaultPath);
		chooser.setFileFilter(filter);
		//chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		
		JPanel panel = new JPanel( new BorderLayout() );
		int approval = chooser.showSaveDialog(panel);
		System.out.println("start");
		if(approval == JFileChooser.APPROVE_OPTION){
			System.out.println("approved");
			File dir = chooser.getSelectedFile();
			StringBuffer text = new StringBuffer();
			String path = dir.getAbsolutePath();
			if(!path.contains(".m3u")){
				path = path + ".m3u";
			}
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"))) {
				text.append("#EXTM3U\n\n");
				for(Track t : playlist){
					text.append("#EXTINF:")
						.append(t.getRuntime() + ",")
						.append(t.getArtist() + " - ")
						.append(t.getTitle() + "\n")
						.append(t.getTrackLocation() + "\n\n");
				}
				System.out.println(path + ":\n" + text);
				writer.write(text.toString());
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
