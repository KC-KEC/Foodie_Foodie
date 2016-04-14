package edu.nyu.cs.foodie;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class GenerateFrequentFeatures {

  static final double MINSUPPORT = 0.01;

  public static void getFrequentFeatures(String filename) {
    Map<String, Integer> wordFreq = new HashMap<>();
    int lineCount = readFile(filename, wordFreq);
    try {
      PrintWriter pw = new PrintWriter("Freq_Features.txt");
      for (String word : wordFreq.keySet()) {
        if (wordFreq.get(word) > lineCount * MINSUPPORT) {
          pw.println(word);
        }
      }
      pw.flush();
      pw.close();
    } catch (FileNotFoundException e) {
      System.err.println("File not found!");
      System.exit(1);
    }
  }

  private static int readFile(String filename, Map<String, Integer> wordFreq) {
    int lineCount = 0;
    try {
      File f = new File(filename);
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        lineCount++;
        if (!wordFreq.containsKey(line)) {
          wordFreq.put(line, 0);
        }
        wordFreq.put(line, 1);
      }
      br.close();
      fr.close();
    } catch (IOException e) {
      System.err.println("Fail to open noun_word file");
      System.exit(1);
    }

    return lineCount;
  }


}
