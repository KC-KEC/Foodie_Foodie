package edu.nyu.cs.foodie.Loader;


import edu.nyu.cs.foodie.ConnDB.DBConnection;

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
    c = DBConnection.openDB();
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

    DBConnection.closeDB();
  }
}
