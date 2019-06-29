
package sudoku.model;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;


/** An abstraction of Sudoku puzzle. */
public class Board {

	/** Store inputs of this board. */
	public List<List<Integer>> sudoku = new ArrayList<List<Integer>>();

	/** Store inputs of solved board. */
	public List<List<Integer>> solved = new ArrayList<List<Integer>>();

	/** Size of this board (number of columns/rows). */
	public int size;

	/** Limit of numbers to be filled on the board. */
	public int limit = 0;

	/** Sets a square to be unmodifiable. */
	public boolean [][] flag;

	/** Location of the random numbers that are going to be on the board. */
	public int[] cory;public int[] corx;

	/** Create a new board of the given size. */
	public Board(int size) {
		this.size = size;
		sudoku = new ArrayList<List<Integer>>(size * size);
		sudoku = initialize(sudoku);
		solved = new ArrayList<List<Integer>>(size * size);
		solved = initialize(solved);
		flag = new boolean[size][size];
		flag = initialize(flag);
	}

	/** Return the size of this board. */
	public int size() {
		return size;
	}

	/** Initialize the list that is going to be used for the game. */
	public List<List<Integer>> initialize(List<List<Integer>> s) { 
		for (int i = 0; i < size; i++){
			ArrayList<Integer> row = new ArrayList<Integer>(size);
			s.add(row);
			for (int j = 0; j < size; j++){row.add(0);}
		}
		return s;
	}

