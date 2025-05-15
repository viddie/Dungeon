package puzzles.floor3.algorithms;

import core.utils.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BogoSort implements SortingStrategy {

  private final Random random = new Random();
  private static final int MAX_SWAPS = 10_000;

  @Override
  public List<Tuple<Integer, Integer>> produceStrategy(int[] arr) {
    List<Tuple<Integer, Integer>> toRet = new ArrayList<>();
    int swapCount = 0;

    while (!isSorted(arr) && swapCount < MAX_SWAPS) {
      // Fisher-Yates shuffle with logging
      for (int i = arr.length - 1; i > 0; i--) {
        int j = random.nextInt(i + 1);
        if (i != j) {
          toRet.add(new Tuple<>(i, j));
          swap(arr, i, j);
          swapCount++;
          if (swapCount >= MAX_SWAPS) {
            break; // early exit from inner loop
          }
        }
      }
    }

    return toRet;
  }

  private boolean isSorted(int[] arr) {
    for (int i = 1; i < arr.length; i++) {
      if (arr[i - 1] > arr[i]) return false;
    }
    return true;
  }
}
