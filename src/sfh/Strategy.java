package sfh;

// Game state, hero, villain
public interface Strategy<GS, H, V> {

	/**
	 * Get the best response to villain's strategy.
	 * @return hero's new strategy
	 */
	H getBestResponse(GS gs, V villain);

	/**
	 * Merge the other strategy into this one, updating by epsilon.
	 * 
	 * @param epsilon a value from 0 to 1.0, where 0 ignores the other strategy, and 1.0 is a full copy of the other strategy
	 *
	 * @return a measure of the difference between the original and new strategies
	 */
	double mergeFrom(H other, double epsilon);
}
