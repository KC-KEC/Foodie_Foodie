package edu.nyu.cs.foodie.Recommender;

import edu.nyu.cs.foodie.ConnDB.DBConnection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {


  public static void main(String[] args) {

    String userID = "rpOyqD_893cqmDAtJLbdog";
    Connection c = DBConnection.openDB();

    Set<String> filterGA = new FilterGA(c).recommend(userID);
    Set<String> filterContent = FilterSimilarity.recommend(filterGA, userID);

    Set<String> recommendList;
    if (filterContent.size() > 10) {
      recommendList = FilterLoc.recommend(filterContent, userID);
    }
    else {
      recommendList = new HashSet<>(filterContent);
    }

    System.out.println(recommendList.size());
    System.out.println(recommendList);
  }
}
