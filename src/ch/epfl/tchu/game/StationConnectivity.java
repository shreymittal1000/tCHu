package ch.epfl.tchu.game;

/**
 * Interface representing a connectivity of a player's network i.e. whether or not two stations are connected by the network of the player in question.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public interface StationConnectivity {
	
	/**
	 * Checks whether 
	 * @param s1 (Station): one of the station in question.
	 * @param s2 (Station): the other station in question.
	 * @return (boolean): true if the stations are connected by the network of the player in question, false otherwise.
	 */
    boolean connected(Station s1, Station s2);
}
