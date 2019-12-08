import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.util.Pair;
import sun.awt.image.ImageWatched;

import static javax.swing.UIManager.get;


public class Server {
    private int port;
    int count = 0;
    ArrayList<ClientThread> clients = new ArrayList<>();
    ArrayList<String> clientIds = new ArrayList<>();
    String computerLevel;
    ArrayList<Games> games = new ArrayList<Games>();

    // games is a List of Pairs.
    // Those Pairs have a Pair of Client IDs
    // Those Pairs also have a List of game choices
    TheServer server;
    private Consumer<Serializable> callback;

    Server(int port, Consumer<Serializable> call) {
        this.port = port;
        callback = call;
        server = new TheServer();
        server.start();
    }

    public int GetPort() {
        return this.port;
    }

    public class TheServer extends Thread {

        public void run() {

            try (ServerSocket mysocket = new ServerSocket(Server.this.port);) {
                System.out.println("Server is waiting for a client!");

                while (true) {

                    ClientThread c = new ClientThread(mysocket.accept(), clients.size());

                    //TODO: Figure out callback schema
//                    callback.accept("client has connected to server: " + "client #" + count);
                    clients.add(c);
                    clientIds.add(Integer.toString(c.count));

                    // Update Server GUI with updated list of clients
                    callback.accept(new GameInfo("UpdatePlayers", clients, computerLevel));
                    c.start();

                    // Update all Clients with list of active clients
                   c.updateClients(new Pair("ConnectedPlayers", clientIds));

                    Games game = new Games(count);
                    games.add(game);

                    count++;
                }
            }//end of try
            catch (Exception e) {
                // TODO: Figure out callback schema
                callback.accept("Server socket did not launch");
            }
        }//end of while
    }


    class ClientThread extends Thread {
        Socket connection;
        int count;
        boolean inGame;
        ObjectInputStream in;
        ObjectOutputStream out;

        ClientThread(Socket s, int count) {
            this.connection = s;
            this.count = count;
            this.inGame = false;
        }

        public void updateClients(Pair pair) {
            for (ClientThread client : clients) {
                if (client == null)
                    continue;
                try {
                    client.out.writeObject(new Pair(pair.getKey(), new Pair(client.count, pair.getValue())));
                    client.out.reset();
                } catch (Exception e) {
                    System.out.println("Line 75 Server.java " + e);
                }

            }
        }

        public void handleComputerLevel(Pair data) {

            // get the player id from the data key
            int PlayerID = (Integer) data.getKey();

            //// get the subpair from the data
            Pair<String, String> subpair = (Pair<String, String>) data.getValue();

            // set the computer level the players wants to play
            games.get(PlayerID).computerLevel =  subpair.getValue();
        }

        public void handleGameBoardUpdate(Pair data) {
            // get the player id from the data key
            int PlayerID = (Integer) data.getKey();

            //// get the subpair from the data
            Pair<String, ArrayList<String>> subpair = (Pair<String, ArrayList<String>>) data.getValue();

            System.out.println(subpair.getValue());

            games.get(PlayerID).boardState = subpair.getValue();

            // will call min max and get new board here
//            ArrayList<String> newBoard = new ArrayList<>(Arrays.asList("O", "b", "b", "b", "b", "b", "b", "b" ,"X"));
            int newBoard = FindNextMove.getMove(games.get(PlayerID).boardState, games.get(PlayerID).computerLevel);

            try {
                clients.get(PlayerID).out.writeObject(new Pair("UpdatedBoard", new Pair(PlayerID, newBoard)));
                clients.get(PlayerID).out.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void handlePlayAgain(Pair data) {
            // get the player id from the data key
            int PlayerID = (Integer) data.getKey();

            //// get the subpair from the data
            Pair<String, ArrayList<String>> subpair = (Pair<String, ArrayList<String>>) data.getValue();

            // Clear the game board for the player
            games.get(PlayerID).boardState = subpair.getValue();

            // Clear the computer level from previous game
            games.get(PlayerID).computerLevel = null;
        }

        public void run() {

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            } catch (Exception e) {
                System.out.println("Streams not open");
            }

            updateClients(new Pair("ConnectedPlayers", clientIds));

            while (true) {
                try {
                    Pair<Integer, Pair<String, String>> data = (Pair) in.readObject();

                    Pair<String, String> subpair = data.getValue();

                    // TODO: Figure out callback schema

                    switch (subpair.getKey()) {
                        case "levelType": handleComputerLevel(data);
                                          break;
                        case "gameBoardUpdate": handleGameBoardUpdate(data);
                                                break;
                        case "playAgain": handlePlayAgain(data);
                                          break;
                    }

                } catch (Exception e) {
                    clients.set(count, null);
                    clientIds.set(count, null);
                    callback.accept(new GameInfo("UpdatePlayers", clients, computerLevel));
                    updateClients(new Pair("ConnectedPlayers", clientIds));
                    break;
                }
            }
        }//end of run

    }//end of client thread
}