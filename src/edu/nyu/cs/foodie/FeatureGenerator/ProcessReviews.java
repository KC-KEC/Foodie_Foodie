package edu.nyu.cs.foodie.FeatureGenerator;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class ProcessReviews {

  private static final MaxentTagger posTagger;
  private static final WordnetStemmer stemmer;
  private static JSONParser jParser;

  static {
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
    String filename =
        "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review.json";

    String output = "src/edu/nyu/cs/foodie/Files/features_reviews.txt";

    try {
      File f = new File(filename);
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;

      Set<String> featuresSet = new HashSet<>();
      int count = 1;
      while ((line = br.readLine()) != null) {
        JSONObject jobj = (JSONObject) jParser.parse(line);
        String user_id = (String) jobj.get("user_id");
        String review = (String) jobj.get("text");
//        getFeatures(review, features);
      }

      br.close();
      fr.close();
    } catch (ParseException e) {
      System.err.println("[Error]: Fail to parse JSON file.");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("[Error]: Fail to write data to transition_file");
      System.exit(1);
    }
  }

  private void getFeatures(String review, Set<String> features) {
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

  private String stem(String word) {
    List<String> stemmed = stemmer.findStems(word, POS.NOUN);
    if (stemmed.isEmpty()) {
      return word;
    }
    else {
      return stemmed.get(0);
    }
  }

  private boolean isNoun(String tag) {
    if (tag == null || tag.isEmpty()) {
      return false;
    }
    return tag.equals("NN") || tag.equals("NNS");
  }

  private boolean isWord(String word) {
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
