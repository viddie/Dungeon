package systems;

import core.Game;
import core.System;
import core.level.elements.ILevel;
import level.EscapeRoomLevel;
import level.utils.ITickable;
import puzzles.simpleLevers.SimpleLeverPuzzle;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The LevelTickSystem is responsible for ticking the current level. It checks if the current level
 * has changed and calls the onTick method of the current level if it implements the ITickable
 * interface.
 *
 * @see ITickable
 * @see EscapeRoomLevel DevDungeonLevel
 */
public class TickableSystem extends System {

  public static final int TIMING_FIRST = Integer.MIN_VALUE;
  public static final int TIMING_EARLY = -1000;
  public static final int TIMING_NORMAL = 0;
  public static final int TIMING_LATE = 1000;
  public static final int TIMING_LAST = Integer.MAX_VALUE;

  private static final Map<ITickable, Integer> tickables = new HashMap();
  private static final Map<ITickable, Integer> levelTickables = new HashMap();
  private static final Map<ITickable, Integer> addTickables = new HashMap();
  private static final Map<ITickable, Integer> addLevelTickables = new HashMap();
  private static final Map<ITickable, Boolean> hasTicked = new HashMap<>();

  /**
   * Registers a tickable in the current level.
   * @param tickable The tickable to add
   */
  public static void registerInLevel(ITickable tickable, int timing){
    addLevelTickables.put(tickable, timing);
    hasTicked.put(tickable, false);
  }
  public static void registerInLevel(ITickable tickable){
    registerInLevel(tickable, TIMING_NORMAL);
  }

  /**
   * Registers a tickable that also exists outside of the current level.
   * @param tickable The tickable to add
   */
  public static void register(ITickable tickable, int timing){
    addTickables.put(tickable, timing);
    hasTicked.put(tickable, false);
  }
  public static void register(ITickable tickable){
    register(tickable, TIMING_NORMAL);
  }

  /**
   * Clears all tickables
   */
  public static void clear(){
    tickables.clear();
    levelTickables.clear();
    hasTicked.clear();
  }

  /**
   * Clears all tickables in the current level
   */
  public static void clearLevel(){
    levelTickables.forEach(hasTicked::remove);
    levelTickables.clear();
  }

  /**
   * Remove a certain tickable from either the current level or overall
   * @param tickable The tickable to remove
   */
  public static void remove(ITickable tickable) {
    tickables.remove(tickable);
    levelTickables.remove(tickable);
  }

  public static <K> List<K> sortKeysByValue(Map<K, Integer> map) {
    return map.entrySet()
      .stream()
      .sorted(Map.Entry.comparingByValue())
      .map(Map.Entry::getKey)
      .collect(Collectors.toList());
  }

  @Override
  public void execute() {
    sortKeysByValue(tickables).forEach(t -> {
      boolean firstTick = !hasTicked.get(t);
      if(firstTick){
        hasTicked.put(t, true);
      }
      t.onTick(firstTick);
    });
    tickables.putAll(addTickables);
    addTickables.clear();

    sortKeysByValue(levelTickables).forEach(t -> {
      boolean firstTick = !hasTicked.get(t);
      if(firstTick){
        hasTicked.put(t, true);
      }
      t.onTick(firstTick);
    });
    levelTickables.putAll(addLevelTickables);
    addLevelTickables.clear();
  }
}
