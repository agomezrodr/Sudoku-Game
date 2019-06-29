
package sudoku.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;

import com.sun.glass.events.KeyEvent;

import sudoku.model.Board;

/**
 * A dialog template for playing simple Sudoku games.
 */
@SuppressWarnings("serial")
public class SudokuDialog extends JFrame {

	/** Default dimension of the dialog. */
	private final static Dimension DEFAULT_SIZE = new Dimension(310, 430);

	/** Directory of images and sounds. */
	private final static String IMAGE_DIR = "/image/";
	private final static String SOUND_DIR = "/sound/";
	URL soundUrl = getClass().getResource(SOUND_DIR + "solved.au");

	/** Extra variables.*/
	static int x, x1, y, y1, squareSize;
	boolean mistake;
	int counterMov = 1;
	String host;
	int port = 8000;

	/** Sudoku board. */
	private Board board;

	/** undoing and redoing buttons. */
	JButton und;
	JButton red;
	/** LinkedList storing movements. */
	LinkedList<int []> movements = new LinkedList<int []>();

	/** Array containing main buttons. */
	JButton [] bts = new JButton[10];

	/** Special panel to display a Sudoku board. */
	private BoardPanel boardPanel;

	/** Message bar to display various messages. */
	private static JLabel msgBar = new JLabel("");

	/** Create a new dialog. */
	public SudokuDialog() {
		this(DEFAULT_SIZE);
	}

	/** Create a new dialog of the given screen dimension. */
	public SudokuDialog(Dimension dim) {
		super("Sudoku Game");
		setSize(dim);
		board = new Board(9);
		boardPanel = new BoardPanel(board, this::boardClicked);
		configureUI();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		board.fillSudoku(board.size, 25);
		for(int i = 0; i <= board.size; i++) {bts[i].setEnabled(false);}
		repaint();
	}

	/**
	 * Callback to be invoked when a square of the board is clicked.
	 * @param x 0-based row index of the clicked square.
	 * @param y 0-based column index of the clicked square.
	 */
	private void boardClicked(int x, int y) {
		if(board.isSolved(board.sudoku) != true) {
			x1 = x+1;
			y1 = y+1;
			checkButtons(y, x);
			showMessage(String.format("Board clicked: x = %d, y = %d",  x+1, y+1));
		}
		repaint();
	}

	/** Checks what buttons are going to be enabled depending on the selected square. */
	private void checkButtons(int r, int c) {
		if(board.flag[r][c] != true) {
			for(int i = 1; i <= board.size; i++) {
				if(board.checkValue(board.sudoku, r, c, i, board.size) == true) {bts[i-1].setEnabled(false);}
				else {bts[i-1].setEnabled(true);}
			}
			if(board.sudoku.get(r).get(c) == 0) {bts[9].setEnabled(false);}
			else {bts[9].setEnabled(true);}
		}
		else {
			for(int i = 1; i <= board.size; i++) {bts[i-1].setEnabled(false);}
			bts[9].setEnabled(false);
		}
	}

	/**
	 * Callback to be invoked when a number button is clicked.
	 * @param number Clicked number (1-9), or 0 for "X".
	 */
	private void numberClicked(int number) {
		if(board.flag[y][x] != true) {
			if(number == 0) {
				showMessage(String.format(" "));
				if(board.sudoku.get(y).get(x) != 0) {
					und.setEnabled(true);	
					red.setEnabled(false);
					board.sudoku.get(y).set(x, 0);
					checkButtons(y, x);
					int [] mov = new int [3];
					mov[0] = y; mov[1] = x; mov[2] = number; 
					if(movements.size() + 1 == counterMov) {movements.add(mov);}
					else {
						int sz = movements.size();
						for(int i = sz; i >= counterMov; i--) {movements.removeLast();}
						movements.add(mov);
					}
					counterMov = counterMov + 1;
					repaint();
				}
			}
			else {
				if(x1 != 0 && y1 != 0 && board.sudoku.get(y).get(x) != number) {
					mistake = board.checkValue(board.sudoku, y, x, number, board.size);
					if(mistake == true) {showMessage(String.format("Conflict Number!"));}
					else {
						und.setEnabled(true);	
						red.setEnabled(false);
						board.sudoku.get(y).set(x, number);
						checkButtons(y, x);
						int [] mov = new int [3];
						mov[0] = y; mov[1] = x; mov[2] = number; 
						if(movements.size() + 1 == counterMov) {movements.add(mov);}
						else {
							int sz = movements.size();
							for(int i = sz; i >= counterMov; i--) {movements.removeLast();}
							movements.add(mov);
						}
						counterMov = counterMov + 1;
						if(board.isSolved(board.sudoku) == true) {
							play(soundUrl);
							for(int i = 0; i <= board.size; i++) {bts[i].setEnabled(false);}
							if(board.size == 4) {bts[9].setEnabled(false);}
							und.setEnabled(false);red.setEnabled(false);
							showMessage(String.format("Solved!"));
						}
						repaint();
					}
				}  
			}
		}
	}

