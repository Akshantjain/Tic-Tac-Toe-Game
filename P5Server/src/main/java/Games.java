import java.util.ArrayList;

public class Games {
    String computerLevel;
    int PlayerID;
    ArrayList<String> boardState;


    public String getComputerLevel() {
        return computerLevel;
    }

    public void setComputerLevel(String computerLevel) {
        this.computerLevel = computerLevel;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    int highScore = 0;

    Games(int PlayerID) {
//        this.boardState = boardState;
//        this.computerLevel = computerLevel;
        this.PlayerID = PlayerID;
    }

}
