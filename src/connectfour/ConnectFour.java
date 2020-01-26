//John Sheehan
package connectfour;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ConnectFour
{ 
	// dimensions of board
	private int width;
 	private int height;
 	private int moveCount;
 	private int maxMoves;
 
 	// array for board
 	private char[][] board;
  
 	// store last move made by a player
 	private int lastCol = -1;
 	private int lastTop = -1;

 	//store if the game is over; controls the game loop
 	private boolean complete;
 	
	//constructor
	public ConnectFour(int w, int h)
	{
		width = w;
		height = h;
		moveCount = 0;
		maxMoves = width * height;
		board = new char[h][w];
		complete = false;

		// initialize board with blank cells
		for (int i = 0; i < h; i++) 
		{
			  Arrays.fill(board[i] = new char[w], '.');
		}
	}
	
	public boolean isComplete()
	{
		return complete;
	}

	public void displayBoard()
	{
		System.out.println("1234567");
		for(int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
	}
	    
	// we use Streams to make a more concise method 
	// for representing the board
	public String toString()
	{
		return IntStream.range(0,width).mapToObj(Integer::toString).collect(Collectors.joining()) + "\n" + Arrays.stream(board).map(String::new).collect(Collectors.joining("\n"));
	}

	//method to get row of last move 
	public String horizontal() 
	{
		return new String(board[lastTop]);
	}

	//method to get column of last move
	public String vertical() 
	{
		StringBuilder sb = new StringBuilder(height);

		for (int h = 0; h < height; h++)
		{
			sb.append(board[h][lastCol]);
		}

		return sb.toString();
	}
  
	// get string representation of the "/" diagonal 
	// containing the last play of the user
	public String slashDiagonal() 
	{
		StringBuilder sb = new StringBuilder(height);

		for (int h = 0; h < height; h++) 
		{
			int w = lastCol + lastTop - h;

			if (0 <= w && w < width)
			{
				sb.append(board[h][w]);
			}
		}

		return sb.toString();
	}

	// get string representation of the "\" 
	// diagonal containing the last play of the user
	public String backslashDiagonal() 
	{
		StringBuilder sb = new StringBuilder(height);

		for (int h = 0; h < height; h++) 
		{
			int w = lastCol - lastTop + h;

			if (0 <= w && w < width) 
			{
				sb.append(board[h][w]);
			}
		}

		return sb.toString();
	}

	// static method checking if a substring is in str
	public static boolean contains(String str, String substring) 
	{
		return str.indexOf(substring) >= 0;
	}

	// method to check for win
	public boolean isWinningPlay() 
	{
		if (lastCol == -1) 
		{
			System.err.println("No move has been made yet");
			return false;
		}

		char sym = board[lastTop][lastCol];
		// winning streak with the last play symbol
		String streak = String.format("%c%c%c%c", sym, sym, sym, sym);

		// check if streak is in row, col, diagonal or backslash diagonal
		return contains(horizontal(), streak) ||  contains(vertical(), streak) || contains(slashDiagonal(), streak) || contains(backslashDiagonal(), streak);
	}

	  
	// gets a move as input from user and calculates the cpu move, then places the pieces
	public void chooseAndDrop(Scanner input) 
	{
		do 
		{
			if(moveCount >= maxMoves)
			{
				System.out.println("\nDraw!");
				complete = true;
			}
			//Human turn
			System.out.println("\nPlayer turn: ");
			int col = input.nextInt() -1;

			// check if column is ok
			if (!(0 <= col && col < width))
			{
				System.out.println("Column must be between 1 and " + (width));
				continue;
			}
			

			// place symbol at first '.' in column	
			for (int h = height - 1; h >= 0; h--) 
			{
				if (board[h][col] == '.') 
				{
					lastCol = col;
					board[lastTop = h][col] = 'R';
					
					//check for win
					if (isWinningPlay())
					{
						System.out.println("\nYou win!");
						complete = true;
						return;
					}
					
					moveCount++;
					break;
				}
			}
		
			displayBoard();
			
			//CPU Turn
			System.out.println("\nThinking...");
			
			//minimax to calculate best move
			col = minimax();
			
			// place symbol at first '.' in column
			for (int h = height - 1; h >= 0; h--) 
			{
				if (board[h][col] == '.') 
				{
					lastCol = col;
					board[lastTop = h][col] = 'Y';
					
					//check for win
					if (isWinningPlay())
					{
						System.out.println("\nYou lose!");
						complete = true;
					}
					
					moveCount++;
					return;
				}
			}
		}
		
		while (true);
	}
	
	//evaluate quality of a move at given column
	private int value(int col)
	{
		int computerCount = 0;
		int playerCount = 0;
		int row = -1;
		int value = 0;
		
		//get the row where the move would be
		for (int h = height - 1; h >= 0; h--)
		{
			if(board[h][col] == '.')
				row = h-1;
		}
		
		//if row = -1 then column is full, return -1000
		if (row == -1)
			return -1000;
		
		//count the pieces moving down
		int tempRow = row-1;
		int tempCol = col;
		
		for (int i = 1; i <= 4; i++)
		{
			if(tempRow < 0)
				break;
			
			if(board[tempRow][tempCol] == 'Y')
				computerCount++;
			
			else if(board[tempRow][tempCol] == 'R')
				playerCount++;
			
			else 
				break;
			
			tempRow--;
		}
		
		//neither player can connect 4 in this case
		if(computerCount > 0 && playerCount > 0)
			value +=0;
		
		//regardless of which piece it is, going at the end of a chain is good for cpu
		//having a longer chain of Yellow or blocking a chain of red
		else
			value += computerCount + playerCount;
		
		//count the pieces moving left
		tempRow = row;
		tempCol = col-1;
		
		for(int i = 1; i <= 4; i++)
		{
			if(tempCol < 0)
				break;
			
			if(board[tempRow][tempCol] == 'Y')
				computerCount++;
			
			
			else if(board[tempRow][tempCol] == 'R')
				playerCount++;
			
			else 
				break;
			
			tempCol--;
			
		}
		
		//neither player can connect 4 in this case
		if(computerCount > 0 && playerCount > 0)
			value +=0;
				
		//regardless of which piece it is, going at the end of a chain is good for cpu
		//having a longer chain of Yellow or blocking a chain of red
		else
			value += computerCount + playerCount;
		
		
		//count the pieces moving right
		tempRow = row;
		tempCol = col+1;
				
		for(int i = 1; i <= 4; i++)
		{
			if(tempCol > width-1)
				break;
					
			if(board[tempRow][tempCol] == 'Y')
				computerCount++;
									
			else if(board[tempRow][tempCol] == 'R')
				playerCount++;
			
			else
				break;
					
			tempCol++;
					
		}
				
		//neither player can connect 4 in this case
		if(computerCount > 0 && playerCount > 0)
			value +=0;
						
		//regardless of which piece it is, going at the end of a chain is good for cpu
		//having a longer chain of Yellow or blocking a chain of red
		else
			value += computerCount + playerCount;
		
		
		//count the pieces moving down and to the left
		tempRow = row-1;
		tempCol = col-1;
						
		for(int i = 1; i <= 4; i++)
		{
			if(tempCol < 0)
				break;
					
			else if(tempRow < 0)
				break;
							
			if(board[tempRow][tempCol] == 'Y')
				computerCount++;
											
			else if(board[tempRow][tempCol] == 'R')
				playerCount++;
					
			else
				break;
							
			tempCol--;
			tempRow--;
							
		}
						
		//neither player can connect 4 in this case
		if(computerCount > 0 && playerCount > 0)
			value +=0;
								
		//regardless of which piece it is, going at the end of a chain is good for cpu
		//having a longer chain of Yellow or blocking a chain of red
		else
			value += computerCount + playerCount;
		
		return value;
	
	}
	public int minimax()
	{
		//loop through all 7 columns
		//get a value for the move in each column
		
		Integer[] values = {0,0,0,0,0,0,0};
		for(int i = 0; i < width-1; i++)
		{	
			//place piece in each column to see if it wins
			for (int h = height - 1; h >= 0; h--) 
			{
				if (board[h][i] == '.') 
				{
					int tempCol = lastCol;
					int tempTop = lastTop;
					lastCol = i;
					lastTop = h;
					board[h][i] = 'Y';
					
					//return 1000 for winning move meaning it will always play
					if (isWinningPlay())
					{
						values[i] = 1000;
					}
					
					board[h][i] = 'R';
					
					//if human player has a win cpu must go here to block it so return 1000
					if(isWinningPlay())
					{
						values[i] = 1000;
					}
	
					board[h][i] = '.';
					lastCol = tempCol;
					lastTop = tempTop;
					
				}
				

			}
			if(board[height-1][i] != '.')
				values[i] = -1000;
			
			else
				values[i] += value(i);
		}
			
		//return the column with the best move for the computer
		int max = -999;
		int index = -1;
		for(int i = 0; i < values.length; i++)
		{
			if (values[i] > max)
			{
				max = values[i];
				index = i;
				
			}				
		}
		
		return index;
		
   }
	public static void main(String[] args) 
	{
		  
		try (Scanner input = new Scanner(System.in)) 
		{
			//dimensions of board and max moves
			int width = 7; 
			int height = 6; 

			// create the board
			ConnectFour board = new ConnectFour(width, height);

			// display board
			System.out.println("Use 1-" + (width) + " to choose a column");
			board.displayBoard();

			//game loop
			while(!board.isComplete())
			{
				// user input for columns
				board.chooseAndDrop(input);

				// display board
				board.displayBoard();

			}
		}
	}

}



