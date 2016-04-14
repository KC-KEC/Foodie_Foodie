package edu.nyu.cs.foodie;


import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.List;


public final class ParseJSON {
  private static final String FILENAME =
      "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review.json";
  private final JSONParser jparser;
  private final MaxentTagger tagger;



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
      PrintWriter pw = new PrintWriter("review");
      File f = new File(FILENAME);
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;
      int count = 1;
      while ((line = br.readLine()) != null) {
        System.out.println(count++);
        parseLine(line, pw);
      }

      pw.flush();
      pw.close();
      br.close();
      fr.close();
    } catch (IOException e) {
      System.err.println("IO Exception");
      System.exit(1);
    }
  }

  private void parseLine(String line, PrintWriter pw) {
    try {

      // parse the JSON file
      JSONObject jobj = (JSONObject) jparser.parse(line);
      String businessID = (String) jobj.get("business_id");
      String userID = (String) jobj.get("user_id");
      long stars = (Long) jobj.get("stars");

      String review = (String) jobj.get("text");

      // find All features
      findAllFeature(review, pw);

      // apply TextRank
      textRank(review);

      pw.println(review);
    } catch (ParseException e) {
      System.err.println("JSON parse error!");
      System.exit(1);
    }
  }

  // Find all noun/noun phrase in review, Write to the "noun_word.txt" file
  private void findAllFeature(String review, PrintWriter pw) {
    Reader reader = new StringReader(review);
    DocumentPreprocessor docPreprocessor = new DocumentPreprocessor(reader);
    for (List<HasWord> sentence : docPreprocessor) {
      List<TaggedWord> tSentence = tagger.tagSentence(sentence);
      for (int i = 0; i < tSentence.size(); i++) {
        TaggedWord tWord = tSentence.get(i);
        if (isNoun(tWord.tag())) {
          StringBuilder sb = new StringBuilder(tWord.word());
          while (i + 1 < tSentence.size() && isNoun(tSentence.get(i + 1).tag())) {
            //TODO stem each word
            sb.append(" ").append(tSentence.get(i + 1).word());
            i++;
          }
          pw.println(sb.toString());
        }
        else if (tWord.tag().startsWith("NN")) {
          //TODO stemming

          pw.println(tWord.word());
        }
      }
    }
    pw.flush();
    pw.close();
  }

  private boolean isNoun(String tag) {
    if (tag == null || tag.isEmpty()) {
      return false;
    }
    return tag.equals("NN") || tag.equals("NNS");
  }

  private void textRank(String review) {
    // TODO
  }

}
