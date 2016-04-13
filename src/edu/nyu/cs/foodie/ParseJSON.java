package edu.nyu.cs.foodie;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.List;

public final class ParseJSON {
  private static final String FILENAME =
      "yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review.json";
  private final JSONParser jparser;
  private final MaxentTagger tagger;
  List<String> noun;

  public ParseJSON() {
    this.jparser = new JSONParser();
    this.tagger = new MaxentTagger("lib/english-bidirectional-distsim.tagger");
  }

  // main
  public static void main(String[] args) {
    ParseJSON obj = new ParseJSON();
    obj.parseReview();
  }

  // parse and analyze the customer review
  public void parseReview() {
    try {
      File f = new File(FILENAME);
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;

      while ((line = br.readLine()) != null) {
        parseLine(line);
      }

      br.close();
      fr.close();
    } catch (IOException e) {
      System.out.println("IO Exception");
      System.exit(1);
    }
  }

  private void parseLine(String line) {
    try {
      // parse the JSON file
      JSONObject jobj = (JSONObject) jparser.parse(line);
      String businessID = (String) jobj.get("business_id");
      String userID = (String) jobj.get("user_id");
      long stars = (Long) jobj.get("stars");
      String review = (String) jobj.get("text");

      // summarizeReview(review), Write to disk
      Reader reader = new StringReader(review);
      DocumentPreprocessor docPreprocessor = new DocumentPreprocessor(reader);
      for (List<HasWord> sentence : docPreprocessor) {
        List<TaggedWord> tSentence = tagger.tagSentence(sentence);
        for (TaggedWord tWord : tSentence) {
          if (tWord.tag().startsWith("NN")) {
            String word = tWord.word();
            Tree parse = new Tree

          }
        }
      }
    } catch (ParseException e) {
      System.out.println("File not found!");
      System.exit(1);
    }
  }

}
