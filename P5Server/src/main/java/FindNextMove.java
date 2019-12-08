import java.util.ArrayList;
import java.util.Random;

public class FindNextMove {


    public static ArrayList<String> getMove(ArrayList<String> gameBoard, String difficulty) {
        String[] init_board;
        ArrayList<Node> movesList;

        init_board = getMoves(gameBoard);

        if (init_board.length != 9) {
            System.out.println("You have entered an invalid state for tic tac toe, exiting......");
            System.exit(-1);
        }

        MinMax sendIn_InitState = new MinMax(init_board);

        movesList = sendIn_InitState.findMoves();

        switch (difficulty) {
            case "easy":
                return makeBadMove(movesList, gameBoard);
            case "medium":
                return makeOkayMove(movesList, gameBoard);
            case "expert":
                return makeGoodMove(movesList, gameBoard);
            default:
                return null;
        }
    }


    private static String[] getMoves(ArrayList<String> gameBoard) {
        String[] temp = new String[9];
        for (int i = 0; i < 9; i++) {
            temp[i] = gameBoard.get(i);
        }
        return temp;

    }

    private static ArrayList<String> makeBadMove(ArrayList<Node> movesList, ArrayList<String> gameBoard) {
        int maxScore = 99;
        ArrayList<Integer> maxIndexes = new ArrayList<>();
        for (int i = 0; i < movesList.size(); i++) {
            if (movesList.get(i).getMinMax() < maxScore) {
                maxIndexes.clear();
                maxIndexes.add(i);
                maxScore = movesList.get(i).getMinMax();
            } else if (movesList.get(i).getMinMax() == maxScore) {
                maxIndexes.add(i);
            }
        }

        Random rand = new Random();
        int index = rand.nextInt(maxIndexes.size());
        Node choice = movesList.get(maxIndexes.get(index));

        gameBoard.set(choice.getMovedTo()-1, "X");

        return gameBoard;
    }

    private static ArrayList<String> makeOkayMove(ArrayList<Node> movesList, ArrayList<String> gameBoard) {

        Random rand = new Random();
        int index = rand.nextInt(movesList.size());
        Node choice = movesList.get(index);

        gameBoard.set(choice.getMovedTo()-1, "X");

        return gameBoard;
    }

    private static ArrayList<String> makeGoodMove(ArrayList<Node> movesList, ArrayList<String> gameBoard) {
        int maxScore = -99;
        ArrayList<Integer> maxIndexes = new ArrayList<>();
        for (int i = 0; i < movesList.size(); i++) {
            if (movesList.get(i).getMinMax() > maxScore) {
                maxIndexes.clear();
                maxIndexes.add(i);
                maxScore = movesList.get(i).getMinMax();
            } else if (movesList.get(i).getMinMax() == maxScore) {
                maxIndexes.add(i);
            }
        }

        Random rand = new Random();
        int index = rand.nextInt(maxIndexes.size());
        Node choice = movesList.get(maxIndexes.get(index));

        gameBoard.set(choice.getMovedTo()-1, "X");

        return gameBoard;
    }
}
