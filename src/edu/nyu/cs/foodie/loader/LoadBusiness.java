package edu.nyu.cs.foodie.Loader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class LoadBusiness extends Loader {
  private final JSONParser jparser;
  Map<String, String> city_stateMap;
  PrintWriter pw_cityMap;
  PrintWriter pw_businessLoc;

  public LoadBusiness(String filename, PrintWriter pw_cityMap, PrintWriter pw_businessLoc) {
    super(filename);
    this.jparser = new JSONParser();
    this.city_stateMap = new HashMap<>();
    this.pw_cityMap = pw_cityMap;
    this.pw_businessLoc = pw_businessLoc;
  }

  public static void main(String[] args) {

    try {
      PrintWriter pw1 = new PrintWriter("src/edu/nyu/cs/foodie/Files/city_state");
      PrintWriter pw2 = new PrintWriter("src/edu/nyu/cs/foodie/Files/business_location");
      Loader loader = new LoadBusiness(
          "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"
      , pw1, pw2);

      loader.load();

      pw1.flush();
      pw2.flush();
      pw1.close();
      pw2.close();
    } catch (IOException e) {
      System.err.println("[Error]: Fail to open 'city_state' or 'business_location' file.");
      System.exit(1);
    }
  }

  @Override
  void createTable() {
    try {
      Statement stmt = c.createStatement();
      String businessSQL = "CREATE TABLE restaurants " +
          "(ID TEXT PRIMARY KEY NOT NULL," +
          " NAME TEXT NOT NULL," +
          " STATE TEXT NOT NULL," +
          " CITY TEXT NOT NULL," +
          " STARS DOUBLE PRECISION NOT NULL," +
          " LONGITUDE DOUBLE PRECISION NOT NULL," +
          " LATITUDE DOUBLE PRECISION NOT NULL," +
          " CATEGORIES TEXT NOT NULL)";

      stmt.executeUpdate(businessSQL);
      stmt.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Can not create table or table exists.");
    }
  }

  @Override
  void loadLine(String line) {
    if (pw_businessLoc == null || pw_cityMap == null) {
      System.err.println("[Error]: set output file first.");
      System.exit(1);
    }
    try {
      JSONObject jobj = (JSONObject) jparser.parse(line);
      String business_id = (String) jobj.get("business_id");
      String state = (String) jobj.get("state");
      String city = (String) jobj.get("city");
      String name = (String) jobj.get("name");
      double stars = (Double) jobj.get("stars");
      double longitude = (Double) jobj.get("longitude");
      double latitude = (Double) jobj.get("latitude");
      JSONArray categories = (JSONArray) jobj.get("categories");
      StringBuilder sb = new StringBuilder();
      for (Object obj : categories) {
        if (sb.length() == 0) {
          sb.append((String) obj);
        }
        else {
          sb.append(",").append((String) obj);
        }
      }
      String insertBusiness = "INSERT INTO RESTAURANTS VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
      PreparedStatement pstmt = c.prepareStatement(insertBusiness);
      pstmt.setString(1, business_id);
      pstmt.setString(2, name);
      pstmt.setString(3, state);
      pstmt.setString(4, city);
      pstmt.setDouble(5, stars);
      pstmt.setDouble(6, longitude);
      pstmt.setDouble(7, latitude);
      pstmt.setString(8, sb.toString());

      pstmt.executeUpdate();
      pstmt.close();

      if (!city_stateMap.containsKey(city)) {
        city_stateMap.put(city, state);
        pw_cityMap.println(String.format("%s,%s", city, state));
      }

      pw_businessLoc.println(String.format("%s,%s,%s", business_id, city, state));
    } catch (ParseException e) {
      System.err.println("[Error]: Fail to parse JSON");
      System.exit(1);
    } catch (SQLException e) {
      System.err.println("[Error]: Insert Error");
      System.exit(1);
    }
  }
}
