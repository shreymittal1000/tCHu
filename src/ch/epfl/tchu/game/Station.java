package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Immutable class representing a station in the game.
 * Can connect with other stations by forming a route between them.
 * Can be listed on tickets which ask for routes to be completed between different stations.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class Station {
	
	/**
	 * (int): the unique numerical ID of the Station.
	 */
    private final int id;
    
    /**
     * (String): The name of the Station.
     */
    private final String name;
    
    /**
     * Default Station constructor. Initializes a Station by giving it a unique ID and a name.
     * @param id (int): the unique numerical ID of the Station.
     * @param name (String): The name of the Station.
     * @throws IllegalArgumentException if id < 0.
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }
    
    /**
     * Returns the unique ID of the Station.
     * @return (int): the unique ID of the Station.
     */
    public int id() {
        return id;
    }
    
    /**
     * Return the name of the Station.
     * @return (String): the name of the Station.
     */
    public String name() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
