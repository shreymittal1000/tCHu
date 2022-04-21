package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.tchu.gui.StringsFr;

/**
 * Immutable class representing a path in a player's network.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class Trail {
    
    /**
     * (Station): the starting station of the path.
     */
    private final Station station1;
    
    /**
     * (Station): the destination station of the path.
     */
    private final Station station2;
    
    /**
     * (List<Route>: List of routes that the path contains.
     */
    private final List<Route> routes;
    
    private final int length;
    
    /**
     * Private Trail constructor. Initializes a Trail by giving it a length and a unique Starting (station1) and Ending (station2) Station.
     * @param routes (List<Route>): the List of routes of the trail.
     * @param station1 (Station): the starting station of the trail.
     * @param station2 (Station): the destination station of the trail.
     */
    private Trail(List<Route> routes, Station station1, Station station2) {
        this.routes = routes;
        this.station1 = station1;
        this.station2 = station2;
        
        int length = 0;
        for(Route r : routes) {
            length += r.length();
        }
        this.length = length;
    }
    
    /**
     * Returns the longest path in the network made up of the routes in the parameter; if there are several paths of maximum length,
     * then either one can be returned. If the list of given routes is empty, returns a path of length 0, whose stations are both
     * equal to null.
     * @param routes (List<Route>): a List of all the routes using which paths can be constructed.
     * @return (Trail): the longest (or joint-longest) possible trail possible using the provided paths.
     */
    public static Trail longest(List<Route> routes) {
        if(routes.isEmpty()) {
            return new Trail(new ArrayList<Route>(), null, null);
        }
        
        List<Trail> trails = new ArrayList<>();
        List<Trail> trailsNew = new ArrayList<>();
        List<Trail> allTrails = new ArrayList<>();
        List<Route> extensibleRoutes = new ArrayList<>();
        
        trails = doubleAllRoutes(trails, routes);
        
        while(!trails.isEmpty()) {
            trailsNew = new ArrayList<>();
            
            for(Trail c: trails) {
                
                extensibleRoutes = computeExtensibleRoutes(routes, c);
                if(extensibleRoutes.isEmpty()) {
                    allTrails.addAll(trails);
                }
                
                for(Route r : extensibleRoutes){  
                    if(r.stations().contains(c.station2())) {
                        trailsNew.add(new Trail(computeExtendedRoutes(r ,c) , c.station1(), r.stationOpposite(c.station2())));
                    }
                }
            }
            allTrails.addAll(trailsNew);
            trails = List.copyOf(trailsNew);
        }
        return computeLongestTrail(allTrails);
    }

    /**
     * Doubles all the routes (with the duplicate having the starting and departing stations
     * inverted).
     * @param trails (List<Trail>): Represents the list of all length 1 trails.
     * @param routes (List<Route>): Represents all the routes in the network in question.
     * @return (List<Trail>): a list of all the routes duplicated (with the duplicate having
     * the starting and departing stations inverted).
     */
    private static List<Trail> doubleAllRoutes(List<Trail> trails, List<Route> routes){
        for(int i = 0; i < routes.size(); i++) {
            trails.add(new Trail(List.of(routes.get(i)), routes.get(i).station1(), routes.get(i).station2()));
            trails.add(new Trail(List.of(routes.get(i)), routes.get(i).station2(), routes.get(i).station1()));
        }
        return trails;
    }
    
    /**
     * Returns a list of all the routes in the network in question that can extend a
     * given trail.
     * @param routes (List<Route>): the list of all the routes in the network in question.
     * @param trail (Trail): the trail to be extended.
     * @return (List<Routes>): a list of all the routes in the network in question that
     * can extend a given trail.
     */
    private static List<Route> computeExtensibleRoutes(List<Route> routes, Trail trail){
        List<Route> rs = new ArrayList<>();
        for(Route r: routes) {
            if(trail.routes.contains(r)) {
                continue;
            }
            else if(r.stations().contains(trail.station2())) {
                rs.add(r);
            }
        }
        return rs;
    }
    
    /**
     * Returns a list of all the routes in a trail, with another route (passed as parameter)
     * added to that list (essentially extending the trail by that route).
     * @param route (Route): the route to be added to the list.
     * @param trail (Trail): the trail to be extended.
     * @return (List<Routes>): a list of all the routes in a trail, with another route (passed
     * as parameter) added to that list (essentially extending the trail by that route).
     */
    private static List<Route> computeExtendedRoutes(Route route, Trail trail){
        List<Route> newRoutes = new ArrayList<>();
        newRoutes.addAll(trail.routes);
        newRoutes.add(route);
        return newRoutes;
    }
    
    /**
     * Returns the longest trail in the list of given trails.
     * @param allTrails (List<Trail>): the list of trails from which the longest trail is to
     * be selected.
     * @return (Trail): the longest trail in the list of given trails.
     */
    private static Trail computeLongestTrail(List<Trail> allTrails) {
        int max = 0; 
        Trail longestTrail = null;
        for(Trail t: allTrails) {
            if(t.length() > max) {
                max = t.length();
                longestTrail = t;
            }
        }
        return longestTrail;
    }
    
    /**
     * Returns the length of the path i.e. the number of cars + locomotives comprising this instance of Path.
     * @return (int): the length of the path i.e. the number of cars + locomotives comprising this instance of Path.
     */
    public int length() {
    	return length;
    }
    
    /**
     * Returns the starting station of the path.
     * @return (Station): the starting station of the path.
     */
    public Station station1() {
        return length() == 0 ? null : station1;
    }
    
    /**
     * Returns the destination station of the path.
     * @return (Station): the destination station of the path.
     */
    public Station station2() {
        return length() == 0 ? null : station2;
    }
    
    @Override
    public String toString() {
        Station st = this.station1();
        StringBuilder builder = new StringBuilder();
        
        if(st == null || routes.isEmpty()) {
            return builder.toString();
        }
        
        builder.append(st.name());
        for (Route rt : this.routes) {
            
            builder.append( StringsFr.EN_DASH_SEPARATOR + rt.stationOpposite(st).name() ) ;
            st = rt.stationOpposite(st);
        }
        return  builder.toString();
    }
}