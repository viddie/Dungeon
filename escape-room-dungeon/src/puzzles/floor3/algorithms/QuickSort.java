package puzzles.floor3.algorithms;


import core.utils.Tuple;

import java.util.ArrayList;
import java.util.List;

public class QuickSort implements SortingStrategy {

  @Override
  public List<Tuple<Integer, Integer>> produceStrategy(int[] arr) {
    List<Tuple<Integer, Integer>> toRet = new ArrayList<>();
    quickSort(arr, 0, arr.length - 1, toRet);
    return toRet;
  }

  private void quickSort(int[] arr, int low, int high, List<Tuple<Integer, Integer>> toRet) {
    if (low < high) {
      int pivotIndex = partition(arr, low, high, toRet);
      quickSort(arr, low, pivotIndex - 1, toRet);
      quickSort(arr, pivotIndex + 1, high, toRet);
    }
  }

  private int partition(int[] arr, int low, int high, List<Tuple<Integer, Integer>> toRet) {
    int pivot = arr[high];  // choose the last element as pivot
    int i = low - 1;

    for (int j = low; j < high; j++) {
      if (arr[j] < pivot) {
        i++;
        if (i != j) {
          toRet.add(new Tuple<>(i, j));
          swap(arr, i, j);
        }
      }
    }

    if (i + 1 != high) {
      toRet.add(new Tuple<>(i + 1, high));
      swap(arr, i + 1, high);
    }

    return i + 1;
  }
}
