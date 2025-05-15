package puzzles.floor3.algorithms;


import core.utils.Tuple;

import java.util.ArrayList;
import java.util.List;

public class InsertionSort implements SortingStrategy {

  @Override
  public List<Tuple<Integer, Integer>> produceStrategy(int[] arr) {
    List<Tuple<Integer, Integer>> toRet = new ArrayList<>();
    int n = arr.length;

    for (int i = 1; i < n; i++) {
      int j = i;
      // Move the element at i backward to its correct position by swapping
      while (j > 0 && arr[j] < arr[j - 1]) {
        toRet.add(new Tuple<>(j, j - 1));
        swap(arr, j, j - 1);
        j--;
      }
    }

    return toRet;
  }
}
