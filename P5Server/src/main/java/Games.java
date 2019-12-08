import java.util.ArrayList;

public class Games {
    String computerLevel;
    int PlayerID;
    ArrayList<String> boardState;
    int highScore;

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

    Games(int PlayerID) {
        this.PlayerID = PlayerID;
    }

}
