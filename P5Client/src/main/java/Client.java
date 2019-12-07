import javafx.util.Pair;

import java.awt.image.ConvolveOp;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Client {
    private ClientThread connectionThread = new ClientThread();
    private Consumer<Serializable> scores;
    private Consumer<Serializable> progress;
    private Consumer<Serializable> player;
    private Consumer<Serializable> newBoard;
    private String IP;
    private int port;
    private int playerID;

    Client(String ip, int port,
           Consumer<Serializable> progress,
           Consumer<Serializable> scores,
           Consumer<Serializable> player,
           Consumer<Serializable> newBoard
            ) {
        this.progress = progress;
        this.scores = scores;
        connectionThread.setDaemon(true);
        this.IP = ip;
        this.port = port;
        this.player = player;
        this.newBoard = newBoard;
    }

    private String getIP() {
        return this.IP;
    }

    private int getPort() {
        return this.port;
    }

    void start() {
        connectionThread.start();
    }

    void sendData(Serializable data) throws Exception {
        connectionThread.out.writeObject(data);
        connectionThread.out.reset();
    }

    void shutDown() throws Exception {
        connectionThread.socket.close();
    }

    public void handleUpdatedBoard(Pair data) {
        newBoard.accept(((Pair<Integer, ArrayList<String>>) data).getValue());
    }

    class ClientThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;

        public void run() {
            try {
                Socket socket = new Socket(getIP(), getPort());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);
                progress.accept("Connected to the game on Server: " + getPort() + "\n\n");
                scores.accept("Top 3 Scores on the Server: " + getPort() + "\n\n");
//                sendData(new Pair<String, Pair<String, Integer>>("ClientInfo", new Pair<>(playerName, id)));
//                sendData("Player Name: " + playerName + "player ID: ");

                while (true) {
                    Pair<String, Pair<Integer, ArrayList<String>>> data = (Pair) in.readObject();

                    System.out.println(data.getKey());
                    String choice = data.getKey();

                    switch (choice) {
                        case "ConnectedPlayers": Pair<Integer, ArrayList<String>> subpair = data.getValue();
                                                 player.accept(subpair.getKey());
                                                 break;
                        case "UpdatedBoard": handleUpdatedBoard(data);
                    }
                }
            } catch (Exception e) {
                progress.accept("Game Server Closed\nThank you for playing.");
            }
        }
    }
}
