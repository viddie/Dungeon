package puzzles.simpleLevers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import components.DrawTextComponent;
import components.LeverComponent;
import components.VicinityComponent;
import contrib.components.CollideComponent;
import core.Entity;
import core.Game;
import core.System;
import core.components.PositionComponent;
import core.level.Tile;
import core.level.elements.tile.DoorTile;
import core.level.utils.LevelElement;
import core.systems.LevelSystem;
import core.utils.Point;
import entities.DrawTextFactory;
import entities.LeverFactory;
import hud.DebugOverlay;
import level.utils.ITickable;
import puzzles.PuzzleController;
import systems.TickableSystem;
import utils.Constants;
import utils.ICommand;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SimpleLeverPuzzle extends PuzzleController implements ITickable {

  private final List<Entity> levers = new ArrayList<>();
  private final List<Entity> hints = new ArrayList<>();
  private DoorTile door;

  public SimpleLeverPuzzle(Point p) {
    super(p);
  }

  @Override
  public void loadEntities() {
    TickableSystem.registerInLevel(this);

    for(int i = 0; i < 5; i++){
      Entity lever = LeverFactory.createLever(this.position.add((i-2) * 3, 0), new ICommand() {
        @Override
        public void execute() { checkLeverState(); }
        @Override
        public void undo() { checkLeverState(); }
      });
      lever.add(new VicinityComponent(2, new VicinityComponent.IVicinityCommand() {
        @Override
        public void onEnterRange() {}
        @Override
        public void onLeaveRange() {}
        @Override
        public void onInRange(double distance) {
          PositionComponent pc = lever.fetchOrThrow(PositionComponent.class);
          Point p = pc.position();
          Point heroPos = Game.hero().orElseThrow().fetchOrThrow(PositionComponent.class).position();
          float push = -Interpolation.exp5.apply(0, 1, 1 - (2 / (float)distance));
          Point v = Point.unitDirectionalVector(p, heroPos).mult(push * 4);
          DebugOverlay.renderArrow(p, p.add(v.mult(15)));
          pc.position(p.add(v));
        }
      }, Game.hero().orElseThrow()));
      levers.add(lever);
      Game.add(lever);
    }

    Point doorPos = this.position.add(0, 4);
    Tile doorTile = LevelSystem.level().tileAt(doorPos);
    if (doorTile == null) {
      return;
    }
    LevelSystem.level().changeTileElementType(doorTile, LevelElement.DOOR);
    door = (DoorTile) LevelSystem.level().tileAt(doorPos);
    door.close();


    Point hintsBase = this.position.add(81 - 59, 0);
    hints.add(DrawTextFactory.createTextEntity("1 - 'final' schützt eine Variable vor Änderungen", hintsBase.add(-3, 5), 0.3f, Color.WHITE, 9, 0.7f));
    hints.add(DrawTextFactory.createTextEntity("2 - Eine 'static' Methode kann direkt auf\n Instanzvariablen zugreifen", hintsBase.add(-1, 3), 0.3f, Color.WHITE, 10, 0.7f));
    hints.add(DrawTextFactory.createTextEntity("3 - Eine Klasse, die mit 'private' deklariert wurde, kann von\nanderen Klassen aus demselben Paket instanziiert werden", hintsBase.add(0, 0), 0.3f, Color.WHITE, 10, 0.7f));
    hints.add(DrawTextFactory.createTextEntity("4 - Eine Variable, die mit 'volatile' markiert ist, kann von\nmehreren Threads sicher gelesen und geschrieben werden", hintsBase.add(-1, -2), 0.3f, Color.WHITE, 10, 0.7f));
    hints.add(DrawTextFactory.createTextEntity("5 - Das Keyword 'super' wird verwendet, um auf Methoden und\nKonstruktoren der Elternklasse zuzugreifen", hintsBase.add(-3, -5), 0.3f, Color.WHITE, 9, 0.7f));
    hints.forEach(Game::add);
  }

  @Override
  public void unloadEntities() {
    TickableSystem.remove(this);
    levers.forEach(Game::remove);
    hints.forEach(Game::remove);
  }

  @Override
  public List<System> createSystems() {
    return null;
  }


  private void checkLeverState(){
    boolean[] states = new boolean[]{true, false, false, true, true};
    boolean allCorrect = true;
    for(int i = 0; i < levers.size(); i++){
      LeverComponent lc = levers.get(i).fetchOrThrow(LeverComponent.class);
      DrawTextComponent dtc = hints.get(i).fetchOrThrow(DrawTextComponent.class);
      allCorrect &= states[i] == lc.isOn();
    }

    if(door != null){
      if(allCorrect){
        door.open();
      } else {
        door.close();
      }
    }
  }

  @Override
  public void onTick(boolean isFirstTick) {
    String leverState = "";
    for (Entity lever : levers) {
      LeverComponent lc = lever.fetchOrThrow(LeverComponent.class);
      leverState += lc.isOn() + " ";
    }
    DebugOverlay.drawText(leverState);

    DebugOverlay.renderCircle(Constants.offset(this.position), 0.25f);
  }
}
