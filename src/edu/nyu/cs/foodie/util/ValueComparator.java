package edu.nyu.cs.foodie.util;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator<K, V extends Comparable<V>> implements Comparator<Map.Entry<K, V>> {

  @Override
  public int compare(Map.Entry<K, V> entry1, Map.Entry<K, V> entry2) {
    return entry2.getValue().compareTo(entry1.getValue());
  }
}
