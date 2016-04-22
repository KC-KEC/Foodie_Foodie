package edu.nyu.cs.foodie.GeneticAlgorithm;

import java.util.*;

public class GA {

  private static final double MUTATION_RATE = 0.04;
  private static final double EXCLUDE_RATE = 0.3;
  private static Random rand = new Random();

  public static Population evolvePopulation(Population population) {

    int iteration = 1;
    while (iteration <= 4) {
      double currFitness = population.getFittest().getFitness();
      Population newPopulation = new Population(population);
      int excludeCnt = (int) EXCLUDE_RATE * newPopulation.size();
      int remainCnt = newPopulation.size() - excludeCnt;
      newPopulation.exclude(excludeCnt);

      List<Individual> children = new ArrayList<>();

//      for (int i = 0; i < remainCnt; i++) {
//        for (int j = i + 1; j < remainCnt; j++) {
//          Individual indiv1 = newPopulation.getIndividual(i);
//          Individual indiv2 = newPopulation.getIndividual(j);
//          List<Individual> newIndivs = crossover(indiv1, indiv2);
//          children.addAll(newIndivs);
//        }
//      }

      for (int i = 0; i < excludeCnt; i++) {
        int index1 = rand.nextInt(remainCnt);
        int index2 = rand.nextInt(remainCnt);
        while (index2 == index1) {
          index2 = rand.nextInt(remainCnt);
        }
        Individual indiv1 = newPopulation.getIndividual(index1);
        Individual indiv2 = newPopulation.getIndividual(index2);
        List<Individual> newIndivs = crossover(indiv1, indiv2);
        children.addAll(newIndivs);
      }

      mutate(children);

      Set<Individual> parentSet = new HashSet<>();
      parentSet.addAll(newPopulation.getAll());
      for (Individual child : children) {
        if (parentSet.contains(child)) {
          newPopulation.addIndividual(child);
        }
      }

      double newFitness = newPopulation.getFittest().getFitness();
      if (newFitness <= currFitness) {
        iteration++;
      }
      else {
        iteration = 1;
      }
      population = newPopulation;
    }

    return population;
  }

  private static void mutate(List<Individual> children) {
    for (Individual indiv : children) {
      for (int i = 0; i < indiv.w1.size(); i++) {
        if (Math.random() <= MUTATION_RATE) {
          indiv.w1.flip(i);
        }
        if (Math.random() <= MUTATION_RATE) {
          indiv.w2.flip(i);
        }
        if (Math.random() <= MUTATION_RATE) {
          indiv.w2.flip(i);
        }
      }
    }
  }

  private static List<Individual> crossover(Individual p1, Individual p2) {
    List<Individual> children = new ArrayList<>();

    int cross1 = rand.nextInt(7) + 1;
    BitSet w1_p1 = p1.w1;
    BitSet w1_p2 = p2.w1;
    BitSet newW1_1 = generateBitSet(cross1, w1_p1, w1_p2);
    BitSet newW1_2 = generateBitSet(cross1, w1_p2, w1_p1);


    int cross2 = rand.nextInt(7) + 1;
    BitSet w2_p1 = p1.w2;
    BitSet w2_p2 = p2.w2;
    BitSet newW2_1 = generateBitSet(cross2, w2_p1, w2_p2);
    BitSet newW2_2 = generateBitSet(cross2, w2_p2, w2_p1);

    int cross3 = rand.nextInt(7) + 1;
    BitSet w3_p1 = p1.w3;
    BitSet w3_p2 = p2.w3;
    BitSet newW3_1 = generateBitSet(cross3, w3_p1, w3_p2);
    BitSet newW3_2 = generateBitSet(cross3, w3_p2, w3_p1);


    children.add(new Individual(newW1_1, newW2_1, newW3_1));
    children.add(new Individual(newW1_1, newW2_2, newW3_1));
    children.add(new Individual(newW1_1, newW2_1, newW3_2));
    children.add(new Individual(newW1_1, newW2_2, newW3_2));
    children.add(new Individual(newW1_2, newW2_1, newW3_1));
    children.add(new Individual(newW1_2, newW2_2, newW3_1));
    children.add(new Individual(newW1_2, newW2_1, newW3_2));
    children.add(new Individual(newW1_2, newW2_2, newW3_2));

    return children;
  }

  private static BitSet generateBitSet(int cross, BitSet bs1, BitSet bs2) {
    BitSet result = new BitSet(8);
    int k = 0;
    for (int i = 0; i < cross; i++) {
      result.set(k++, bs1.get(i));
    }
    for (int i = cross; i < 8; i++) {
      result.set(k++, bs2.get(i));
    }

    return result;
  }
}
