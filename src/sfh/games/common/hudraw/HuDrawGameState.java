package sfh.games.common.hudraw;

import sfh.GameState;

public interface HuDrawGameState<OOP, IP> extends GameState<OOP, IP> {

  double getValue(OOP oop, IP ip);

  /**
   * Calculates the expected value for OOP in a 1 bet pot with no betting for now.
   */
  double calculateValue(OOP oop, IP ip);
}
