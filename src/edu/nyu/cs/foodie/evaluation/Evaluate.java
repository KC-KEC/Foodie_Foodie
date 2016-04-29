package edu.nyu.cs.foodie.evaluation;

import edu.nyu.cs.foodie.ConnDB.DBConnection;
import edu.nyu.cs.foodie.Recommender.FilterGA;
import edu.nyu.cs.foodie.Recommender.FilterLoc;
import edu.nyu.cs.foodie.Recommender.FilterSimilarity;

import java.sql.*;
import java.util.*;

public class Evaluate {

  private static Map<String, Set<String>> user_test_frends = new HashMap<>();
//  private static Map<String, Set<String>> user_dev_friends = new HashMap<>();
  private static List<String> testUsers = new ArrayList<>();

  public static double evalute() {
    Connection c = DBConnection.openDB();
    int userCount = 0;

    try {
      Statement stmt = c.createStatement();
      String query = "select id from users";
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String userid = rs.getString(1);

        String queryTestFriends = "select toid from test_friends where fromid = ?";
        PreparedStatement pstmt_test = c.prepareStatement(queryTestFriends);
        pstmt_test.setString(1, userid);
        ResultSet rsTest = pstmt_test.executeQuery();
        int actualFriends = 0;
        while (rsTest.next()) {
          actualFriends++;
        }

        if (actualFriends > 100) {
          userCount++;
          testUsers.add(userid);
          user_test_frends.put(userid, new HashSet<>());
          rsTest = pstmt_test.executeQuery();
          while (rsTest.next()) {
            user_test_frends.get(userid).add(rsTest.getString(1));
          }
        }
        rsTest.close();
        pstmt_test.close();
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println("[Error]: Fail to execute query.");
      System.exit(1);
    }

    DBConnection.closeDB();

    System.out.println("test user numbers: " + userCount);

    double totalPrecision = 0.0;
    for (String user : testUsers) {
      List<String> result;
      result = FilterGA.recommend(user);
      if (result.size() > 20) {
        result = FilterSimilarity.recommend(result, user);
      }
      if (result.size() > 10) {
        result = FilterLoc.fileterLoc(result, user);
      }

      int recommendSize = result.size();

      int correct = 0;
      for (String recommend : result) {
        if (user_test_frends.get(user).contains(recommend)) {
          correct++;
        }
      }

      double precision = 0.0;
      if (recommendSize != 0) {
        precision = (double) correct / recommendSize;
      }
      totalPrecision += precision;

      System.out.println(recommendSize + " | " + correct + " | " + user_test_frends.get(user).size()
          + " | " + precision);
    }

    return totalPrecision / userCount;
  }

  public static void main(String[] args) {
    System.out.println("Recommendation size | Correct Number | Actual Friends size | Precision");
    System.out.println("Final result: " + Evaluate.evalute());
  }
}
