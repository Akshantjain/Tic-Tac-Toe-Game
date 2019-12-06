import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.util.Pair;
import sun.awt.image.ImageWatched;

public class Server {
    private int port;
    int count = 0;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    ArrayList<String> clientIds = new ArrayList<>();

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
                    callback.accept(new GameInfo("UpdatePlayers", clients));
                    c.start();

                    // Update all Clients with list of active clients
//                    c.updateClients(new GameInfo("UpdateClients", clients));
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
//                    client.out.writeObject(new Pair(pair.getKey(), new Pair(client.count, pair.getValue())));
//                    client.out.reset();
                } catch (Exception e) {
                    System.out.println("Line 75 Server.java " + e);
                }

            }
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
//                    Pair<String, Integer> data = (Pair) in.readObject();
//
//                    System.out.println(data.getKey() + " " + data.getValue());

                    // TODO: Figure out callback schema
//                    callback.accept("client: " + count + " sent: " + data);
//                    updateClients("client #" + count + " said: " + data);

                } catch (Exception e) {
                    clients.set(count, null);
                    clientIds.set(count, null);
//                    clients.remove(this);
                    callback.accept(new GameInfo("UpdatePlayers", clients));

                    updateClients(new Pair("ConnectedPlayers", clientIds));
                    break;
                }
            }
        }//end of run


    }//end of client thread
}

