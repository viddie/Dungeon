package puzzles.floor3.algorithms;

import core.utils.Tuple;

import java.util.ArrayList;
import java.util.List;

public class BubbleSort implements SortingStrategy {
  @Override
  public List<Tuple<Integer, Integer>> produceStrategy(int[] arr) {
    List<Tuple<Integer, Integer>> toRet = new ArrayList<>();

    for(int i = arr.length; i > 1; i--){
      for(int j = 0; j < i-1; j++){
        if(arr[j] > arr[j+1]){
          toRet.add(new Tuple<>(j, j + 1));
          swap(arr, j, j+1);
        }
      }
    }

    return toRet;
  }
}