	/**
	 * Callback to be invoked when a new button is clicked.
	 * If the current game is over, start a new game of the given size;
	 * otherwise, prompt the user for a confirmation and then proceed
	 * accordingly.
	 * @param size Requested puzzle size, either 4 or 9.
	 */
	private void newClicked() {
		//Create new game frame with options to select level and board size.
		JFrame f= new JFrame("New Game"); 
		f.setSize(400,400);f.setVisible(true); 
		JPanel contentn = new JPanel(), contentc = new JPanel(), contents = new JPanel(), 
				contentcn = new JPanel(), contentcs = new JPanel(),contentccn = new JPanel(), contentccs = new JPanel();
		JLabel label1 = new JLabel(), label2 = new JLabel();
		label1.setText("Quit the current Game?");label2.setText("Select the board size.");

		contentn.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
		contentn.setLayout(new BorderLayout());
		contentn.add(label1, BorderLayout.NORTH);
		contentn.add(label2, BorderLayout.SOUTH);
		f.add(contentn, BorderLayout.NORTH);

		contentccn.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Board Size"));
		contentccn.setLayout(new BorderLayout());
		contentcn.setBorder(BorderFactory.createEmptyBorder(45,125,45,125));
		contentcn.setLayout(new BorderLayout());
		JCheckBox checkbox1 = new JCheckBox("4 X 4"), checkbox2 = new JCheckBox("9 X 9", true);

		checkbox1.addActionListener(e -> {checkbox2.setSelected(false);});
		checkbox2.addActionListener(e -> {checkbox1.setSelected(false);});

		contentcn.add(checkbox1, BorderLayout.WEST);
		contentcn.add(checkbox2, BorderLayout.EAST);
		contentccn.add(contentcn, BorderLayout.CENTER);

		contentccs.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Difficulty Level"));
		contentccs.setLayout(new BorderLayout());

		JCheckBox checkbox3 = new JCheckBox("Easy"), checkbox4 = new JCheckBox("Normal", true), checkbox5 = new JCheckBox("Hard");

		checkbox3.addActionListener(e -> {checkbox4.setSelected(false);checkbox5.setSelected(false);});
		checkbox4.addActionListener(e -> {checkbox3.setSelected(false);checkbox5.setSelected(false);});
		checkbox5.addActionListener(e -> {checkbox3.setSelected(false);checkbox4.setSelected(false);});

		contentcs.setBorder(BorderFactory.createEmptyBorder(50,90,50,90));
		contentcs.setLayout(new BorderLayout());
		contentcs.add(checkbox3, BorderLayout.WEST);
		contentcs.add(checkbox4, BorderLayout.CENTER);
		contentcs.add(checkbox5, BorderLayout.EAST);
		contentccs.add(contentcs, BorderLayout.CENTER);

		contentc.setLayout(new BorderLayout());
		contentc.setLayout(new BorderLayout());
		contentc.add(contentccn, BorderLayout.NORTH);
		contentc.add(contentccs, BorderLayout.CENTER);

		f.add(contentc, BorderLayout.CENTER);

		contents.setBorder(BorderFactory.createEmptyBorder(5,95,5,95));
		contents.setLayout(new BorderLayout());

		JButton buttonp = new JButton("Play"), buttonc = new JButton("Cancel");

		contents.add(buttonp, BorderLayout.WEST);
		contents.add(buttonc, BorderLayout.EAST);
		f.add(contents, BorderLayout.SOUTH);


		buttonp.addActionListener(e -> {
			x1 = 0; y1 = 0;

			if(checkbox1.isSelected()) {
				setSize(4);
				if(checkbox3.isSelected()) {board.fillSudoku(board.size, 8);}
				if(checkbox4.isSelected()) {board.fillSudoku(board.size, 7);}
				if(checkbox5.isSelected()) {board.fillSudoku(board.size, 4);}
			}

			if(checkbox2.isSelected()) {
				setSize(9);
				if(checkbox3.isSelected()) {
					board.fillSudoku(board.size, 33);
					repaint();
					f.dispose();
				}
				if(checkbox4.isSelected()) {
					board.fillSudoku(board.size, 25);
					repaint();
					f.dispose();
				}
				if(checkbox5.isSelected()) {
					board.fillSudoku(board.size, 20);
					repaint();
					f.dispose();
				}
			}
			repaint();
			f.dispose();
		});
		buttonc.addActionListener(e -> {f.dispose();});
	}

