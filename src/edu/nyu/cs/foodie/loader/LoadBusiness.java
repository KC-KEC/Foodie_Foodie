package edu.nyu.cs.foodie.loader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class LoadBusiness extends Loader {
  private final JSONParser jparser;

  public LoadBusiness(String filename) {
    super(filename);
    jparser = new JSONParser();
  }

  public static void main(String[] args) {
    Loader loader = new LoadBusiness(
        "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json");
    loader.load();
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
    } catch (ParseException e) {
      System.err.println("[Error]: Fail to parse JSON");
      System.exit(1);
    } catch (SQLException e) {
      System.err.println("[Error]: Insert Error");
      System.exit(1);
    }
  }
}
