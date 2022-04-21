package ch.epfl.tchu.game;

import java.util.List;

import ch.epfl.tchu.Preconditions;

/**
 * Immutable class representing the player states that are publicly visible to everyone and do not require special access.
 * This includes the 5 face-up cards, the size of the deck and the number of discards.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public class PublicPlayerState {
	
	/**
	 * (int): represents the number of tickets possessed by the player.
	 */
	private final int ticketCount;
	
	/**
	 * (int): represents the number of cards possessed by the player.
	 */
	private final int cardCount;
	
	/**
	 * (List<Route>): a list of all the routes the player controls.
	 */
	private final List<Route> routes;
	
	/**
	 * (int): represents the number of wagons the player has left.
	 */
	private final int carCount;
	
	/**
	 * represents the number of points the player has obtained.
	 */
	private final int claimPoints;
	
	/**
	 * Default public constructor of PublicCardState. Initializes the PublicPlayerState by initializing
	 * the number of cards and tickets in possession of the player, along with a list of all the routes
	 * controlled by the player.
	 * @param ticketCount (int): represents the number of tickets possessed by the player.
	 * @param cardCount (int): represents the number of cards possessed by the player.
	 * @param routes (List<Route>): a list of all the routes the player controls.
	 * @throws IllegalArgumentException if either ticketCount or cardCount are strictly negative.
	 */
	public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
		Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
		this.ticketCount = ticketCount;
		this.cardCount = cardCount;
		this.routes = List.copyOf(routes);
		
		int length = 0;
        for(Route r: routes) {
            length += r.length();
        }
        carCount = Constants.INITIAL_CAR_COUNT-length;
        int points = 0;
        for(Route r: routes) {
            points += r.claimPoints();
        }
		claimPoints = points;
	}
	
	/**
	 * Returns the number of tickets possessed by the player.
	 * @return (int): the number of tickets possessed by the player.
	 */
	public int ticketCount() {
		return ticketCount;
	}
	
	/**
	 * Returns the number of cards possessed by the player.
	 * @return (int): the number of cards possessed by the player.
	 */
	public int cardCount() {
		return cardCount;
	}
	
	/**
	 * Returns a list of all the routes the player controls.
	 * @return (List<Route>): a list of all the routes the player controls.
	 */
	public List<Route> routes(){
		return List.copyOf(routes);
	}
	
	/**
	 * Returns represents the number of wagons the player has left.
	 * @return (int): represents the number of wagons the player has left.
	 */
	public int carCount() {
	    return carCount;
	}
	
	/**
	 * Returns represents the number of points the player has obtained.
	 * @return (int): represents the number of points the player has obtained.
	 */
	public int claimPoints() {
	    return claimPoints;
	    
	}
}
