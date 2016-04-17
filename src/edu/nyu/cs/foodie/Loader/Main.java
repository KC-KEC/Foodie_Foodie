package edu.nyu.cs.foodie.Loader;

public class Main {
  public static void main(String[] args) {
    Loader userLoader = new LoadUsers(
        "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_user.json");
    Loader reviewLoader = new LoadReviews(
        "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review.json");

    PreLoad.preload();
    userLoader.load();
    reviewLoader.load();
  }
}
