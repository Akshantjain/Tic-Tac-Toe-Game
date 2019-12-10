import java.io.Serializable;
import java.util.ArrayList;

class GameInfo implements Serializable {
    String type;
    private String computerLevel;

    ArrayList<Server.ClientThread> clients;
    // TODO: Add more data members as needed

    // Player Count
    GameInfo(String type, ArrayList<Server.ClientThread> clients, String computerLevel) {
        this.type = type;
        this.clients = clients;
        this.computerLevel = computerLevel;
    }
}