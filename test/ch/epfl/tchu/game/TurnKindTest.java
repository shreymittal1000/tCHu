package ch.epfl.tchu.game;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ch.epfl.tchu.game.Player.TurnKind.DRAW_TICKETS;
import static ch.epfl.tchu.game.Player.TurnKind.DRAW_CARDS;
import static ch.epfl.tchu.game.Player.TurnKind.CLAIM_ROUTE;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TurnKindTest {
    @Test
    void TurnKindValuesAreDefinedInTheRightOrder() {
        var expectedValues = new Player.TurnKind[]{
                DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE
        };
        assertArrayEquals(expectedValues, Player.TurnKind.values());
    }

    @Test
    void TurnKindIsDefinedCorrectly() {
        assertEquals(List.of(Player.TurnKind.values()), Player.TurnKind.ALL);
    }
}
