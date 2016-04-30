package edu.nyu.cs.foodie.Loader;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PreLoad {

  static Set<String> targetUsers = new HashSet<>();
  static final int REVIEWSCOUNT = 100;

  public static void preload() {
    JSONParser jParser = new JSONParser();
    String filename =
        "/Users/Kyle/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review.json";
    try {
      File f = new File(filename);
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;

      Map<String, Integer> map = new HashMap<>();
      while ((line = br.readLine()) != null) {
        JSONObject jobj = (JSONObject) jParser.parse(line);
        String user_id = (String) jobj.get("user_id");
        if (!map.containsKey(user_id)) {
          map.put(user_id, 0);
        }
        map.put(user_id, map.get(user_id) + 1);
      }

      for (String user : map.keySet()) {
        if (map.get(user) >= REVIEWSCOUNT) {
          targetUsers.add(user);
        }
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

}
