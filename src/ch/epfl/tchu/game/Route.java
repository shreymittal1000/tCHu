package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Immutable class representing a route in the game.
 * Can be used to connect two stations to each other.
 * Is either of a certain color type or is "grey" meaning any color card can be used to complete it.
 * Can be overground or underground.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class Route {
	
	/**
	 * (String): the unique ID of the station.
	 */
    private final String id;
    
    /**
     * (Station): the starting station the route connects.
     */
    private final Station station1;
    
    /**
     * (Station): the destination station the route connects.
     */
    private final Station station2;
    
    /**
     * (int): the length of the route (in terms of cards required to complete it).
     */
    private final int length;
    
    /**
     * (Level): the "level" of the route i.e. whether the route goes underground or stays overground.
     */
    private final Level level;
    
    /**
     * (Color): the color of the route. Determines what cards can be used to seize the route.
     */
    private final Color color;
    
    /**
     * Default Route constructor. Initializes a Route initializing its field using the parameters of the constructors.
     * @param id (String): the unique ID of the route.
     * @param station1 (Station): the starting station the route connects.
     * @param station2 (Station): the destination station the route connects.
     * @param length (int): the length of the route (in terms of cards required to complete it).
     * @param level (Level): the "level" of the route i.e. whether the route goes underground or stays overground.
     * @param color (Color): the color of the route. Determines what cards can be used to seize the route.
     * @throws IllegalArgumentException if station1 is the same as station2 or the route length is not in the given boundaries.
     * @throws NullPointerException if id, station1, station2 or level are null.
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!(station1.equals(station2)) && length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH);
        Objects.requireNonNull(id); Objects.requireNonNull(station1); Objects.requireNonNull(station2); Objects.requireNonNull(level);
        this.color = color;
        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.level = level;
        this.length = length;
    }   
    
    /**
     * Returns the unique ID of the route.
     * @return (String): the unique ID of the route.
     */
    public String id() { 
        return id; 
    }
    
    /**
     * Returns the starting station of the route.
     * @return (Station): the starting station of the route.
     */
    public Station station1() { 
        return station1; 
    }
    
    /**
     * Returns the destination station of the route.
     * @return (Station): the destination station of the route.
     */
    public Station station2() { 
        return station2; 
    }
    
    /**
     * Returns the length of the route (in terms of cards required to complete it).
     * @return the length of the route (in terms of cards required to complete it).
     */
    public int length() { 
        return length; 
    }
    
    /**
     * Returns the color of the route.
     * @return (Color): the color of the route.
     */
    public Color color() { 
        return color; 
    }
    
    /**
     * Returns the "level" of the route i.e. whether the route goes underground or stays overground.
     * @return the "level" of the route. Can be the constant OVERGROUND or the constant UNDERGROUND.
     */
    public Level level() { 
        return level; 
    }
    
    /**
     * Level represents whether a route is above the ground (OVERGROUND) or goes underground (UNDERGROUND).
     */
    public enum Level {
        OVERGROUND, UNDERGROUND;
    }
    
    /**
     * Returns a List containing the two stations the route connects.
     * @return (List<Station>): a List containing the two instances of Station that are fields of Route.
     */
    public List<Station> stations(){
        return List.of(station1, station2);
    }
    
    /**
     * Returns the instance of Station opposite to the station mentioned as parameter in the route. If the station passed as a parameter isn't connected by the route,
     * then an IllegalArgumentException is thrown.
     * @param station (Station): the connecting station.
     * @return (Station): the opposite station to the one passed as parameter.
     * @throws IllegalArgumentException if station is not connected by this route.
     */
    public Station stationOpposite(Station station) {
    	Preconditions.checkArgument(station == station1 || station == station2);
    	return station1 == station ? station2 : station1;
    }
    
    /**
     * Returns a List of a SortedBag (itself a SortedBag of Cards) containing all the possible combinations of cards which can be used to claim the route.
     * The returned list is also sorted by increasing order of locomotives simply be method of adding the combinations in an increasing order. 
     * @return (List<SortedBag<Card>>): a List containing all the possible combinations of cards which can be used to claim the route.
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        ArrayList<SortedBag<Card>> claimCardsList = new ArrayList<SortedBag<Card>>();
        int possibleLocomotives = level == Level.UNDERGROUND ? length : 0;
        
        for(int i = 0; i <= possibleLocomotives ; i++) {
            
            List<Color> possibleColors = color != null ? List.of(color) : Color.ALL;
            for(Color C : possibleColors) {
                
                SortedBag<Card> possibleCombination = SortedBag.of(length-i, Card.of(C), i, Card.LOCOMOTIVE);        
                if(!claimCardsList.contains(possibleCombination)) {
                    claimCardsList.add(possibleCombination);
                }
            }
        }
        return claimCardsList;
    }
    
    /**
     * Returns the number of additional cards to be played to claim the route (in a tunnel),
     * knowing that the player has already laid claimCards and that the three cards drawn from
     * the top of the deck are drawnCards; raises the IllegalArgumentException if the route to which
     * it is applied is not a tunnel, or if drawnCards does not contain exactly 3 cards.
     * @param claimCards (SortedBag<Card>): The cards which were already played trying to claim the route (tunnel).
     * @param drawnCards (SortedBad<Card>): The 3 cards drawn in the attempt to claim the route.
     * @return (int): the number of cards needed to claim the route.
     * @throws IllegalArgumentException if level is not UNDERGROUND or if the size of drawnCards is not 3.
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(level == Level.UNDERGROUND && drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
        SortedBag<Card> routeColor = claimCards.difference(SortedBag.of(claimCards.size(), Card.LOCOMOTIVE));
        return drawnCards.countOf(Card.LOCOMOTIVE) + (routeColor.isEmpty() ? 0 : drawnCards.countOf(routeColor.get(0)));
    }
    
    /**
     * Returns the number of points a player gets when they claim the route.
     * @return (int): the number of points a player gets when they claim the route.
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }
}

