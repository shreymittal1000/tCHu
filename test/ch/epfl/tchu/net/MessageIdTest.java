package ch.epfl.tchu.net;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import static ch.epfl.tchu.net.MessageId.*;

public class MessageIdTest {
	@Test
    void colorValuesAreDefinedInTheRightOrder() {
        var expectedValues = new MessageId[]{
        		INIT_PLAYERS, RECEIVE_INFO, UPDATE_STATE, SET_INITIAL_TICKETS, CHOOSE_INITIAL_TICKETS,
        		NEXT_TURN, CHOOSE_TICKETS, DRAW_SLOT, ROUTE, CARDS, CHOOSE_ADDITIONAL_CARDS
        };
        assertArrayEquals(expectedValues, MessageId.values());
    }

    @Test
    void colorAllIsDefinedCorrectly() {
        assertEquals(List.of(MessageId.values()), ALL);
    }

    @Test
    void colorCountIsDefinedCorrectly() {
        assertEquals(11, COUNT);
    }
}
