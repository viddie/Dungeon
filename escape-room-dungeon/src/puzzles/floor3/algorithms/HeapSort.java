package puzzles.floor3.algorithms;

import core.utils.Tuple;

import java.util.ArrayList;
import java.util.List;


public class HeapSort implements SortingStrategy {

  @Override
  public List<Tuple<Integer, Integer>> produceStrategy(int[] arr) {
    List<Tuple<Integer, Integer>> toRet = new ArrayList<>();
    int n = arr.length;

    // Build max heap
    for (int i = n / 2 - 1; i >= 0; i--) {
      heapify(arr, n, i, toRet);
    }

    // Extract elements from heap one by one
    for (int i = n - 1; i > 0; i--) {
      toRet.add(new Tuple<>(0, i));  // Record indices being swapped
      swap(arr, 0, i);               // Move current root to end
      heapify(arr, i, 0, toRet);     // Heapify reduced heap (size i)
    }

    return toRet;
  }

  private void heapify(int[] arr, int heapSize, int rootIndex, List<Tuple<Integer, Integer>> toRet) {
    int largest = rootIndex;
    int left = 2 * rootIndex + 1;
    int right = 2 * rootIndex + 2;

    // Compare left child
    if (left < heapSize && arr[left] > arr[largest]) {
      largest = left;
    }

    // Compare right child
    if (right < heapSize && arr[right] > arr[largest]) {
      largest = right;
    }

    // If root is not largest, swap with largest and continue heapifying
    if (largest != rootIndex) {
      toRet.add(new Tuple<>(rootIndex, largest)); // Record the swap indices
      swap(arr, rootIndex, largest);
      heapify(arr, heapSize, largest, toRet);     // Recursive call for affected subtree
    }
  }
}
