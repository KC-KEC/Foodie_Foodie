package edu.nyu.cs.foodie.Recommender;

import java.util.ArrayList;
import java.util.List;

public class Main {


  public static void main(String[] args) {

    String userID = "rpOyqD_893cqmDAtJLbdog";

    List<String> filterGA = FilterGA.recommend(userID);
    List<String> filterContent = FilterSimilarity.filter(filterGA, userID);

    List<String> recommendList;
    if (filterContent.size() > 10) {
      recommendList = FilterLoc.fileterLoc(filterContent, userID);
    }
    else {
      recommendList = new ArrayList<>(filterContent);
    }

    System.out.println(recommendList.size());
    System.out.println(recommendList);
  }
}
