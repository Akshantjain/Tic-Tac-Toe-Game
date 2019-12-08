import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class TicTacToe extends Application {

    private Client clientConnection;        // variable for the client
    private int filledSquares = 0;
    private int port;    // port number
    private String host = null;        // IP Address
    private ArrayList<String> currentMoves = new ArrayList<>();
    private TextField ipAddressInput, portInput;        // text fields variables
    private HashMap<String, Scene> SceneMap = new HashMap<>();        // hash map fo the scenes
    private Button exitButton, connectClient, playAgain, clearInfo, easyMode, mediumMode, expertMode;    // buttons variables
    private ListView<Serializable> scores = new ListView<>();
    private ListView<Serializable> progress = new ListView<>();
    private Integer playerID;
    private Vector<Rectangle> board = new Vector<>();
    private Text clientTitle3;
    private TextField result;
    private String gameResult;
    private PauseTransition pauseSendMove = new PauseTransition(Duration.seconds(1));
    private boolean gotResult = false;

    public static void main(String[] args) {
        launch(args);
    }

    //feel free to remove the starter code from this method
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tic Tac Toe Game");

        SceneMap.put("ClientScene1", ClientGUI1());
        SceneMap.put("ClientScene2", ClientGUI2());
        SceneMap.put("ClientScene3", ClientGUI3());

        exitButton.setOnAction(e -> primaryStage.close());

        playAgain.setOnAction(e -> {
            primaryStage.setScene(SceneMap.get("ClientScene2"));

            clearBoard();
            setClickable(false);

            filledSquares = 0;

            initMoves();

            result.setText("");

            gotResult = false;

            // send the updated game board to the server
            try {
                clientConnection.sendData(new Pair(playerID, new Pair("playAgain", currentMoves)));
            } catch (Exception error) {
                error.printStackTrace();
            }
        });

        exitButton.setOnAction(e -> {
            primaryStage.close();
            try {
                // close down the client
                clientConnection.sendData("QUIT");
                clientConnection.shutDown();
                Platform.exit();
                System.exit(0);
            } catch (Exception ignored) {
            }
        });

        connectClient.setOnAction(e -> {
            try {
                host = ipAddressInput.getText();
                port = Integer.parseInt(portInput.getText());
                clientConnection = new Client(host, port,
                        data -> Platform.runLater(() -> progress.getItems().add(data.toString())),
                        data -> Platform.runLater(() -> {
                            scores.getItems().clear();
                            scores.getItems().add(data.toString());
                        }),
                        data -> Platform.runLater(() -> playerID = (Integer) data),
                        data -> Platform.runLater(() -> {
                            makeComputerMove((int) data - 1);
                        }),
                        data -> Platform.runLater(() -> updateClientUI(data.toString())),
                        data -> Platform.runLater(() -> {
                            makeComputerMove((int) data - 1);
                        })
                );
                primaryStage.setScene(SceneMap.get("ClientScene2"));

                clientConnection.start();

                System.out.println("Client Start Called");
            } catch (Exception ignored) {
                clientTitle3.setText("SERVER NOT FOUND. RETRY!!!");
                clientTitle3.setStyle("-fx-font-size: 25;" + "-fx-fill: RED;");
                ipAddressInput.setText("");
                portInput.setText("");
                clearAll();
                ipAddressInput.setFocusTraversable(false);
                portInput.setFocusTraversable(false);
            }
        });
        EventHandler<KeyEvent> tabEventHandler = event -> {
            if (event.getCode() == KeyCode.TAB) {
                if (ipAddressInput.isFocused()) portInput.requestFocus();
                else if (portInput.isFocused()) ipAddressInput.requestFocus();
            }
            if (event.getCode() == KeyCode.ENTER) {
                connectClient.fire();
                event.consume();
            }
        };

        ipAddressInput.setOnKeyPressed(tabEventHandler);
        portInput.setOnKeyPressed(tabEventHandler);

        easyMode.setOnAction(mouseClick -> {
            try {
                clientConnection.sendData(new Pair(playerID, new Pair("levelType", "easy")));

//                playAgain.setDisable(true);
//                exitButton.setDisable(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            primaryStage.setScene(SceneMap.get("ClientScene3"));
        });

        mediumMode.setOnAction(mouseEvent -> {
            try {
                clientConnection.sendData(new Pair(playerID, new Pair("levelType", "medium")));

//                playAgain.setDisable(true);
//                exitButton.setDisable(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            primaryStage.setScene(SceneMap.get("ClientScene3"));
        });

        expertMode.setOnAction(mouseEvent -> {
            try {
                clientConnection.sendData(new Pair(playerID, new Pair("levelType", "expert")));

//                playAgain.setDisable(true);
//                exitButton.setDisable(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            primaryStage.setScene(SceneMap.get("ClientScene3"));
        });

        primaryStage.setScene(SceneMap.get("ClientScene1"));
        primaryStage.show();
    }

    private Scene ClientGUI1() {
        Text clientTitle1 = desiredText(new Text("TIC TAC TOE"));
        Text clientTitle2 = desiredText(new Text("MIN MAX GAME!"));
        Text empty = desiredText(new Text(""));
        clientTitle3 = desiredText(new Text("ENTER PLAYER INFORMATION"));

        empty.setStyle("-fx-font-size: 20px");
        clientTitle3.setStyle("-fx-font-size: 25px");

        ipAddressInput = createTextField(new Text("IP  ADDRESS"));
        portInput = createTextField(new Text("PORT  NUMBER"));

        connectClient = createButton("CONNECT");
        clearInfo = createButton("CLEAR");
        exitButton = createButton("EXIT");

        VBox startBox = new VBox();
        HBox buttons = new HBox();
        VBox inputBox = new VBox();

        startBox.setAlignment(Pos.CENTER);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setMaxSize(300, 20);
        inputBox.setPadding(new Insets(20, 0, 0, 0));
        buttons.setSpacing(10);
        inputBox.setSpacing(10);

        startBox.getChildren().addAll(clientTitle1, clientTitle2, empty, clientTitle3, inputBox);
        buttons.getChildren().addAll(connectClient, clearInfo);
        inputBox.getChildren().addAll(ipAddressInput, portInput, buttons, exitButton);

        clearInfo.setOnAction(e -> clearAll());

        return new Scene(startBox, 700, 700);
    }

    private void clearAll() {
        ipAddressInput.setText("");
        portInput.setText("");
        clearInfo.setFocusTraversable(false);
    }

    private Scene ClientGUI2() {
        Text text = new Text("SELECT  COMPUTER  LEVEL");
        text.setStyle("-fx-font-size: 20px; -fx-underline: single");
        easyMode = createButton("EASY");
        mediumMode = createButton("MEDIUM");
        expertMode = createButton("EXPERT");

        VBox modesArea = new VBox(20, text, easyMode, mediumMode, expertMode);
        modesArea.setAlignment(Pos.CENTER);
        return new Scene(modesArea, 400, 300);
    }

    private Scene ClientGUI3() {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-spacing: 20;-fx-padding: 20");
        playAgain = createButton("PLAY AGAIN");
        HBox buttonsArea = new HBox(20, playAgain, exitButton);
        buttonsArea.setAlignment(Pos.CENTER);

        GridPane stackPane = createTheStackPane();
        initMoves();
        setClickable(false);
        stackPane.setGridLinesVisible(true);

        result = new TextField();
        result.setPromptText("RESULT");
        result.setPrefWidth(170);
        result.setPrefHeight(150);
        result.setAlignment(Pos.CENTER);
        result.setFocusTraversable(false);
        result.setDisable(true);
        result.setStyle("-fx-font-size: 20px;" +
                "-fx-border-radius: 1em;" +
                "-fx-background-radius: 1em;" +
                "-fx-text-alignment: center;" +
                "-fx-opacity: 1");


        VBox vBox = new VBox(40, scores, result);
        vBox.setPadding(new Insets(0, 0, 0, 20));
        vBox.setPrefHeight(200);
        vBox.setPrefWidth(200);
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox(30, stackPane, vBox);
        scores.setFocusTraversable(false);
        scores.setPrefWidth(200);
        scores.setPrefHeight(200);
        scores.getItems().add("Top Scores: ");

        hBox.setAlignment(Pos.CENTER);
        hBox.setAlignment(Pos.CENTER);

        borderPane.setCenter(hBox);
        borderPane.setBottom(buttonsArea);

        return new Scene(borderPane, 700, 500);
    }

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

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setFocusTraversable(false);
        button.setStyle(
                "-fx-pref-width: 150px;" + "-fx-pref-height: 10px;" +
                        "-fx-font-size: 20px;" + "-fx-spacing: 100px;" +
                        "-fx-border-radius: 1em;" + "-fx-background-radius: 1em;" +
                        "-fx-background-color: #ffffff;" + "-fx-border-color: #000000;"
        );
        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-pref-width: 150px;" + "-fx-pref-height: 10px;" +
                                "-fx-font-size: 20px;" + "-fx-spacing: 100px;" +
                                "-fx-border-radius: 1em;" + "-fx-background-radius: 1em;" +
                                "-fx-background-color: #c2c2c2;" + "-fx-border-color: #000000;"
                )
        );
        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-pref-width: 150px;" + "-fx-pref-height: 10px;" +
                                "-fx-font-size: 20px;" + "-fx-spacing: 100px;" +
                                "-fx-border-radius: 1em;" + "-fx-background-radius: 1em;" +
                                "-fx-background-color: #ffffff;" + "-fx-border-color: #000000;"
                )
        );
        button.setOnMousePressed(e ->
                button.setStyle(
                        "-fx-pref-width: 150px;" + "-fx-pref-height: 10px;" +
                                "-fx-font-size: 20px;" + "-fx-spacing: 100px;" +
                                "-fx-border-radius: 1em;" + "-fx-background-radius: 1em;" +
                                "-fx-background-color: #676767;" + "-fx-border-color: #000000;"
                )
        );
        button.setOnMouseReleased(e ->
                button.setStyle(
                        "-fx-pref-width: 150px;" + "-fx-pref-height: 10px;" +
                                "-fx-font-size: 20px;" + "-fx-spacing: 100px;" +
                                "-fx-border-radius: 1em;" + "-fx-background-radius: 1em;" +
                                "-fx-background-color: #ffffff;" + "-fx-border-color: #000000;"
                )
        );
        return button;
    }

    private Text desiredText(Text string) {
        string.setStyle("-fx-font-size: 40");
        return string;
    }

    private void initMoves() {
        currentMoves.clear();
        for (int i = 0; i < 9; ++i)
            currentMoves.add("b");
    }

    private GridPane createTheStackPane() {
        Rectangle TopLeft = createRectangle(0);
        Rectangle TopCenter = createRectangle(1);
        Rectangle TopRight = createRectangle(2);

        Rectangle CenterLeft = createRectangle(3);
        Rectangle CenterCenter = createRectangle(4);
        Rectangle CenterRight = createRectangle(5);

        Rectangle BottomLeft = createRectangle(6);
        Rectangle BottomCenter = createRectangle(7);
        Rectangle BottomRight = createRectangle(8);

        GridPane gridPane = new GridPane();
        gridPane.add(TopLeft, 0, 0);
        gridPane.add(TopCenter, 1, 0);
        gridPane.add(TopRight, 2, 0);

        gridPane.add(CenterLeft, 0, 1);
        gridPane.add(CenterCenter, 1, 1);
        gridPane.add(CenterRight, 2, 1);

        gridPane.add(BottomLeft, 0, 2);
        gridPane.add(BottomCenter, 1, 2);
        gridPane.add(BottomRight, 2, 2);

        board.add(TopLeft);
        board.add(TopCenter);
        board.add(TopRight);

        board.add(CenterLeft);
        board.add(CenterCenter);
        board.add(CenterRight);

        board.add(BottomLeft);
        board.add(BottomCenter);
        board.add(BottomRight);

        return gridPane;
    }

    private Rectangle createRectangle(int location) {
        Rectangle rectangle = new Rectangle(133, 133);
        rectangle.setFill(new Color(0, 0, 0, 0));
        rectangle.setOnMouseClicked(e -> makePlayerMove(location));
        return rectangle;
    }

    private void updateClientUI (String data) {
        result.setText(data);
        setClickable(true);
        gotResult = true;

//        playAgain.setDisable(false);
//        exitButton.setDisable(false);
    }

    private void setClickable(boolean bool) {
        for (Rectangle rectangle : board) {
            rectangle.setDisable(bool);
        }
    }

    private void clearBoard() {
        for (Rectangle rectangle : board) {
            rectangle.setFill(new Color(0, 0, 0, 0));
            rectangle.setDisable(false);
        }
    }

    private void enableOpenMoves() {

        if (gotResult) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            if (currentMoves.get(i).equals("X") || currentMoves.get(i).equals("O"))
                continue;
            board.get(i).setDisable(false);
        }
    }

    private void makePlayerMove(int location) {
        board.get(location).setFill(new ImagePattern(new Image("O.png")));
        setClickable(true);
        currentMoves.set(location, "O");
        filledSquares++;

        // send the updated game board to the server using the pause transition
        pauseSendMove.setOnFinished(e -> {
            try {
                if (filledSquares <= 9) {
                    clientConnection.sendData(new Pair(playerID, new Pair("gameBoardUpdate", currentMoves)));
                    pauseSendMove.stop();
                }
            } catch (Exception i) {
                i.printStackTrace();
            }
        });
        pauseSendMove.play();
    }

    private void makeComputerMove(int location) {
        board.get(location).setFill(new ImagePattern(new Image("X.png")));
        currentMoves.set(location, "X");
        enableOpenMoves();
        filledSquares++;
    }
}
