package ch.epfl.tchu.net;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.test.TestRandomizer;

public class SerdeTest {
	
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private static String randomName(Random rng, int length) {
        var sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        return sb.toString();
    }
	
    private static final Serde<Integer> intSerde = Serde.of(
            (i) -> String.valueOf(i) ,
            (i) -> Integer.parseInt(i));
    
    private static final Serde<String> stringSerde = Serde.of(
            (i) -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)) ,
            (i) -> new String (Base64.getDecoder().decode(i.getBytes(StandardCharsets.UTF_8))));
    
    private static final Serde<PlayerId> playerIdSerde = Serde.oneOf(PlayerId.ALL);
    private static final Serde<TurnKind> turnKindSerde = Serde.oneOf(TurnKind.ALL);
    private static final Serde<Card> cardSerde = Serde.oneOf(Card.ALL);
    private static final Serde<Route> routeSerde = Serde.oneOf(ChMap.routes());
    private static final Serde<Ticket> ticketSerde = Serde.oneOf(ChMap.tickets());
    
    private static final Serde<List<String>> stringListSerde = Serde.listOf(stringSerde, ',');
    private static final Serde<List<Card>> cardListSerde = Serde.listOf(cardSerde, ',');
    private static final Serde<List<Route>> routeListSerde = Serde.listOf(routeSerde, ',');
    private static final Serde<List<Ticket>> ticketListSerde = Serde.listOf(ticketSerde, ','); //Not in Serdes
    private static final Serde<SortedBag<String>> stringBagSerde = Serde.bagOf(stringSerde, ','); //Not in Serdes
    private static final Serde<SortedBag<Card>> cardBagSerde = Serde.bagOf(cardSerde, ',');
    private static final Serde<SortedBag<Ticket>> ticketBagSerde = Serde.bagOf(ticketSerde, ',');
    
	@Test
	void ofWorks() {
	    for(int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
	    	int deserializedInt = TestRandomizer.newRandom().nextInt();
	    	String serializedInt = String.valueOf(deserializedInt);
	    	assertEquals(serializedInt, intSerde.serialize(deserializedInt));
	    	assertEquals(deserializedInt, intSerde.deserialize(serializedInt).intValue());
	    	
	    	String deserializedString = randomName(new Random(), (new Random()).nextInt(10));
	    	String serializedString = Base64.getEncoder().encodeToString(deserializedString.getBytes(StandardCharsets.UTF_8));
	    	assertEquals(serializedString, stringSerde.serialize(deserializedString));
	    	assertEquals(deserializedString, stringSerde.deserialize(serializedString));
	    }
	}
	
	@Test
	void oneOfWorks() {
	    for(PlayerId pid : PlayerId.ALL) {
	    	String serialized = String.valueOf(pid.ordinal());
	    	assertEquals(serialized, playerIdSerde.serialize(pid));
	    	assertEquals(pid, playerIdSerde.deserialize(serialized));
	    }
	    
	    for(TurnKind tk : TurnKind.ALL) {
	    	String serialized = String.valueOf(tk.ordinal());
	    	assertEquals(serialized, turnKindSerde.serialize(tk));
	    	assertEquals(tk, turnKindSerde.deserialize(serialized));
	    }
	    
	    for(Card c : Card.ALL) {
	    	String serialized = String.valueOf(c.ordinal());
	    	assertEquals(serialized, cardSerde.serialize(c));
	    	assertEquals(c, cardSerde.deserialize(serialized));
	    }
	    
	    for(Route r : ChMap.routes()) {
	    	String serialized = String.valueOf(ChMap.routes().indexOf(r));
	    	assertEquals(serialized, routeSerde.serialize(r));
	    	assertEquals(r, routeSerde.deserialize(serialized));
	    }
	    
	    for(Ticket t : ChMap.tickets()) {
	    	String serialized = String.valueOf(ChMap.tickets().indexOf(t));
	    	assertEquals(serialized, ticketSerde.serialize(t));
	    	assertEquals(t, ticketSerde.deserialize(serialized));
	    }
	}
	
	@Test
	void listOfWorks() {
		Random rng = TestRandomizer.newRandom();
		for(int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
	    	int listSize = TestRandomizer.newRandom().nextInt(10);
	    	
	    	List<String> deserializedStringList = new ArrayList<String>();
	    	List<Card> deserializedCardList = new ArrayList<Card>();
	    	List<Route> deserializedRouteList = new ArrayList<Route>();
	    	List<Ticket> deserializedTicketList = new ArrayList<Ticket>();
	    	
	    	for(int j = 0; j < listSize; j++) {
	    		deserializedStringList.add(randomName(rng, rng.nextInt(10)));
	    		deserializedCardList.add(Card.ALL.get(rng.nextInt(Card.COUNT)));
	    		deserializedRouteList.add(ChMap.routes().get(rng.nextInt(ChMap.routes().size())));
	    		deserializedTicketList.add(ChMap.tickets().get(rng.nextInt(ChMap.tickets().size())));
	    		
	    	}
	    	
	    	String serializedStringList = stringListSerde.serialize(deserializedStringList);
	    	String serializedCardList = cardListSerde.serialize(deserializedCardList);
	    	String serializedRouteList = routeListSerde.serialize(deserializedRouteList);
	    	String serializedTicketList = ticketListSerde.serialize(deserializedTicketList);
	    	
	    	assertEquals(serializedStringList, stringListSerde.serialize(deserializedStringList));
	    	assertEquals(serializedCardList, cardListSerde.serialize(deserializedCardList));
	    	assertEquals(serializedRouteList, routeListSerde.serialize(deserializedRouteList));
	    	assertEquals(serializedTicketList, ticketListSerde.serialize(deserializedTicketList));
	    	
	    	assertEquals(deserializedStringList, stringListSerde.deserialize(serializedStringList));
	    	assertEquals(deserializedCardList, cardListSerde.deserialize(serializedCardList));
	    	assertEquals(deserializedRouteList, routeListSerde.deserialize(serializedRouteList));
	    	assertEquals(deserializedTicketList, ticketListSerde.deserialize(serializedTicketList));
	    }
	}
	
	@Test
	void bagOfWorks() {
		Random rng = TestRandomizer.newRandom();
		for(int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
	    	int listSize = TestRandomizer.newRandom().nextInt(10);
	    	
	    	List<String> deserializedStringList = new ArrayList<String>();
	    	List<Card> deserializedCardList = new ArrayList<Card>();
	    	List<Ticket> deserializedTicketList = new ArrayList<Ticket>();
	    	
	    	for(int j = 0; j < listSize; j++) {
	    		deserializedStringList.add(randomName(rng, rng.nextInt(10)));
	    		deserializedCardList.add(Card.ALL.get(rng.nextInt(Card.COUNT)));
	    		deserializedTicketList.add(ChMap.tickets().get(rng.nextInt(ChMap.tickets().size())));
	    	}
	    	
	    	SortedBag<String> deserializedStringBag = SortedBag.of(deserializedStringList);
	    	SortedBag<Card> deserializedCardBag = SortedBag.of(deserializedCardList);
	    	SortedBag<Ticket> deserializedTicketBag = SortedBag.of(deserializedTicketList);
	    	
	    	String serializedStringBag = stringBagSerde.serialize(deserializedStringBag);
	    	String serializedCardBag = cardBagSerde.serialize(deserializedCardBag);
	    	String serializedTicketBag = ticketBagSerde.serialize(deserializedTicketBag);
	    	
	    	assertEquals(serializedStringBag, stringBagSerde.serialize(deserializedStringBag));
	    	assertEquals(serializedCardBag, cardBagSerde.serialize(deserializedCardBag));
	    	assertEquals(serializedTicketBag, ticketBagSerde.serialize(deserializedTicketBag));
	    	
	    	assertEquals(deserializedStringBag, stringBagSerde.deserialize(serializedStringBag));
	    	assertEquals(deserializedCardBag, cardBagSerde.deserialize(serializedCardBag));
	    	assertEquals(deserializedTicketBag, ticketBagSerde.deserialize(serializedTicketBag));
	    }
	}
}
