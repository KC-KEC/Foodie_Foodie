package edu.nyu.cs.foodie.Summarization;

import edu.nyu.cs.foodie.util.ValueComparator;

import java.util.*;

public class TextRank {

  private static double d = 0.85;
  private static Comparator<Map.Entry<Integer, Double>> comparator = new ValueComparator<>();
  private static int top = 2;

  public static List<String> textRank(String text) {
    if (text == null || text.isEmpty()) {
      throw new IllegalArgumentException();
    }

    String[] sentences = text.split("\\.");

    List<String> sentenceList = new ArrayList<>();
    for (int i = 0; i < sentences.length; i++) {
      sentences[i] = sentences[i].trim();
      if (!sentences[i].isEmpty()) {
        sentenceList.add(sentences[i]);
      }
    }

    int n = sentenceList.size();

    double[][] graph = buildGraph(sentenceList);

    double[] scores = new double[n];
    Arrays.fill(scores, 1 / n);

    double[] newScores = rank(graph, scores);
    while (!isConverge(scores, newScores)) {
      System.arraycopy(newScores, 0, scores, 0, scores.length);
      newScores = rank(graph, scores);
    }

    List<Map.Entry<Integer, Double>> sortedScore = new ArrayList<>();
    for (int i = 0; i < newScores.length; i++) {
      sortedScore.add(new AbstractMap.SimpleEntry<>(i, newScores[i]));
    }
    Collections.sort(sortedScore, comparator);

    List<String> result = new ArrayList<>();
    for (int i = 0; i < n && i < top; i++) {
      result.add(sentenceList.get(sortedScore.get(i).getKey()));
    }

    return result;
  }

  private static boolean isConverge(double[] scores, double[] newScores) {
    for (int i = 0; i < scores.length; i++) {
      if (scores[i] != newScores[i]) {
        return false;
      }
    }
    return true;
  }

  private static double[] rank(double[][] graph, double[] scores) {
    int n = scores.length;
    double[] newScores = new double[n];

    for (int i = 0; i < n; i++) {
      double sumIn = 0.0;
      for (int j = 0; j < n; j++) {
        if (graph[j][i] != 0.0) {
          double sumOut = 0.0;
          for (int k = 0; k < n; k++) {
            sumOut += graph[j][k];
          }
          sumIn += graph[j][i] / sumOut;
        }
      }

      newScores[i] = (1 - d) + d * sumIn;
    }

    return newScores;
  }

  private static double[][] buildGraph(List<String> sentenceList) {
    int n = sentenceList.size();
    double[][] graph = new double[n][n];
    Map<Integer, String[]> lookup = new HashMap<>();

    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        double similarScore = similarity(sentenceList, i, j, lookup);
        if (similarScore != 0.0) {
          graph[i][j] = similarScore;
          graph[j][i] = similarScore;
        }
      }
    }

    return graph;
  }

  private static double similarity(List<String> sentenceList, int i, int j, Map<Integer, String[]>
      lookup) {

    String[] sp1 = lookup.containsKey(i) ? lookup.get(i) : sentenceList.get(i).split
        ("[^a-zA-Z0-9]+");
    String[] sp2 = lookup.containsKey(j) ? lookup.get(j) : sentenceList.get(j).split
        ("[^a-zA-Z0-9]+");

    Set<String> set1 = new HashSet<>(Arrays.asList(sp1));
    Set<String> set2 = new HashSet<>(Arrays.asList(sp2));
    set1.retainAll(set2);

    double numerator = set1.size();
    double denominator = Math.log(sp1.length) + Math.log(sp2.length);

    if (denominator == 0.0) {
      return set1.size();
    }
    return numerator / denominator;
  }

  public static void main(String[] args) {
    List<String> result = textRank("Now this will interest a few clubs: Juventus striker Alvaro Morata is uncertain about his future and open to the possibility of a change of scene, the Sun reports.\n" +
        "\n" +
        "Juventus were crowned Serie A champions for the fifth time in a row on Monday, but Spain international Morata is reported to be weighing up his options after spells out of the starting lineup in a season that has seen him suffer one or two barren runs.\n" +
        "\n" +
        "That's likely to raise an eyebrow at his former club Real Madrid -- who included a buy-back clause of around £20 million in the deal that took him to Turin -- as well as at Arsenal and Manchester City.\n" +
        "\n" +
        "Earlier this month, reports suggested Morata was \"at the top of Arsene Wenger's wish-list,\" and one reported scenario is that Real, aware of that interest, could try to buy him back before selling him on to the Gunners for a tidy profit.\n" +
        "\n" +
        "The striker has also been touted as a target for incoming City boss Pep Guardiola, who was reported to have been very impressed by him during this season's Champions League clash between Juve and his current side Bayern Munich.\n" +
        "\n" +
        "Falcao 'has no future at Chelsea'\n" +
        "Recent reports have suggested a Radamel Falcao move to Roma could soon be in the offing -- and it might happen sooner than anyone thought.\n" +
        "\n" +
        "The Mirror reports that the Colombian, who is on loan at Chelsea but still belongs to Monaco, \"has been given the green light to try to get a move elsewhere\" after the Blues \"made it clear they are not interested in extending his stay.\"\n" +
        "\n" +
        "The striker reportedly wanted to remain in London as the new Antonio Conte era got under way, but now -- and with Monaco eager to sort out a permanent transfer away, a Rome switch appears ever likelier for the 30-year-old.\n" +
        "\n" +
        "Meanwhile, France Football reports that Malian teenager Amadou Haidara is a target for Chelsea as well as several French clubs.\n" +
        "\n" +
        "The 18-year-old defensive midfielder was part of the Mali squad that finished second at last year's Under-17 World Cup and is thought to be keen on a move to Europe.\n" +
        "\n" +
        "France Football suggests Chelsea could opt to sign him before sending him out on loan to gain experience.\n" +
        "\n" +
        "Woodward working towards Sanches swoop\n" +
        "Well, this might move on quickly. Recent reports warned that Manchester United faced disappointment in their pursuit of Renato Sanches with Paris Saint-Germain, Manchester City and Real Madrid all lurking in the wings.\n" +
        "\n" +
        "But not a bit of it, says the Manchester Evening News. Old Trafford executive vice chairman Ed Woodward has been negotiating with Benfica, and his labours mean United are \"favourites to land the 18-year-old for a potential final fee of around £40 million.\"\n" +
        "\n" +
        "It adds that United are well aware of the potential competition for Sanches -- a target for one Jose Mourinho before his departure from Chelsea -- and are prepared to trigger the £35 million release clause in the midfielder's contract to get things moving swiftly.");
  }

}
