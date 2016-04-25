package edu.nyu.cs.foodie.Loader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * AfterLoad class is used to find indirect relations from data that already loaded.
 * Like, user location relation.
 */
public class AfterLoad {

  private static Map<String, Map<String, Integer>> cityCount = new HashMap<>();

  public static void loadUserLocation() {
    try {
      File f = new File("src/edu/nyu/cs/foodie/Files/userLocCount.txt");
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        String[] sp = line.split(",");
        if (sp.length < 2) {
          continue;
        }
        cityCount.putIfAbsent(sp[0], new HashMap<>());
        cityCount.get(sp[0]).putIfAbsent(sp[1], 0);

        int currCount = cityCount.get(sp[0]).get(sp[1]);
        cityCount.get(sp[0]).put(sp[1], currCount + 1);
      }

      br.close();
      fr.close();

      f.delete();
    } catch (FileNotFoundException e) {
      System.err.println("[Error]: 'userLocCount.txt' not found, load business data, then reviews" +
          " data first.");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("[Error]: IOException");
      System.exit(1);
    }

    try {
      PrintWriter pw = new PrintWriter("src/edu/nyu/cs/foodie/Files/userLocation.txt");

      for (String user : cityCount.keySet()) {
        String maxCity = null;
        int maxCount = 0;
        for (String city : cityCount.get(user).keySet()) {
          if (cityCount.get(user).get(city) > maxCount) {
            maxCount = cityCount.get(user).get(city);
            maxCity = city;
          }
        }
        pw.println(user + "," + maxCity);
      }

      pw.flush();
      pw.close();
    } catch (IOException e) {
      System.err.println("[Error]: Fail to open 'userLocation.txt'");
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    AfterLoad.loadUserLocation();
  }
}
