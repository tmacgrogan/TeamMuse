import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import java.lang.reflect.Array;
import java.util.ArrayList;
//import java.util.List;


public class MainView {

	public static ArrayList<Track> activeTrackList;
	public static Track selectedTrack;
	public static Tag selectedTag;
	private static JFrame frmSnap;
	private static Color frameBG = new Color(32, 32, 32);
	private static Color sideBG = new Color(64, 64, 64);
	private static Color middleBG = new Color(38,38,38);
	//private static Dimension listSize = new Dimension(610, 445);
	private static int width = 1000;
	private static int height = 600;
	private static JTextField searchField;
	private static JTable songTable;
	private static DefaultTableModel trackModel;
	private static DefaultTableModel tagModel;
	private static JTextField TagInfo;
	private static JButton btnAddTag;
	public static ArrayList<Tag> activeTagList = new ArrayList<Tag>();
	private static JTextField AddTagField;
	private static JTable TagTable;
	private static JButton btnDeleteTag;
	/*************************************************************/
	
	
	
	public static void main(String[] args) {
		SnapMain();
		
		
//		ArrayList<Tag> testTagList = new ArrayList<Tag>();
//		for(int i = 0; i<226; i++){
//			testTagList.add(new Tag(""+i));
//			System.out.println(""+i+testTagList.get(i).getUniqueIdentifier());
//		}
//		System.out.println(testTagList.get(222).getUniqueIdentifier());
//		System.out.println(testTagList.get(223).getUniqueIdentifier());
//		System.out.println(testTagList.get(224).getUniqueIdentifier());
//		System.out.println(testTagList.get(225).getUniqueIdentifier());
//		Tag tag = new Tag("pop");
//		System.out.println( "First tag: " + tag.getUniqueIdentifier()  );
//		
//		Tag tag1 = new Tag("rap");
//		System.out.println("Second tag: " + tag1.getUniqueIdentifier() );
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//MainView window = new MainView();
					//window.frmSnap.setVisible(true);
					MainView.frmSnap.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public static void SnapMain() {
		initialize();
		
		activeTrackList = DbManager.getLibrary();
		
		trackModel = (DefaultTableModel) songTable.getModel();
		
		for(int i = 0; i < activeTrackList.size(); i++){
			Track currTrack = activeTrackList.get(i);
			trackModel.addRow(new Object[]{currTrack.getTitle(), currTrack.getArtist(), currTrack.getAlbum(), currTrack.getGenre()});
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private static void initialize() {
		/** Make dummy mp3s */
		//Util_DemoMP3.copyMP3(int numOfCopies );
		
		
		DbManager.setupConnection();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		frmSnap = new JFrame();
		frmSnap.setTitle("Snap");
		frmSnap.getContentPane().setBackground(frameBG);
		
		JLabel lblMenu = new JLabel("Menu");
		lblMenu.setHorizontalAlignment(SwingConstants.LEFT);
		lblMenu.setBackground(Color.GRAY);
		lblMenu.setFont(new Font("Lucida Sans", Font.PLAIN, 13));
		lblMenu.setForeground(Color.WHITE);
		lblMenu.setOpaque(true);
		
		JLabel lblSongList = new JLabel("Song List");
		lblSongList.setHorizontalAlignment(SwingConstants.CENTER);
		lblSongList.setBackground(Color.GRAY);
		lblSongList.setFont(new Font("Lucida Sans", Font.PLAIN, 13));
		lblSongList.setForeground(Color.WHITE);
		lblSongList.setOpaque(true);
		
		JLabel lblDetails = new JLabel("Details");
		lblDetails.setOpaque(true);
		lblDetails.setHorizontalAlignment(SwingConstants.LEFT);
		lblDetails.setForeground(Color.WHITE);
		lblDetails.setFont(new Font("Lucida Sans", Font.PLAIN, 13));
		lblDetails.setBackground(Color.GRAY);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setBackground(sideBG);
		
		JPanel middlePanel = new JPanel();
		middlePanel.setBorder(new LineBorder(Color.DARK_GRAY));
		middlePanel.setForeground(Color.WHITE);
		middlePanel.setBackground(frameBG);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(sideBG);
		
		GroupLayout groupLayout = new GroupLayout(frmSnap.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(leftPanel, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(middlePanel, GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblMenu, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblSongList, GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblDetails, GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMenu)
						.addComponent(lblSongList)
						.addComponent(lblDetails))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
								.addComponent(leftPanel, GroupLayout.PREFERRED_SIZE, 556, Short.MAX_VALUE))
							.addGap(12))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(middlePanel, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
							.addContainerGap())))
		);
		rightPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblTagName = new JLabel("Tag Information");
		lblTagName.setForeground(Color.LIGHT_GRAY);
		lblTagName.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblTagName.setHorizontalAlignment(SwingConstants.CENTER);
		rightPanel.add(lblTagName, BorderLayout.NORTH);
		
