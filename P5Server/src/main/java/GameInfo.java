import java.io.Serializable;
import java.util.ArrayList;

public class GameInfo implements Serializable {
    public String type;
    public String computerLevel;

    ArrayList<Server.ClientThread> clients;
    // TODO: Add more data members as needed

    // Player Count
    GameInfo(String type, ArrayList<Server.ClientThread> clients, String computerLevel) {
        this.type = type;
        this.clients = clients;
        this.computerLevel = computerLevel;
    }
}