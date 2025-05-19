package puzzles.floor3;

import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.Tuple;
import core.utils.components.path.SimpleIPath;
import level.utils.ITickable;
import puzzles.floor3.algorithms.*;
import utils.Constants;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class SortingMachine implements ITickable {

  private static final int[] SORT_START = new int[] { 8, 6, 3, 1, 7, 2, 5, 4 };
  private static final int FRAMES_PER_STEP = 45;
  private static final int FRAMES_FOR_JUMP = 30;
  private static final List<SortingStrategy> STRATEGIES = new ArrayList<>();

  static {
    STRATEGIES.add(new SelectionSort()); //0
    STRATEGIES.add(new HeapSort());      //1
    STRATEGIES.add(new QuickSort());     //2
    STRATEGIES.add(new BubbleSort());    //3
    STRATEGIES.add(new BogoSort());      //4
    STRATEGIES.add(new InsertionSort()); //5
  }

  private final Point position;
  private final Dictionary<Integer, Entity> children = new Hashtable<>();

  private int selectedStrategy = 0;
  private State state = State.Waiting;
  private float stepProgress = 0f;
  private float jumpProgress = 0f;
  private int[] currentSort;
  private List<Tuple<Integer, Integer>> remainingSwaps = new ArrayList<>();

  public SortingMachine(Point pos){
    this.position = pos;
    for(int i = 0; i < 8; i++){
      int noteNumber = i+1;
      Entity note = new Entity("sort-obj-"+noteNumber);
      note.add(new DrawComponent(new SimpleIPath("objects/note/note-"+noteNumber+".png")));
      note.add(new PositionComponent(Constants.offset(pos).add(i, 0.5f)));
      children.put(noteNumber, note);
      Game.add(note);
    }
  }

  public void setSelectedStrategy(int selectedStrategy){
    this.selectedStrategy = selectedStrategy;
    currentSort = SORT_START.clone();
    remainingSwaps.clear();
    resetPositions();
    endSorting();
  }

  public void startSorting(){
    if(state != State.Waiting) return;
    if(selectedStrategy >= STRATEGIES.size()) return; //Selected invalid strategy

    if(remainingSwaps.size() == 0){
      currentSort = SORT_START.clone();
      remainingSwaps = STRATEGIES.get(selectedStrategy).produceStrategy(SORT_START.clone()); //Clone twice, as we emulate the sorting swaps through the animation
      stepProgress = 0f;
      jumpProgress = 0f;
      resetPositions();
    }

    if(jumpProgress < 1f){
      state = State.InitialJump;
    } else {
      state = State.Sorting;
    }
  }

  public void endSorting(){
    state = State.Waiting;
  }

  private void resetPositions(){
    for(int i = 0; i < currentSort.length; i++){
      int value = currentSort[i];
      Point pos = Constants.offset(position).add(i, 0.5f);
      children.get(value).fetchOrThrow(PositionComponent.class).position(pos);
    }
  }

  @Override
  public void onTick(boolean isFirstTick) {
    if(state == State.Waiting) return;

    if(state == State.InitialJump){
      jumpProgress += 1f / FRAMES_FOR_JUMP;
      float y = calculateY(jumpProgress);
      for(int i = 0; i < children.size(); i++){
        Entity e = children.get(i+1);
        int index = findIndex(currentSort, i+1);
        Point pos = Constants.offset(position).add(index, 0.5f).add(0, y * 0.25f);
        e.fetchOrThrow(PositionComponent.class).position(pos);
      }
      if(jumpProgress >= 1f){
        jumpProgress = 1f;
        state = State.Sorting;
      }
      return;
    }

    Tuple<Integer, Integer> currentSwap = remainingSwaps.get(0);
    int aIndex = currentSwap.a();
    int bIndex = currentSwap.b();
    int a = currentSort[aIndex];
    int b = currentSort[bIndex];

    Point aInitialPos = Constants.offset(position).add(aIndex, 0.5f);
    Point bInitialPos = Constants.offset(position).add(bIndex, 0.5f);

    //Calculate current positions
    float xDiff = bInitialPos.x - aInitialPos.x;
    float yCalc = calculateY(stepProgress);

    Point currentA = aInitialPos.add(xDiff * stepProgress, yCalc * 0.5f);
    Point currentB = bInitialPos.add(-xDiff * stepProgress, -yCalc * 0.5f);

    children.get(a).fetchOrThrow(PositionComponent.class).position(currentA);
    children.get(b).fetchOrThrow(PositionComponent.class).position(currentB);

    stepProgress += 1f / FRAMES_PER_STEP;
    if(stepProgress >= 1f){
      //Set entities to ending positions
      children.get(a).fetchOrThrow(PositionComponent.class).position(bInitialPos);
      children.get(b).fetchOrThrow(PositionComponent.class).position(aInitialPos);

      //Proceed to next step
      swap(currentSort, aIndex, bIndex);
      remainingSwaps.remove(0);
      stepProgress = 0f;

      //Animation end
      if(remainingSwaps.size() == 0){
        state = State.Waiting;
      }
    }
  }

  /**
   * Calculates a parabola that starts at 0;0, goes to 0.5;1, and ends at 0;0 again
   * @param t
   * @return
   */
  private static float calculateY(float t){
    return -4 * (float)Math.pow(t - 0.5f, 2) + 1;
  }

  private static int findIndex(int[] arr, int value){
    for(int i = 0; i < arr.length; i++){
      if(arr[i] == value) return i;
    }
    return -1;
  }
  private static void swap(int[] arr, int a, int b){
    int temp = arr[a];
    arr[a] = arr[b];
    arr[b] = temp;
  }

  enum State {
    Waiting,
    InitialJump,
    Sorting
  }
}
