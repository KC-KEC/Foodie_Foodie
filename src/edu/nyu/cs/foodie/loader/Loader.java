package edu.nyu.cs.foodie.loader;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Loader {
  Connection c;
  String filename;

  public Loader(String filename) {
    this.filename = filename;
  }

  abstract void createTable();

  abstract void loadLine(String line);

  void load() {
    openDB();
    createTable();
    try {
      File f = new File(filename);
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        loadLine(line);
      }
      br.close();
      fr.close();
    } catch (IOException e) {
      System.err.println("[Error]: Can not open file " + filename);
      System.exit(1);
    }

    closeDB();
  }

  void openDB() {
    try {
      Class.forName("org.postgresql.Driver");
      this.c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/foodiedb", "Kyle", "");
    } catch (SQLException e) {
      System.err.println("[Error]: Can not connect to database.");
      System.exit(1);
    } catch (ClassNotFoundException e) {
      System.err.println("[Error]: org.postgresql.Driver not found.");
      System.exit(1);
    }
  }

  void closeDB() {
    try {
      this.c.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Can not close database");
    }
  }
}
