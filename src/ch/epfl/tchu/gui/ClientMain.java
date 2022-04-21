<<<<<<< HEAD
package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Class that represents the client on which tchu is played 
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class ClientMain extends Application{

	/**
	 * The main method for the client.
	 * @param args (String[]): the arguments passed to the program at the beginning. 
	 * The first will be the name of the address and the second the port.
	 */
    public static void main(String[] args) {
        launch(args);        
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> param = getParameters().getRaw();
        
        // analysis of parameters in order to instantiate variables necessary to the creation of RemotePlayerClient 
        String address = param.isEmpty() 
                ? "localhost" 
                : param.get(0);
        int port = param.isEmpty() 
                ? 5108 
                : Integer.parseInt(param.get(1));
        GraphicalPlayerAdapter playerAdapter = new GraphicalPlayerAdapter();
        
        // Creation of the Client.
        RemotePlayerClient client = new RemotePlayerClient(playerAdapter, address, port, "");
        
        // Running the thread.
        new Thread(() -> client.run()).start();
        
    }
}
=======
package ch.epfl.tchu.gui;

public class ClientMain {
	public static void main(String[] args) {
		
	}
}
>>>>>>> 511729f (Hola)
