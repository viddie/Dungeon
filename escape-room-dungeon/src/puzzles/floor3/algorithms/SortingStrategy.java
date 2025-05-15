package puzzles.floor3.algorithms;

import core.utils.Tuple;

import java.util.List;

public interface SortingStrategy {

  List<Tuple<Integer, Integer>> produceStrategy(int[] arr);

  default void swap(int[] arr, int indexA, int indexB){
    int temp = arr[indexA];
    arr[indexA] = arr[indexB];
    arr[indexB] = temp;
  }

}
