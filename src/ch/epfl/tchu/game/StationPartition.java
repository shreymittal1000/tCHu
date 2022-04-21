package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Immutable class representing a flat station partition in the game.
 * Implements the StationConnectivity interface, as it allows its instances to be passed as arguments in the points method of Ticket.
 * Keeps track of the connectivity of stations.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class StationPartition implements StationConnectivity{

    /**
     * (int): represents the connectivity of each station.
     * The index represents the station id.
     * The integer in the array represents the "reference station" of the disjoint set to which the index station belongs.
     */
    private final int[] stationsConnections;

    /**
     * Default private StationPartition constructor. Initializes a StationPartition by giving it an array of station connectivity.
     * @param stations (int[]): an array representing station connectivity.
     */
    private StationPartition(int[] stations) {
        this.stationsConnections = stations;
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        return s1.id() < stationsConnections.length 
               && s2.id() < stationsConnections.length 
               && stationsConnections[s1.id()] == stationsConnections[s2.id()] 
               || s1.id() == s2.id();
    }

    /**
     * Builder class that builds a StationPartition.
     */
    public static final class Builder{

        /**
         * (int): represents the connectivity of each station.
         * The index represents the station id.
         * The integer in the array represents the "reference station" of the disjoint set to which the index station belongs.
         */
        private final int[] stationsConnections;

        /**
         * Default Builder constructor. Initializes a Builder by giving it an array of station connectivity.
         * @param stations (int[]): an array representing station connectivity.
         * @throws IllegalArgumentException if stationCount is strictly negative.
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            stationsConnections = new int[stationCount];
            for(int i = 0; i < stationCount; i++) {
                stationsConnections[i] = i;
            }
        }

        /**
         * Joins the subsets containing the two stations passed as arguments, "electing" one of the two representatives as
         * representative of the joined subset; returns the builder (this).
         * @param s1 (Station): the first Station in question.
         * @param s2 (Station): the second Station in question.
         * @return (Builder): this.
         */
        public Builder connect(Station s1, Station s2) {
            stationsConnections[representative(s2.id())] = representative(s1.id());
            return this;
        }

        /**
         * Returns the representative station of the set that the station in question (whose id is the parameter) belongs to.
         * @param indexStation (int): the id of the station in question.
         * @return (int): the representative station of the set.
         */
        private int representative(int indexStation) {
            int indexOfRep = indexStation;
            while(indexOfRep != stationsConnections[indexOfRep]) {
                indexOfRep = stationsConnections[indexOfRep];
            }
            return indexOfRep;
        }

        /**
         * Returns the flattened partition of stations corresponding to the deep partition being built by this builder.
         * @return (StationPartition): the flattened partition of stations corresponding to the deep partition being built by
         * this builder.
         */
        public StationPartition build() {
            for(int i = 0; i < stationsConnections.length; i++) {
                stationsConnections[i] = representative(i);
            }
            return new StationPartition(stationsConnections);
        }
    }
}
