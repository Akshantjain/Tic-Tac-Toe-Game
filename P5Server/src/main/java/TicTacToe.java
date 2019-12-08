import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.Serializable;
import java.io.Serializable;
import java.util.HashMap;

public class TicTacToe extends Application {
	private Server serverConnection;        // variable for a connection to server
	private int port;        // variable for port number
	private HashMap<String, Scene> SceneMap = new HashMap<>();        // different scenes hash map
	private TextField portInput;        // scene 1 text field for port number input
	//private Label progress1 = new Label("PROGRESS"), progress2 = new Label("PROGRESS");
	private Button exitButton, createServer, closeServer;        // buttons variables
	private ListView<Serializable> connectedClients = new ListView<>();        // list view variable
	private ListView<Serializable> roundPlayStats = new ListView<>();
	private ListView<Serializable> progress = new ListView<>();
	private Text serverTitle3;        // variable used for animation

	public static void main(String[] args) {
		launch(args);
	}

	public void handleCallback(GameInfo data) {
		switch (data.type) {
			case "UpdatePlayers":
				connectedClients.getItems().clear();
				for (Server.ClientThread thread : data.clients) {
					if (thread == null)
						continue;
					connectedClients.getItems().add("Player #" + thread.count);
				}
		}
	}

	@Override
	public void start(Stage primaryStage) {    // main method
		primaryStage.setTitle("Tic Tac Toe Game");

		// embed the scenes in to the hash map
		SceneMap.put("ServerScene1", ServerGUI1());
		SceneMap.put("ServerScene2", ServerGUI2());

		// create a server using pause transitions showing some animations
		createServer.setOnAction(e -> {
			try {
				port = Integer.parseInt(portInput.getText());
				serverConnection = new Server(port,
						data -> Platform.runLater(
								() -> handleCallback((GameInfo) data)
						));

				primaryStage.setScene(SceneMap.get("ServerScene2"));
//                serverConnection.start();
			} catch (Exception ignored) {
				serverTitle3.setText("CANNOT CREATE SERVER. RETRY!!!");
				serverTitle3.setStyle("-fx-font-size: 25;" + "-fx-fill: RED;");
				portInput.setText("");
				portInput.setFocusTraversable(false);
				clearAll(false);
			}
		});
		// set button to close the program without creating a  server
		exitButton.setOnAction(e -> primaryStage.close());

		// closing the server and exit the program
		closeServer.setOnAction(e -> {
			try {
				primaryStage.close();

				//TODO: Clear connections with all clients, and then kill program
				//serverConnection.shutdown();
			} catch (Exception ignored) {
			}
		});

		EventHandler <KeyEvent> tabEventHandler = event -> {
			if (event.getCode() == KeyCode.TAB)
				if (portInput.isFocused()) portInput.requestFocus();
			if (event.getCode() == KeyCode.ENTER)   {
				createServer.fire();
				event.consume();
			}
		};

		portInput.setOnKeyPressed(tabEventHandler);
		createServer.setOnKeyPressed(tabEventHandler);

		primaryStage.setScene(SceneMap.get("ServerScene1"));    // set scene 1
		primaryStage.setFullScreenExitHint("");    // just in case
		primaryStage.show();        // start the program
	}

