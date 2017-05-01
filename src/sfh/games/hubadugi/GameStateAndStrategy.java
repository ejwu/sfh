package sfh.games.hubadugi;

/**
 * Encapsulate a game state and pair of strategies to make them cacheable.
 * TODO: Unclear if this happens enough to bother with caching.
 */
public class GameStateAndStrategy {
  private HuBadugiGameState gs;
  private HuBadugiOopStrategy oop;
  private HuBadugiIpStrategy ip;

  public GameStateAndStrategy(HuBadugiGameState gs, HuBadugiOopStrategy oop, HuBadugiIpStrategy ip) {
    this.gs = gs;
    this.oop = oop;
    this.ip = ip;
  }

  public double calculateValue() {
    return gs.calculateValue(oop, ip);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GameStateAndStrategy that = (GameStateAndStrategy) o;

    if (gs != null ? !gs.equals(that.gs) : that.gs != null) return false;
    if (oop != null ? !oop.equals(that.oop) : that.oop != null) return false;
    return ip != null ? ip.equals(that.ip) : that.ip == null;
  }

  @Override
  public int hashCode() {
    int result = gs != null ? gs.hashCode() : 0;
    result = 31 * result + (oop != null ? oop.hashCode() : 0);
    result = 31 * result + (ip != null ? ip.hashCode() : 0);
    return result;
  }
}
