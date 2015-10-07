import java.awt.BorderLayout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.FieldKey;
//im here

/**
 * Utility to create "Dummy" mp3 copies.
 * 
 * @author Bim
 *
 */
public final class Util_DemoMP3{

	/**
	 * Generates numOfCopies of mp3 file chosen. Inserts copies within home directory(i.e. Desktop).
	 * 
	 * 
	 * @param numOfCopies
	 */
	public static void copyMP3(int numOfCopies) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Files", "mp3");
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		
		FileSystemView OSFileSys = FileSystemView.getFileSystemView();
		File homeDir = OSFileSys.getHomeDirectory();
		
		JPanel panel = new JPanel( new BorderLayout() );
		int approval = chooser.showOpenDialog(panel);
		
		
		File target = new File(homeDir.toString(),"DemoMP3s");
		if( approval == JFileChooser.APPROVE_OPTION  ){//User gives input && creation of directory successful
			target.mkdir();
			
			File originalMP3 = chooser.getSelectedFile();
			Path originalMP3_Path = originalMP3.toPath();
			for(int i=0; i < numOfCopies; i++){
				try {
					String mp3CopyName = "Dummy_" + i + ".mp3";
					
					File mp3Copy = new File( target.toString(), mp3CopyName);
					
					Files.copy(originalMP3_Path, mp3Copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
					
					//Change Tag of mp3 to "Dummy_" + i + ".mp3"
					MP3File mp3CopyFile = new MP3File(mp3Copy);
					Tag tag = mp3CopyFile.getTag();
					tag.setField(FieldKey.TITLE, mp3CopyName);
					tag.setField(FieldKey.ALBUM, mp3CopyName);
					tag.setField(FieldKey.ARTIST, mp3CopyName);
					tag.setField(FieldKey.GENRE, mp3CopyName);
					
					mp3CopyFile.commit();
					mp3Copy.createNewFile();					
					
				} catch (IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotWriteException e) {
					e.printStackTrace();
				}
			}
		}else System.out.println("DemoMP3s directory already exists. Delete it");
	}
		
}
