import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client {
    private ClientThread connectionThread = new ClientThread();
    private Consumer<Serializable> scores;
    private Consumer<Serializable> progress;
    private String IP;
    private int port;

    Client(String ip, int port,
           Consumer<Serializable> progress,
           Consumer<Serializable> scores) {
        this.progress = progress;
        this.scores = scores;
        connectionThread.setDaemon(true);
        this.IP = ip;
        this.port = port;
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
                    Serializable data = (Serializable) in.readObject();
                }
            } catch (Exception e) {
                progress.accept("Game Server Closed\nThank you for playing.");
            }
        }
    }
}
