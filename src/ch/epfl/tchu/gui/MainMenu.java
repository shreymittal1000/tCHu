package ch.epfl.tchu.gui;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static ch.epfl.tchu.game.PlayerId.PLAYER_3;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public final class MainMenu extends Application{
    String name = "";
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        
        
        
        Text titleText = new Text("tCHu - Main Menu");
        titleText.setLayoutX(70.0);
        titleText.setLayoutY(50.0);
        titleText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 26)); 
        
        Text newIntroText = new Text("choisissez si vous souhaitez héberger un serveur ou en rejoindre un");
        newIntroText.setLayoutX(70.0);
        newIntroText.setLayoutY(79.0);
        newIntroText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 14)); 
        
        Button joinGameButton = new Button("Rejoins une partie");
        joinGameButton.setLayoutX(70.0);
        joinGameButton.setLayoutY(120.0); 
        joinGameButton.prefHeight(30.0); 
        joinGameButton.prefWidth(128.0);
        joinGameButton.setMnemonicParsing(false);
        
        Button hostGameButton = new Button("héberger un jeu");
        hostGameButton.setLayoutX(70.0);
        hostGameButton.setLayoutY(175.0); 
        hostGameButton.prefHeight(30.0); 
        hostGameButton.prefWidth(128.0);
        hostGameButton.setMnemonicParsing(false);
        
        Button quitGameButton = new Button("Quitter");
        quitGameButton.setLayoutX(70.0);
        quitGameButton.setLayoutY(230.0); 
        quitGameButton.prefHeight(30.0); 
        quitGameButton.prefWidth(128.0);
        quitGameButton.setMnemonicParsing(false);
        
        AnchorPane mainMenuPane = new AnchorPane(joinGameButton, hostGameButton, quitGameButton, newIntroText, titleText);
        mainMenuPane.setPrefHeight(285.0);
        mainMenuPane.setPrefWidth(700.0);
        
        
        
        Scene mainMenuScene = new Scene(mainMenuPane);
        Stage mainMenuStage = new Stage();
        mainMenuStage.setScene(mainMenuScene);
        mainMenuStage.setTitle("tCHu - Main Menu");
        mainMenuStage.show();
        
        joinGameButton.setOnMouseClicked((e) -> {
            TextField nameField = new TextField();
            nameField.setLayoutX(120.0);
            nameField.setLayoutY(80.0);
            Text nameText = new Text("Votre Nom");
            nameText.setLayoutX(40.0);
            nameText.setLayoutY(100.0);
            nameText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 13));
            mainMenuPane.setVisible(false);
            Text joinIntroText = new Text("Veuillez saisir l'adresse IP et le port que vous souhaitez rejoindre");
            joinIntroText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 16));
            Text addressText = new Text("Address");
            Text portText = new Text("Port");
            addressText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 13));
            portText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 13));
            joinIntroText.setLayoutX(40.0);
            joinIntroText.setLayoutY(50.0);
            TextField address = new TextField();
            address.setLayoutX(120.0);
            address.setLayoutY(120.0);
            addressText.setLayoutX(40.0);
            addressText.setLayoutY(140.0);
            TextField port = new TextField();
            port.setLayoutX(120.0);
            port.setLayoutY(160.0);
            portText.setLayoutX(40.0);
            portText.setLayoutY(180.0);
            Button Submit = new Button("Rejoindre");
            Submit.setLayoutX(40.0);
            Submit.setLayoutY(210.0);
            
            Button back = new Button("Retourner");
            AnchorPane newMenuPane = new AnchorPane(back, addressText, portText, joinIntroText, address, port, Submit, nameField, nameText);
            newMenuPane.setMinWidth(700);
            newMenuPane.setMinHeight(280);
            
            
            mainMenuStage.setScene(new Scene(newMenuPane));
            
            back.setOnMouseClicked((event) -> {
                mainMenuStage.setScene(mainMenuScene);
                mainMenuPane.setVisible(true);
                newMenuPane.setVisible(false);
                
            });
            
            Submit.setOnMouseClicked((event) -> {
                List<String> arguments = List.of(address.textProperty().get(), port.textProperty().get(), nameField.textProperty().get());
                
                try {
                    initializeClient(arguments);
                } catch (Exception e1) {
                    throw new Error();
                }
            });
        });
        
        hostGameButton.setOnMouseClicked((e) -> {
            TextField nameField = new TextField();
            nameField.setLayoutX(130.0);
            nameField.setLayoutY(80.0);
            Text nameText = new Text("Votre Nom");
            nameText.setLayoutX(50.0);
            nameText.setLayoutY(100.0);
            nameText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 13));
            
            mainMenuPane.setVisible(false);
            Text hostIntroText = new Text("Choissisez le nombre des joueurs qui vont jouer !");
            hostIntroText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 16));
            hostIntroText.setLayoutX(50.0);
            hostIntroText.setLayoutY(60.0);
            
            Button hostBack = new Button("Retourner");
            
            Button twoPlayerButton = new Button("2 Joueurs");
            twoPlayerButton.setLayoutX(50.0);
            twoPlayerButton.setLayoutY(120.0);
            Button threePlayerButton = new Button("3 Joueurs");
            threePlayerButton.setLayoutX(50.0);
            threePlayerButton.setLayoutY(160.0);
            
            AnchorPane newPane = new AnchorPane(hostBack, twoPlayerButton, threePlayerButton, hostIntroText, nameText, nameField);
            newPane.setPrefHeight(200);
            newPane.setPrefWidth(550);
            
            Scene newScene = new Scene(newPane);
            
            hostBack.setOnMouseClicked((event) -> {
                mainMenuStage.setScene(mainMenuScene);
                mainMenuPane.setVisible(true);
                newPane.setVisible(false);
            });
            
            twoPlayerButton.setOnMouseClicked((event) -> {
                Constants.THREE_PLAYER = false;
                newPane.setVisible(false);
                initializeMenu(false, twoPlayerButton, newScene, mainMenuStage, newPane, nameField.textProperty().get());
            });
            threePlayerButton.setOnMouseClicked((event) -> {
                Constants.THREE_PLAYER = true;
                newPane.setVisible(false);
                initializeMenu(true, threePlayerButton, newScene, mainMenuStage, newPane, nameField.textProperty().get());
            });
            mainMenuStage.setScene(newScene);
            
        });
        
        quitGameButton.setOnMouseClicked((e) -> {
            mainMenuStage.hide();
        });
    }
    
    private void initializeClient(List<String> param) throws Exception{

        // analysis of parameters in order to instantiate variables necessary to the creation of RemotePlayerClient 
        String address = param.isEmpty() 
                ? "localhost" 
                : param.get(0);
        int port = param.isEmpty() 
                ? 5108 
                : Integer.parseInt(param.get(1));
        GraphicalPlayerAdapter playerAdapter = new GraphicalPlayerAdapter();
        
        // Creation of the Client.
        RemotePlayerClient client = new RemotePlayerClient(playerAdapter, address, port, param.get(2));
        
        // Running the thread.
        new Thread(() -> client.run()).start();
    }
    
    private void initializeMenu(boolean isThreePlayer, Button button, Scene oldScene, Stage stage, AnchorPane oldPane, String name) {
        
        Text introText = new Text("Cliquez sur le bouton pour démarrer le serveur");
        introText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 16));
        
        Button startServerButton = new Button("Démarrer le serveur");
        
        startServerButton.setOnMouseClicked((e) -> {
            try {
                initializeServer( name);
            } catch (Exception e1) {
                throw new Error();
            }
        });
        Button anotherBackButton = new Button("Retourner");
        AnchorPane anotherOne = new AnchorPane(anotherBackButton, startServerButton, introText);
        introText.setLayoutX(50.0);
        introText.setLayoutY(60.0);
        anotherOne.setPrefWidth(500);
        anotherOne.setPrefHeight(140);
        startServerButton.setLayoutY(80.0);
        startServerButton.setLayoutX(50.0);
        
        anotherBackButton.setOnMouseClicked((e) -> {
            stage.setScene(oldScene);
            oldPane.setVisible(true);
        });
        
        stage.setScene(new Scene(anotherOne));
    }
    
    private void initializeServer(String name1) throws Exception{
        
        
        if(Constants.THREE_PLAYER) {
            // Setting up Server
            RemotePlayerProxy playerProxy1 = null;
            RemotePlayerProxy playerProxy2 = null;
            GraphicalPlayerAdapter gpa = new GraphicalPlayerAdapter();
            
            try{
                ServerSocket server = new ServerSocket(5108);
                Socket s1 = server.accept();
                Socket s2 = server.accept();
                
                
                playerProxy1 = new RemotePlayerProxy(s1);
                playerProxy2 = new RemotePlayerProxy(s2);
                
                
                
            } catch(IOException e) {
                throw new UncheckedIOException(e);
            }
            
            String name2 = playerProxy1.setPlayerName();
            String name3 = playerProxy2.setPlayerName();
            
            
            Random rand = new Random();
            
            //Initializaing Player Maps and GraphicalPlayerAdapter through analyzing parameters
            Map<PlayerId, String> playerNames = Map.of( 
                    PLAYER_1, name1.isEmpty() ? "Ada" : name1,  
                    PLAYER_2, name2.isEmpty() ? "Charles" : name2,
                    PLAYER_3, name3.isEmpty() ? "Michel" : name3); 
            Map<PlayerId, Player> players = Map.of( 
                    PLAYER_1, gpa,
                    PLAYER_2, playerProxy1,
                    PLAYER_3, playerProxy2);
            
            new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), rand, 1)).start();
        }
        else {

            // Setting up Server
            RemotePlayerProxy playerProxy = null;
            GraphicalPlayerAdapter gpa = new GraphicalPlayerAdapter();
            
            try{
                ServerSocket server = new ServerSocket(5108);
                Socket s = server.accept();
                
                playerProxy = new RemotePlayerProxy(s);
                
            } catch(IOException e) {
                throw new UncheckedIOException(e);
            }
            
            String name2 = playerProxy.setPlayerName();
            
            Random rand = new Random();
            
            //Initializaing Player Maps and GraphicalPlayerAdapter through analyzing parameters
            Map<PlayerId, String> playerNames = Map.of( 
                    PLAYER_1, name1.isEmpty() ? "Ada" : name1,  
                    PLAYER_2, name2.isEmpty() ? "Charles" : name2); 
            Map<PlayerId, Player> players = Map.of( 
                    PLAYER_1, gpa,
                    PLAYER_2, playerProxy);
            
            new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), rand, 0)).start();
        }
    }

	
}
