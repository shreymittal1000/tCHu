package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Class that allows for the creation of the graphical elements needed to display the map of the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
final class MapViewCreator {
	private static final int WAGON_WIDTH = 36;
	private static final int WAGON_HEIGHT = 12;
    private static final int STATION_X = 12;
    private static final int STATION_Y = 6;
    private static final int STATION_RADIUS = 3;
    private static final int AUTO_CLAIM_SIZE = 1;
    
    
	/**
	 * Private MapViewCreator constructor, whose sole aim is to make this class uninstanciable.
	 */
	private MapViewCreator() {}
	
	/**
	 * Creates a view of the map of the game, based on the given parameters.
	 * @param gameState (ObservableGameState): the observable game state of the game.
	 * @param property (ObjectProperty<ClaimRouteHandler>): the property of the game.
	 * @param chooser (CardChooser): the card chooser of the game.
	 */
	public static Pane createMapView(ObservableGameState gameState, ObjectProperty<ClaimRouteHandler> claimRouteHP, CardChooser cardChooser) {
		Pane Carte = new Pane();
		Carte.getStylesheets().addAll("map.css", "colors.css");
		ImageView fond = new ImageView("map.png");
		Carte.getChildren().add(fond);
		
		// Group called route on diagram
		
		for(Route route : ChMap.routes()) {
		    Group routeGroup = new Group();
		    
		    // avoiding repeated recalculation of the syleClass of routeGroup by storing it
		    ObservableList<String> routeGroupStyleClass = routeGroup.getStyleClass();
		    
		    gameState.routeId(route).addListener((o, oV, nV) -> routeGroupStyleClass.add(nV.name()));
		    routeGroup.disableProperty().bind(claimRouteHP.isNull().or(gameState.claimable(route).not()));
	        routeGroup.setId(route.id());
	        routeGroupStyleClass.addAll(
	                "route",
	                route.level().name(),  
	                route.color() == null ? "NEUTRAL" : route.color().name());
	        
	        routeGroup.setOnMouseClicked((e) -> {
	            List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);
	            
	            if(possibleClaimCards.size() == AUTO_CLAIM_SIZE) {
	                claimRouteHP.get().onClaimRoute(route, possibleClaimCards.get(0));
	            } else if(possibleClaimCards.size() > AUTO_CLAIM_SIZE){
	                ClaimRouteHandler claimRouteH = claimRouteHP.get();
	                ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
	                cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
	            }
	        });
	        
	        for(int j = 0; j < route.length(); j++) {
	            
	            Rectangle Voie = new Rectangle(WAGON_WIDTH, WAGON_HEIGHT);
	            Voie.getStyleClass().addAll("track", "filled");
	            
	            Rectangle wagonR;
	            wagonR = new Rectangle(WAGON_WIDTH , WAGON_HEIGHT);
	            wagonR.getStyleClass().add("filled");
	            Circle circleR1;
	            circleR1 = new Circle(STATION_X, STATION_Y, STATION_RADIUS);           
	            Circle circleR2; 
	            circleR2 = new Circle(2*STATION_X, STATION_Y, STATION_RADIUS);
	            
	            Group wagonGroup = new Group(wagonR, circleR1, circleR2);
                wagonGroup.getStyleClass().add("car");
                
                Group caseGroup = new Group(Voie, wagonGroup);
                caseGroup.setId(route.id() + "_" + (j+1));
                
	            routeGroup.getChildren().add(caseGroup);
	            
	        }
	        
	        Carte.getChildren().add(routeGroup);
	        
		}
        return Carte;
	}
	
	/**
	 * Functional interface which is embedded into the MapViewCreator class, and whose aim
	 * is to force implementing classes to define a method which allows it to choose cards.
	 * @author Shrey Mittal (312275)
	 * @author David Chernis (310298)
	 */
	@FunctionalInterface
	interface CardChooser {
		/**
		 * Chooses cards from the list of options and based on the handler provided.
		 * @param options (List<SortedBag<Card>>): the options of cards that can be drawn.
		 * @param handler (ChooseCardsHandler): the handler which is used to choose cards.
		 */
		void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
	}
}
