package ch.epfl.tchu.gui;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static ch.epfl.tchu.game.PlayerId.PLAYER_3;

/**
 * Class that represents the server that manages communication between the client and proxy player on which tchu is being played.  
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public class ServerMain extends Application{

    /**
     * program that calls launch with the parameters passed to the program
     * @param args (String[]) parameters passed to the program, which will be the names of the two players. 
     */
    public static void main(String[] args) {
        launch(args);
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	//Creates main menu
    	
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
	        
	        List<String> names = getParameters().getRaw();
	        Random rand = new Random();
	        
	        //Initializaing Player Maps and GraphicalPlayerAdapter through analyzing parameters
	        Map<PlayerId, String> playerNames = Map.of( 
	                PLAYER_1, names.isEmpty() ? "Ada" : names.get(0),  
	                PLAYER_2, names.isEmpty() ? "Charles" : names.get(1),
	        		PLAYER_3, names.isEmpty() ? "Michel" : names.get(2)); 
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
	        
	        List<String> names = getParameters().getRaw();
	        Random rand = new Random();
	        
	        //Initializaing Player Maps and GraphicalPlayerAdapter through analyzing parameters
	        Map<PlayerId, String> playerNames = Map.of( 
	                PLAYER_1, names.isEmpty() ? "Ada" : names.get(0),  
	                PLAYER_2, names.isEmpty() ? "Charles" : names.get(1)); 
	        Map<PlayerId, Player> players = Map.of( 
	                PLAYER_1, gpa,
	                PLAYER_2, playerProxy);
	        
	        new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), rand, 0)).start();
    	}
    }
}
