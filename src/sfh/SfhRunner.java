package sfh;

import com.google.common.collect.Lists;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import sfh.badugi.Hand;
import sfh.cards.Deck;
import sfh.games.hubadugi.HuBadugiGameState;
import sfh.games.hubadugi.HuBadugiIpStrategy;
import sfh.games.hubadugi.HuBadugiOopStrategy;
import sfh.games.hulhe.HulheGameState;
import sfh.games.hulhe.IpStrategy;
import sfh.games.hulhe.OopStrategy;
import sfh.games.roshambo.RoshamboGameState;
import sfh.games.roshambo.RoshamboStrategy;

import java.util.List;

public class SfhRunner {
  private static final String FLAG_GAME = "game";
  private static final String FLAG_ITERATIONS = "iterations";
  private static OptionSpec<Integer> FLAG_SPEC_ITERATIONS;

  private static final String FLAG_GAME_BADUGI = "badugi";
  private static final String FLAG_GAME_HULHE = "hulhe";
  private static final String FLAG_GAME_ROSHAMBO = "roshambo";

  private static final String SPACER = "        ";

  public static void main(String[] args) {
    OptionSet options = getOptions(args);
    int iterations = options.valueOf(FLAG_SPEC_ITERATIONS);
    if (FLAG_GAME_ROSHAMBO.equals(options.valueOf(FLAG_GAME))) {
      play(iterations, new RoshamboGameState(), new RoshamboStrategy(), new RoshamboStrategy());
    } else if (FLAG_GAME_BADUGI.equals(options.valueOf(FLAG_GAME))) {
      Deck deck = Deck.createDeck();
      Hand oopHand = deck.drawHand("5c", "7d", "9s", "9d");
      Hand ipHand = deck.drawHand("4c", "6d", "8s", "Ks");
      HuBadugiGameState gs = new HuBadugiGameState(deck, oopHand, ipHand);
      HuBadugiIpStrategy ip = new HuBadugiIpStrategy();
      HuBadugiOopStrategy oop = new HuBadugiOopStrategy();

      play(iterations, gs, oop, ip);
    }
  }

  private static OptionSet getOptions(String[] args) {
    OptionParser parser = new OptionParser();
    parser.accepts(FLAG_GAME).withRequiredArg();
    FLAG_SPEC_ITERATIONS = parser.accepts(FLAG_ITERATIONS).withRequiredArg()
        .ofType(Integer.class)
        .defaultsTo(1);

    return parser.parse(args);
  }

  public static <
      GS extends GameState<? super H, ? super V>,
      H extends Strategy<GS, H, V>,
      V extends Strategy<GS, V, H>>
  void play(int iterations, GS gs, H strategy1, V strategy2) {
    System.out.println("Original OOP strategy:\n\n" + strategy1);
    System.out.printf("\n%sOriginal IP strategy:\n\n%s", SPACER, indented(strategy2.toString()));
    System.out.println("\nOriginal OOP EV: " + gs.getValue(strategy1, strategy2));

    List<Double> oopDiff = Lists.newArrayList();
    List<Double> ipDiff = Lists.newArrayList();
    List<Double> oopValue = Lists.newArrayList();

    long startTime = System.currentTimeMillis();

    double epsilon;
    for (int i = 0; i < iterations; i++) {
      // just taking random shots in the dark at some function that makes things converge quickly

      // Doesn't converge very fast
      // epsilon = 1.0 / (iterations + 1);

      //epsilon = 0.1;

      epsilon = 5.0 / (iterations + 5);

      oopDiff.add(strategy1.mergeFrom(strategy1.getBestResponse(gs, strategy2), epsilon));
      System.out.println("\n--------------------\n#" + i + " OOP strategy:\n\n" + strategy1);
      System.out.println("EV: " + gs.getValue(strategy1, strategy2));

      ipDiff.add(strategy2.mergeFrom(strategy2.getBestResponse(gs, strategy1), epsilon));
      System.out.printf("\n%s------------------\n%s#%d IP strategy:\n\n%s\n",
          SPACER, SPACER, i, indented(strategy2.toString()));
      double value = gs.getValue(strategy1, strategy2);
      System.out.printf("%sEV: %f\n", SPACER, value);
      oopValue.add(value);
      long elapsed = (System.currentTimeMillis() - startTime);
      System.out.println(((float)elapsed/(i+1)) + " mseconds/iter");
    }

    System.out.println("\n-----------------------------\nFinal strategies:\n");
    System.out.println("OOP:");
    System.out.println(strategy1);
    System.out.println(indented("IP:"));
    System.out.println(indented(strategy2.toString()));
    System.out.println("Final OOP EV: " + gs.getValue(strategy1, strategy2));

    System.out.println((System.currentTimeMillis() - startTime) + " mseconds");

    printCsvForStupidGoogleDocs(oopDiff, "OOP");
    System.out.println();
    printCsvForStupidGoogleDocs(ipDiff, "IP");
    System.out.println();
    printCsvForStupidGoogleDocs(oopValue, "EV");
    System.out.println();
  }

  private static void printCsvForStupidGoogleDocs(List<Double> diffs, String name) {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    sb.append(',');
    for (Double diff : diffs) {
      sb.append(diff);
      sb.append(',');
    }
    System.out.println(sb);
  }

  private static String indented(String string) {
    StringBuilder indented = new StringBuilder();
    for (String line : string.split("\n")) {
      indented.append(SPACER).append(line).append("\n");
    }
    return indented.toString();
  }

}
