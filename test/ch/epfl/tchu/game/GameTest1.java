package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.gui.Info;

@SuppressWarnings("unused")
public class GameTest1 {
    private static final long seed = 5425l;
    private final Random rng = new Random(seed);
    @Test
    void PlayWorks(){
<<<<<<< HEAD
    	//Hello World
=======
>>>>>>> 49f434b (Nique la marek)
        Player player1 = new TestPlayer(seed, ChMap.routes());
        Player player2 = new TestPlayer(seed, ChMap.routes());
        Map<PlayerId, Player> players = Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2, player2);
        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, "player1", PlayerId.PLAYER_2, "player2");
        Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), rng, 0);
    }
    
    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;
        private List<Route> claimableRoutes;
        
        // Lorsque setInitialTicketChoice est appele
        private SortedBag<Ticket> initialTicketChoice;
        
        
        private String playerName;
        private PlayerId ownId;
        

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> claimableRoutes = new ArrayList<Route>();
            for(int i = 0; i < allRoutes.size(); i++) {
                if(!(gameState.claimedRoutes().contains(allRoutes.get(i))) &&  ownState.canClaimRoute(allRoutes.get(i))) {
                    claimableRoutes.add(allRoutes.get(i));
                }
            }
            this.claimableRoutes = claimableRoutes;
            if (claimableRoutes.isEmpty()) {
                return TurnKind.DRAW_CARDS;
            } else {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE;
            }
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownId = ownId;
            this.playerName = playerNames.get(ownId);
        }

        @Override
        public void receiveInfo(String info) {
            //System.out.println(info + " Comminucated to: " + playerName + " Id: " + ownId);
            
            if(ownState != null) {
            	//System.out.println(ownId + " Cars: " + ownState.carCount());
            	//System.out.println(gameState.cardState().deckSize() + gameState.cardState().discardsSize() + 
            	//		gameState.playerState(ownId).cardCount() +  gameState.playerState(ownId.next()).cardCount() + 
            	//		gameState.cardState().faceUpCards().size());
            }
            else {
            	//System.out.println("\n");
            }
            
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            initialTicketChoice = tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            SortedBag<Ticket> chosenInitialTickets = SortedBag.of(initialTicketChoice);
            for(int i = 0; i < rng.nextInt(2); i++) {
                chosenInitialTickets = chosenInitialTickets.difference(SortedBag.of(initialTicketChoice.get(rng.nextInt(4))));
            }
            return chosenInitialTickets;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            SortedBag<Ticket> chosenInitialTickets = SortedBag.of(initialTicketChoice);
            for(int i = 0; i < rng.nextInt(2); i++) {
                chosenInitialTickets = chosenInitialTickets.difference(SortedBag.of(initialTicketChoice.get(rng.nextInt(2))));
            }
            return chosenInitialTickets;
        }

        @Override
        public int drawSlot() {
            int chosenSlot = rng.nextInt(5)-1;
            return chosenSlot;
        }

        @Override
        public Route claimedRoute() {
            return claimableRoutes.get(rng.nextInt(claimableRoutes.size()));
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            List<SortedBag<Card>> claimCardPossibilities = ownState.possibleClaimCards(routeToClaim);
            if(claimCardPossibilities.isEmpty()) {
                return SortedBag.of();
            } 
            return claimCardPossibilities.get(rng.nextInt(claimCardPossibilities.size()));
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards( List<SortedBag<Card>> options) {
            if(options.isEmpty()) {
                return SortedBag.of();
            }
            return options.get(rng.nextInt(options.size()));
        }

        @Override
        public void setPlayerNumber(int playerNum) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public String setPlayerName() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}