	/** Set the size of the new board depending on the oprion that the player selected. */
	private void setSize(int s) {
		for(int i = 0; i <= board.size; i++) {bts[i].setEnabled(false);}
		board.size = s;
		und.setEnabled(false);red.setEnabled(false);
		board.newBoard(s);
		movements.clear();
		counterMov = 1;
		showMessage(String.format(" "));
	}

	/**
	 * Display the given string in the message bar.
	 * @param msg Message to be displayed.
	 */
	private void showMessage(String msg) {msgBar.setText(msg);}

	/** Configure the UI. */
	private void configureUI() {
		setIconImage(createImageIcon("sudoku.png").getImage());
		setLayout(new BorderLayout());

		JPanel buttons = makeControlPanel();
		// boarder: top, left, bottom, right
		add(buttons, BorderLayout.NORTH);

		JPanel board = new JPanel();
		board.setBorder(BorderFactory.createEmptyBorder(10,16,0,16));
		board.setLayout(new GridLayout(1,1));
		board.add(boardPanel);
		add(board, BorderLayout.CENTER);

		msgBar.setBorder(BorderFactory.createEmptyBorder(10,16,10,0));
		add(msgBar, BorderLayout.SOUTH);
	}

	/** Create a control panel consisting of new and number buttons. */
	private JPanel makeControlPanel() {
		//Icons that are used for buttons and menu options.
		ImageIcon playi = new ImageIcon(createImageIcon("play.png").getImage());
		ImageIcon checki = new ImageIcon(createImageIcon("check.png").getImage());
		ImageIcon solvei = new ImageIcon(createImageIcon("solve.png").getImage());
		ImageIcon disconnectedi = new ImageIcon(createImageIcon("disconnected.png").getImage());
		ImageIcon exiti = new ImageIcon(createImageIcon("exit.png").getImage());
		und = new JButton(new ImageIcon(createImageIcon("und.png").getImage()));
		red = new JButton(new ImageIcon(createImageIcon("red.png").getImage()));
		//menu and its options.
		JPanel menu = new JPanel(new FlowLayout());
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem newMenuItem = new JMenuItem("New Game     Alt+N");
		newMenuItem.setIcon(playi);
		newMenuItem.setMnemonic(KeyEvent.VK_N);
		newMenuItem.addActionListener(e -> {newClicked();});

		JMenuItem checkMenuItem = new JMenuItem("Check             Alt+C");
		checkMenuItem.setIcon(checki);
		checkMenuItem.setMnemonic(KeyEvent.VK_C);
		checkMenuItem.addActionListener(e -> {checkValid();});

		JMenuItem solveMenuItem = new JMenuItem("Solve              Alt+S");
		solveMenuItem.setIcon(solvei);
		solveMenuItem.setMnemonic(KeyEvent.VK_S);
		solveMenuItem.addActionListener(e -> {solve();;});

		JMenuItem pairMenuItem = new JMenuItem("Pair                Alt+P");
		pairMenuItem.setIcon(disconnectedi);
		pairMenuItem.setMnemonic(KeyEvent.VK_P);
		pairMenuItem.addActionListener(e -> {pair();;});

		JMenuItem exitMenuItem = new JMenuItem("Exit                Alt+E");
		exitMenuItem.setIcon(exiti);
		exitMenuItem.setMnemonic(KeyEvent.VK_E);
		exitMenuItem.addActionListener(e -> {dispose();});

		gameMenu.add(newMenuItem);
		gameMenu.add(checkMenuItem);
		gameMenu.add(solveMenuItem);
		gameMenu.add(pairMenuItem);
		gameMenu.add(exitMenuItem);

		menuBar.add(gameMenu);
		menu.setLayout(new BorderLayout());
		menu.add(menuBar);

		//ToolBar with buttons.
		JPanel tool = new JPanel(new FlowLayout());
		JPanel redund = new JPanel();
		JPanel nest = new JPanel();

		JToolBar tb = new JToolBar(null, JToolBar.HORIZONTAL);
		tb.setRollover(true);
		JButton play = new JButton(playi);
		play.setToolTipText("Play a new game");
		play.addActionListener(e -> {newClicked();});
		tb.add(play);

		JButton check = new JButton(checki);
		check.setToolTipText("Check");
		check.addActionListener(e -> {checkValid();});
		tb.add(check);

		JButton solve = new JButton(solvei);
		solve.setToolTipText("Solve");
		solve.addActionListener(e -> {solve();});
		tb.add(solve);

		JButton pair = new JButton(disconnectedi);
		pair.setToolTipText("Pair");
		pair.addActionListener(e -> {pair();});
		tb.add(pair);

		JButton about = new JButton(new ImageIcon(createImageIcon("about.png").getImage()));
		about.setToolTipText("About");
		tb.add(about);
		about.addActionListener(e -> {about();});

		tool.setLayout(new BorderLayout());
		tool.add(tb);
		red.setMargin(new Insets(0,1,0,1));
		und.setMargin(new Insets(0,1,0,1));
		redund.add(und);
		und.addActionListener(e -> {undoing();});
		redund.add(red);
		red.addActionListener(e -> {redoing();});
		und.setEnabled(false);
		red.setEnabled(false);
		nest.add(tb);
		nest.add(redund);

		// buttons labeled 1, 2, ..., 9, and X.
		JPanel numberButtons = new JPanel(new FlowLayout());
		int maxNumber = board.size() + 1;
		for (int i = 1; i <= maxNumber; i++) {
			int number = i % maxNumber;
			JButton button = new JButton(number == 0 ? "X" : String.valueOf(number));
			bts[i - 1] = button;
			button.setFocusPainted(false);
			button.setMargin(new Insets(0,2,0,2));
			button.addActionListener(e -> numberClicked(number));
			numberButtons.add(button);
		}
		numberButtons.setAlignmentX(LEFT_ALIGNMENT);

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		content.add(menu, BorderLayout.NORTH);
		content.add(nest, BorderLayout.EAST);
		content.add(numberButtons, BorderLayout.SOUTH);
		return content;
	}

