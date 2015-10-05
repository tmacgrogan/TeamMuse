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
import javax.swing.table.JTableHeader;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainView {

	public static ArrayList<Track> activeTrackList;
	public static ArrayList<Tag> activeTagList = new ArrayList<Tag>();
	
	private static Dimension listSize = new Dimension(610, 445);
	
	public static Track selectedTrack;
	public static Tag selectedTag;
	
	private static JFrame frmSnap;
	
	private static Color frameBG = new Color(32, 32, 32);
	private static Color sideBG = new Color(64, 64, 64);
	private static Color middleBG = new Color(38,38,38);
	
	private static int width = 1000;
	private static int height = 600;
	
	private static JTextField tagInfo;
	private static JTextField searchField;
	private static JTextField addTagField;
	
	private static JTable trackTable;
	private static JTable tagTable;
	private static JTableHeader header;
	
	private static JPanel leftPanel;
	private static JPanel middlePanel;
	private static JPanel rightPanel;
	private static JPanel addTagPanel;
	private static JPanel tagInfoPanel;
	private static JPanel searchPanel;
	private static JPanel playerPanel;
	private static JPanel songPanel;
	
	private static DefaultTableModel trackModel;
	private static DefaultTableModel tagModel;
	
	private static JButton btnImport;
	private static JButton btnAddTag;
	private static JButton btnDeleteTag;
	private static JButton btnMusicPlayer;
	private static JButton btnSave;

	private static JLabel lblMenu;
	private static JLabel lblSongList;
	private static JLabel lblDetails;
	private static JLabel lblTagName;
	private static JLabel lblSearch;
	
	private static GroupLayout groupLayout;
	private static GroupLayout gl_leftPanel;
	
	private static JScrollPane scrollPane;
	
	
	/*************************************************************/
	
	public static void main(String[] args) {
		SnapMain();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView window = new MainView();
					window.frmSnap.setVisible(true);
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
		
		trackModel = (DefaultTableModel) trackTable.getModel();
		
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
		tagTable = new JTable();
		
		lblMenu = new JLabel("Menu");
		lblSongList = new JLabel("Song List");
		lblDetails = new JLabel("Details");
		
		leftPanel = new JPanel();
		middlePanel = new JPanel();
		rightPanel = new JPanel();
		addTagPanel = new JPanel();
		tagInfoPanel = new JPanel();
		searchPanel = new JPanel();
		songPanel = new JPanel();
		playerPanel = new JPanel();
		
		lblTagName = new JLabel("Tag Information");
		lblSearch = new JLabel("Search");
		
		btnAddTag = new JButton("Add Tag");
		btnDeleteTag = new JButton("Delete Tag");
		btnImport = new JButton("Import");
		btnSave = new JButton("Save");
		btnMusicPlayer = new JButton("Music Player");
		
		addTagField = new JTextField();
		tagInfo = new JTextField();
		searchField = new JTextField();
		
		gl_leftPanel = new GroupLayout(leftPanel);
		groupLayout = new GroupLayout(frmSnap.getContentPane());
		
		frmSnap.setTitle("Snap");
		frmSnap.getContentPane().setBackground(frameBG);
		
		lblMenu.setHorizontalAlignment(SwingConstants.LEFT);
		lblMenu.setBackground(Color.GRAY);
		lblMenu.setFont(new Font("Lucida Sans", Font.PLAIN, 13));
		lblMenu.setForeground(Color.WHITE);
		lblMenu.setOpaque(true);
		
		lblSongList.setHorizontalAlignment(SwingConstants.CENTER);
		lblSongList.setBackground(Color.GRAY);
		lblSongList.setFont(new Font("Lucida Sans", Font.PLAIN, 13));
		lblSongList.setForeground(Color.WHITE);
		lblSongList.setOpaque(true);
		
		lblDetails.setOpaque(true);
		lblDetails.setHorizontalAlignment(SwingConstants.LEFT);
		lblDetails.setForeground(Color.WHITE);
		lblDetails.setFont(new Font("Lucida Sans", Font.PLAIN, 13));
		lblDetails.setBackground(Color.GRAY);
		
		leftPanel.setBackground(sideBG);
		
		middlePanel.setBorder(new LineBorder(Color.DARK_GRAY));
		middlePanel.setForeground(Color.WHITE);
		middlePanel.setBackground(frameBG);
		
		rightPanel.setBackground(sideBG);
		
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
		
		lblTagName.setForeground(Color.LIGHT_GRAY);
		lblTagName.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblTagName.setHorizontalAlignment(SwingConstants.CENTER);
		rightPanel.add(lblTagName, BorderLayout.NORTH);
		
		addTagPanel.setForeground(Color.WHITE);
		addTagPanel.setBackground(Color.DARK_GRAY);
		rightPanel.add(addTagPanel, BorderLayout.SOUTH);
		
		btnAddTag.setEnabled(false);
		btnAddTag.setForeground(Color.BLACK);
		btnAddTag.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		btnAddTag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedTrack.addTag(addTagField.getText());
				updateTagTable();
				addTagField.setText("");
			}
		});
		addTagPanel.setLayout(new BorderLayout(0, 0));
		
		addTagField.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		addTagField.setHorizontalAlignment(SwingConstants.CENTER);
		addTagField.setEnabled(false);
		addTagField.setColumns(10);
		addTagPanel.add(addTagField);
		btnAddTag.setPreferredSize(new Dimension(60, 30));
		btnAddTag.setBackground(Color.DARK_GRAY);
		addTagPanel.add(btnAddTag, BorderLayout.SOUTH);
		
		tagInfoPanel.setForeground(Color.WHITE);
		tagInfoPanel.setBackground(Color.DARK_GRAY);
		rightPanel.add(tagInfoPanel, BorderLayout.CENTER);
		tagInfoPanel.setLayout(new BorderLayout(0, 0));
		
		tagInfo.setHorizontalAlignment(SwingConstants.CENTER);
		tagInfo.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		tagInfoPanel.add(tagInfo, BorderLayout.NORTH);
		tagInfo.setForeground(Color.WHITE);
		tagInfo.setBackground(Color.DARK_GRAY);
		tagInfo.setColumns(13);
		tagInfo.setEditable(false);
		
		tagTable.setShowGrid(false);
		tagTable.setForeground(Color.LIGHT_GRAY);
		tagTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Tag"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class
			};
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
		tagTable.getColumnModel().getColumn(0).setResizable(false);
		tagTable.getColumnModel().getColumn(0).setMaxWidth(2147483599);
		tagTable.setBackground(Color.DARK_GRAY);
		tagTable.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		tagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tagTable.setShowVerticalLines(false);
		tagInfoPanel.add(tagTable, BorderLayout.CENTER);
		
		btnDeleteTag.setEnabled(false);
		btnDeleteTag.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
	        	
				selectedTag = activeTagList.get(tagTable.getSelectedRow());

				selectedTrack.removeTag(selectedTag);
				
				updateTagTable();
			}
		});
		
		tagInfoPanel.add(btnDeleteTag, BorderLayout.SOUTH);
		middlePanel.setLayout(new BorderLayout(0, 0));
		
		tagTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        			
	        	btnDeleteTag.setEnabled(true);
	        	
	            //System.out.println(songTable.getValueAt(songTable.getSelectedRow(), 0).toString());
	        }
	    });
		
		searchPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		searchPanel.setBackground(middleBG);
		searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		middlePanel.add(searchPanel, BorderLayout.NORTH);
		
		searchField.setColumns(30);
		searchPanel.add(searchField);
		
		lblSearch.setForeground(Color.GRAY);
		searchPanel.add(lblSearch);
		
		playerPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		playerPanel.setBackground(middleBG);
		middlePanel.add(playerPanel, BorderLayout.SOUTH);
		
		playerPanel.add(btnMusicPlayer);
		
		middlePanel.add(songPanel, BorderLayout.CENTER);
		
		songPanel.setOpaque(true);
		songPanel.setBackground(middleBG);
		songPanel.setLayout(new BoxLayout(songPanel, BoxLayout.X_AXIS));
		
		scrollPane = new JScrollPane();
		songPanel.add(scrollPane);
		
		trackTable = new JTable();
		scrollPane.setViewportView(trackTable);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(middleBG);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		trackTable.setAlignmentY(Component.TOP_ALIGNMENT);
		trackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		trackTable.setShowVerticalLines(false);
		trackTable.setShowHorizontalLines(false);
		trackTable.setShowGrid(false);
		trackTable.setForeground(Color.WHITE);
		trackTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Name", "Artist", "Album", "Genre"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		trackTable.getColumnModel().getColumn(0).setPreferredWidth(180);
		trackTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		trackTable.getColumnModel().getColumn(2).setPreferredWidth(150);
		trackTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		trackTable.setOpaque(true);
		trackTable.setBackground(middleBG);
		
		trackTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	if (trackTable.getSelectedRows().length == 1){
	        		selectedTrack = activeTrackList.get(trackTable.getSelectedRow());
	        		updateTagTable();
	        	}
	        			
	        	btnAddTag.setEnabled(true);
	        	addTagField.setEnabled(true);
	        	
	        	updateTagTable();
	            //System.out.println(songTable.getValueAt(songTable.getSelectedRow(), 0).toString());
	        }
	    });
		
		trackTable.setRowSelectionAllowed(true);
		trackTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		header = trackTable.getTableHeader();
	    header.setBackground(middleBG);
	    header.setForeground(Color.white);
		
		
		btnImport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				TrackListController.importToSnap();
				updateTrackTable();
			}
		});
		btnImport.setForeground(Color.GRAY);
		btnImport.setBackground(Color.DARK_GRAY);
		btnImport.setHorizontalAlignment(SwingConstants.LEFT);
		btnImport.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		
		btnSave.setForeground(Color.GRAY);
		btnSave.setHorizontalAlignment(SwingConstants.LEFT);
		btnSave.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		
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
		frmSnap.getContentPane().setLayout(groupLayout);
		frmSnap.setBounds((screen.width/2)-(width/2), (screen.height/2)-(height/2), width, height);
		frmSnap.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	private static void updateTrackTable(){
		
		activeTrackList = DbManager.getLibrary();
		
		clearTable(trackTable);
		
		for(int i = 0; i < activeTrackList.size(); i++){
			Track currTrack = activeTrackList.get(i);
			trackModel.addRow(new Object[]{currTrack.getTitle(), currTrack.getArtist(), currTrack.getAlbum(), currTrack.getGenre()});
		}
	}
	
	private static void updateTagTable() {
		// TODO Auto-generated method stub
		tagModel = (DefaultTableModel) tagTable.getModel();
    	
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
    	
    	tagInfo.setText(trackTable.getValueAt(trackTable.getSelectedRow(), 0).toString());
	}
	
	/**
	 * Clears all rows of table
	 * @param T 
	 */
	private static void clearTable(JTable T){
		
		DefaultTableModel currModel = (DefaultTableModel) T.getModel();
		
		int rows = currModel.getRowCount(); 
    	for(int i = rows - 1; i >=0; i--){
    		currModel.removeRow(i); 
    	}	
	}
	
	/** Adds all .mp3 files in specified folder into the Library and updates activeTrackList
	 * @param folderLocation location of folder containing files to import 
	 */
	private void importFiles(String folderLocation){
		
	}
	
	/**Converts the search parameters passed by the user into a Search object and then sets the activeTracklist to the results of executing the search
	 * @param searchParams
	 */
	private void search(String searchParams){
		
	}	
	
	/**resets the activeTrackList to the whole Library
	 * 
	 */
	private void clearSearch(){
		
	}
}
