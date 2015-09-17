import java.awt.BorderLayout;
import java.io.File;
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
	public static ArrayList<Track> merge(ArrayList<Track>[] toMerge){
		return null;
	}
	
	/**Returns 
	 * @param toIntersect
	 * @return the intersection of every set of toIntersect
	 */
	public static ArrayList<Track> intersect(ArrayList<Track>[] toIntersect){
		return null;
	}
	
	/**
	 * @param mustBe 
	 * @param cantBe
	 * @return a ArrayList<Track> containing every member that is in mustBe and is not in cantBe
	 */
	public static ArrayList<Track> exclude(ArrayList<Track> mustBe, ArrayList<Track> cantBe){
		return null;
	}
	
	public static ArrayList<Track> importToSnap()
	{

		FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Files", "mp3");
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		
		//default mode is FILES_ONLY. Changed it
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		
		JPanel panel = new JPanel( new BorderLayout() );
		int approval = chooser.showOpenDialog(panel);
		
		File[] files;
		ArrayList<Track> trackList = new ArrayList<Track>();
		//make list of track objects from tracks user selects
		if(approval == JFileChooser.APPROVE_OPTION){//Click Open
			//a directory, list of tracks, or a track
			files = chooser.getSelectedFiles();
			
			File[] filesInDir;
			for(File file: files)
			{
				if(file.isDirectory())
				{
					FileSystemView fileSysView = chooser.getFileSystemView();
					filesInDir = fileSysView.getFiles(file, false);
					for(File fileInDir: filesInDir)
					{
						trackList.add( new Track( fileInDir.getAbsolutePath() ) );
					}
					
				}else{//isFile
					trackList.add( new Track(file.getAbsolutePath())	);
				}  
				
			}
			
		}
		//DbManager.importTracks(trackList);
		return trackList;
	}
}
