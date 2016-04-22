package edu.nyu.cs.foodie.GeneticAlgorithm;

import java.util.BitSet;
import java.util.Random;

public class Individual {

  private static Random rand = new Random();
  BitSet w1;
  BitSet w2;
  BitSet w3;

  public Individual() {
    this.w1 = new BitSet(8);
    this.w2 = new BitSet(8);
    this.w3 = new BitSet(8);

    for (int i = 0; i < 8; i++) {
      w1.set(i, rand.nextBoolean());
      w2.set(i, rand.nextBoolean());
      w3.set(i, rand.nextBoolean());
    }
  }

  public Individual(BitSet w1, BitSet w2, BitSet w3) {
    this.w1 = w1;
    this.w2 = w2;
    this.w3 = w3;
  }

  public byte getValue(BitSet bs) {
    byte result = 0;
    int shift = 7;
    for (int i = 0; i < bs.size(); i++) {
      if (bs.get(i)) {
        result |= (1 << shift);
      }
      shift--;
    }
    return result;
  }

  public int getFitness() {
    return FitnessCalc.calcFitness(getValue(w1), getValue(w2), getValue(w3));
  }

}
