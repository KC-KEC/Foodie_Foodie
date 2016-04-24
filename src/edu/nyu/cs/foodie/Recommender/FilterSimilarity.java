package edu.nyu.cs.foodie.Recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class FilterSimilarity {

  private List<String> recommendList;

  public void filter(List<String> recommendList, String userID) {
    try {
      File f = new File("user_features.txt");
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;

      while ((line = br.readLine()) != null) {

      }

      br.close();
      fr.close();
    } catch (IOException e) {
      System.err.println("[Error]: Fail to open 'user_features.txt' file.");
      System.exit(1);
    }
  }
}
