
package sudoku.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import sudoku.model.Board;

/**
 * A special panel class to display a Sudoku board modeled by the
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel {

	public interface ClickListener {

		/** Callback to notify clicking of a square. 
		 * 
		 * @param x 0-based column index of the clicked square
		 * @param y 0-based row index of the clicked square
		 */
		void clicked(int x, int y);
	}

	/** Extra variables.*/
	static int xy;
	int i = 0;
	int j = 0;

	/** Background color of the board. */
	private static final Color boardColor = new Color(247, 223, 150);

	/** Board to be displayed. */
	private Board board;

	/** Width and height of a square in pixels. */
	private int squareSize;

	/** Create a new board panel to display the given board. */
	public BoardPanel(Board board, ClickListener listener) {
		this.board = board;
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int xy = locateSquaree(e.getX(), e.getY());
				if (xy >= 0) {
					listener.clicked(xy / 100, xy % 100);
					SudokuDialog.location(xy/100, xy%100, squareSize);
				}
			}
		});
	}

	/** Set the board to be displayed. */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Given a screen coordinate, return the indexes of the corresponding square
	 * or -1 if there is no square.
	 * The indexes are encoded and returned as x*100 + y, 
	 * where x and y are 0-based column/row indexes.
	 */
	private int locateSquaree(int x, int y) {
		if (x < 0 || x > board.size * squareSize
				|| y < 0 || y > board.size * squareSize) {
			return -1;
		}
		int xx = x / squareSize;
		int yy = y / squareSize;
		return xx * 100 + yy;
	}

	/** Draw the associated board. */
	@Override
	public void paint(Graphics g) {
		super.paint(g); 

		// determine the square size
		Dimension dim = getSize();
		squareSize = Math.min(dim.width, dim.height) / board.size;

		// draw background
		g.setColor(boardColor);
		g.fillRect(0, 0, squareSize * board.size, squareSize * board.size);

		// draw grid and squares.
		i = SudokuDialog.x1;j = SudokuDialog.y1;
		if(board.size == 9) {
			//Draw grid if size is 9.
			for (int x = 0; x <= squareSize * board.size; x += (squareSize * board.size)/9) {
				if(x == 0 || x == (squareSize * board.size)/3 || x == ((squareSize * board.size)/3)*2 ||
						x == ((squareSize * board.size)/3)*3) {g.setColor(Color.BLACK);}
				else {g.setColor(Color.GRAY);}
				g.drawLine(x, 0, x, squareSize * board.size); 
				for (int y = 0; y <= squareSize * board.size; y += (squareSize * board.size)/9) {
					if(y == 0 || y == (squareSize * board.size)/3 || y == ((squareSize * board.size)/3)*2 ||
							y == ((squareSize * board.size)/3)*3) {g.setColor(Color.BLACK);}
					else {g.setColor(Color.GRAY);}
					g.drawLine(0, y, squareSize * board.size, y);        
				}
			}

			//fill squares with default numbers
			for(int m = 0; m < board.limit; m++) {
				g.setColor(Color.ORANGE);
				g.fillRect(((board.corx[m]) * ((squareSize * board.size)/9)) + 4, ((board.cory[m] - 1) * ((squareSize * board.size)/9)) + 4, 
						((squareSize * board.size)/9)-7, ((squareSize * board.size)/9)-7);
			}

			//Fill the selected square.
			if(i != 0 || j !=0) {
				g.setColor(Color.PINK);
				g.fillRect(((i-1) * ((squareSize * board.size)/9)) + 4, ((j - 1) * ((squareSize * board.size)/9)) + 4, 
						((squareSize * board.size)/9)-7, ((squareSize * board.size)/9)-7);
			}

			//Draw the given numbers into the puzzle.
			for (int row = 0; row < board.size; row++) {
				for (int col = 0; col < board.size; col++) {
					if(board.sudoku.get(row).get(col) != 0){
						g.setColor(Color.black);
						g.setFont(new Font("TimesRoman", Font.PLAIN, (squareSize * board.size)/18));
						g.drawString(Integer.toString(board.sudoku.get(row).get(col)), (col * ((squareSize * board.size)/9) + 
								(squareSize * board.size)/22), (row * ((squareSize * board.size)/9) + (squareSize * board.size)/13));
					}
				}
			}
		}
		if(board.size == 4) {   
			//Draw grid if size is 4.
			for (int x = 0; x <= squareSize * board.size; x += (squareSize * board.size)/4) {
				if(x == 0 || x == (squareSize * board.size)/2 || x == ((squareSize * board.size)/2)*2 || 
						x == ((squareSize * board.size)/2)*3) {g.setColor(Color.BLACK);}
				else {g.setColor(Color.GRAY);}
				g.drawLine(x, 0, x, squareSize * board.size); 
				for (int y = 0; y <= squareSize * board.size; y += (squareSize * board.size)/4) {
					if(y == 0 || y == (squareSize * board.size)/2 || y == ((squareSize * board.size)/2)*2 || 
							y == ((squareSize * board.size)/2)*3) {g.setColor(Color.BLACK);}
					else {g.setColor(Color.GRAY);}
					g.drawLine(0, y, squareSize * board.size, y);        
				}
			}

			//fill squares with default numbers
			for(int m = 0; m < board.limit; m++) {
				g.setColor(Color.ORANGE);
				g.fillRect(((board.corx[m]) * ((squareSize * board.size)/4)) + 4, ((board.cory[m] - 1) * ((squareSize * board.size)/4)) + 4, 
						((squareSize * board.size)/4)-7, ((squareSize * board.size)/4)-7);
			}

			//Fill the selected square.
			if(i != 0 || j !=0) {
				g.setColor(Color.PINK);
				g.fillRect(((i-1) * ((squareSize * board.size)/4)) + 4, ((j - 1) * ((squareSize * board.size)/4)) + 4, 
						((squareSize * board.size)/4)-7, ((squareSize * board.size)/4)-7);
			}
			//Draw the given numbers into the puzzle.
			for (int row = 0; row < board.size; row++) {
				for (int col = 0; col < board.size; col++) {
					if(board.sudoku.get(row).get(col) != 0){
						g.setColor(Color.black);
						g.setFont(new Font("TimesRoman", Font.PLAIN, (squareSize * board.size)/9));
						g.drawString(Integer.toString(board.sudoku.get(row).get(col)), (col * ((squareSize * board.size)/4) + 
								(squareSize * board.size)/10), (row * ((squareSize * board.size)/4) + (squareSize * board.size)/7));
					}
				}
			}
		}
	}
}