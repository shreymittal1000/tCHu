package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Immutable class representing the player states that are visible only to those given access.
 * This includes the tickets and the cards the player has in their possession.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class PlayerState extends PublicPlayerState{

	/**
	 * (SortedBag<Ticket>): is the set of all the tickets the player possesses.
	 */
	private final SortedBag<Ticket> tickets;
	
	/**
	 * (SortedBag<Card>): is the list of all cards the player possesses.
	 */
	private final SortedBag<Card> cards;
	
	/**
	 * Default PlayerState constructor. Initializes a PlayerState by giving it a SortedBag of tickets and cards, along
	 * with a List of routes.
	 * @param tickets (SortedBag<Ticket>): is the set of all the tickets the player possesses.
	 * @param cards (SortedBag<Card>): is the list of all cards the player possesses.
	 * @param routes (List<Route>): is the list of all the routes the player controls.
	 */
	public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
		super(tickets.size(), cards.size(), routes);
		this.tickets = SortedBag.of(tickets);
		this.cards = SortedBag.of(cards);
	}
	
	/**
	 * Returns the initial state of a player to whom the given initial cards have been dealt; in this initial state, the
	 * player does not yet have any tickets, and has not seized any routes.
	 * @param initialCards (SortedBag<Card>): the 4 cards initially dealt to the player at the start of the game.
	 * @return (PlayerState): the initial state of a player to whom the given initial cards have been dealt.
	 * @throws IllegalArgumentException if the size of initial cards in not 4.
	 */
	public static PlayerState initial(SortedBag<Card> initialCards) {
		Preconditions.checkArgument(initialCards.size() == 4);
		
		return new PlayerState(SortedBag.of(), initialCards, new ArrayList<Route>());
	}
	
	/**
	 * Returns the set of all the tickets the player possesses.
	 * @return (SortedBag<Ticket>): the set of all the tickets the player possesses.
	 */
	public SortedBag<Ticket> tickets(){
		return SortedBag.of(tickets);
	}
	
	/**
     * Returns a state identical to the receiver, except that the player also has the given tickets.
     * @return (PlayerState): a state identical to the receiver, except that the player also has the given tickets.
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(newTickets.union(tickets()), cards(), routes());
    }
	
	/**
	 * Returns the list of all cards the player possesses.
	 * @return (SortedBag<Card>): the list of all cards the player possesses.
	 */
	public SortedBag<Card> cards(){
		return SortedBag.of(cards);
	}
	
	/**
	 * Returns an identical state to the receiver, except that the player also has the given card.
	 * @param card (Card): the card to be added.
	 * @return (PlayerState): an identical state to the receiver, except that the player also has the given card.
	 */
	public PlayerState withAddedCard(Card card) {
		return new PlayerState(tickets, SortedBag.of(card).union(cards()), routes());
	}
	
	/**
	 * Returns true if the player can claim the given route, i.e. if he has enough wagons left and if he has the necessary cards.
	 * @param route (Route): the route in question.
	 * @return (boolean): true if the player can claim the given route, i.e. if he has enough wagons left and if he has the
	 * necessary cards.
	 */
	public boolean canClaimRoute(Route route) {
        boolean hasCars = carCount() >= route.length();
        boolean hasCorrectCards = hasCars && !possibleClaimCards(route).isEmpty();
        return hasCars && hasCorrectCards;
	}
	
	/**
     * Returns a List of a SortedBag (itself a SortedBag of Cards) containing all the possible combinations of cards which the
     * player can use to claim the route.
     * @return (List<SortedBag<Card>>): a List of all the possible combinations of card combinations which the player can use to
     * claim the route.
     * @throws IllegalArgumentException if the player doesn't have enough wagons to claim the route.
     */
	public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(carCount() >= route.length());
        List<SortedBag<Card>> possibleClaimCards = route.possibleClaimCards();
        
        for(int i = 0; i < possibleClaimCards.size(); i++) {
            if(!cards.contains(possibleClaimCards.get(i))) {
                possibleClaimCards.remove(i);
                i--;
            }
        }
        
        return possibleClaimCards;
    }
	
	/**
	 * Returns a list of all sets of additional cards the player could use to take over a tunnel, sorted in ascending order by the
	 * number of locomotive cards.
	 * @param additionalCardsCount (int): the number of extra cards the player needs to use to claim the tunnel.
	 * @param initialCards (SortedBag<Card>): the cards initially used by the player to try claim the tunnel.
	 * @param drawnCards (SortedBag<Card>): the 3 cards drawn by the player upon the attempt to claim the tunnel.
	 * @return (List<SortedBag<Card>>): a list of all sets of additional cards the player could use to take over a tunnel, sorted
	 * in ascending order by the number of locomotive cards.
	 * @throws IllegalArgumentException if additionalCardsCount is not between 1 and 3 (both inclusive), if initialCards contains
	 * more than 2 different types of cards, or if drawnCards doesn't contain exactly 3 cards.
	 */
	public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards){
	    boolean rightCsCounts = additionalCardsCount >= 1 && additionalCardsCount <= 3 && drawnCards.size() == 3;
	    boolean rightInitial = !initialCards.isEmpty() && initialCards.toSet().size() <= 2;
		Preconditions.checkArgument(rightCsCounts  && rightInitial);
		
		SortedBag<Card> relevantCsLeft = SortedBag.of(computeRelevantCardsLeft(initialCards));
		
		Set<SortedBag<Card>> relevantCardSet = additionalCardsCount <= relevantCsLeft.size() 
		        ? relevantCsLeft.subsetsOfSize(additionalCardsCount) 
		        : new HashSet<SortedBag<Card>>();
		
        List<SortedBag<Card>> relevantCardList = new ArrayList<SortedBag<Card>>(relevantCardSet);
		relevantCardList.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
		return relevantCardList;
	}
	
	/**
	 * Private method used to facilitate the calculation of the cards relevent to the calculation of the possible additional cards.
	 * @param initialCards (SortedBag<Card>): the intial cards that the player used to claim the route
	 * @return (List<Card>): list of the cards that the player has left that he can use for the additional drawn cards.
	 */
	private List<Card> computeRelevantCardsLeft(SortedBag<Card> initialCards){
	    List<Card> csList = new ArrayList<Card>();
        SortedBag<Card> csBag = cards.difference(initialCards);
        
        for(Card card: csBag) {
            if(initialCards.contains(card) || card == Card.LOCOMOTIVE) {
                csList.add(card);
            }
        }
        return csList;
	}
	
	/**
	 * Returns an identical state to the receiver, except that the player has additionally claimed the given route with the given
	 * cards.
	 * @param route (Route): the route in question.
	 * @param claimCards (SortedBag<Card>): the cards used to claim the given route.
	 * @return (PlayerState): an identical state to the receiver, just with the additionally claimed route by recipient player.
	 */
	public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
	    
		List<Route> routes = new ArrayList<Route>();
		for(int i = 0; i < routes().size(); i++) {
		    routes.add(routes().get(i));
		}
		routes.add(route);
		
		SortedBag<Card> cards = cards().difference(claimCards);
		return new PlayerState(this.tickets(), SortedBag.of(cards), routes);
	}
	
	/**
	 * Method that returns the number of points (possibly negative) obtained by the player because of their tickets.
	 * @return (int): the number of points (possibly negative) obtained by the player because of their tickets.
	 */
	public int ticketPoints() {
	    StationPartition.Builder builder = new StationPartition.Builder(computeMaxId() + 1);
	    int points = 0;
	    
	    for(Route r: routes()) {
	        builder.connect(r.station1(), r.station2());
	    }
	    StationPartition partition = builder.build();
		
	    
	    for(int i = 0; i < tickets.size(); i++) {
		    points += tickets.get(i).points(partition);
		}
	    return points;
	}
	
	/**
	 * private method that helps facilitate the computation of the largest station Id owned by the player 
	 * @return (int): the maximum station Id of all the stations owned by said player.
	 */
	private int computeMaxId() {
	    int maxId = 0;
        for(Route r: routes()) {
            if(r.station1().id() > maxId ) {
                maxId = r.station1().id();
            } 
            if(r.station2().id() > maxId) {
                maxId = r.station2().id();
            }
        }
        return maxId;
	}
	
	/**
	 * Returns all the points obtained by the player at the end of the game.
	 * @return (int): all the points obtained by the player at the end of the game.
	 */
	public int finalPoints() {
		return claimPoints() + ticketPoints();
	}
}
