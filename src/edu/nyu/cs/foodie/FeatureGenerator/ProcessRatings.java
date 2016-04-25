package edu.nyu.cs.foodie.FeatureGenerator;

import edu.nyu.cs.foodie.ConnDB.DBConnection;
import edu.nyu.cs.foodie.util.ValueComparator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ProcessRatings {

  private static Map<String, List<String>> restaurantType
      = new HashMap<>();

  // user -> (features -> frequency)
  private static Map<String, Map<String, Integer>> userFeatures = new HashMap<>();

  private static Comparator<Map.Entry<String, Integer>> comparator = new ValueComparator();

  public static void main(String[] args) {
    process();
  }

  public static void process() {
    Connection c = DBConnection.openDB();

    // load restaurants from DB
    loadRestaurantType(c);

    try {
      String query = "select userid, businessid, stars from reviews";
      Statement stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery(query);

      while (rs.next()) {
        String user = rs.getString(1);
        String business = rs.getString(2);
        double stars = rs.getDouble(3);

        if (!restaurantType.containsKey(business)) {
          continue;
        }
        userFeatures.putIfAbsent(user, new HashMap<>());

        if (stars >= 4.0) {
          List<String> types = restaurantType.get(business);
          for (String type : types) {
            if (!type.equals("Restaurants") && !type.equals("Food")) {
              userFeatures.get(user).putIfAbsent(type, 0);
              int freq = userFeatures.get(user).get(type);
              userFeatures.get(user).put(type, freq + 1);
            }
          }
        }
      }

      rs.close();
      stmt.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Fail to execute query to fetch reviews table.");
      System.exit(1);
    }

    DBConnection.closeDB();

    // print out userFeatures map
    printoutUserFeatures();
  }

  private static void  printoutUserFeatures() {
    try {
      PrintWriter pw = new PrintWriter("src/edu/nyu/cs/foodie/Files/user_features.txt");
      StringBuilder sb = new StringBuilder();
      for (String user : userFeatures.keySet()) {
        sb.append(user).append("##");

        List<Map.Entry<String, Integer>> sortedFeature = new ArrayList<>();
        sortedFeature.addAll(userFeatures.get(user).entrySet());
        Collections.sort(sortedFeature, comparator);

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedFeature) {
          if (count == 0) {
            sb.append(entry.getKey()).append("-:").append(entry.getValue());
          }
          else {
            sb.append(",").append(entry.getKey()).append("-:").append(entry.getValue());
          }
          count++;
          if (count > 10) {
            break;
          }
        }

        pw.println(sb.toString());
        sb.setLength(0);
      }

      pw.flush();
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("[Error]: Fail to write to user_features.txt");
      System.exit(1);
    }

  }

  private static void loadRestaurantType(Connection c) {

    try {
      String query = "select id, categories from restaurants";
      Statement stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String business = rs.getString(1);
        String categories = rs.getString(2);
        if (!categories.contains("Restaurants") && !categories.contains("Food") ) {
          continue;
        }
        String[] sp = categories.split(",");
        restaurantType.putIfAbsent(business, new ArrayList<>());
        restaurantType.get(business).addAll(Arrays.asList(sp));
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Fail to execute query to fetch restaurants table.");
      System.exit(1);
    }
  }

}
