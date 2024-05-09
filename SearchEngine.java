package finalproject;

import java.util.HashMap;
import java.util.ArrayList;

public class SearchEngine {
	public HashMap<String, ArrayList<String> > wordIndex;   // this will contain a set of pairs (String, ArrayList of Strings)	
	public MyWebGraph internet;
	public XmlParser parser;
    private final double dampingFactor = 0.5;
	

	public SearchEngine(String filename) throws Exception{
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new XmlParser(filename);
	}
	
	/* 
	 * This does an exploration of the web, starting at the given url.
	 * For each new page seen, it updates the wordIndex, the web graph,
	 * and the set of visited vertices.
	 * 
	 */

public void crawlAndIndex(String url) throws Exception {
        
        if (!internet.getVisited(url)){
            
            internet.setVisited(url, true);
            String rnUrl = url;
            internet.addVertex(rnUrl);

            ArrayList<String> words = parser.getContent(rnUrl);

            for (String word : words) {
                String ncsWord = word.toLowerCase();
                wordIndex.computeIfAbsent(ncsWord, k -> new ArrayList<>());
                
                if (!ncsWord.isEmpty()){
                    
                    if (!wordIndex.get(ncsWord).contains(rnUrl)) {
                        wordIndex.get(ncsWord).add(rnUrl);
                    }
                    
                    if (!wordIndex.containsKey(ncsWord)){
                        wordIndex.put(ncsWord, new ArrayList<>());
                    }


                }
            }


            ArrayList<String> neighbor = parser.getLinks(rnUrl);

            for (String adjacent : neighbor) {
                internet.addEdge(rnUrl, adjacent);
                crawlAndIndex(adjacent);
                if (internet.addEdge(url, adjacent)) {
                    crawlAndIndex(adjacent);
                }
            }
        }
        return;
    }
        
	
	
	/* 
	 * This computes the pageRanks for every vertex in the web graph.
	 * It will only be called after the graph has been constructed using
	 * crawlAndIndex(). 
	 * To implement this method, refer to the algorithm described in the 
	 * assignment pdf. 
	 * 
	 */

public void assignPageRanks(double epsilon) {
    ArrayList<String> vertices = internet.getVertices();
    ArrayList<Double> old = new ArrayList<>();
    ArrayList<Double> newRanks = new ArrayList<>();

    for (int i = 0; i < vertices.size(); i++) {
        old.add(1.0);
    }
    double dampingFactor = 0.5;
    // Iteratively compute ranks until convergence
    while (true) {
        for (int i = 0; i < vertices.size(); i++) {
            String vertex = vertices.get(i);
            ArrayList<String> inLinks = internet.getEdgesInto(vertex);
            double sum = 0.0;
            for (String inLink : inLinks) {
                int outDegree = internet.getOutDegree(inLink);
                sum += old.get(vertices.indexOf(inLink)) / outDegree;
            }
            double newRank = (1 - dampingFactor) + dampingFactor * sum;
            newRanks.add(newRank);
        }
        // Check convergence
        boolean converged = true;
        for (int i = 0; i < vertices.size(); i++) {
            double diff = Math.abs(newRanks.get(i) - old.get(i));
            if (diff >= epsilon) {
                converged = false;
                break;
            }
        }
        if (converged) {
            for (int i = 0; i < vertices.size(); i++) {
                internet.setPageRank(vertices.get(i), newRanks.get(i));
            }
            break;
        }
        old = newRanks;
        newRanks = new ArrayList<>();
    }
}
	
	/*
	 * The method takes as input an ArrayList<String> representing the urls in the web graph 
	 * and returns an ArrayList<double> representing the newly computed ranks for those urls. 
	 * Note that the double in the output list is matched to the url in the input list using 
	 * their position in the list.
	 * 
	 */

public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
    ArrayList<Double> newRanks = new ArrayList<>(vertices.size());
    double baseRank = 1.0; // Set base rank to 1

    for (String vertice : vertices) {
        double rankSum = 0;
        for (String w : internet.getEdgesInto(vertice)) {
            rankSum += internet.getPageRank(w) / internet.getOutDegree(w);
        }
        newRanks.add(baseRank * (1 - 0.5) + 0.5 * rankSum);
    }
    return newRanks;
}
    /* Returns a list of urls containing the query, ordered by rank
	 * Returns an empty list if no web site contains the query.
	 * 
	 */

public ArrayList<String> getResults(String query) {
        String ncsQuery = query.toLowerCase();
        if (wordIndex.containsKey(ncsQuery)) {
            ArrayList<String> urlContainWord = wordIndex.get(ncsQuery);


            HashMap<String, Double> urlRank = new HashMap<>();
            for (String url : urlContainWord) {
                double pageRank = internet.getPageRank(url);
                urlRank.put(url, pageRank);
            }

            ArrayList<String> sortUrl = Sorting.fastSort(urlRank);
            return sortUrl;
        }
        return new ArrayList<>(); 
    }

}