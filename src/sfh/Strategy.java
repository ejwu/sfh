package sfh;

public interface Strategy<Hero, Villain> {

    /**
     * Get the best response to villain's strategy.
     * @return hero's new strategy
     */
    public Hero getBestResponse(Villain villain);
    
    /**
     * Merge the other strategy into this one, updating by epsilon.
     */
    public void mergeFrom(Hero other, double epsilon);
}
