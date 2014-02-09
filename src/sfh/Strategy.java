package sfh;

// Game state, hero, villain
public interface Strategy<GS, H, V> {

    /**
     * Get the best response to villain's strategy.
     * @return hero's new strategy
     */
    public H getBestResponse(GS gs, V villain);
    
    /**
     * Merge the other strategy into this one, updating by epsilon.
     * 
     * @param epsilon a value from 0 to 1.0, where 0 ignores the other strategy, and 1.0 is a full copy of the other strategy
     */
    public void mergeFrom(H other, double epsilon);
}
