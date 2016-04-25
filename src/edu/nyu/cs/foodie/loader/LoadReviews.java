package edu.nyu.cs.foodie.Loader;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoadReviews extends Loader {
  private final JSONParser jparser;
  private Set<String> targetUsers;
  private Map<String, String> businessLoc;
  private Map<String, String> city_state;
  private PrintWriter pw;

  public LoadReviews(String filename, PrintWriter pw) {
    super(filename);
    this.targetUsers = PreLoad.targetUsers;
    this.jparser = new JSONParser();
    this.pw = pw;
    this.city_state = new HashMap<>();
    this.businessLoc = new HashMap<>();

    try {
      File f;
      FileReader fr;
      BufferedReader br;
      String line;

      f = new File("src/edu/nyu/cs/foodie/Files/business_location.txt");
      fr = new FileReader(f);
      br = new BufferedReader(fr);
      while ((line = br.readLine()) != null) {
        String[] sp = line.split(",");
        businessLoc.put(sp[0], sp[1]);
        if (sp.length == 3) {
          city_state.put(sp[1], sp[2]);
        }
      }

      br.close();
      fr.close();
    } catch (FileNotFoundException e) {
      System.err.println("[Error]: 'business_location.txt' not found. Load business data first.");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("[Error]: Fail to read 'business_location.txt'. Load business data first.");
      System.exit(1);
    }
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
      System.err.println("[Error]: Can not create reviews table or table exists.");
    }
  }

  @Override
  void loadLine(String line) {
    try {
      JSONObject jobj = (JSONObject) jparser.parse(line);
      String review_id = (String) jobj.get("review_id");
      String user_id = (String) jobj.get("user_id");
      if (!targetUsers.contains(user_id)) {
        return;
      }
      String business_id = (String) jobj.get("business_id");
      long stars = (Long) jobj.get("stars");
      String date = (String) jobj.get("date");
      String reviews = (String) jobj.get("text");

      String city = businessLoc.get(business_id);
      pw.println(user_id + "," + city);

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
