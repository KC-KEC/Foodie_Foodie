package edu.nyu.cs.foodie.Recommender;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.nyu.cs.foodie.ConnDB.*;

public class FilterGA {

  Connection c = DBConnection.openDB();
  Set<String> fofs = new HashSet<>();
  Set<String> friends = new HashSet<>();
  Set<String> commonFriends = new HashSet<>();

  public List<String> recommend(String userID) {
    List<String> result = new ArrayList<>();
    if (userID == null || userID.isEmpty()) {
      return result;
    }

    filtering(userID);
    for (String fof : fofs) {
      if (friends.contains(fof)) {
        commonFriends.add(fof);
      }
    }

    ordering(userID);

    return result;

  }

  private void ordering(String userID) {
    int firstIndex = commonFriends.size();
//    int secondIndex = getDensity(commonFriends);
  }

//  private int getDensity(Set<String> set) {
//
//  }

  private void filtering(String userID) {
    try {
      PreparedStatement fstmt = c.prepareStatement("select toid from friends where fromid = ?");
      PreparedStatement fofStmt = c.prepareStatement("select toid from friends where fromid = ?");
      fstmt.setString(1, userID);
      ResultSet rs = fstmt.executeQuery();
      ResultSet fofRS;
      while (rs.next()) {
        String friend = rs.getString(1);
        friends.add(friend);
        fofStmt.setString(1, friend);
        fofRS = fofStmt.executeQuery();
        while (fofRS.next()) {
          fofs.add(fofRS.getString(1));
        }
        fofRS.close();
      }
      rs.close();
      fstmt.close();
      DBConnection.closeDB();
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println("[Error]: Fail to fetch user friends in DB.");
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    FilterGA ga = new FilterGA();
    ga.recommend("rpOyqD_893cqmDAtJLbdog");

    System.out.println(ga.commonFriends.size());
    System.out.println(ga.friends.size());
    System.out.println(ga.fofs.size());
  }
}
