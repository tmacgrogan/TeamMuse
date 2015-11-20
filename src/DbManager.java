import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DbManager {
	// TODO Refactor with abstraction because to singleton.

	private static Connection connection;

	public static void setupConnection() {
		System.out.println("Opening database");
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:db/Snap.db");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
		try {
			Statement stmt = connection.createStatement();
			String[] statements = readSql("./db/SnapDbDef.sql");
			System.out.println("\tStarting table def");
			for (String stmtStr : statements) {
				System.out.println(stmtStr);
				stmt.executeUpdate(stmtStr);
			}
			stmt.close();
			// connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Track> importTracks(ArrayList<Track> trackList) {
		String updateStr = "INSERT INTO Track ( Name, FileLocation ) VALUES ( ?, ?);";
		for (Track t : trackList) {
			try {
				/******************** DEBUG ******************************/
				System.out
						.println("DbManager:importTracks: (before going into database) trackLocation: "
								+ t.getTrackLocation());

				safeUpdate(updateStr, t.getTitle(), t.getTrackLocation());
			} catch (SQLException e) {
				if (!e.getMessage()
						.equals("[SQLITE_CONSTRAINT]  Abort due to constraint violation (UNIQUE constraint failed: Track.FileLocation)")) {
					e.printStackTrace();
				}
			}
		}
		return trackList;
	}

	public static ArrayList<Tag> getTags(Track track) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		try {
			Statement stmt = connection.createStatement();
			String statementStr = readSql("./db/GetTagsForTrack.sql")[0];
			ResultSet results = safeQuery(statementStr,
					Integer.toString(track.getTrackId()));
			tags = tagListFromResult(results);
			stmt.close();
			// connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tags;
	}

	public static ArrayList<Track> getTracks(Tag tag) {
		ArrayList<Track> tracks = new ArrayList<Track>();
		try {
			Statement stmt = connection.createStatement();
			String statementStr = readSql("./db/GetTracksWithTag.sql")[0];
			ResultSet results = safeQuery(statementStr,
					Integer.toString(tag.getTagId()));
			while (results.next()) {
				/********************* DEBUG *********************************************/
				System.out
						.println("DbManager:getTracks: (after imported to database)this track name: "
								+ results.getString("FileLocation"));
				Track track = new Track(results.getString("FileLocation"),
						results.getInt("TrackId"));
				track.setImportDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(results.getString("CreatedDate")));
				tracks.add(track);
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tracks;
	}

	/**
	 * 
	 * @param trackName
	 *            - file location
	 * @return
	 */
	public static int getTrackId(String trackLocation) {
		int id = 0;
		ResultSet existing;
		String queryString = "SELECT * FROM Track WHERE FileLocation LIKE ?;";
		try {
			existing = safeQuery(queryString, trackLocation);
			if (!existing.next()) {
				id = -1;
			} else {
				id = existing.getInt("TrackId");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	public static boolean setTagName(String newName, int tagId) {
		try {
			safeUpdate("UPDATE Tag SET Name = ? WHERE TagId = " + tagId,
					newName);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// TODO split this into component functions
	// disallow [space] -atTheBeginning ( ) not
	public static Tag addTagToTrack(String tagName, Track track) {

		// VALIDATION: space, comma, dash, "not", parentheses, empty/whitespace
		String trimName = tagName.trim();
		if (trimName.equalsIgnoreCase("not") || trimName.equalsIgnoreCase(""))
			return null;

		String regex_begin = "[\\(,\\-\\s\\)].+";
		String regex_middle = ".+[\\s\\,\\(\\)].+";
		String regex_end = ".+[\\,\\(\\)]$";

		if (trimName.matches(regex_begin))
			return null;
		else if (trimName.matches(regex_middle))
			return null;
		else if (trimName.matches(regex_end))
			return null;
		// END VALIDATION

		ResultSet existing = null;
		Tag tag = null;
		String queryString = "SELECT * FROM Tag WHERE Name LIKE ?;";
		try {
			existing = safeQuery(queryString, tagName);
			if (!existing.next()) {
				safeUpdate("INSERT INTO Tag(Name) VALUES( ? );", tagName);
				existing = safeQuery(queryString, tagName);
				existing.next();
			}
			tag = new Tag(existing.getString("Name"), existing.getInt("TagId"));
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("INSERT INTO TrackTag(TrackId, TagId) VALUES( "
					+ track.getTrackId() + ", " + tag.getTagId() + ");");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tag;
	}

	public static ArrayList<Track> getLibrary() {
		ResultSet results;
		ArrayList<Track> library = new ArrayList<Track>();
		try {
			Statement stmt = connection.createStatement();
			results = stmt.executeQuery("SELECT * FROM Track;");
			while (results.next()) {
				Track libTrack = new Track(results.getString("FileLocation"),
						results.getInt("TrackId"));
				libTrack.setImportDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(results.getString("CreatedDate")));
				library.add(libTrack);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return library;
	}

	public static Date getTrackCreatedDate(int trackId) {
		ResultSet result;
		Date date = null;
		try {
			Statement stmt = connection.createStatement();
			result = stmt.executeQuery("SELECT CreatedDate FROM Track;");
			if (result.next()) {
				date = result.getDate("CreatedDate");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static int getTagId(String tagName) {
		int id = 0;
		ResultSet existing = null;
		String queryString = "SELECT * FROM Tag WHERE Name LIKE ?;";
		try {
			existing = safeQuery(queryString, tagName);
			if (!existing.next()) {
				id = -1;
			} else {
				int idCol = existing.findColumn("TagId");
				id = existing.getInt(idCol);
			}
		} catch (SQLException e) {
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
	// Catch SQLException in caller and prompt user that parent already attached
	// to this child
	public static boolean insertParentTagLink(int parentTagID, int childTagID) {
		String insert = ("INSERT INTO ParentTagLink(ParentTagId, ChildTagId) VALUES("
				+ parentTagID + "," + childTagID + ");");

		Statement stmt;
		boolean status = false;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(insert);
			status = true;
		} catch (SQLException e) {
			// Silent
		}
		return status;
	}

	public static void removeParentTagLink(int parentTagId, int childTagId) {
		try {
			String delete = "DELETE FROM ParentTagLink WHERE ParentTagId = "
					+ parentTagId + " AND ChildTagId = " + childTagId + ";";
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(delete);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Tag> getParents(int tagId) {
		String query = readSql("./db/GetParents.sql")[0];
		ResultSet result = null;
		ArrayList<Tag> parents = new ArrayList<Tag>();
		try {
			result = safeQuery(query, Integer.toString(tagId));
			parents = tagListFromResult(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return parents;
	}

	public static ArrayList<Tag> getChildren(int tagId) {
		String query = readSql("./db/GetChildren.sql")[0];
		ResultSet result = null;
		ArrayList<Tag> children = new ArrayList<Tag>();
		try {
			result = safeQuery(query, Integer.toString(tagId));
			children = tagListFromResult(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return children;
	}

	public static void saveSearch(String searchString) {
		try {
			safeUpdate("INSERT INTO Search(SearchText) VALUES( ? );",
					searchString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteSearch(String searchString){
		try {
			safeUpdate("DELETE FROM Search WHERE SearchText LIKE ?;",
					searchString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Search> getSavedSearches(){
		ArrayList<Search> searches = new ArrayList<Search>();
		ResultSet results = null;
		try {
			Statement stmt = connection.createStatement();
			results = stmt.executeQuery("SELECT * FROM Search;");
			while (results.next()) {
				Search search = new Search(results.getString("SearchText"));
				searches.add(search);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return searches;
	}
	
	/**
	 * Inserts tagName into Tag table and returns the tag object.
	 * 
	 * @param tagName
	 * @return Tag
	 */
	public static Tag insertTag(String tagName) {

		ResultSet existing = null;
		Tag tag = null;
		try {
			safeUpdate("INSERT INTO Tag(Name) VALUES( ? );", tagName);
			existing = safeQuery("SELECT * FROM Tag WHERE Name LIKE ?;",
					tagName);
			existing.next();

			int idCol = existing.findColumn("TagId");
			tag = new Tag(tagName, existing.getInt(idCol));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tag;
	}

	public static void removeTagFromTrack(Tag tag, Track track) {
		try {
			// System.out.println("Removing tag " + tag.getName() + " (" +
			// tag.getTagId() + ")");
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("DELETE FROM TrackTag WHERE TrackId = "
					+ track.getTrackId() + " AND TagId = " + tag.getTagId()
					+ ";");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// returns list of executable statements
	// ignores commented lines and empty lines
	private static String[] readSql(String sqlFile) {
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(sqlFile));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			String trimmedLine = line.trim();

			while (line != null) {
				if (!trimmedLine.equals("")
						&& !trimmedLine.substring(0, 2).equals("--")) { // exclude
																		// whitespace
																		// and
																		// comments
					sb.append(line);
					sb.append(System.lineSeparator());
				}
				line = br.readLine();
				if (line != null)
					trimmedLine = line.trim();
			}
			result = sb.toString();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.split("(?<=;)"); // separate by statement
	}

	private static ArrayList<Tag> tagListFromResult(ResultSet results)
			throws SQLException {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		while (results.next()) {
			Tag tag = new Tag(results.getString("Name"), results.getInt("TagId"));
			tags.add(tag);
		}
		return tags;
	}

	private static void safeUpdate(String... args) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(args[0]);
		for (int i = 1; i < args.length; i++) {
			statement.setString(i, args[i]);
		}
		statement.executeUpdate();
		statement.close();
	}

	private static ResultSet safeQuery(String... args) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(args[0]);
		for (int i = 1; i < args.length; i++) {
			statement.setString(i, args[i]);
		}
		return statement.executeQuery();
	}

}
