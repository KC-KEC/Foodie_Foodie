package edu.nyu.cs.foodie.loader;

import edu.nyu.cs.foodie.loader.Loader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;

public final class LoadUsers extends Loader {
  private final JSONParser jparser;

  public LoadUsers(String filename) {
    super(filename);
    jparser = new JSONParser();
  }

  @Override
  void loadLine(String line) {
    try {
      JSONObject jobj = (JSONObject) jparser.parse(line);
      String userID = (String) jobj.get("user_id");
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
        pstmt.setString(1, userID);
        pstmt.setString(2, friendID);
        pstmt.addBatch();
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
}
