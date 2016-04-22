package edu.nyu.cs.foodie.GeneticAlgorithm;

import java.util.*;

public class Population {

  private List<Individual> individuals;

  public Population(int initSize, boolean initialize) {
    this.individuals = new ArrayList<>();

    if (initialize) {
      for (int i = 0; i < initSize; i++) {
        Individual newIndividual = new Individual();
        individuals.add(newIndividual);
      }
    }
  }

  public Population(Population pop) {
    this.individuals = new ArrayList<>(pop.getAll());
  }

  public Individual getIndividual(int index) {
    if (index < 0 || index >= individuals.size()) {
      throw new IllegalArgumentException();
    }

    return individuals.get(index);
  }

  public Individual getFittest() {
    Individual fittest = individuals.get(0);
    for (int i = 1; i < individuals.size(); i++) {
      if (fittest.getFitness() <= individuals.get(i).getFitness()) {
        fittest = individuals.get(i);
      }
    }
    return fittest;
  }

  public int size() {
    return individuals.size();
  }

  public void saveIndividual(int index, Individual individual) {
    if (index < 0 || index >= individuals.size() || individual == null) {
      throw new IllegalArgumentException();
    }

    individuals.set(index, individual);
  }

  public void sort() {
    Collections.sort(individuals, new Comparator<Individual>() {
      @Override
      public int compare(Individual i1, Individual i2) {
        return (int) (i2.getFitness() - i1.getFitness());
      }
    });
  }

  public void addIndividual(Individual individual) {
    individuals.add(individual);
  }

  public List<Individual> getAll() {
    return Collections.unmodifiableList(individuals);
  }

  public void exclude(int size) {
    sort();
    while (size > 0) {
      individuals.remove(individuals.size() - 1);
      size--;
    }
  }

}
