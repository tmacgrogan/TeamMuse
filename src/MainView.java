import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainView {

	public ArrayList<Track> activeTrackList;
	private static JFrame frmSnap;
	private static Color frameBG = new Color(32, 32, 32);
	private static Color sideBG = new Color(64, 64, 64);
	private static Color middleBG = new Color(38,38,38);
	private static Dimension listSize = new Dimension(610, 445);
	private static int width = 1000;
	private static int height = 600;
	private static JTextField searchField;
	private static JTable songTable;
	private static DefaultTableModel model;
	
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
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private static void initialize() {
		//DbManager.setupConnection();
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
		middlePanel.setLayout(new BorderLayout(0, 0));
		
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
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class, String.class
			};
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
		
		//JTableHeader header = songTable.getTableHeader();
		//songPanel.add(header, BorderLayout.NORTH);
		
		JButton btnImport = new JButton("Import");
		btnImport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				ArrayList<Track> tracklist = TrackListController.importToSnap();
				
				model = (DefaultTableModel) songTable.getModel();
				
				for(int i = 0; i < tracklist.size(); i++){
					Track currTrack = tracklist.get(i);
					model.addRow(new Object[]{currTrack.getTitle(), currTrack.getArtist(), currTrack.getAlbum(), currTrack.getGenre()});
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
		
		JButton btnTag = new JButton("Tag");
		btnTag.setForeground(Color.GRAY);
		btnTag.setHorizontalAlignment(SwingConstants.LEFT);
		btnTag.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		GroupLayout gl_leftPanel = new GroupLayout(leftPanel);
		gl_leftPanel.setHorizontalGroup(
			gl_leftPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_leftPanel.createSequentialGroup()
					.addGap(20)
					.addGroup(gl_leftPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnSave)
						.addComponent(btnTag)
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
					.addGap(4)
					.addComponent(btnTag)
					.addGap(460))
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
