package edu.nyu.cs.foodie.Recommender;

import edu.nyu.cs.foodie.util.ValueComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FilterLoc {

  private static Map<String, String> userLoc;
  private static Map<String, String> city_state;

  static {
    userLoc = new HashMap<>();
    city_state = new HashMap<>();

    try {
      File f = new File("src/edu/nyu/cs/foodie/Files/userLocation.txt");
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        String[] sp = line.split(",");
        userLoc.put(sp[0], sp[1]);
      }
      br.close();
      fr.close();
    } catch (IOException e) {
      System.err.println("[Error]: Fail to open userLocation.txt file.");
      System.exit(1);
    }

    try {
      File f = new File("src/edu/nyu/cs/foodie/Files/city_state.txt");
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        String[] sp = line.split(",");
        city_state.put(sp[0], sp[1]);
      }
      br.close();
      fr.close();
    } catch (IOException e) {
      System.err.println("[Error]: Fail to open city_state.txt file.");
      System.exit(1);
    }

  }

  public static Set<String> recommend(Set<String> recommendList, String userID) {
    Set<String> result = new HashSet<>();
    if (recommendList == null || recommendList.isEmpty()) {
      return result;
    }

    Map<String, Integer> scores = new HashMap<>();
    String city = userLoc.get(userID);
    String state = city_state.get(city);

    for (String friend : recommendList) {
      String friendCity = userLoc.get(friend);
      if (friendCity.equals(city)) {
        scores.put(friend, 2);
      }
      else if (city_state.get(friendCity).equals(state)) {
        scores.put(friend, 1);
      }
      else {
        scores.put(friend, 0);
      }
    }

    List<Map.Entry<String, Integer>> sortedScore = new ArrayList<>();
    sortedScore.addAll(scores.entrySet());
    Collections.sort(sortedScore, new ValueComparator<>());

    int i = 0;
    while (i < sortedScore.size() && sortedScore.get(i).getValue() > 0) {
      result.add(sortedScore.get(i).getKey());
      i++;
    }

    while (result.size() < 15 && i < sortedScore.size()) {
      result.add(sortedScore.get(i).getKey());
      i++;
    }


    return result;
  }
}
