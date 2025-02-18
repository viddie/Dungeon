package puzzles;

import core.Game;
import core.System;
import core.utils.Point;
import starter.EscapeRoomDungeon;

import java.util.List;
import java.util.logging.Logger;

/**
 * The PuzzleController is the central component of all puzzles that exist in the Escape Room.
 * It is responsible for loading and unloading the necessary Entities + Components, as well as the Systems each Puzzle
 * needs. It gets a Point at which it's supposed to load all of its components, which is handled in each subclass.
 */
public abstract class PuzzleController {

  protected static final Logger LOGGER = EscapeRoomDungeon.LOGGER;

  protected Point position;
  protected int player;

  public PuzzleController(Point position, int player){
    this.position = position;
    this.player = player;
  }

  public void load(){
    loadResources(player);
    loadSystems();
    loadEntities(player);
  }
  public void unload(){
    unloadEntities(player);
    unloadSystems();
  }

  public abstract void loadResources(int player);
  public abstract void loadEntities(int player);
  public abstract void unloadEntities(int player);
  public abstract List<System> createSystems(int player);


  /**
   * Loads all systems that only this Puzzle needs.
   */
  public void loadSystems(){
    List<System> systems = createSystems(player);
    if(systems == null) return;

    systems.forEach(s -> {
      //Only add the system if it doesn't already exist
      if(Game.getSystem(s.getClass()) == null){
        Game.add(s);
      }
    });
  }

  public void unloadSystems(){
    List<System> systems = createSystems(player);
    if(systems == null) return;

    systems.forEach(s -> {
      //Only remove the system if it exists
      if(Game.getSystem(s.getClass()) != null){
        Game.remove(s.getClass());
      }
    });
  }
}
