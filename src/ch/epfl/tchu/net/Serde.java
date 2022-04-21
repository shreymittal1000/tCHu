package ch.epfl.tchu.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;

/**
 * Interface representing (de)serialization algorithms for communications between the players and the server.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 * @param <T> represents the type of object that will be (de)serialized.
 */
public interface Serde<T> {

    /**
     * Returns the String that represents the serialized representation of the given object.
     * @param deserialized (T): the object to be serialized.
     * @return (String): the String that represents the serialized representation of the given object.
     */
    String serialize(T deserialized);

    /**
     * Returns the Object that the given serialized String represents.
     * @param serialized (String): the String to be deserialized.
     * @return (T): the Object that the given serialized String represents.
     */
    T deserialize(String serialized);

    /**
     * Returns the corresponding Serde based on the given serialization and deserialization functions.
     * @param <T> the Object type that will be (de)serialized with the given functions.
     * @param serialization (Function<T, String>): the serialization function.
     * @param deserialization (Function<T, String>): the deserialization function.
     * @return (Serde<T>): the corresponding Serde based on the given serialization and deserialization
     * functions.
     */
    public static <T> Serde<T> of(Function<T, String> serialization, Function<String, T> deserialization){
        return new Serde<T>() {
            public String serialize(T deserialized) {
                return serialization.apply(deserialized);
            }

            public T deserialize(String serialized) {
                return deserialization.apply(serialized);
            }
        };
    }

    /**
     * Returns a Serde that can (de)serialize enumerable or enumerable-like values based on a list containing
     * all the possible enumerable or enumerable-like values, which is given as parameter to the method.
     * @param <T> the Object type to be (de)serialized.
     * @param allEnumValues (List<T>): the list containing all the possible enumerable or enumerable-like
     * values.
     * @return (Serde<T>): a Serde that can (de)serialize enumerable or enumerable-like values based on a list
     * containing all the possible enumerable or enumerable-like values, which is given as parameter to the
     * method.
     */
    public static <T> Serde<T> oneOf(List<T> allEnumValues){
        return new Serde<T>() {
            public String serialize(T deserialized) {
                Integer integerRepresentation = allEnumValues.indexOf(deserialized);
                return integerRepresentation.toString();
            }

            public T deserialize(String serialized) {
                return allEnumValues.get(Integer.parseInt(serialized));
            }
        };
    }

    /**
     * Returns a Serde that can (de)serialize a list of a given type of Objects using a serde that can (de)serialize
     * each element individually and a separator to separate each individual Object in its serialized version.
     * @param <T> the Object type to be (de)serialized.
     * @param tSerde (Serde<T>): the Serde used to (de)serialize each of the individual elements of type T of the
     * list.
     * @param separator (char): a character that is used as a separator for the individual elements of the list in
     * its serialized form.
     * @return (<T> Serde<List<T>>): a Serde that can (de)serialize a list of a given type of Objects using a serde
     * that can (de)serialize each element individually and a separator to separate each individual Object in its
     * serialized version.
     */
    public static <T> Serde<List<T>> listOf(Serde<T> tSerde, char separator){
        return new Serde<List<T>>() {
            public String serialize(List<T> deserialized) {
                if(deserialized.isEmpty()) {
                    return "";
                }
                List<String> newList = new ArrayList<>();

                for(int i = 0; i < deserialized.size(); i++) {
                    newList.add(tSerde.serialize(deserialized.get(i)));
                }

                return String.join(String.valueOf(separator), newList);

            }

            public List<T> deserialize(String serialized) {
                if(serialized.equals("")) {
                    return List.of();
                }
                List<String> oldList = Arrays.asList(serialized.split(Pattern.quote(String.valueOf(separator)), -1));
                List<T> newList = new ArrayList<T>();
                for(int i = 0; i < oldList.size(); i++) {
                    newList.add(tSerde.deserialize(oldList.get(i)));
                }

                return newList;
            }
        };
    }

    /**
     * Returns a Serde that can (de)serialize a SortedBag of a given type of Objects using a serde that can
     * (de)serialize each element individually and a separator to separate each individual Object in its serialized
     * version.
     * @param <T> the Object type to be (de)serialized.
     * @param tSerde (Serde<T>): the Serde used to (de)serialize each of the individual elements of type T of the
     * list.
     * @param separator (char): a character that is used as a separator for the individual elements of the list in
     * its serialized form.
     * @return (<T> Serde<SortedBag<T>>): a SortedBag that can (de)serialize a list of a given type of Objects using
     * a serde that can (de)serialize each element individually and a separator to separate each individual Object in
     * its serialized version.
     */
    public static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> tSerde, char separator){
        return new Serde<SortedBag<T>>() {

            public String serialize(SortedBag<T> deserialized) {
                return listOf(tSerde, separator).serialize(deserialized.toList());
            }

            public SortedBag<T> deserialize(String serialized) {
                return SortedBag.of(listOf(tSerde, separator).deserialize(serialized));
            }
        };
    }
}