	/** Checks if the current board can be solved. */
	private void checkValid() {
		List<List<Integer>> dummie = new ArrayList<List<Integer>>();
		dummie = new ArrayList<List<Integer>>(board.size * board.size); 
		dummie = board.initialize(dummie);
		dummie = board.copy(dummie);
		if(board.isSolved(board.sudoku)) {showMessage(String.format("The puzzle is already solved."));}
		else if(board.solvable(dummie, 0, 0) == true) {showMessage(String.format("Solvable."));}
		else {showMessage(String.format("Not Solvable."));}
		repaint();
	}

	/** Solve the current board. */
	private void solve() {
		List<List<Integer>> d = new ArrayList<List<Integer>>();
		d = new ArrayList<List<Integer>>(board.size * board.size); 
		d = board.initialize(d);
		d = board.copy(d);
		if(board.isSolved(board.sudoku) != true) {
			if(board.solvable(d, 0, 0) == false) {showMessage(String.format("Not Solvable!"));}
			else {
				board.solvable(board.sudoku, 0, 0);
				und.setEnabled(false);red.setEnabled(false);
				for(int i = 0; i <= board.size; i++) {bts[i].setEnabled(false);}
				if(board.size == 4) {bts[9].setEnabled(false);}
				showMessage(String.format("Solved!"));
			}
		}
		repaint();
	}

