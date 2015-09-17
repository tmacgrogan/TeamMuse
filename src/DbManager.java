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
		} catch (SQLException e) {
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
	
//	public ArrayList<Tag> getTags(Track track){
//		
//	}
	
	public void addTagToTrack(String tagName, Track track){
		
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
	}
	
}

