package edu.nyu.cs.foodie.Summarization;

import edu.nyu.cs.foodie.ConnDB.DBConnection;

import java.sql.*;
import java.util.List;

public class Summarize {

  private static Connection c;

  public static void summarizeAll() {
    c = DBConnection.openDB();

    createTable();

    try {
      Statement stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery("select id, reviews from reviews");

      String insertSQL = "insert into reivew_summary values(?, ?)";
      PreparedStatement pstmt = c.prepareStatement(insertSQL);
      StringBuilder sb =new StringBuilder();
      int count = 0;
      while (rs.next()) {
        System.out.println(count++);
        String id = rs.getString(1);
        String review = rs.getString(2);
        List<String> summary = TextRank.textRank(review);
        for (String sentence : summary) {
          sb.append(sentence).append(". ");
        }
        pstmt.setString(1, id);
        pstmt.setString(2, sb.toString());
        pstmt.addBatch();
        sb.setLength(0);
      }

      pstmt.executeBatch();
      pstmt.close();
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Fail to execute query.");
      System.exit(1);
    }

    DBConnection.closeDB();
  }

  private static void createTable() {
    try {
      Statement stmt = c.createStatement();
      String summarySQL = "CREATE TABLE IF NOT EXISTS REVIEW_SUMMARY " +
          "(ID TEXT PRIMARY KEY NOT NULL REFERENCES REVIEWS(ID)," +
          " SUMMARY TEXT NOT NULL)";

      stmt.executeUpdate(summarySQL);
      stmt.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Fail to create review_summary table");
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    summarizeAll();
  }
}
