package edu.nyu.cs.foodie.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {

  private Map<K, V> map = new HashMap<>();

  public ValueComparator(Map<K, V> map) {
    this.map = map;
  }

  @Override
  public int compare(K key1, K key2) {
    return -map.get(key1).compareTo(map.get(key2));
  }
}
