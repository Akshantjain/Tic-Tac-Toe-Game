import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class has 8 Tests for the MinMax Class
 */
class TicTacToeTest {

	private String[] init_board;
	private ArrayList<Node> movesList;
	private MinMax sendIn_InitState;


	@Test
	void findMoves1() {
		init_board = new String[] {"O","b","b","b","b","b","b","b","b"};
		sendIn_InitState = new MinMax(init_board);
		movesList = sendIn_InitState.findMoves();

		assertTrue(movesList.size()==8);
		assertTrue(movesList.get(3).getMinMax() == 0);
	}

	@Test
	void findMoves2() {
		init_board = new String[] {"O","b","X","b","b","O","b","b","b"};
		sendIn_InitState = new MinMax(init_board);
		movesList = sendIn_InitState.findMoves();
		assertTrue(movesList.size()==6);
		assertTrue(movesList.get(1).getMinMax() == 0 && movesList.get(2).getMinMax() == 0);
	}

	@Test
	void findMoves3() {
		init_board = new String[] {"O","X","X","O","X","O","O","X","b"};
		sendIn_InitState = new MinMax(init_board);
		movesList = sendIn_InitState.findMoves();
		assertTrue(movesList.size()==1);
		assertTrue(movesList.get(0).getMinMax() == 10);
	}

	@Test
	void findMoves4() {
		init_board = new String[] {"O","X","X","O","b","O","X","O","b"};
		sendIn_InitState = new MinMax(init_board);
		movesList = sendIn_InitState.findMoves();
		assertTrue(movesList.size()==2);
		assertTrue(movesList.get(0).getMinMax() == 10 && movesList.get(1).getMinMax() == -10);
	}

	@Test
	void min1() {
		init_board = new String[] {"O","X","X","O","b","O","X","O","b"};
		sendIn_InitState = new MinMax(init_board);
		movesList = sendIn_InitState.findMoves();
		assertTrue(sendIn_InitState.Min(movesList.get(1))==-10);
	}

	@Test
	void min2() {
		init_board = new String[] {"O","O","X","O","b","O","X","O","b"};
		sendIn_InitState = new MinMax(init_board);
		movesList = sendIn_InitState.findMoves();
		assertTrue(sendIn_InitState.Min(movesList.get(0))==0);
	}

	@Test
	void max1() {
		init_board = new String[] {"O","X","X","O","b","O","X","O","b"};
		sendIn_InitState = new MinMax(init_board);
		movesList = sendIn_InitState.findMoves();
		assertTrue(sendIn_InitState.Max(movesList.get(1))==10);
	}

	@Test
	void max2() {
		init_board = new String[] {"O","b","b","O","b","O","b","O","b"};
		sendIn_InitState = new MinMax(init_board);
		movesList = sendIn_InitState.findMoves();
		assertTrue(sendIn_InitState.Max(movesList.get(0))==-10);
	}
}