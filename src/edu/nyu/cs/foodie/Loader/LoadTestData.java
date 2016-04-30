package edu.nyu.cs.foodie.Loader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.Set;

public final class LoadTestData extends Loader {
  private final JSONParser jparser;
  private Set<String> targetUser;
  private final static double TESTRATE = 0.7;

  public LoadTestData(String filename) {
    super(filename);
    if (PreLoad.targetUsers.isEmpty()) {
      System.err.println("PreLoad first!");
      System.exit(1);
    }
    this.targetUser = PreLoad.targetUsers;
    this.jparser = new JSONParser();
  }

  @Override
  void loadLine(String line) {
    try {
      JSONObject jobj = (JSONObject) jparser.parse(line);
      String userID = (String) jobj.get("user_id");
      if (!targetUser.contains(userID)) {
        return;
      }
      String userName = (String) jobj.get("name");
      String insertUser = "INSERT INTO USERS (ID, NAME) VALUES (?, ?)";
      PreparedStatement pstmt = c.prepareStatement(insertUser);
      pstmt.setString(1, userID);
      pstmt.setString(2, userName);
      pstmt.executeUpdate();
      pstmt.close();

      JSONArray jArray = (JSONArray) jobj.get("friends");
      String friendID;
      String insertFriends = "INSERT INTO FRIENDS (FROMID, TOID) VALUES (?, ?)";
      String insertTest = "INSERT INTO TEST_FRIENDS (FROMID, TOID) VALUES (?, ?)";

      PreparedStatement pstmt1 = c.prepareStatement(insertFriends);
      PreparedStatement pstmt2 = c.prepareStatement(insertTest);

      for (int i = 0; i < jArray.size(); i++) {
        Object obj = jArray.get(i);
        friendID = (String) obj;
        if (targetUser.contains(friendID)) {
          if (i <= jArray.size() * TESTRATE) {
            pstmt1.setString(1, userID);
            pstmt1.setString(2, friendID);
            pstmt1.addBatch();
          }
          else {
            pstmt2.setString(1, userID);
            pstmt2.setString(2, friendID);
            pstmt2.addBatch();
          }
        }
      }
      pstmt1.executeBatch();
      pstmt2.executeBatch();
      pstmt1.close();
      pstmt2.close();
    } catch (ParseException e) {
      System.err.println("[Error]: Fail to parse JSON");
      System.exit(1);
    } catch (SQLException e) {
      System.err.println("[Error]: Insert Error");
      System.exit(1);
    }
  }

  @Override
  void createTable() {
    try {
      Statement stmt = c.createStatement();
      String userSQL = "CREATE TABLE USERS " +
          "(ID TEXT PRIMARY KEY NOT NULL," +
          " NAME TEXT NOT NULL)";
      String relationSQL = "CREATE TABLE FRIENDS " +
          "(FROMID TEXT NOT NULL," +
          " TOID TEXT NOT NULL," +
          " PRIMARY KEY(FROMID, TOID))";
      String evaluateSQL = "CREATE TABLE TEST_FRIENDS " +
          "(FROMID TEXT NOT NULL," +
          " TOID TEXT NOT NULL," +
          " PRIMARY KEY(FROMID, TOID))";

      stmt.executeUpdate(userSQL);
      stmt.executeUpdate(relationSQL);
      stmt.executeUpdate(evaluateSQL);
      stmt.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Can not create table or table exists.");
    }
  }

  public static void main(String[] args) {
    PreLoad.preload();
    Loader testLoader = new LoadTestData(
        "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_user.json");
    testLoader.load();
  }
}
