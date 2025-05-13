package puzzles.floor1;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import components.LeverComponent;
import components.VicinityComponent;
import core.Entity;
import core.Game;
import core.System;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.level.Tile;
import core.level.elements.tile.DoorTile;
import core.level.utils.LevelElement;
import core.systems.LevelSystem;
import core.utils.Point;
import core.utils.Tuple;
import entities.DrawTextFactory;
import entities.LeverFactory;
import hud.DebugOverlay;
import level.utils.ITickable;
import puzzles.PuzzleController;
import systems.TickableSystem;
import utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Floor1LeversPuzzle extends PuzzleController implements ITickable {
  private final List<Entity> levers = new ArrayList<>();
  private final List<Entity> hints = new ArrayList<>();
  private DoorTile door;

  private Array<Boolean> resources;

  private static final List<Point> hintPositions = new ArrayList<>();
  private static final HashMap<Integer, List<String>> hintTexts = new HashMap<>();

  public Floor1LeversPuzzle(Point p, int player) { super(p, player); }

  @Override
  public void loadResources(int player) {
    Array<Boolean> defaultResources = new Array<>();
    for(int i = 0; i < 5; i++){
      defaultResources.add(false);
    }
    resources = GameState.getResourceObject(this.getClass().getSimpleName(), player, defaultResources);

    hintPositions.clear();
    hintPositions.addAll(parent.listPointsRaw("hint"));

    String p1Hint1 = "A: 'final' schützt eine Variable vor Änderungen";
    String p1Hint2 = "C = 5";
    String p1Hint3 = "E = 2";
    String p1Hint4 = "D: Eine 'static' Methode kann direkt auf\n Instanzvariablen zugreifen";
    String p1Hint5 = "B: Eine Klasse, die mit 'private' deklariert wurde, kann von\nanderen Klassen aus demselben Paket instanziiert werden";

    String p2Hint1 = "B = 1";
    String p2Hint2 = "C: Eine Variable, die mit 'volatile'\nmarkiert ist, kann von mehreren\nThreads sicher gelesen und\ngeschrieben werden";
    String p2Hint3 = "E: Das Keyword 'super' wird verwendet, um auf\nMethoden und Konstruktoren der Elternklasse\nzuzugreifen";
    String p2Hint4 = "A = 4";
    String p2Hint5 = "D = 3";
    hintTexts.put(1, Arrays.asList(p1Hint1, p1Hint2, p1Hint3, p1Hint4, p1Hint5));
    hintTexts.put(2, Arrays.asList(p2Hint1, p2Hint2, p2Hint3, p2Hint4, p2Hint5));
  }

  @Override
  public void loadEntities(int player) {
    TickableSystem.registerInLevel(this);

    for(int i = 0; i < 5; i++){
      Entity lever = LeverFactory.createLever(this.position.add((i-2) * 3, 0), new ICommand() {
        @Override
        public void execute() { checkLeverState(); }
        @Override
        public void undo() { checkLeverState(); }
      });
      levers.add(lever);
      Game.add(lever);
    }

    Point doorPos = this.position.add(0, 3);
    Tile doorTile = LevelSystem.level().tileAt(doorPos);
    if (doorTile == null) {
      return;
    }
    LevelSystem.level().changeTileElementType(doorTile, LevelElement.DOOR);
    door = (DoorTile) LevelSystem.level().tileAt(doorPos);
    door.close();


    List<String> texts = hintTexts.get(this.player);
    for(int i = 0; i < hintPositions.size(); i++){
      hints.add(DrawTextFactory.createTextEntity(texts.get(i), hintPositions.get(i), 0.7f, Color.WHITE, 7.5f, 0.3f));
    }
    hints.forEach(Game::add);

    //Apply saved game state
    for(int i = 0; i < levers.size(); i++){
      Entity lever = levers.get(i);
      if(resources.get(i)){
        LeverComponent lc = lever.fetchOrThrow(LeverComponent.class);
        lc.setOn(true);
        lever.fetchOrThrow(DrawComponent.class).sendSignal("on");
      }
    }
    checkLeverState();
  }

  @Override
  public void unloadEntities(int player) {
    TickableSystem.remove(this);
    levers.forEach(Game::remove);
    hints.forEach(Game::remove);
  }

  @Override
  public List<System> createSystems(int player) {
    return null;
  }


  private void checkLeverState(){
    boolean[] states = new boolean[]{false, true, false, true, true};
    boolean allCorrect = true;
    for(int i = 0; i < levers.size(); i++){
      LeverComponent lc = levers.get(i).fetchOrThrow(LeverComponent.class);
      allCorrect &= states[i] == lc.isOn();
      resources.set(i, lc.isOn());
    }

    if(door != null){
      if(allCorrect){
        if(!door.isOpen()){
          SoundManager.playSound(Sounds.DoorOpened);
        }
        door.open();
      } else {
        door.close();
      }
    }
  }

  @Override
  public void onTick(boolean isFirstTick) {}
}