	// creating the scene 1
	private Scene ServerGUI1() {
		// creating and setting up various variables like buttons, text fields, and titles to their positions
		// and set their styles for scene 1
		Text serverTitle1 = desiredText(new Text("Tic Tac"));
		Text serverTitle2 = desiredText(new Text("Toe!"));
		Text empty = desiredText(new Text(""));
		serverTitle3 = desiredText(new Text("NEW SERVER INFORMATION"));

		empty.setStyle("-fx-font-size: 20px");
		serverTitle3.setStyle("-fx-font-size: 25px");

		portInput = createTextField(new Text("PORT NUMBER"));

		createServer = new Button("CREATE");
		exitButton = new Button("EXIT");

		exitButton.setStyle(
				"-fx-pref-width: 150px;" + "-fx-pref-height: 10px;" +
						"-fx-font-size: 25px;" + "-fx-spacing: 100px;" +
						"-fx-border-radius: 1em;" + "-fx-background-radius: 1em;"
		);
		createServer.setStyle(
				"-fx-pref-width: 200px;" + "-fx-pref-height: 10px;" +
						"-fx-font-size: 25px;" + "-fx-spacing: 100px;" +
						"-fx-border-radius: 1em;" + "-fx-background-radius: 1em;"
		);

		VBox startBox = new VBox();
		VBox inputBox = new VBox(10);

		startBox.setAlignment(Pos.CENTER);
		inputBox.setAlignment(Pos.CENTER);
		inputBox.setMaxSize(300, 20);
		inputBox.setPadding(new Insets(20, 0, 0, 0));

		// disable auto selection
		portInput.setFocusTraversable(false);
		createServer.setFocusTraversable(false);
		exitButton.setFocusTraversable(false);

		startBox.getChildren().addAll(serverTitle1, serverTitle2, empty, serverTitle3, inputBox);
		inputBox.getChildren().addAll(portInput, createServer, exitButton);

		return new Scene(startBox, 700, 700);
	}

	// creating and setting up various variables like buttons, text fields, and titles to their positions
	// and set their styles for scene 1
	private Scene ServerGUI2() {
		BorderPane borderPane = new BorderPane();
		closeServer = new Button("CLOSE SERVER");

		closeServer.setStyle(
				"-fx-pref-width: 220px;" + "-fx-pref-height: 10px;" +
						"-fx-font-size: 22px;" + "-fx-spacing: 100px;" +
						"-fx-border-radius: 1em;" + "-fx-background-radius: 1em;"
		);

		HBox buttonsArea = new HBox(40, closeServer);
		closeServer.setFocusTraversable(false);
		buttonsArea.setAlignment(Pos.CENTER);
		buttonsArea.setPadding(new Insets(20, 0, 20, 0));

		VBox leftBox = new VBox(connectedClients);
		connectedClients.setPrefHeight(600);
		connectedClients.setPrefWidth(500);
		connectedClients.setStyle(
				"-fx-background-radius: 1em;" +
						"-fx-border-radius: 1em;"
		);
		connectedClients.setFocusTraversable(false);

		leftBox.setPrefHeight(700);
		leftBox.setPrefWidth(500);
		leftBox.setPadding(new Insets(20, 10, 20, 20));
		leftBox.setAlignment(Pos.CENTER);

		VBox rightBox = new VBox(roundPlayStats, progress);
		rightBox.setPrefHeight(700);
		rightBox.setPrefWidth(500);
		rightBox.setPadding(new Insets(20, 10, 20, 20));
		rightBox.setSpacing(20);
		rightBox.setAlignment(Pos.CENTER);
		roundPlayStats.setPrefHeight(280);
		roundPlayStats.setPrefWidth(500);
		roundPlayStats.setStyle(
				"-fx-background-radius: 1em;" +
						"-fx-border-radius: 1em;"
		);
		roundPlayStats.setFocusTraversable(false);

		progress.setPrefHeight(280);
		progress.setPrefWidth(500);
		progress.setStyle(
				"-fx-background-radius: 1em;" +
						"-fx-border-radius: 1em;"
		);
		progress.setFocusTraversable(false);

		borderPane.setRight(rightBox);
		borderPane.setCenter(leftBox);
		borderPane.setBottom(buttonsArea);
		return new Scene(borderPane, 1000, 700);
	}

	// creating and then returning new text fields
	private TextField createTextField(Text string) {
		TextField textField = new TextField();
		textField.setPromptText(string.getText());
		textField.setFocusTraversable(false);
		textField.setStyle(
				"-fx-font-size: 20px;" +
						"-fx-background-radius: 1em;" +
						"-fx-border-radius: 1em;" +
						"-fx-display-carezt: false;"
		);
		return textField;
	}

	// set style of text
	private Text desiredText(Text string) {
		string.setStyle("-fx-font-size: 40");
		return string;
	}

	// enable and disable scene 1 buttons and text fields during the animation
	private void clearAll(boolean bool) {
		portInput.setDisable(bool);
		exitButton.setDisable(bool);
		createServer.setDisable(bool);
	}


}
