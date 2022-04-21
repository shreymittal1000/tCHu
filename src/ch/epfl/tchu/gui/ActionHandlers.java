package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Interface permitting the grouping of interfaces that handle JavaFX Actions.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public interface ActionHandlers {   

    /**
     * Interface permitting handling of the drawing of tickets.
     * @author Shrey Mittal (312275)
     * @author David Chernis (310298)
     */
    @FunctionalInterface
    interface DrawTicketsHandler{
        void onDrawTickets();
    }

    /**
     * Interface permitting handling of the drawing of cards.
     * @author Shrey Mittal (312275)
     * @author David Chernis (310298)
     */
    @FunctionalInterface
    interface DrawCardHandler{
        void onDrawCard(int slot);
    }

    /**
     * Interface permitting handling of the claiming of routes.
     * @author Shrey Mittal (312275)
     * @author David Chernis (310298)
     */
    @FunctionalInterface
    interface ClaimRouteHandler{
        void onClaimRoute(Route r, SortedBag<Card> cardBag);
    }

    /**
     * Interface permitting handling of the choosing of tickets.
     * @author Shrey Mittal (312275)
     * @author David Chernis (310298)
     */
    @FunctionalInterface
    interface ChooseTicketsHandler{
        void onChooseTickets(SortedBag<Ticket> ticketBag);

    }

    /**
     * Interface permitting handling of the choosing of cards.
     * @author Shrey Mittal (312275)
     * @author David Chernis (310298)
     */
    @FunctionalInterface
    interface ChooseCardsHandler{
        void onChooseCards(SortedBag<Card> cardBag);
    }

}
