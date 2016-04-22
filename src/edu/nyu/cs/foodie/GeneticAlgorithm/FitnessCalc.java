package edu.nyu.cs.foodie.GeneticAlgorithm;

public class FitnessCalc {

  private static int i1;
  private static int i2;
  private static int i3;

  public static void set(int index1, int index2, int index3) {
    i1 = index1;
    i2 = index2;
    i3 = index3;
  }

  public static int calcFitness(byte w1, byte w2, byte w3) {
    return i1 * w1 + i2 * w2 + i3 * w3;
  }
}
