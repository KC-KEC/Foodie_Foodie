package edu.nyu.cs.foodie.FeatureGenerator;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.nyu.cs.foodie.ConnDB.DBConnection;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ProcessReviews {

  private static final MaxentTagger posTagger;
  private static final WordnetStemmer stemmer;
  private static JSONParser jParser;
  private static Connection c;

  private static String filename =
      "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review.json";

  static {
    c = DBConnection.openDB();
    jParser = new JSONParser();
    posTagger = new MaxentTagger("lib/english-bidirectional-distsim.tagger");
    Dictionary dict = new Dictionary(new File("lib/dict"));
    try {
      dict.open();
    } catch (IOException e) {
      System.err.println("[Error]: Fail to open WordNet dictionary.");
      System.exit(1);
    }
    stemmer = new WordnetStemmer(dict);
  }

  public static void main(String[] args) {
    ProcessReviews.process();
  }

  public static void process() {

    Connection c = DBConnection.openDB();
    Set<String> allFeatures = new HashSet<>();
    try {
      String query = "select userid, reviews from reviews";
      Statement stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      int count = 1;
      while (rs.next()) {
        System.out.println(count++);
        String user = rs.getString(1);
        String review = rs.getString(2);
        getFeatures(review, allFeatures);
      }

      rs.close();
      stmt.close();
    } catch (SQLException e) {
      System.err.println("[Error]: Fail to execute query to fetch reviews table.");
      System.exit(1);
    }

    DBConnection.closeDB();
  }

  private static void getFeatures(String review, Set<String> features) {
    Reader reader = new StringReader(review);
    DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(reader);
    for (List<HasWord> sentence : documentPreprocessor) {
      List<TaggedWord> tSentence = posTagger.tagSentence(sentence);
      for (int i = 0; i < tSentence.size(); i++) {
        TaggedWord tWord = tSentence.get(i);
        if (isNoun(tWord.tag()) && isWord(tWord.word())) {
          String stemmedWord = stem(tWord.word());
          StringBuilder sb = new StringBuilder(stemmedWord);
          while (i + 1 < tSentence.size() && isNoun(tSentence.get(i + 1).tag())) {
            sb.append(" ").append(stem(tSentence.get(i + 1).word()));
            i++;
          }
          features.add(sb.toString());
        }
      }
    }
  }

  private static String stem(String word) {
    List<String> stemmed = stemmer.findStems(word, POS.NOUN);
    if (stemmed.isEmpty()) {
      return word;
    }
    else {
      return stemmed.get(0);
    }
  }

  private static boolean isNoun(String tag) {
    if (tag == null || tag.isEmpty()) {
      return false;
    }
    return tag.equals("NN") || tag.equals("NNS");
  }

  private static boolean isWord(String word) {
    if (word == null || word.isEmpty()) {
      return false;
    }

    for (char c : word.toCharArray()) {
      if (!Character.isLetter(c)) {
        return false;
      }
    }
    return true;
  }
}
