import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class DbManager {
	
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
		try {
			String updateStr = "INSERT INTO Track ( Name, FileLocation ) VALUES ( ?, ?);";
			for(Track t : trackList){
				safeUpdate(updateStr, t.getTitle(), t.getTrackLocation());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
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
					idCol = results.findColumn("TrackId");
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
	
	public static Tag addTagToTrack(String tagName, Track track){
		ResultSet existing;
		try{
			existing = safeQuery("SELECT * FROM Tag WHERE Name = ?;", tagName);
			if(!existing.next()){   //if tag does not yet exist
				existing = safeQuery("INSERT INTO Tag(Name) VALUES( ? );", tagName);
				existing.next();
			}
			int nameCol = existing.findColumn("Name");
			int idCol = existing.findColumn("TagId");
			Tag tag = new Tag(existing.getString(nameCol), existing.getInt(idCol));
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("INSERT INTO TrackTag(TrackId, TagId) VALUES( " + track.getTrackId() + ", " + tag.getTagId() + ");");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<Track> getLibrary(){
		ResultSet results;
		ArrayList<Track> library = new ArrayList<Track>();
		try{
			Statement stmt = connection.createStatement();
			results = stmt.executeQuery("SELECT TrackId, Location FROM Track;");
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
	
	public void deleteTagFromTrack(String tagName, Track track){
		
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