	/** Initialize the array in charge of block squares of the board. */
	public boolean[][] initialize(boolean [][] s) { 
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){flag[i][j] = false;}
		}
		return s;
	}

	/** Checks if the given value is already on the same row, column, or sub-grid. */
	public boolean checkValue(List<List<Integer>> s, int y, int x, int v, int size){
		boolean conflict = false;
		if(v > size) {conflict = true;}
		if(conflict != true) {conflict = checkRow(s, y, x, v);}
		if(conflict != true) { conflict = checkCol(s, y, x, v);}
		if(conflict != true && size == 4) { conflict = checkSubGrid4(s, y, x, v);}
		if(conflict != true && size == 9) { conflict = checkSubGrid9(s, y, x, v);}
		return conflict;
	}

	/** Check the row of the given location.*/
	public boolean checkRow(List<List<Integer>> s, int y, int x, int v) {
		for(int i = 0; i < size; i++) {
			if(s.get(y).get(i) != 0) {
				if((s.get(y).get(i) == v)  && x != i) {return true;}
			}
		}
		return false;
	}

	/** Check the column of the given location.*/
	public boolean checkCol(List<List<Integer>> s, int y, int x, int v) {
		for(int i = 0; i < size; i++) {
			if(s.get(i).get(x) != 0) {
				if(s.get(i).get(x) == v && y != i) {return true;}
			}
		}
		return false;
	}

	/** Checks sub-grid of the table if the size is 4.*/
	public  boolean checkSubGrid4(List<List<Integer>> s, int y, int x, int v) {
		if((x == 0 || x == 2) && (y == 0 || y == 2)) {
			if(s.get(y+1).get(x+1) != 0) {
				if(s.get(y+1).get(x+1) == v) {return true;}
			}
		}
		if((x == 1 || x == 3) && (y == 0 || y == 2)) {
			if(s.get(y+1).get(x-1) != 0) {
				if(s.get(y+1).get(x-1) == v) {return true;}
			}
		}
		if((x == 0 || x == 2) && (y == 1 || y == 3)) {
			if(s.get(y-1).get(x+1) != 0) {
				if(s.get(y-1).get(x+1) == v) {return true;}
			}
		}
		if((x == 1 || x == 3) && (y == 1 || y == 3)) {
			if(s.get(y-1).get(x-1) != 0) {
				if(s.get(y-1).get(x-1) == v) {return true;}
			}
		}
		return false;
	}

	/** Checks sub-grid of the table if the size is 9.*/
	public  boolean checkSubGrid9(List<List<Integer>> s, int y, int x, int v) {
		if((x == 0 || x == 3 || x == 6) && (y == 0 || y == 3 || y == 6)) {
			if(s.get(y+1).get(x+1) != 0) {
				if(s.get(y+1).get(x+1) == v) {return true;}
			}
			if(s.get(y+1).get(x+2) != 0) {
				if(s.get(y+1).get(x+2) == v) {return true;}
			}
			if(s.get(y+2).get(x+1) != 0) {
				if(s.get(y+2).get(x+1) == v) {return true;}
			}
			if(s.get(y+2).get(x+2) != 0) {
				if(s.get(y+2).get(x+2) == v) {return true;}
			}
		}
		if((x == 1 || x == 4 || x == 7) && (y == 0 || y == 3 || y == 6)) {
			if(s.get(y+1).get(x-1) != 0) {
				if(s.get(y+1).get(x-1) == v) {return true;}
			}
			if(s.get(y+1).get(x+1) != 0) {
				if(s.get(y+1).get(x+1) == v) {return true;}
			}
			if(s.get(y+2).get(x-1) != 0) {
				if(s.get(y+2).get(x-1) == v) {return true;}
			}
			if(s.get(y+2).get(x+1) != 0) {
				if(s.get(y+2).get(x+1) == v) {return true;}
			}
		}
		if((x == 2 || x == 5 || x == 8) && (y == 0 || y == 3 || y == 6)) {
			if(s.get(y+1).get(x-2) != 0) {
				if(s.get(y+1).get(x-2) == v) {return true;}
			}
			if(s.get(y+1).get(x-1) != 0) {
				if(s.get(y+1).get(x-1) == v) {return true;}
			}
			if(s.get(y+2).get(x-2) != 0) {
				if(s.get(y+2).get(x-2) == v) {return true;}
			}
			if(s.get(y+2).get(x-1) != 0) {
				if(s.get(y+2).get(x-1) == v) {return true;}
			}
		}
		if((x == 0 || x == 3 || x == 6) && (y == 1 || y == 4 || y == 7)) {
			if(s.get(y-1).get(x+1) != 0) {
				if(s.get(y-1).get(x+1) == v) {return true;}
			}
			if(s.get(y+1).get(x+1) != 0) {
				if(s.get(y+1).get(x+1) == v) {return true;}
			}
			if(s.get(y-1).get(x+2) != 0) {
				if(s.get(y-1).get(x+2) == v) {return true;}
			}
			if(s.get(y+1).get(x+2) != 0) {
				if(s.get(y+1).get(x+2) == v) {return true;}
			}
		}
		if((x == 1 || x == 4 || x == 7) && (y == 1 || y == 4 || y == 7)) {
			if(s.get(y-1).get(x-1) != 0) {
				if(s.get(y-1).get(x-1) == v) {return true;}
			}
			if(s.get(y-1).get(x+1) != 0) {
				if(s.get(y-1).get(x+1) == v) {return true;}
			}
			if(s.get(y+1).get(x-1) != 0) {
				if(s.get(y+1).get(x-1) == v) {return true;}
			}
			if(s.get(y+1).get(x+1) != 0) {
				if(s.get(y+1).get(x+1) == v) {return true;}
			}
		}
		if((x == 2 || x == 5 || x == 8) && (y == 1 || y == 4 || y == 7)) {
			if(s.get(y-1).get(x-2) != 0) {
				if(s.get(y-1).get(x-2) == v) {return true;}
			}
			if(s.get(y-1).get(x-1) != 0) {
				if(s.get(y-1).get(x-1) == v) {return true;}
			}
			if(s.get(y+1).get(x-2) != 0) {
				if(s.get(y+1).get(x-2) == v) {return true;}
			}
			if(s.get(y+1).get(x-1) != 0) {
				if(s.get(y+1).get(x-1) == v) {return true;}
			}
		}
		if((x == 0 || x == 3 || x == 6) && (y == 2 || y == 5 || y == 8)) {
			if(s.get(y-2).get(x+1) != 0) {
				if(s.get(y-2).get(x+1) == v) {return true;}
			}
			if(s.get(y-2).get(x+2) != 0) {
				if(s.get(y-2).get(x+2) == v) {return true;}
			}
			if(s.get(y-1).get(x+1) != 0) {
				if(s.get(y-1).get(x+1) == v) {return true;}
			}
			if(s.get(y-1).get(x+2) != 0) {
				if(s.get(y-1).get(x+2) == v) {return true;}
			}
		}
		if((x == 1 || x == 4 || x == 7) && (y == 2 || y == 5 || y == 8)) {
			if(s.get(y-2).get(x-1) != 0) {
				if(s.get(y-2).get(x-1) == v) {return true;}
			}
			if(s.get(y-2).get(x+1) != 0) {
				if(s.get(y-2).get(x+1) == v) {return true;}
			}
			if(s.get(y-1).get(x-1) != 0) {
				if(s.get(y-1).get(x-1) == v) {return true;}
			}
			if(s.get(y-1).get(x+1) != 0) {
				if(s.get(y-1).get(x+1) == v) {return true;}
			}
		}
		if((x == 2 || x == 5 || x == 8) && (y == 2 || y == 5 || y == 8)) {
			if(s.get(y-2).get(x-2) != 0) {
				if(s.get(y-2).get(x-2) == v) {return true;}
			}
			if(s.get(y-2).get(x-1) != 0) {
				if(s.get(y-2).get(x-1) == v) {return true;}
			}
			if(s.get(y-1).get(x-2) != 0) {
				if(s.get(y-1).get(x-2) == v) {return true;}
			}
			if(s.get(y-1).get(x-1) != 0) {
				if(s.get(y-1).get(x-1) == v) {return true;}
			}
		}
		return false;
	}

	/** Checks if the puzzle is solved.*/
	public boolean isSolved(List<List<Integer>> s){
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(s.get(i).get(j) == 0) {
					return false;
				}
			}
		}
		return true;
	}

	/** Creates a new board.*/
	public void newBoard(int s) {
		sudoku = new ArrayList<List<Integer>>(s * s);
		sudoku = initialize(sudoku);
		solved = new ArrayList<List<Integer>>(s * s);
		solved = initialize(solved);
		flag = new boolean[size][size];
		flag = initialize(flag);
	}

	/** Checks if the puzzle can be solved.*/
	public boolean solvable(List<List<Integer>> s, int y, int x) {
		if (y == size) {
			y = 0; //Next row/
			if (++x == size) {return true;}//It found a solution/
		}
		if (s.get(y).get(x) != 0) {return solvable(s, y+1,x);}// skip filled cells.
		for (int val = 1; val <= size; ++val) {
			if (checkValue(s, y, x, val, size) == false) {
				//checks if the given value has any conflict checking the row, column, and sub-grid
				s.get(y).set(x, val);
				if (solvable(s, y+1,x)) {return true;}
			}
		}
		s.get(y).set(x, 0); // reset on backtrack
		return false;
	}

	/** Fills the sudoku board with random numbers.*/
	public void fillSudoku(int max, int l) {
		cory = new int [l];
		corx = new int [l];
		limit = l;
		int i = 0;
		Random r = new Random();
		if(max == 4) {
			while(i < limit) {
				int yran = r.nextInt(max);
				int xran = r.nextInt(max);
				int nran = r.nextInt(max + 1);
				if(nran == 0) {nran = nran + 1;}
				if(sudoku.get(yran).get(xran) == 0) {
					if(checkValue(sudoku, yran, xran, nran, size) == false) {
						corx[i] = xran;
						cory[i] = yran + 1;
						sudoku.get(yran).set(xran, nran);
						flag[yran][xran] = true;
						i++;      
					}
				}
			}
		}
		if(max == 9) {
			while(i < limit) {
				int yran = r.nextInt(max);
				int xran = r.nextInt(max);
				int nran = r.nextInt(max + 1);
				if(nran == 0) {nran = nran + 1;}
				if(sudoku.get(yran).get(xran) == 0) {
					if(checkValue(sudoku, yran, xran, nran, size) == false) {
						corx[i] = xran;
						cory[i] = yran + 1;
						sudoku.get(yran).set(xran, nran);
						flag[yran][xran] = true;
						i++;      
					}
				}
			}
		}
		if(solvable(sudoku, 0, 0) == false) {newBoard(size);fillSudoku(size, l);}
		else {reset();}
	}

	/** Resets the board after it filled a valid Sudoku.*/
	public void reset(){
		for(int row = 0; row < size; row++){
			for(int col = 0; col < size;col++){
				if(flag[row][col] == false){sudoku.get(row).set(col, 0);}
			}
		}
	}

	/** Copies and assign a board to another board.*/
	public List<List<Integer>> copy(List<List<Integer>> d){
		for(int row = 0; row < size; row++){
			for(int col = 0; col < size;col++){d.get(row).set(col, sudoku.get(row).get(col));}
		}
		return d;
	}
}