package ch.epfl.tchu.game;

import java.util.List;
import java.util.Map;
import ch.epfl.tchu.SortedBag;

/**
 * Interface representing a player in the game tCHu.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public interface Player {
    
    /**
     * The enumerable type TurnKind represents the three types of actions that a player can make during a turn in tCHu.
     */
    public static enum TurnKind{
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE; 
        
        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }
    
    String setPlayerName();
    
    void setPlayerNumber(int playerNum);
    /**
     * Method which is called at the start of the game to communicate to the player his own ownId identity, 
     * as well as the names of the different players, his own included, which can be found in playerNames.
     * @param ownId (PlayerId): The player's Id
     * @param playerNames (Map<PlayerId, String>): the names of all of the players
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);
    
    /**
     * Method that is called whenever information needs to be communicated to the player 
     * during the game; this information is given in the form of a string
     * @param info (String): the information communicated to the player
     */
    void receiveInfo(String info);
    
    /**
     * Method that is called whenever the state of the game has changed, to inform the player of the 
     * public component of this new state, newState, as well as of its own state, ownState
     * 
     * @param newState (PublicGameState): the updated newState of the game
     * @param ownState (PlayerState): the player's updated ownState
     */
    void updateState(PublicGameState newState, PlayerState ownState);
    
    /**
     * Method that is called at the start of the game to communicate to 
     * the player the five tickets that have been distributed to him/her.
     * @param tickets (SortedBag<Ticket>): A SortedBag of the five tickets that have been distributed to the player.
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);
    
    /**
     * Method that is called at the start of the game to ask the player 
     * which of the tickets he was initially given he is keeping
     * @return (SortedBag<Ticket>): Bag of the tickets that the player chose.
     */
    SortedBag<Ticket> chooseInitialTickets();
    
    /**
     * Method that is called at the start of a player's turn, to find out what type of action he wishes to perform during that turn
     * @return (TurnKind): The type of action that will be performed next turn. 
     */
    TurnKind nextTurn();
    
    /**
     * Method that is called when the player has decided to draw additional tickets during the game, 
     * in order to communicate the tickets drawn and to know which ones he is keeping.
     * @param options (SortedBag<Ticket>): The additional tickets drawn by the player.
     * @return (SortedBag<Ticket>): Tickets that were chosen by the player.
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);
    
    /**
     * Method that is called when the player has decided to draw wagon / locomotive cards, in order to
     * know from where he wishes to draw them: from one of the spaces containing a face-up card or blindlt from the deck.
     * @return (int): The slot that the player will draw from.
     */
    int drawSlot();
    
    /**
     * Method that is called when the player has decided to (attempt to) seize a road, in order to know which route it is
     * @return (Route): the route that is being claimed.
     */
    Route claimedRoute();
    
    /**
     * Method that is called when the player has decided to (attempt to) seize a road, 
     * in order to know which card (s) he initially wishes to use for this,
     * @return (SortedBag<Card>): the cards that must be used to claim the route.
     */
    SortedBag<Card> initialClaimCards();
    
    /**
     * Method that is called when the player has decided to try to seize a tunnel and 
     * additional cards are needed, in order to know which card (s) he wishes to use for this
     * @param options (List<SortedBag<Card>>): The different possibilities of cards that the player can use for the 3 drawn cards.
     * @return (SortedBag<Card>): Cards that must be used to take the tunnel upon drawing 3 additional cards.
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
    
}
