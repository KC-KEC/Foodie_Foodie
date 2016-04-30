package edu.nyu.cs.foodie.evaluation;

import edu.nyu.cs.foodie.ConnDB.DBConnection;
import edu.nyu.cs.foodie.Recommender.FilterGA;
import edu.nyu.cs.foodie.Recommender.FilterLoc;
import edu.nyu.cs.foodie.Recommender.FilterSimilarity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

class EvaluateV2 {
  static Map<String, Set<String>> recommendList = new HashMap<>();


  public static void evaluate() {
    Connection c = DBConnection.openDB();

    try {
      Statement stmt = c.createStatement();
      String query = "select id from users";
      ResultSet rs = stmt.executeQuery(query);

      int count = 0;
      int emptyCount = 0;
      while (rs.next()) {
        System.out.println(++count);
        String userID = rs.getString(1);
        System.out.println(userID);
        Set<String> fromGA = new FilterGA(c).recommend(userID);
        System.out.println("fromGA size: " + fromGA.size());

        Set<String> fromSim = FilterSimilarity.recommend(fromGA, userID);
        System.out.println("fromSim size: " + fromSim.size());

        Set<String> result = FilterLoc.recommend(fromSim, userID);

        if (!result.isEmpty()) {
          recommendList.put(userID, new HashSet<>(result));
          System.out.println("result size: " + result.size());
        }
        else {
          System.out.println("Empty recommend list: " + count + " " + userID);
          emptyCount++;
        }

        System.out.println();
      }

      rs.close();
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    DBConnection.closeDB();
  }

  public static void main(String[] args) {
    evaluate();

    System.out.println(recommendList.size());

    System.out.println("same count | recommend size | rate");

    double totalMeasure = 0.0;
    for (String user : recommendList.keySet()) {
      Set<String> recommends = recommendList.get(user);

      int count = 0;
      int recommendSize = 0;
      for (String friend : recommends) {
        if (recommendList.containsKey(friend)) {
          recommendSize++;
          Set<String> friendRecommends = recommendList.get(friend);
          if (friendRecommends.contains(user)) {
            count++;
          }
        }
      }
      double rate = (double) count / recommendSize;
      totalMeasure += rate;
      System.out.println(String.format("%d | %d | %.2f", count, recommendSize, rate));
    }

    System.out.println("total: " + totalMeasure / recommendList.size());
  }

}
