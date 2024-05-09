package finalproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry; // You may (or may not) need it to implement fastSort


    
	/*
	 * This method takes as input an HashMap with values that are Comparable. 
	 * It returns an ArrayList containing all the keys from the map, ordered 
	 * in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n*log(n)), where n is the number 
	 * of pairs in the map. 
	 */
   public static <K, V extends Comparable<V>> ArrayList<K> fastSort(HashMap<K, V> results) {
        ArrayList<K> sortedUrls = new ArrayList<K>(results.keySet());
        quickSort(results, sortedUrls, 0, sortedUrls.size() - 1);
        return sortedUrls;
    }

    /*
     * Quicksort algorithm implementation with median-of-three pivot selection
     * strategy.
     */
    private static <K, V extends Comparable<V>> void quickSort(HashMap<K, V> results, ArrayList<K> urls, int low,
            int high) {
        if (low < high) {
            // Choose pivot using median-of-three strategy
            int pivotIndex = medianOfThree(results, urls, low, high);
            // Partition the array around the pivot
            int partitionIndex = partition(results, urls, low, high, pivotIndex);
            // Recursively sort the sub-arrays
            quickSort(results, urls, low, partitionIndex - 1);
            quickSort(results, urls, partitionIndex + 1, high);
        }
    }

    
	// Selects the pivot using median-of-three strategy and returns its index.
     
    private static <K, V extends Comparable<V>> int medianOfThree(HashMap<K, V> results, ArrayList<K> urls, int low,
            int high) {
        int mid = low + (high - low) / 2;
        V lowValue = results.get(urls.get(low));
        V midValue = results.get(urls.get(mid));
        V highValue = results.get(urls.get(high));

        // Compare values to determine median
        if (lowValue.compareTo(midValue) > 0) {
            if (midValue.compareTo(highValue) > 0) {
                return mid; // is median
            } else if (lowValue.compareTo(highValue) > 0) {
                return high; 
            } else {
                return low; 
            }
        } else {
            if (lowValue.compareTo(highValue) > 0) {
                return low; // is median
            } else if (midValue.compareTo(highValue) > 0) {
                return high; 
            } else {
                return mid; 
            }
        }
    }

    
      // Partitions the array around the pivot and returns the index of the pivot.
    
    private static <K, V extends Comparable<V>> int partition(HashMap<K, V> results, ArrayList<K> urls, int low,
            int high, int pivotIndex) {
        V pivotValue = results.get(urls.get(pivotIndex));
        // Move pivot to the end
        swap(urls, pivotIndex, high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (results.get(urls.get(j)).compareTo(pivotValue) > 0) {
                i++;
                swap(urls, i, j);
            }
        }
        // Move pivot to its final place
        swap(urls, i + 1, high);
        return i + 1;
    }

    
    // swap two elements in an ArrayList.
    
    private static <K> void swap(ArrayList<K> urls, int i, int j) {
        K temp = urls.get(i);
        urls.set(i, urls.get(j));
        urls.set(j, temp);
    }
}
