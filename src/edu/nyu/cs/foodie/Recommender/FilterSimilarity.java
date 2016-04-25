package edu.nyu.cs.foodie.Recommender;

import edu.nyu.cs.foodie.util.ValueComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FilterSimilarity {

  private static Map<String, Map<String, Integer>> userFeatures;
  private static Comparator<Map.Entry<String, Double>> comparator = new ValueComparator<>();

  static {
    userFeatures = new HashMap<>();
    try {
      File f = new File("src/edu/nyu/cs/foodie/Files/user_features.txt");
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;

      while ((line = br.readLine()) != null) {
        String[] sp = line.split("##");
        String user = sp[0];
        userFeatures.putIfAbsent(user, new HashMap<>());
        if (sp.length < 2) {
          continue;
        }

        String[] featureSet = sp[1].split(",");
        for (String featureEntry : featureSet) {
          String feature = featureEntry.split("-:")[0];
          int freq = Integer.parseInt(featureEntry.split("-:")[1]);
          userFeatures.get(user).put(feature, freq);
        }
      }

      br.close();
      fr.close();
    } catch (IOException e) {
      System.err.println("[Error]: Fail to open 'user_features.txt' file.");
      System.exit(1);
    }
  }

  public static List<String> filter(List<String> recommendList, String userID) {
    Map<String, Double> scores = new HashMap<>();
    for (String recommend : recommendList) {
      double score = similarity(userID, recommend);
      scores.put(recommend, score);
    }

    List<Map.Entry<String, Double>> sortedScore = new ArrayList<>();
    sortedScore.addAll(scores.entrySet());
    Collections.sort(sortedScore, comparator);

    List<String> result = new ArrayList<>();
    double rate = 0.5;
    int size = (int) (recommendList.size() * rate);
    for (Map.Entry<String, Double> entry : sortedScore) {
      result.add(entry.getKey());
      size--;
      if (size == 0) {
        break;
      }
    }

    return result;
  }

  private static double similarity(String user1, String user2) {
    Set<String> feature1 = userFeatures.get(user1).keySet();
    Set<String> feature2 = userFeatures.get(user2).keySet();

    // compute UT, the total frequency of all features
    int UT1 = 0, UT2 = 0;
    for (int freq : userFeatures.get(user1).values()) {
      UT1 += freq;
    }
    for (int freq : userFeatures.get(user2).values()) {
      UT2 += freq;
    }

    // compute u1Mean and u2Mean, the mean of the frequencies of user1 and user2
    double u1Mean, u2Mean;
    double total = 0;
    for (int freq : userFeatures.get(user1).values()) {
      total += (double) freq / UT1;
    }
    u1Mean = total / feature1.size();

    total = 0.0;
    for (int freq : userFeatures.get(user2).values()) {
      total += (double) freq / UT2;
    }
    u2Mean = total / feature2.size();

    // compute the intersection of features between two users
    Set<String> intersection = new HashSet<>(feature1);
    intersection.retainAll(feature2);

    // compute the numerator of tb(u1, u2)
    double numerator = 0.0;
    double y1 = 0.0, y2 = 0.0;
    for (String feature : intersection) {
      double x1 = (double) userFeatures.get(user1).get(feature) / UT1;
      x1 -= u1Mean;
      double x2 = (double) userFeatures.get(user2).get(feature) / UT2;
      x2 -= u2Mean;
      numerator += x1 * x2;

      y1 += x1 * x1;
      y2 += x2 * x2;
    }

    // compute the denominator of tb(u1, u2)
    double denominator = Math.sqrt(y1) * Math.sqrt(y2);

    double tb = -1.0;
    if (denominator != 0.0) {
      tb = numerator / denominator;
    }
    return tb;
  }
}
