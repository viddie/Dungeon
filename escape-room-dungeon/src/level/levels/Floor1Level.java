package level.levels;

import core.Entity;
import core.Game;
import core.level.Tile;
import core.level.elements.tile.DoorTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.systems.LevelSystem;
import core.utils.Point;
import level.EscapeRoomLevel;
import modules.keypad.KeypadFactory;
import modules.showimage.ShowImageComponent;
import modules.showimage.ShowImageFactory;
import puzzles.floor1.Floor1LeversPuzzle;
import utils.GameState;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Floor1Level extends EscapeRoomLevel {

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout      The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   */
  public Floor1Level(LevelElement[][] layout, DesignLabel designLabel, Map<String, Point> namedPoints) {
    super(layout, designLabel, namedPoints);
  }

  @Override
  protected void onFirstTick() {
    Point pos = getPoint("levers");
    Floor1LeversPuzzle puzzle = new Floor1LeversPuzzle(pos, GameState.playerNumber());
    puzzle.load();

    Point notePos = getPoint("note");
    if(notePos != null){
      Entity showImage = ShowImageFactory.createShowImage(getPoint("note"), "objects/note/note-sprite.png", "images/note-tutorial.png", 1f);
      Game.add(showImage);
    }

    Point keypadPos = getPoint("keypad");
    Point doorPos = getPoint("keypad-door");
    if(keypadPos != null && doorPos != null){
      Tile t = LevelSystem.level().tileAt(doorPos);
      LevelSystem.level().changeTileElementType(t, LevelElement.DOOR);
      DoorTile door = (DoorTile)LevelSystem.level().tileAt(doorPos);
      door.close();

      List<Integer> correctDigits = Arrays.asList(2, 3, 4);
      Entity keypad = KeypadFactory.createKeypad(keypadPos, correctDigits, () -> {
        door.open();
      }, false);
      Game.add(keypad);
    }
  }

  @Override
  protected void onTick() {

  }
}
