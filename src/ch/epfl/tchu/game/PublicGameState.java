package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.Preconditions;

/**
 * Immutable class representing the game state that is publicly visible to every player and does not require special access.
 * This includes the number of tickets in the deck of tickets, the PublicCardState, the PlayerId of the player who's turn
 * it is, the public player states of the 2 players and the PlayerId of the player who took the last turn.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public class PublicGameState {
	
	/**
	 * (int): represents the number of tickets in the deck of tickets.
	 */
	private final int ticketsCount;
	
	/**
	 * (PublicCardState): represents the public card state of the game.
	 */
	private final PublicCardState cardState;
	
	/**
	 * (PlayerId): represents the PlayerId of the player who's turn it is.
	 */
	private final PlayerId currentPlayerId;
	
	/**
	 * (Map<PlayerId, PublicPlayerState>): represents the public player states of the 2 players
	 * in the game.
	 */
	private final Map<PlayerId, PublicPlayerState> publicPlayerStates;
	
	/**
	 * (PlayerId): represents the PlayerId of the player who will take the last turn.
	 */
	private final PlayerId lastPlayer;
	
	/**
	 * Default public constructor of PublicGameState. Initializes a PublicGameState by giving it
	 * the number of tickets in the deck of tickets, the PublicCardState, the PlayerId of the player
	 * who's turn it is, the public player states of the 2 players and the PlayerId of the player
	 * who took the last turn.
	 * @param ticketsCount (int): represents the number of tickets in the deck of tickets.
	 * @param cardState (PublicCardState): represents the public card state of the game.
	 * @param currentPlayerId (PlayerId): represents the PlayerId of the player who's turn it is.
	 * @param playerState (Map<PlayerId, PublicPlayerState>): represents the public player states of
	 * the 2 players in the game.
	 * @param lastPlayer (PlayerId): represents the PlayerId of the player who will take the last
	 * turn.
	 * @throws IllegalArgumentException if the size of the deck is strictly negative or if
	 * playerState does not contain exactly two key/value pairs.
	 * @throws NullPointerException if one of the other arguments (except lastPlayer!) is null.
	 */
	public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
			Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
		Preconditions.checkArgument(ticketsCount >= 0 && (playerState.size() == 2 || playerState.size() == 3));
		if(cardState.equals(null) || currentPlayerId.equals(null) || playerState.equals(null)) {
			throw new NullPointerException();
		}
		this.ticketsCount = ticketsCount;
		this.cardState = cardState;
		this.currentPlayerId = currentPlayerId;
		this.publicPlayerStates = playerState;
		this.lastPlayer = lastPlayer;
	}
	
	/**
	 * Returns the number of tickets in the deck of tickets.
	 * @return (int): the number of tickets in the deck of tickets.
	 */
	public int ticketsCount() {
		return ticketsCount;
	}
	
	/**
	 * Determines whether it is possible to draw tickets.
	 * @return (boolean): true if it is possible to draw tickets, false otherwise.
	 */
	public boolean canDrawTickets() {
		return ticketsCount() != 0;
	}
	
	/**
	 * Returns the public card state of the game.
	 * @return (PublicCardState): the public card state of the game.
	 */
	public PublicCardState cardState() {
		return cardState;
	}
	
	/**
	 * Determines whether it is possible to draw cards.
	 * @return (boolean): true if it is possible to draw cards, false otherwise.
	 */
	public boolean canDrawCards() {
		return cardState().deckSize() + cardState().discardsSize() >= 5;
	}
	
	/**
	 * Returns the PlayerId of the player who's turn it is.
	 * @return (PlayerId): the PlayerId of the player who's turn it is.
	 */
	public PlayerId currentPlayerId() {
		return currentPlayerId;
	}
	
	/**
	 * Returns the public player state of the given player.
	 * @param playerId (PlayerId): the PlayerId of the player.
	 * @return (PublicPlayerState): the public player state of the given player.
	 */
	public PublicPlayerState playerState(PlayerId playerId) {
		return publicPlayerStates.get(playerId);
	}
	
	/**
	 * Returns the public player state of the current player.
	 * @return (PublicPlayerState): the public player state of the current player.
	 */
	public PublicPlayerState currentPlayerState() {
		return publicPlayerStates.get(currentPlayerId());
	}
	
	/**
	 * Returns all the roads that have been taken by either player.
	 * @return (List<Route>): a list of all the roads that have been taken by either player.
	 */
	public List<Route> claimedRoutes(){
		List<Route> currentPlayerRoutes = new ArrayList<Route>();
		for(int i = 0; i < publicPlayerStates.size(); i++) {
			currentPlayerRoutes.addAll(publicPlayerStates.get(PlayerId.ALL.get(i)).routes());
		}
		return currentPlayerRoutes;
	}
	
	/**
	 * Returns the PlayerId of the player who will take the last turn.
	 * @return (PlayerId): represents the PlayerId of the player who will take the last turn.
	 */
	public PlayerId lastPlayer() {
		return lastPlayer;
	}
}
