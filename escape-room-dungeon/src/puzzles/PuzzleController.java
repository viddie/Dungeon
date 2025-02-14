package puzzles;

import core.Game;
import core.System;
import core.utils.Point;

import java.util.List;

/**
 * The PuzzleController is the central component of all puzzles that exist in the Escape Room.
 * It is responsible for loading and unloading the necessary Entities + Components, as well as the Systems each Puzzle
 * needs. It gets a Point at which it's supposed to load all of its components, which is handled in each subclass.
 */
public abstract class PuzzleController {

  protected Point position;

  public PuzzleController(float x, float y){
    position = new Point(x, y);
  }
  public PuzzleController(Point p){
    position = p;
  }

  public void load(){
    loadSystems();
    loadEntities();
  }
  public void unload(){
    unloadEntities();
    unloadSystems();
  }

  public abstract void loadEntities();
  public abstract void unloadEntities();
  public abstract List<System> createSystems();

  /**
   * Loads all non-general systems that this Puzzle needs to exist.
   */
  public void loadSystems(){
    List<System> systems = createSystems();
    if(systems == null) return;

    systems.forEach(s -> {
      //Only add the system if it doesn't already exist
      if(Game.getSystem(s.getClass()) == null){
        Game.add(s);
      }
    });
  }

  public void unloadSystems(){
    List<System> systems = createSystems();
    if(systems == null) return;

    systems.forEach(s -> {
      //Only remove the system if it exists
      if(Game.getSystem(s.getClass()) != null){
        Game.remove(s.getClass());
      }
    });
  }

}
