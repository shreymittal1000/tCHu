package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * Immutable class representing a trip in the game.
 * All tickets have a number of trips associated with them, completing which gives the player in question a certain number of points.
 * Each trip has 2 stations and an int field, representing the stations to be connected and the number of points awarded for completing
 * the trip respectively.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class Trip {
	
	/**
	 * (Station): the starting station of the trip.
	 */
    private final Station from;
    
    /**
     * (Station): the destination station of the trip.
     */
    private final Station to;
    
    /**
     * (int): the number of points associated with the journey.
     * Completing the trip will add this number to the player in question's total score.
     */
    private final int points;
    
    /**
     * Default Trip constructor. Initializes the two stations and number of points associated with this instance of Trip.
     * @param from (Station): the station that needs to be connected with the other given station.
     * @param to (Station): the other station which needs to be connected with the previously given station.
     * @param points (int): the number of points gained by a player if they connect the given stations.
     * @throws IllegalArgumentException if points <= 0.
     */
    public Trip(Station from, Station to, int points) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        Preconditions.checkArgument(points > 0);
        this.points = points;
    }

    /**
     * Computes all the possible trips that can be taken to and from the given stations, along with the points for each trip.
     * @param from (List<Station>): the list of stations that needs to be connected with the other given stations.
     * @param to (List<Station>): the other list of stations which needs to be connected with the previously given list of stations.
     * @param points (int): the number of points gained by a player if they complete any of the trips computed.
     * @return (List<Trip>): a list of all the possible trips given the starting and destination stations.
     * @throws IllegalArgumentException if from or to are null or if points <= 0.
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        Preconditions.checkArgument(from != null && !from.isEmpty() && to != null && !to.isEmpty() && points > 0);
        ArrayList<Trip> allTrips = new ArrayList<Trip>();
        for(Station stationFrom : from) {
            for(Station stationTo : to) {
                allTrips.add(new Trip(stationFrom, stationTo, points));
            }
        }
        return allTrips;
    }
    
    /**
     * Returns the starting station.
     * @return (Station): the "from" field of this instance of Trip.
     */
    public Station from() {
        return from;
    }
    
    /**
     * Returns the destination station.
     * @return (Station): the "to" field of this instance of Trip.
     */
    public Station to() {
        return to;
    }
    
    /**
     * Returns the points associated with this instance of Trip.
     * @return (int): the points field of this instance of Trip.
     */
    public int points() {
        return points;
    }
    
    /**
     * Computes the amount of points to be added/subtracted to the player in question's score.
     * @param connectivity (StationConnectivity): determines whether 2 stations are connected or not by the player in question.
     * @return (int): the number of points to be added/subtracted to the player in question's score.
     */
    public int points(StationConnectivity connectivity) {
        return connectivity.connected(to, from) ? points : -(points);
    }
}
