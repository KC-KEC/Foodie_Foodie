package edu.nyu.cs.foodie.ConnDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
  static Connection c;

  public static Connection openDB() {
    try {
      Class.forName("org.postgresql.Driver");
      c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/foodiedb", "Kyle", "");
      return c;
    } catch (SQLException e) {
      System.err.println("[Error]: Can not connect to database.");
      System.exit(1);
    } catch (ClassNotFoundException e) {
      System.err.println("[Error]: org.postgresql.Driver not found.");
      System.exit(1);
    }

    return null;
  }

  public static void closeDB() {
    try {
      c.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Can not close database");
    }
  }
}
