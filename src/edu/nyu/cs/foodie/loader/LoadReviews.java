package edu.nyu.cs.foodie.loader;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class LoadReviews extends Loader {
  private final JSONParser jparser;

  public LoadReviews(String filename) {
    super(filename);
    jparser = new JSONParser();
  }

  public static void main(String[] args) {
    Loader loader = new LoadReviews(
        "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review.json");
    loader.load();
  }

  @Override
  void createTable() {
    try {
      Statement stmt = c.createStatement();
      String reviewSQL = "CREATE TABLE REVIEWS " +
          "(ID TEXT PRIMARY KEY NOT NULL," +
          " USERID TEXT NOT NULL," +
          " BUSINESSID TEXT NOT NULL," +
          " STARS DOUBLE PRECISION NOT NULL," +
          " DATE TEXT NOT NULL," +
          " REVIEWS TEXT NOT NULL)";
      stmt.executeUpdate(reviewSQL);
      stmt.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Can not create table or table exists.");
    }
  }

  @Override
  void loadLine(String line) {
    try {
      JSONObject jobj = (JSONObject) jparser.parse(line);
      String review_id = (String) jobj.get("review_id");
      String user_id = (String) jobj.get("user_id");
      String business_id = (String) jobj.get("business_id");
      long stars = (Long) jobj.get("stars");
      String date = (String) jobj.get("date");
      String reviews = (String) jobj.get("text");

      String insertReviews = "INSERT INTO REVIEWS VALUES (?,?,?,?,?,?)";
      PreparedStatement pstmt = c.prepareStatement(insertReviews);
      pstmt.setString(1, review_id);
      pstmt.setString(2, user_id);
      pstmt.setString(3, business_id);
      pstmt.setLong(4, stars);
      pstmt.setString(5, date);
      pstmt.setString(6, reviews);

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
