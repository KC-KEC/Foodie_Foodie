package edu.nyu.cs.foodie.Loader;

import java.io.IOException;
import java.io.PrintWriter;

public class Main {
  public static void main(String[] args) {

    System.out.println("Pre Loading ... ");
    PreLoad.preload();

    /* load user data */
    System.out.println("Loading user data ... ");
    Loader userLoader = new LoadUsers(
        "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_user.json");
    userLoader.load();

    /* load business data */
    System.out.println("Loading business data ... ");
    try {
      PrintWriter pw1 = new PrintWriter("src/edu/nyu/cs/foodie/Files/city_state.txt");
      PrintWriter pw2 = new PrintWriter("src/edu/nyu/cs/foodie/Files/business_location.txt");
      Loader businessLoader = new LoadBusiness(
          "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"
          , pw1
          , pw2
      );
      businessLoader.load();

      pw1.flush();
      pw2.flush();
      pw1.close();
      pw2.close();
    } catch (IOException e) {
      System.err.println("[Error]: Fail to open 'city_state' or 'business_location' file.");
      System.exit(1);
    }

    /* load reviews data */
    System.out.println("Loading reviews data ... ");
    try {
      PrintWriter userLocWriter = new PrintWriter("src/edu/nyu/cs/foodie/Files/userLocCount.txt");

      Loader reviewLoader = new LoadReviews(
          "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review.json"
          , userLocWriter
      );

      reviewLoader.load();

      userLocWriter.flush();
      userLocWriter.close();
    } catch (IOException e) {
      System.err.println("[Error]: Fail to open 'user_location' file.");
      System.exit(1);
    }

    /* load user location data */
    System.out.println("After Loading ... ");
    AfterLoad.loadUserLocation();

  }
}
