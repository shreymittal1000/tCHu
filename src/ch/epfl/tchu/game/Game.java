package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;

/**
 * Final and non-instanciable class that represents a part of the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class Game {
	
    private static GameState gameState;
	/**
	 * Private constructor existing for the sole purpose of not allowing this class to not
	 * be instanciating (at least outside of this class).
	 */
	private Game() {}
	
	/**
	 * Makes a game of tCHu be played with the characteristics passed as parameters.
	 * @param players (Map<PlayerId, Player>): A map linking the player IDs with the players they represent.
	 * @param playerNames (Map<PlayerId, String>): A map linking the player IDs with the player names they represent.
	 * @param tickets (SortedBag<Ticket>): The tickets available for this game.
	 * @param rng (Random): A random variable used to randomize certain aspects of the game.
	 */
	public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng, int playerNum){
	    Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);
	    //playerInfoMap to more efficiently handle Info objects.
        Map<PlayerId, Info> playerInfoMap = new HashMap<>();
        players.forEach((playerId, player) -> playerInfoMap.put(playerId, new Info(playerNames.get(playerId))));
        players.forEach((playerId, player) -> player.setPlayerNumber(playerNum)); 
	    
        GameState gameState = initializeGame(players,  playerNames, playerInfoMap, tickets, rng);
        
        // loop that plays the game.
        while(true) {
            gameState = playTurn(playerInfoMap, gameState, players, rng);
            updateState(players, gameState);
            if(gameState.lastTurnBegins()) {
                gameState = gameState.forNextTurn();
                break;
            }
            gameState = gameState.forNextTurn();
        }
        
        // Plays out final turns.
        for(int i = 0; i < players.size(); i++) {
            gameState = playTurn(playerInfoMap, gameState, players, rng);
            updateState(players, gameState);
            if(gameState.currentPlayerId() != gameState.lastPlayer()) {
                gameState = gameState.forNextTurn();        
            }
        }
        updateState(players, gameState);
        calculatePoints(gameState, players,  playerNames, playerInfoMap);
	}
	 
    /**
     * Initializes a game performing all the tasks needed to be performed at the start of a game.
     * @param players (Map<PlayerId, Player>): A map linking the player IDs with the players they represent.
     * @param playerNames (Map<PlayerId, String>): A map linking the player IDs with the player names they represent.
     * @param playerInfoMap (Map<PlayerId, Info>): A map linking the Info objects of player to their Player ID.
     * @param tickets (SortedBag<Ticket>): The tickets available for this game.
     * @param rng (Random): A random variable used to randomize certain aspects of the game.
     * @return (GameState): a GameState initialized with all the steps needed to be performed at the start of a game.
     */
	private static GameState initializeGame(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Map<PlayerId,Info> playerInfoMap, SortedBag<Ticket> tickets, Random rng) {
	    gameState = GameState.initial(tickets, rng);
	    players.forEach((playerId, player) -> player.initPlayers(playerId, playerNames));
	    receiveInfoForAll(playerInfoMap.get(gameState.currentPlayerId()).willPlayFirst(), players);
	    
	    for(int i = 0; i < players.size(); i++) {
	        players.get(PlayerId.ALL.get(i)).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
	        gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
	        players.get(PlayerId.ALL.get(i)).updateState(gameState, gameState.playerState(PlayerId.ALL.get(i)));
	    }
        updateState(players, gameState);
        
        players.forEach(((playerId, player) -> gameState = gameState.withInitiallyChosenTickets(playerId, player.chooseInitialTickets())));
        playerInfoMap.forEach((playerId, info) -> info.keptTickets(gameState.playerState(playerId).ticketCount()));
        return gameState;
	}
	
	
    /**
     * Private static method that is called at the end of the game to calculate the points of both players and declare the victor. 
     * @param gameState (GameState): the given game state at the end of the game.
     * @param players (Map<PlayerId, Player>): A map linking each player id to the player it refers to.
     * @param playerNames (Map<PlayerId, String>): A map linking the player IDs with the player names they represent.
     * @param playerInfoMap (Map<PlayerId, Info>): A map linking the Info objects of player to their Player ID.
     */
	private static void calculatePoints(GameState gameState, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Map<PlayerId, Info> playerInfoMap) {
	    Map<PlayerId, Trail> longestMap = new HashMap<PlayerId, Trail>();
	    
	    players.forEach((playerId, player) -> longestMap.put(playerId, Trail.longest(gameState.playerState(playerId).routes())));
        
	    // Auxiliary variables to increase efficiency
	    int[] bonus = new int[players.size()];
	    int[] length = new int[players.size()];
	    int[] finalPoints = new int[players.size()];
	    Info[] playerInfo = new Info[players.size()];
	    Trail[] trail = new Trail[players.size()];
	    String longestInfo = "";
	    int maxLength = 0;
	    for(int i = 0; i < players.size(); i++) {
	    	bonus[i] = 0;
	    	length[i] = longestMap.get(PlayerId.ALL.get(i)).length();
	    	playerInfo[i] = playerInfoMap.get(PlayerId.ALL.get(i));
	    	trail[i] = longestMap.get(PlayerId.ALL.get(i));
	    	if(maxLength < length[i]) {
	    		maxLength = length[i];
	    	}
	    }
	    
	    for(int i = 0; i < players.size(); i++) {
	    	if(length[i] == maxLength) {
	    		bonus[i] = 10;
	    		longestInfo += playerInfo[i].getsLongestTrailBonus(trail[i]);
	    	}
	    }
	    receiveInfoForAll(longestInfo, players);
	    
	    int winningPoints = Integer.MIN_VALUE;
	    int losingPoints = Integer.MAX_VALUE;
	    for(int i = 0; i < players.size(); i++) {
	    	finalPoints[i] = gameState.playerState(PlayerId.ALL.get(i)).finalPoints() + bonus[i];
	    	if(finalPoints[i] > winningPoints) {
	    		winningPoints = finalPoints[i];
	    	}
	    	if(finalPoints[i] < losingPoints) {
	    		losingPoints = finalPoints[i];
	    	}
	    }
	    
	    if(winningPoints == losingPoints) {
	    	List<String> playerNameList = new ArrayList<>();
	    	for(int i = 0; i < players.size(); i++) {
	    		playerNameList.add(playerNames.get((PlayerId.ALL.get(i))));
	    	}
	    	receiveInfoForAll(Info.draw(playerNameList, winningPoints), players);
	    }
	    else {
	    	String finalText = "";
	    	for(int i = 0; i < players.size(); i++) {
	    		if(winningPoints == finalPoints[i]) {
	    			finalText += playerInfo[i].won(winningPoints, losingPoints);
	    		}
	    	}
	    	receiveInfoForAll(finalText, players);
	    }
	}
	
	/**
     * Method that Plays out a turn of a certain player with the given game state, which then returns a modified gameState at the end of the turn.
     * @param playerInfoMap (Map<PlayerId, Info>): Allows the access of the Info object of both players.
     * @param gameState (GameState): the given game state used to play out a turn.
     * @param players (Map<PlayerId, Player>): A map linking each player id to the player it refers to.
     * @param rng (Random): A random variable used to randomize certain aspects of the game.
     * @return (GameState): an updated game state after the turn of the given player has been played.
     */
	private static GameState playTurn(Map<PlayerId, Info> playerInfoMap, GameState gameState, Map<PlayerId, Player> players, Random rng) {
	    //Start of turn and Choosing Type of Turn
	    PlayerId currentId = gameState.currentPlayerId();
	    Info currentInfo =  playerInfoMap.get(currentId);
	    Player currentPlayer = players.get(currentId);
	    
        receiveInfoForAll(currentInfo.canPlay(), players);
        updateState(players, gameState);
        TurnKind chosenTurnKind = currentPlayer.nextTurn();
        
        // Turn type: Choose Tickets
        switch(chosenTurnKind) {
        case DRAW_TICKETS:  gameState = drawTicketTurn(gameState, players, playerInfoMap);
                            break;
        case DRAW_CARDS:    gameState = drawCardsTurn(gameState, players, playerInfoMap, rng);
                            break;
        case CLAIM_ROUTE:   gameState = claimRouteTurn(gameState, players, playerInfoMap, rng);
                            break;
        }
        
        //informs the player that the last turn has begun
        if(gameState.lastTurnBegins()) {
            receiveInfoForAll(currentInfo.lastTurnBegins(gameState.currentPlayerState().carCount()), players);  
        }
        
        return gameState;
	}
	
	/**
	 * Method that plays out a turn of the player where he chooses to draw tickets. 
	 * @param gameState (GameState) : the current gameState fed into the method to update it.
	 * @param players (Map<PlayerId, Player>) : Map of the players mapped to their respective playerIds
	 * @param playerInfoMap (Map<playerId, Info>) : Map of the player info objects mapped to their respective playerIds
	 * @return (GameState) : the new updated gameState
	 */
	private static GameState drawTicketTurn(GameState gameState , Map<PlayerId, Player> players , Map<PlayerId, Info> playerInfoMap) {
	    PlayerId currentId = gameState.currentPlayerId();
	    Info currentInfo = playerInfoMap.get(currentId);
	    
	    receiveInfoForAll(currentInfo.drewTickets(3), players);
        SortedBag<Ticket> chosenTickets = players.get(currentId).chooseTickets(gameState.topTickets(3));
        gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(3), chosenTickets);
        receiveInfoForAll(currentInfo.keptTickets(chosenTickets.size()), players);
        return gameState;
	}
	
	/**
	 * Method that plays out a turn of the player where he chooses to draw tickets. 
     * @param gameState (GameState) : the current gameState fed into the method to update it.
     * @param players (Map<PlayerId, Player>) : Map of the players mapped to their respective playerIds
     * @param playerInfoMap (Map<playerId, Info>) : Map of the player info objects mapped to their respective playerIds
	 * @param rng (Random) : Random variable for randomizing the recreation of the Deck
	 * @return (GameState) : the new updated gameState
	 */
	private static GameState drawCardsTurn(GameState gameState , Map<PlayerId, Player> players , Map<PlayerId, Info> playerInfoMap, Random rng) {
	    for(int i = 0; i < 2; i++) {
            if(i == 1) {
                updateState(players, gameState);
            }
            int drawSlot = players.get(gameState.currentPlayerId()).drawSlot();
            if(Constants.FACE_UP_CARD_SLOTS.contains(drawSlot)) {
                receiveInfoForAll(playerInfoMap.get(gameState.currentPlayerId()).drewVisibleCard(gameState.cardState().faceUpCard(drawSlot)), players);
                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                gameState = gameState.withDrawnFaceUpCard(drawSlot);
            } else if(drawSlot == Constants.DECK_SLOT) {
                receiveInfoForAll(playerInfoMap.get(gameState.currentPlayerId()).drewBlindCard(), players);
                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                gameState = gameState.withBlindlyDrawnCard();
            }
        }
	    return gameState;
	}
	
	/**
	 * 
	 * Method that plays out a turn of the player where he chooses to draw tickets. 
     * @param gameState (GameState) : the current gameState fed into the method to update it.
     * @param players (Map<PlayerId, Player>) : Map of the players mapped to their respective playerIds
     * @param playerInfoMap (Map<playerId, Info>) : Map of the player info objects mapped to their respective playerIds
     * @param rng (Random) : Random variable for randomizing the recreation of the Deck
	 * @return (GameState) : the new updated gameState
	 */
	private static GameState claimRouteTurn(GameState gameState , Map<PlayerId, Player> players , Map<PlayerId, Info> playerInfoMap, Random rng) {
	    Route currentRoute = players.get(gameState.currentPlayerId()).claimedRoute();
        SortedBag<Card> initialClaimCards = players.get(gameState.currentPlayerId()).initialClaimCards();
        PlayerId currentId = gameState.currentPlayerId();
        PlayerState currentPlayerState = gameState.currentPlayerState();
        Info currentInfo = playerInfoMap.get(currentId);
        
        
        if(currentRoute.level() == Level.OVERGROUND) {
            if(!initialClaimCards.isEmpty()  && currentPlayerState.canClaimRoute(currentRoute) ) {
                receiveInfoForAll(currentInfo.claimedRoute(currentRoute, initialClaimCards), players);
                gameState = gameState.withClaimedRoute(currentRoute, initialClaimCards);
                
            } else {
                receiveInfoForAll(currentInfo.didNotClaimRoute(currentRoute), players);
            }
        }
        
        else if(currentRoute.level() == Level.UNDERGROUND) {
            if(!initialClaimCards.isEmpty()  && currentPlayerState.canClaimRoute(currentRoute) ) {
                receiveInfoForAll(currentInfo.attemptsTunnelClaim(currentRoute, initialClaimCards), players);
                
                // Additional Cards Being Drawn
                SortedBag<Card> drawnCards;
                SortedBag<Card> additionalCards = SortedBag.of();
                SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                for (int additionalCardCount = 0; additionalCardCount < Constants.ADDITIONAL_TUNNEL_CARDS; additionalCardCount++) {
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    drawnCardsBuilder.add(gameState.topCard());
                    gameState = gameState.withoutTopCard();  
                }
                drawnCards = drawnCardsBuilder.build();
                gameState = gameState.withMoreDiscardedCards(drawnCards);
                
                //Building of Possible Additional Cards + Informing player of drawn Additional Cards
                int additionalCardCount = currentRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);
                receiveInfoForAll(currentInfo.drewAdditionalCards(drawnCards, additionalCardCount), players);
                
                List<SortedBag<Card>> possibleAdditionalCards = additionalCardCount >= 1 ? 
                        currentPlayerState.possibleAdditionalCards(additionalCardCount, initialClaimCards, drawnCards) 
                        : List.of();
                
                // Management of the claiming of the route if the additionalCardCount implies an additional cost and can be fulfilled by the player.
                if( additionalCardCount >= 1 && !possibleAdditionalCards.isEmpty()){ 
                    additionalCards = players.get(currentId).chooseAdditionalCards(possibleAdditionalCards);
                    if(!additionalCards.isEmpty()) {
                        receiveInfoForAll(currentInfo.claimedRoute(currentRoute, additionalCards.union(initialClaimCards)), players);
                        gameState = gameState.withClaimedRoute(currentRoute, additionalCards.union(initialClaimCards));
                    } else {
                        receiveInfoForAll(currentInfo.didNotClaimRoute(currentRoute), players);
                    }
                // Management of the claiming of the route if there is no additional Cost.
                } else if(additionalCardCount == 0) {
                    receiveInfoForAll(currentInfo.claimedRoute(currentRoute, initialClaimCards), players);
                    gameState = gameState.withClaimedRoute(currentRoute, initialClaimCards);
                } else {
                    receiveInfoForAll(currentInfo.didNotClaimRoute(currentRoute), players);
                }
            } else {
                receiveInfoForAll(currentInfo.didNotClaimRoute(currentRoute), players);
            }
        }
        return gameState;
	}
	
	/**
     * A private method that essentially calls the receiveInfo method for both players in the game specified
     * by the players Map.
     * @param info (String): the info to be sent to both players.
     * @param players (Map<PlayerId, Player>) the map specifying the players to whom the message must be sent.
     */
    private static void receiveInfoForAll(String info, Map<PlayerId, Player> players) {
        players.forEach((playerId, player) -> player.receiveInfo(info));
    }
    
    /**
     * Updates the state of both players by their provided updateState methods. 
     * @param players (Map<PlayerId, Player>): A map that links each player to their respective playerId.
     * @param gameState (GameState): Current gameState of the game that must be provided in order to update the player and game.
     */
    private static void updateState(Map<PlayerId, Player> players, GameState gameState) {
        players.forEach((playerId, player) -> player.updateState(gameState, gameState.playerState(playerId)));
    }
}