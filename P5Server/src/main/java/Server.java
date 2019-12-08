import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
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

        public String getWinner(ArrayList<String> gameBoard) {

            if (!gameBoard.get(0).equals("b") && gameBoard.get(0).equals(gameBoard.get(1))  && gameBoard.get(0).equals(gameBoard.get(2))) {
                return gameBoard.get(0);
            }
            else if (!gameBoard.get(3).equals("b") && gameBoard.get(3).equals(gameBoard.get(4))  && gameBoard.get(3).equals(gameBoard.get(5))){
                return gameBoard.get(3);
            }
            else if (!gameBoard.get(6).equals("b") && gameBoard.get(6).equals(gameBoard.get(7))  && gameBoard.get(6).equals(gameBoard.get(8))) {
                return gameBoard.get(6);
            }
            else if (!gameBoard.get(0).equals("b") && gameBoard.get(0).equals(gameBoard.get(4))  && gameBoard.get(0).equals(gameBoard.get(8))) {
                return gameBoard.get(0);
            }
            else if (!gameBoard.get(2).equals("b") && gameBoard.get(2).equals(gameBoard.get(4))  && gameBoard.get(2).equals(gameBoard.get(6))) {
                return gameBoard.get(2);
            }
            else if (!gameBoard.get(0).equals("b") && gameBoard.get(0).equals(gameBoard.get(3))  && gameBoard.get(0).equals(gameBoard.get(6))) {
                return gameBoard.get(0);
            }
            else if (!gameBoard.get(1).equals("b") && gameBoard.get(1).equals(gameBoard.get(4))  && gameBoard.get(1).equals(gameBoard.get(7))) {
                return gameBoard.get(1);
            }
            else if (!gameBoard.get(2).equals("b") && gameBoard.get(2).equals(gameBoard.get(5))  && gameBoard.get(2).equals(gameBoard.get(8))) {
                return gameBoard.get(2);
            }

            return "No Result";
        }

        public boolean checkForWinner (int PlayerID, Pair<String, ArrayList<String>> subpair) {
            String decision = getWinner(subpair.getValue());

            if (decision.equals("O")) {
                // update score for that client on the games arraylist
                games.get(PlayerID).setHighScore(games.get(PlayerID).getHighScore() + 1);

                // Update client gui
                try {
                    clients.get(PlayerID).out.writeObject(new Pair("Win", new Pair(games.get(PlayerID).highScore, games.get(PlayerID).boardState)));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                // update the top 3 scores arraylist

                return true;

            }
            else if (decision.equals("X")) {
                // Update the client gui
                try {
                    clients.get(PlayerID).out.writeObject(new Pair("Lost", new Pair(games.get(PlayerID).highScore, games.get(PlayerID).boardState)));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                // update the top 3 scores arraylist

                return true;
            }

            return false;
        }

        public void sortTop3Scores() {

            ArrayList<Integer> finalList = new ArrayList<>();

            for (int i = 0; i < games.size(); i++) {
                if (games.get(i) != null) {

                    finalList.add(games.get(i).getHighScore());
                }
            }

            Collections.reverse(finalList);

            for (ClientThread client : clients) {
                if (client == null)
                    continue;
                try {
                    client.out.writeObject(new Pair("Top3Scores", finalList));
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

            games.get(PlayerID).boardState = subpair.getValue();

            if (checkForWinner(PlayerID, subpair)) {

                sortTop3Scores();

                return;
            }

            // will call min max and get new board here
            int newBoard = FindNextMove.getMove(games.get(PlayerID).boardState, games.get(PlayerID).computerLevel);

            if (checkForWinner(PlayerID, subpair)) {
                sortTop3Scores();

                return;
            }

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
                    games.set(count, null);
                    callback.accept(new GameInfo("UpdatePlayers", clients, computerLevel));
                    updateClients(new Pair("ConnectedPlayers", clientIds));
                    break;
                }
            }
        }//end of run

    }//end of client thread
}