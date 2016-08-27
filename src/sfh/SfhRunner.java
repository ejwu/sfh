package sfh;

import com.google.common.collect.Lists;
import sfh.games.hubadugi.HuBadugiGameState;
import sfh.games.hubadugi.HuBadugiIpStrategy;
import sfh.games.hubadugi.HuBadugiOopStrategy;

import java.util.List;

public class SfhRunner {

  public static void main(String[] args) {

//      ResistanceGoodStrategy good = new ResistanceGoodStrategy();
//      ResistanceEvilStrategy evil = new ResistanceEvilStrategy();
//      ResistanceGameState gs = new ResistanceGameState();

    HuBadugiGameState gs = new HuBadugiGameState();
    HuBadugiIpStrategy ip = new HuBadugiIpStrategy();
    HuBadugiOopStrategy oop = new HuBadugiOopStrategy();

    play(1, gs, oop, ip);
  }

  public static <
      GS extends GameState<? super H, ? super V>,
      H extends Strategy<GS, H, V>,
      V extends Strategy<GS, V, H>>
  void play(int iterations, GS gs, H strategy1, V strategy2) {
    System.out.println("Original UTG strategy:\n\n" + strategy1);
    System.out.println("Original IP strategy:\n\n" + strategy2);
    System.out.println("Original UTG EV: " + gs.getValue(strategy1, strategy2));

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
      System.out.println("\n--------------------\n#" + i + " UTG strategy:\n\n" + strategy1);
      System.out.println("EV: " + gs.getValue(strategy1, strategy2));

      ipDiff.add(strategy2.mergeFrom(strategy2.getBestResponse(gs, strategy1), epsilon));
      System.out.println("\n--------------------\n#" + i + " IP strategy:\n\n" + strategy2);
      double value = gs.getValue(strategy1, strategy2);
      System.out.println("EV: " + value);
      oopValue.add(value);
      long elapsed = (System.currentTimeMillis() - startTime);
      System.out.println(" avg " + ((float)elapsed/(i+1)) + " mseconds/iter");
    }

    System.out.println("\n-----------------------------\nFinal strategies:\n");
    System.out.println("OOP:");
    System.out.println(strategy1);
    System.out.println("IP");
    System.out.println(strategy2);
    System.out.println("Final UTG EV: " + gs.getValue(strategy1, strategy2));

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

}