		JPanel AddTagPanel = new JPanel();
		AddTagPanel.setForeground(Color.WHITE);
		AddTagPanel.setBackground(Color.DARK_GRAY);
		rightPanel.add(AddTagPanel, BorderLayout.SOUTH);
		
		btnAddTag = new JButton("Add Tag");
		btnAddTag.setEnabled(false);
		btnAddTag.setForeground(Color.BLACK);
		btnAddTag.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btnAddTag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedTrack.addTag(AddTagField.getText());
				updateTagTable();
				AddTagField.setText("");
			}
		});
		AddTagPanel.setLayout(new BorderLayout(0, 0));
		
		AddTagField = new JTextField();
		AddTagField.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		AddTagField.setHorizontalAlignment(SwingConstants.CENTER);
		AddTagField.setEnabled(false);
		AddTagField.setColumns(10);
		AddTagPanel.add(AddTagField);
		btnAddTag.setPreferredSize(new Dimension(60, 30));
		btnAddTag.setBackground(Color.DARK_GRAY);
		AddTagPanel.add(btnAddTag, BorderLayout.SOUTH);
		
		JPanel TagInfoPanel = new JPanel();
		TagInfoPanel.setForeground(Color.WHITE);
		TagInfoPanel.setBackground(Color.DARK_GRAY);
		rightPanel.add(TagInfoPanel, BorderLayout.CENTER);
		TagInfoPanel.setLayout(new BorderLayout(0, 0));
		
		TagInfo = new JTextField();
		TagInfo.setHorizontalAlignment(SwingConstants.CENTER);
		TagInfo.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		TagInfoPanel.add(TagInfo, BorderLayout.NORTH);
		TagInfo.setForeground(Color.WHITE);
		TagInfo.setBackground(Color.DARK_GRAY);
		TagInfo.setColumns(13);
		TagInfo.setEditable(false);
		
		TagTable = new JTable();
		TagTable.setShowGrid(false);
		TagTable.setForeground(Color.LIGHT_GRAY);
		TagTable.setModel(new DefaultTableModel(new Object[][]{},new String[]{"Tag"}) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[]{String.class};
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		TagTable.getColumnModel().getColumn(0).setResizable(false);
		TagTable.getColumnModel().getColumn(0).setMaxWidth(2147483599);
		TagTable.setBackground(Color.DARK_GRAY);
		TagTable.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		TagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TagTable.setShowVerticalLines(false);
		TagInfoPanel.add(TagTable, BorderLayout.CENTER);
		
		btnDeleteTag = new JButton("Delete Tag");
		btnDeleteTag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDeleteTag.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
	        	
				selectedTag = activeTagList.get(TagTable.getSelectedRow());

				selectedTrack.removeTag(selectedTag);
				
				updateTagTable();
			}
		});
		btnDeleteTag.setEnabled(false);
		TagInfoPanel.add(btnDeleteTag, BorderLayout.SOUTH);
		middlePanel.setLayout(new BorderLayout(0, 0));
		
		TagTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        			
	        	btnDeleteTag.setEnabled(true);
	        	
	            //System.out.println(songTable.getValueAt(songTable.getSelectedRow(), 0).toString());
	        }
	    });
		
		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		searchPanel.setBackground(middleBG);
		middlePanel.add(searchPanel, BorderLayout.NORTH);
		searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		searchField = new JTextField();
		searchPanel.add(searchField);
		searchField.setColumns(30);
		
		JLabel lblSearch = new JLabel("Search");
		searchPanel.add(lblSearch);
		lblSearch.setForeground(Color.GRAY);
		
		JPanel playerPanel = new JPanel();
		playerPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		playerPanel.setBackground(middleBG);
		middlePanel.add(playerPanel, BorderLayout.SOUTH);
		
		JButton btnMusicPlayer = new JButton("Music Player");
		playerPanel.add(btnMusicPlayer);
		
		JPanel songPanel = new JPanel();
		middlePanel.add(songPanel, BorderLayout.CENTER);
		
		songTable = new JTable();
		songTable.setAlignmentY(Component.TOP_ALIGNMENT);
		songTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		songTable.setShowVerticalLines(false);
		songTable.setShowHorizontalLines(false);
		songTable.setShowGrid(false);
		songTable.setForeground(Color.WHITE);
		songTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Name", "Artist", "Album", "Genre"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class
			};
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		songTable.getColumnModel().getColumn(0).setPreferredWidth(180);
		songTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		songTable.getColumnModel().getColumn(2).setPreferredWidth(150);
		songTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		songTable.setOpaque(true);
		songTable.setBackground(middleBG);
		songPanel.setOpaque(true);
		songPanel.setBackground(middleBG);
		songPanel.setLayout(new BoxLayout(songPanel, BoxLayout.X_AXIS));
		songPanel.add(songTable);
		
		songTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	
	        	selectedTrack = activeTrackList.get(songTable.getSelectedRow());
	        			
	        	btnAddTag.setEnabled(true);
	        	AddTagField.setEnabled(true);
	        	
	        	updateTagTable();
	            //System.out.println(songTable.getValueAt(songTable.getSelectedRow(), 0).toString());
	        }
	    });
		
		JButton btnImport = new JButton("Import");
		btnImport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				TrackListController.importToSnap();
				
				activeTrackList = DbManager.getLibrary();
				
				trackModel = (DefaultTableModel) songTable.getModel();
				
				int trows = trackModel.getRowCount(); 
		    	for(int i = trows - 1; i >=0; i--){
		    		trackModel.removeRow(i); 
		    	}
				
				for(int i = 0; i < activeTrackList.size(); i++){
					Track currTrack = activeTrackList.get(i);
					trackModel.addRow(new Object[]{currTrack.getTitle(), currTrack.getArtist(), currTrack.getAlbum(), currTrack.getGenre()});
				}
			}
		});
		btnImport.setForeground(Color.GRAY);
		btnImport.setBackground(Color.DARK_GRAY);
		btnImport.setHorizontalAlignment(SwingConstants.LEFT);
		btnImport.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		
		JButton btnSave = new JButton("Save");
		btnSave.setForeground(Color.GRAY);
		btnSave.setHorizontalAlignment(SwingConstants.LEFT);
		btnSave.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GroupLayout gl_leftPanel = new GroupLayout(leftPanel);
		gl_leftPanel.setHorizontalGroup(
			gl_leftPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_leftPanel.createSequentialGroup()
					.addGap(20)
					.addGroup(gl_leftPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnSave)
						.addComponent(btnImport))
					.addGap(100))
		);
		gl_leftPanel.setVerticalGroup(
			gl_leftPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_leftPanel.createSequentialGroup()
					.addGap(5)
					.addComponent(btnImport)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSave)
					.addGap(487))
		);
		leftPanel.setLayout(gl_leftPanel);
		btnImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		frmSnap.getContentPane().setLayout(groupLayout);
		frmSnap.setBounds((screen.width/2)-(width/2), (screen.height/2)-(height/2), width, height);
		frmSnap.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	

	private static void updateTagTable() {
		// TODO Auto-generated method stub
		tagModel = (DefaultTableModel) TagTable.getModel();
    	
    	//clears row to be ready to display new set of tags
    	int rows = tagModel.getRowCount(); 
    	for(int i = rows - 1; i >=0; i--){
    		tagModel.removeRow(i); 
    	}
    	
    	activeTagList = selectedTrack.getTags();
    	
    	//populates rows with tags of selected track
    	for(int i = 0; i < activeTagList.size(); i++){
    		tagModel.addRow(new Object[]{activeTagList.get(i).getName()});
    	}
    	
    	TagInfo.setText(songTable.getValueAt(songTable.getSelectedRow(), 0).toString());
	}

	
	/** Adds all .mp3 files in specified folder into the Library and updates activeTrackList
	 * @param folderLocation location of folder containing files to import 
	 */
//	private void importFiles(String folderLocation){
//		
//	}
	
	
	/**Converts the search parameters passed by the user into a Search object and then sets the activeTracklist to the results of executing the search
	 * @param searchParams
	 */
//	private void search(String searchParams){
//		
//	}	
	
	/**resets the activeTrackList to the whole Library
	 * 
	 */
//	private void clearSearch(){
//		
//	}
}
