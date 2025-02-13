package systems;

import core.Game;
import core.System;
import core.level.elements.ILevel;
import level.EscapeRoomLevel;
import level.utils.ITickable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The LevelTickSystem is responsible for ticking the current level. It checks if the current level
 * has changed and calls the onTick method of the current level if it implements the ITickable
 * interface.
 *
 * @see ITickable
 * @see EscapeRoomLevel DevDungeonLevel
 */
public class TickableSystem extends System {

  /** The current level of the game. */
  private static final List<ITickable> tickables = new ArrayList<>();
  private static final List<ITickable> addTickables = new ArrayList<>();
  private static final Map<ITickable, Boolean> hasTicked = new HashMap<>();

  public static void register(ITickable tickable){
    addTickables.add(tickable);
    hasTicked.put(tickable, false);
  }

  public static void clear(){
    tickables.clear();
    hasTicked.clear();
  }

  @Override
  public void execute() {
    tickables.forEach(t -> {
      boolean firstTick = hasTicked.get(t) == false;
      if(firstTick){
        hasTicked.put(t, true);
      }
      t.onTick(firstTick);
    });

    addTickables.forEach(t -> {
      tickables.add(t);
    });
    addTickables.clear();
  }
}
