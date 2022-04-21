package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

/**
 * Immutable class that generates texts describing the course of the game. Most of these messages describe the actions of a given player.
 * These texts will be used to communicate the state of the game between the two players playing on different computers.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class Info {
	
	/**
	 * Returns the message declaring that the players, whose names are those given, have finished the game in a tie, each having won the
	 * given points (uses DRAW).
	 */
	private final String playerName;
	
	/**
	 * Default Info constructor. Initializes an Info by giving it the name of a player.
	 * @param playerName (String): the name of a player.
	 */
	public Info(String playerName){
		this.playerName = playerName;
	}
	
	/**
	 * Returns the (French) name of the given card, in the singular if the absolute value of the second argument is 1.
	 * @param card (Card): the name of the card to be returned.
	 * @param count (int): the amount of cards there are.
	 * @return (String): the (French) name of the given card, in the singular if the absolute value of the second argument is 1, in plural otherwise.
	 */
	public static String cardName(Card card, int count) {
	    
		String cardName = "";
		switch(card) {
    		case BLACK      : 
    		    cardName += StringsFr.BLACK_CARD;
    		    break;
    		case BLUE       :
    		    cardName += StringsFr.BLUE_CARD;
    		    break;
            case GREEN      :
                cardName += StringsFr.GREEN_CARD;
                break;
            case ORANGE     :
                cardName += StringsFr.ORANGE_CARD;
                break;
            case RED        :
                cardName += StringsFr.RED_CARD;
                break;
            case VIOLET     :
                cardName += StringsFr.VIOLET_CARD;
                break;
            case WHITE      :
                cardName += StringsFr.WHITE_CARD;
                break;
            case YELLOW     :
                cardName += StringsFr.YELLOW_CARD;
                break;
            case LOCOMOTIVE :
                cardName += StringsFr.LOCOMOTIVE_CARD;
                break;
		}
		cardName += StringsFr.plural(count);
		return cardName;
	}
	
	/**
	 * Returns a message declaring that the players, whose names are those given, have finished the game in a tie, each having won the given points.
	 * @param playerNames (List<String>): the List containing the names of the 2 players.
	 * @param points (int): the number of points both players have.
	 * @return (String): a message declaring that both the players have finished the game in a tie, each having won "points" points.
	 */
	public static String draw(List<String> playerNames, int points) {
		return String.format(StringsFr.DRAW, playerNames.get(0) + StringsFr.AND_SEPARATOR + playerNames.get(1), points);
	}
	
	/**
	 * Returns a message declaring that the player will play first.
	 * @return (String): the message declaring that the player will play first.
	 */
	public String willPlayFirst() {
		return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
	}
	
	/**
	 * Returns a message declaring that the player has kept the given number of tickets.
	 * @param count (int): the number of tickets kept by the player.
	 * @return (String): the message declaring that the player has kept the given number of tickets.
	 */
	public String keptTickets(int count) {
		return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
	}
	
	/**
	 * Returns a message declaring that the player can play.
	 * @return (String): the message declaring that the player can play.
	 */
	public String canPlay() {
		return String.format(StringsFr.CAN_PLAY, playerName);
	}
	
	/**
	 * Returns a message declaring that the player has drawn the given number of tickets.
	 * @param count (int): the number of tickets drawn by the player.
	 * @return (String): a message declaring that the player has drawn the given number of tickets.
	 */
	public String drewTickets(int count) {
		return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
	}
	
	/**
	 * Returns a message declaring that the player has drawn a "blind" card, i.e. from the top of the deck.
	 * @return (String): a message declaring that the player has drawn a "blind" card, i.e. from the top of the deck.
	 */
	public String drewBlindCard() {
		return String.format(StringsFr.DREW_BLIND_CARD, playerName);
	}
	
	/**
	 * Returns a message declaring that the player has drawn the given face up card.
	 * @param card (Card): the type of card drawn by the player.
	 * @return (String): a message declaring that the player has drawn the given face up card.
	 */
	public String drewVisibleCard(Card card) {
		return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
	}
	
	/**
	 * Returns a message declaring that the player has taken the given route using the given cards.
	 * @param route (Route): the route claimed by the player.
	 * @param initialCards (SortedBag<Card>): the cards used to claim the route.
	 * @return (String): a message declaring that the player has taken the given route using the given cards.
	 */
	public String claimedRoute(Route route, SortedBag<Card> cards) {
		return String.format(StringsFr.CLAIMED_ROUTE, playerName, Info.routeRepresentation(route),
				Info.cardsRepresentation(cards));
	}
	
	/**
	 * Returns a message stating that the player wishes to claim the given tunnel road using the given cards initially.
	 * @param route (Route): the tunnel attempted to be claimed by the player.
	 * @param initialCards (SortedBag<Card>): the cards used to by the player to try claim the tunnel.
	 * @return (String): a message stating that the player wishes to claim the given tunnel road using the given cards
	 * initially.
	 */
	public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
		return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, Info.routeRepresentation(route),
				Info.cardsRepresentation(initialCards));
	}
	
	/**
	 * Returns a message stating that the player has drawn the 3 additional cards given, and that they involve an
	 * additional cost of the given number of cards.
	 * @param drawnCards (SortedBag<Card>): the 3 additional cards drawn.
	 * @param additionalCost (int): the additional cost of the given number of cards.
	 * @return (String): a message stating that the player has drawn the 3 additional cards given, and that they
	 * involve an additional cost of the given number of cards.
	 */
	public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
	    return additionalCost == 0 ? String.format(StringsFr.ADDITIONAL_CARDS_ARE + StringsFr.NO_ADDITIONAL_COST, Info.cardsRepresentation(drawnCards)) : 
	        String.format(StringsFr.ADDITIONAL_CARDS_ARE +  StringsFr.SOME_ADDITIONAL_COST, Info.cardsRepresentation(drawnCards), additionalCost, StringsFr.plural(additionalCost));
	}
	
	/**
	 * Returns a message declaring that the player has only the given number (and less than or equal to 2) of wagons
	 * left, and that the last round therefore begins.
	 * @param route (Route): the route in question that the player was not able to claim.
	 * @return (String): a message declaring that the player has only the given number (and less than or equal to 2)
	 * of wagons left, and that the last round therefore begins.
	 */
	public String didNotClaimRoute(Route route) {
		return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, Info.routeRepresentation(route));
	}
	
	/**
	 * Returns a message declaring that the player has the given amount (less than or equal to 2) of wagons left, making
	 * this turn their last turn of the game.
	 * @param carCount (int): the number of wagons the player has left.
	 * @return (String): a message declaring that the player has the given amount (less than or equal to 2) of wagons
	 * left, making this turn their last turn of the game.
	 */
	public String lastTurnBegins(int carCount) {
		return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
	}
	
	/**
	 * Returns a message stating that the player obtains the end-game bonus through the given path, which is the longest,
	 * or one of the longest.
	 * @param longestTrail (Trail): (one of) the longest trail made by the player in the game.
	 * @return (String): a message stating that the player obtains the end-game bonus through the given path, which is the
	 * longest, or one of the longest.
	 */
	public String getsLongestTrailBonus(Trail longestTrail) {
		return String.format(StringsFr.GETS_BONUS, playerName, longestTrail);
	}
	
	/**
	 * Returns a message declaring that the player wins the game with the given number of points, his opponent having
	 * obtained only loserPoints amount of points.
	 * @param points (int): the number of points obtained by the winning player.
	 * @param loserPoints (int) the number of points obtained by the losing player.
	 * @return (String): a message declaring that the player wins the game with the given number of points, his opponent
	 * having obtained only loserPoints amount of points.
	 */
	public String won(int points, int loserPoints) {
		return String.format(StringsFr.WINS, playerName, points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
	}
	
	/**
	 * Method used to return a String representation of the given set of cards in the correct format.
	 * @param cards (SortedBag<Card>): the set of cards that need a String representation.
	 * @return (String): a String representation of the given set of cards in the correct format.
	 */
	public static String cardsRepresentation(SortedBag<Card> cards) {
		String cardsString = "";
		int counter = 0;
		for(Card c : cards.toSet()) {
			int n = cards.countOf(c);
			cardsString += n + " " + Info.cardName(c, n);
			if(counter == cards.toSet().size()-2 ) {
				cardsString += StringsFr.AND_SEPARATOR;
			}
			else if(counter < cards.toSet().size()-2) {
				cardsString += ", ";
			}
			counter++;
		}
		return cardsString;
	}
	
	/**
	 * Method used to return a String representation of the given route in the correct format.
	 * @param route (Route): the route that need a String representation.
	 * @return (String): a String representation of the given route in the correct format.
	 */
	private static String routeRepresentation(Route route) {
		return route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2();
	}
}
