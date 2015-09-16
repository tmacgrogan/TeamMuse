package muse;
import java.awt.BorderLayout;
import java.io.File;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;



/**
 * A TrackList is a structure to hold Tracks. It can be ordered by any meta data field. 
 * TrackLists should exist temporarily with the exception of the activeTrackList which is displayed by the MainView.
 */
public class TrackList<K>{
	
	/** Generates an empty TrackList
	 * 
	 */
	public TrackList(){
		
	}

	
	public List<Track> importToMuse()
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
		List<Track> trackList = new ArrayList<Track>();;
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
		
		return trackList;
	}
		
}
