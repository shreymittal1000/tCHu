package ch.epfl.tchu;

/**
 * Helper immutable class that cannot be instantiated outside of its own class.
 * Contains the static checkArgument method.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class Preconditions {
	
    /**
     * Default Preconditions constructor. Cannot be used outside the Preconditions class.
     */
	private Preconditions() {}
    
	/**
	 * Method used to check whether certain aspects are proper enough for the game to run.
	 * @param shouldBeTrue (boolean): argument must be a boolean.
	 * @throws IllegalArgumentException if shouldBeTrue is not true.
	 */
    public static void checkArgument(boolean shouldBeTrue) {
        if(!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
