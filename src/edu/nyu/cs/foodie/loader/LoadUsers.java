package edu.nyu.cs.foodie.Loader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.Set;

public final class LoadUsers extends Loader {
  private final JSONParser jparser;
  private Set<String> targetUser;

  public LoadUsers(String filename) {
    super(filename);
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

      JSONArray jArray = (JSONArray) jobj.get("friends");
      String friendID;
      String insertFriends = "INSERT INTO FRIENDS (FROMID, TOID) VALUES (?, ?)";
      pstmt = c.prepareStatement(insertFriends);
      for (Object obj : jArray) {
        friendID = (String) obj;
        if (targetUser.contains(friendID)) {
          pstmt.setString(1, userID);
          pstmt.setString(2, friendID);
          pstmt.addBatch();
        }
      }
      pstmt.executeBatch();
      pstmt.close();
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

      stmt.executeUpdate(userSQL);
      stmt.executeUpdate(relationSQL);
      stmt.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Can not create table or table exists.");
    }
  }

  public static void main(String[] args) {
    Loader userLoader = new LoadUsers(
        "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_user.json");
    PreLoad.preload();
    userLoader.load();
  }
}
