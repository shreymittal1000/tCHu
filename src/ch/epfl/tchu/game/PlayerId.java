package ch.epfl.tchu.game;

import java.util.List;

/**
 * The enumerable type PlayerID represents the 2 different players' IDs playing the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public enum PlayerId {
	PLAYER_1, PLAYER_2, PLAYER_3;
	
	/**
	 * (List<PlayerID>): list of all the different types of PlayerID.
	 */
    public static List<PlayerId> ALL = Constants.THREE_PLAYER ? List.of(PlayerId.values()) : List.of(PLAYER_1, PLAYER_2);
    
    /**
     * (int): number of types of PlayerID.
     */
    public static int COUNT = ALL.size();
    
    /**
     * Returns the identity of the player who follows the one to whom this method is applied.
     * @return (PlayerId): the identity of the player who follows the one to whom this method is applied.
     */
    public PlayerId next() {
        return ALL.get((ALL.indexOf(this)+1)%COUNT);
    }
}
