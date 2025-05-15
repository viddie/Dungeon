package puzzles.floor3.algorithms;


import core.utils.Tuple;

import java.util.ArrayList;
import java.util.List;

public class SelectionSort implements SortingStrategy {

  @Override
  public List<Tuple<Integer, Integer>> produceStrategy(int[] arr) {
    List<Tuple<Integer, Integer>> toRet = new ArrayList<>();
    int n = arr.length;

    for (int i = 0; i < n - 1; i++) {
      int minIndex = i;

      // Find the index of the minimum element in the unsorted part
      for (int j = i + 1; j < n; j++) {
        if (arr[j] < arr[minIndex]) {
          minIndex = j;
        }
      }

      // Swap only if minIndex changed
      if (minIndex != i) {
        toRet.add(new Tuple<>(i, minIndex));
        swap(arr, i, minIndex);
      }
    }

    return toRet;
  }
}
