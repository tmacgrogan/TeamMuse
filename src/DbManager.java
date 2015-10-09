import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class DbManager {
	//TODO Refactor with abstraction because to singleton.
	
	private static Connection connection;
	
	public static void setupConnection(){
		System.out.println("Opening database");
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:db/Snap.db");
	    } catch ( Exception e ) {
		    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
	    }
		System.out.println("Opened database successfully");
		try {
			Statement stmt = connection.createStatement();
			String[] statements = readSql("./db/SnapDbDef.sql");
			System.out.println("\tStarting table def");
			for(String stmtStr : statements){
				stmt.executeUpdate(stmtStr);
			}
			stmt.close();
			//connection.commit();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static ArrayList<Track> importTracks(ArrayList<Track> trackList){
		String updateStr = "INSERT INTO Track ( Name, FileLocation ) VALUES ( ?, ?);";
		for(Track t : trackList){
			try {
				safeUpdate(updateStr, t.getTitle(), t.getTrackLocation());
			}
			catch (SQLException e) {
				if(!e.getMessage().equals("[SQLITE_CONSTRAINT]  Abort due to constraint violation (UNIQUE constraint failed: Track.FileLocation)")) {
					e.printStackTrace();
				}
			}
		}
		return trackList;
	}
	
	public static ArrayList<Tag> getTags(Track track){
		ArrayList<Tag> tags = new ArrayList<Tag>();
		try {
			Statement stmt = connection.createStatement();
			String statementStr = readSql("./db/GetTagsForTrack.sql")[0];
			ResultSet results = safeQuery(statementStr, Integer.toString(track.getTrackId()));
			int nameCol = 0;
			int idCol = 0;
			while(results.next()){
				if(nameCol == 0 || idCol == 0){
					nameCol = results.findColumn("Name");
					idCol = results.findColumn("TagId");
				}
				Tag tag = new Tag(results.getString(nameCol), results.getInt(idCol));
				tags.add(tag);
			}
			stmt.close();
			//connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tags;
	}
	
	public static ArrayList<Track> getTracks(Tag tag){
		ArrayList<Track> tracks = new ArrayList<Track>();
		try {
			Statement stmt = connection.createStatement();
			String statementStr = readSql("./db/GetTracksWithTag.sql")[0];
			ResultSet results = safeQuery(statementStr, Integer.toString(tag.getTagId()));
			int nameCol = 0;
			int idCol = 0;
			while(results.next()){
				if(nameCol == 0 || idCol == 0){
					nameCol = results.findColumn("Name");
					idCol = results.findColumn("TrackId");
				}
				Track track = new Track(results.getString(nameCol), results.getInt(idCol));
				tracks.add(track);
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tracks;
	}
	
	
	/**
	 * 
	 * @param trackName - file location
	 * @return
	 */
	//TODO getTrackId, maybe
	public static int getTrackId(String trackName){
		int id = 0;
		ResultSet existing;
		String queryString = "SELECT * FROM Track WHERE Name LIKE ?;";
		try{
			existing = safeQuery(queryString, trackName);
			if(!existing.next()){
				id = -1;
			}else{
				int idCol = existing.findColumn("TrackId");
				id = existing.getInt(idCol);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public static void setTagName(String newName, int tagId){
		try{
			safeUpdate("UPDATE Tag SET Name = ? WHERE TagId = " + tagId, newName);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	//TODO split this into component functions
	//disallow [space] -atTheBeginning ( ) not
	public static Tag addTagToTrack(String tagName, Track track){
		ResultSet existing = null;
		Tag tag = null;
		String queryString = "SELECT * FROM Tag WHERE Name LIKE ?;";
		try{
			existing = safeQuery(queryString, tagName);
			if(!existing.next()){
				safeUpdate("INSERT INTO Tag(Name) VALUES( ? );", tagName);
				existing = safeQuery(queryString, tagName);
				existing.next();
			}
			int nameCol = existing.findColumn("Name");
			int idCol = existing.findColumn("TagId");
			tag = new Tag(existing.getString(nameCol), existing.getInt(idCol));
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("INSERT INTO TrackTag(TrackId, TagId) VALUES( " + track.getTrackId() + ", " + tag.getTagId() + ");");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return tag;
	}
	
	public static ArrayList<Track> getLibrary(){
		ResultSet results;
		ArrayList<Track> library = new ArrayList<Track>();
		try{
			Statement stmt = connection.createStatement();
			results = stmt.executeQuery("SELECT TrackId, FileLocation FROM Track;");
			int locationCol = 0;
			int idCol = 0;
			while(results.next()){
				if(locationCol == 0 || idCol == 0){
					locationCol = results.findColumn("FileLocation");
					idCol = results.findColumn("TrackId");
				}
				Track libTrack = new Track(results.getString(locationCol), results.getInt(idCol));
				library.add(libTrack);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return library;
	}
	
	public static int getTagId(String tagName){
		int id = 0;
		ResultSet existing = null;
		String queryString = "SELECT * FROM Tag WHERE Name LIKE ?;";
		try{
			existing = safeQuery(queryString, tagName);
			if(!existing.next()){
				id = -1;
			}
			else{
				int idCol = existing.findColumn("TagId");
				id = existing.getInt(idCol);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	/**
	 * Attaches parent tag to child tag.
	 * 
	 * 
	 * @param parentTagID
	 * @param childTagID
	 * @throws SQLException
	 */
	//Catch SQLException in caller and prompt user that parent already attached to this child
	public static boolean insertParentTagLink(int parentTagID, int childTagID){
		String insert = ( "INSERT INTO ParentTagLink(ParentTagId, ChildTagId) VALUES(" + parentTagID + "," + childTagID + ");" );
		
			Statement stmt;
			boolean status = false;
			try {
				stmt = connection.createStatement();
				stmt.executeUpdate(insert);
				status = true;
			} catch (SQLException e) {
				//Silent 
			}
			return status;	
	}
	
	
	
	/**
	 * Inserts tagName into Tag table and returns the tag object.
	 * 
	 * @param tagName
	 * @return Tag
	 */
	public static Tag insertTag(String tagName){
		
		ResultSet existing = null;
		Tag tag = null;
		try {
			safeUpdate("INSERT INTO Tag(Name) VALUES( ? );", tagName);
			existing = safeQuery( "SELECT * FROM Tag WHERE Name LIKE ?;", tagName);
			existing.next();
			
			int idCol = existing.findColumn("TagId");
			tag = new Tag( tagName, existing.getInt(idCol) );
			
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return tag;
	}
	
	//returns list of executable statements
	//  ignores commented lines and empty lines
	private static String[] readSql(String sqlFile){
		String result = "";
		try{
			BufferedReader br = new BufferedReader(new FileReader(sqlFile));
			StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    String trimmedLine = line.trim();
		    
		    
		    while (line != null) {
		    	if(!trimmedLine.equals("") && !trimmedLine.substring(0, 2).equals("--")) {  //exclude whitespace and comments
			        sb.append(line);
			        sb.append(System.lineSeparator());
		    	}
		        line = br.readLine();
		        if(line != null)
		        	trimmedLine = line.trim();
		    }
		    result = sb.toString();
		    br.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return result.split("(?<=;)"); //separate by statement
	}
	
	public static void removeTagFromTrack(Tag tag, Track track){
		try{
			//System.out.println("Removing tag " + tag.getName() + " (" + tag.getTagId() + ")");
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("DELETE FROM TrackTag WHERE TrackId = " + track.getTrackId() + " AND TagId = " + tag.getTagId() + ";");
			stmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	
	private static void safeUpdate(String ... args) throws SQLException{
		PreparedStatement statement = connection.prepareStatement(args[0]);
		for(int i = 1; i < args.length; i++){
			statement.setString(i, args[i]);
		}
		statement.executeUpdate();
		statement.close();
	}
	
	private static ResultSet safeQuery(String ... args) throws SQLException{
		PreparedStatement statement = connection.prepareStatement(args[0]);
		for(int i = 1; i < args.length; i++){
			statement.setString(i, args[i]);
		}
		return statement.executeQuery();
	}
	
}

