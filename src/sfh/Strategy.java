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
     */
    public void mergeFrom(H other, double epsilon);
}