	/** Pair the game with another player. */
	private void pair() {
		JFrame f= new JFrame("Connection"); 

		f.setSize(400,400);f.setVisible(true); 
		JPanel contentc = new JPanel(), contents = new JPanel(), contentcn = new JPanel(), 
				contentcs = new JPanel(),contentccn = new JPanel(), contentccs = new JPanel();
		
		JLabel label1 = new JLabel("Host name: "), label2 = new JLabel("IP number: "), label3 = new JLabel("Port number: ");
		
		JTextField t1 = null;
		JTextField t2 = null;
		
		try {
			InetAddress ip = InetAddress.getLocalHost();
			String currentip = ip.toString();
			String [] split = currentip.split("/");
			t1 = new JTextField(split[0], 15); t2 = new JTextField(split[1], 15);
			host = split[1];
			t1.setEditable(false);t2.setEditable(false);
		} catch (UnknownHostException e) {
		}
		JTextField t3 = new JTextField(Integer.toString(port), 15);t3.setEditable(false);

		contentccn.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Player"));
		contentccn.setLayout(new BorderLayout());
		contentcn.setLayout(new GridLayout(3, 2));
		contentcn.add(label1);
		contentcn.add(t1);
		contentcn.add(label2);
		contentcn.add(t2);
		contentcn.add(label3);
		contentcn.add(t3);

		contentccn.add(contentcn, BorderLayout.NORTH);


		JLabel label4 = new JLabel("Host name/IP: "), label5 = new JLabel("Port number: ");
		JTextField t4 = new JTextField("192.168.0.3", 15), t5 = new JTextField(Integer.toString(port), 15);
		JButton bconnect = new JButton("Connect");JButton bdisconnect = new JButton("Disconnect");
		bconnect.addActionListener(e -> {connect();});

		contentccs.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Peer"));
		contentccs.setLayout(new BorderLayout());

		contentcs.setLayout(new GridLayout(3, 2));
		contentcs.add(label4);
		contentcs.add(t4);
		contentcs.add(label5);
		contentcs.add(t5);
		contentcs.add(bconnect);
		contentcs.add(bdisconnect);
		contentccs.add(contentcs, BorderLayout.NORTH);

		contentc.setLayout(new BorderLayout());
		contentc.add(contentccn, BorderLayout.NORTH);
		contentc.add(contentccs, BorderLayout.CENTER);

		f.add(contentc, BorderLayout.NORTH);

		contents.setLayout(new BorderLayout());

		JTextArea p = new JTextArea();
		p.append("Server started on port " + Integer.toString(port) + ".");
		p.setEditable(false);
		p.setPreferredSize(new Dimension(140, 140));

		JButton buttonc = new JButton("Close");
		buttonc.addActionListener(e -> {f.dispose();});

		contents.add(buttonc, BorderLayout.SOUTH);
		contents.add(p, BorderLayout.NORTH);
		f.add(contents, BorderLayout.SOUTH);
	}

	private void connect() {
		try {
			Socket socket = new Socket(host, port);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/** Display a frame with the information of the program and author. */
	private void about() {
		JFrame a = new JFrame("About"); 
		a.setSize(300,200);a.setVisible(true); 
		a.setResizable(false);
		JLabel label1 = new JLabel(), label2 = new JLabel();
		label1.setText("Implementation of Sudoku puzzle.");label2.setText("@ 2018");
		JButton b = new JButton("Ok");
		b.addActionListener(e -> {a.dispose();});
		a.add(label1, BorderLayout.NORTH);
		a.add(label2, BorderLayout.CENTER);
		a.add(b, BorderLayout.SOUTH);
	}

	/** Undo the previous modification to the board. */
	private void undoing() {
		if(counterMov != 1) {
			int [] undoing = new int [3];
			undoing = movements.get(counterMov - 2);
			if(undoing[2] != 0) {board.sudoku.get(undoing[0]).set(undoing[1], 0);}
			else {
				undoing = movements.get(counterMov - 3);
				board.sudoku.get(undoing[0]).set(undoing[1], undoing[2]);
			}
			counterMov = counterMov - 1;
			red.setEnabled(true);
			checkButtons(undoing[0], undoing[1]);
		}
		if(counterMov == 1) {
			und.setEnabled(false);
			for(int i = 0; i <= board.size; i++) {bts[i].setEnabled(false);}
		}
		repaint();
	}

	/** Retrieves the previous modification to the board. */
	private void redoing() {
		if(movements.size() != counterMov - 1) {
			int [] redoing = new int [3];
			redoing = movements.get(counterMov - 1);
			board.sudoku.get(redoing[0]).set(redoing[1], redoing[2]);
			counterMov = counterMov + 1;
			und.setEnabled(true);
			checkButtons(redoing[0], redoing[1]);
		}
		if(movements.size() == counterMov - 1) {
			red.setEnabled(false);
			for(int i = 0; i <= board.size; i++) {bts[i].setEnabled(false);}
		}
		repaint();
	}

	/** Create an image icon from the given image file. */
	private ImageIcon createImageIcon(String filename) {
		URL imageUrl = getClass().getResource(IMAGE_DIR + filename);
		if (imageUrl != null) {
			return new ImageIcon(imageUrl);
		}
		return null;
	}

	/**
	 * Play the audio clip (wav) specified by a URL. This method has no effect
	 * if the audio clip cannot be found.
	 *
	 * @param url Absolute URL of an audio clip file.
	 */
	public void play(URL url) {
		try {
			AudioInputStream in = AudioSystem.getAudioInputStream(url);
			Clip clip = AudioSystem.getClip();
			clip.open(in);clip.start();
		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	/** Store locations of x and y.*/
	public static void location(int locy, int locx, int squareS) {
		x = locy;
		y = locx;
		squareSize = squareS;
	}

	public static void main(String[] args) {
		new SudokuDialog();
	}
}
