package ch.epfl.tchu.game;
import java.util.List;
import java.util.TreeSet;

import ch.epfl.tchu.Preconditions;

/**
 * Immutable class representing a ticket in the game.
 * Implements the interface comparable to be able to be compared.
 * Can be drawn by a player, giving the player an "objective".
 * Completing the route described in the ticket will give the player a certain number of points.
 * If the route is not completed, a certain number of points will be deducted from the player's score instead.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class Ticket implements Comparable<Ticket> {
	
	/**
	 * (List<Trip>): the trips listed on the ticket, completing which gives the player points, and not completing them removes points instead.
	 */
    private final List<Trip> trips;
    
    /**
     * (String): representing the text found on the ticket.
     */
    private final String text;
    
    /**
     * Constructor that initializes the list of trips on the ticket.
     * Sets the text based on the list of trips on the ticket.
     * @param trips (List<Trip>): the trips associated with the ticket.
     * @throws IllegalArgumentException if trips is empty or if the names of the "from" station on adjacent trips is not the same.
     */
    public Ticket(List<Trip> trips){
        Preconditions.checkArgument(!trips.isEmpty());
        String station1 = trips.get(0).from().name();
        for(int i = 0; i < trips.size(); i++) {
            Preconditions.checkArgument(station1.equals(trips.get(i).from().name()));
        }
        
        this.trips = List.copyOf(trips);
        text = Ticket.computeText(trips);
    }
    
    /**
     * Constructor that initializes a ticket by taking the two stations to be connected and the number of points earned by connecting them.
     * @param from (Station): the station that needs to be connected with the other given station.
     * @param to (Station): the other station which needs to be connected with the previously given station.
     * @param points (int): the number of points gained by a player if they connect the given stations.
     */
    public Ticket(Station from, Station to, int points){
        this(List.of(new Trip(from, to, points)));
    }
    
    /**
     * Returns the text for this instance of Ticket.
     * @return (String): the text of this instance of Ticket.
     */
    public String text() {
        return text;
    }
    
    /**
     * Computes the text of the ticket based on the trips provided
     * @param trips (List<Trip>): the list of trips for which the ticket text is to be computed.
     * @return (String): the text of this instance of Ticket based on its corresponding list of trips.
     */
    private static String computeText(List<Trip> trips) {
        TreeSet<String> Strings = new TreeSet<String>();
        
        for(Trip T : trips) {
            Strings.add(T.to().name() + " (" + T.points() + ")");
        }
        
        return Strings.size() == 1 
                ? trips.get(0).from().name() + " - " + String.join(", ", Strings) + "" 
                
                : trips.get(0).from().name() + " - {" + String.join(", ", Strings) + "}";
    }
    
    /**
     * Computes the amount of points to be added/subtracted to the player in question's score.
     * @param connectivity (StationConnectivity): determines whether 2 stations are connected or not by the player in question.
     * @return (int): the number of points to be added/subtracted to the player in question's score.
     */
    public int points(StationConnectivity connectivity) {
        int max = trips.get(0).points();
        int min = max;
        boolean connects = false;
        
        for(Trip T : trips) {
            max = Math.max(max, T.points(connectivity));
            min = Math.min(min, T.points());
            
            connects = (!connects) ? connectivity.connected(T.to(), T.from()) : connects;
        }
        
        return connects ? max : -(min);
    }
    
    @Override
    public int compareTo(Ticket that) {
        return this.text.compareTo(that.text);
    }
    
    @Override 
    public String toString() {
        return text();
    }
}
