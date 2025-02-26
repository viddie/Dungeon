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

public class Floor1Level extends EscapeRoomLevel {

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout      The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   */
  public Floor1Level(LevelElement[][] layout, DesignLabel designLabel) {
    super(layout, designLabel);
  }

  @Override
  protected void onFirstTick() {
    Point posP1 = new Point(52, 28);
    Point posP2 = new Point(15, 28);
    Floor1LeversPuzzle puzzle = new Floor1LeversPuzzle(posP1, posP2, GameState.playerNumber());
    puzzle.load();

    Entity showImage = ShowImageFactory.createShowImage(new Point(18, 9), "objects/lever/on/lever_0.png", "items/potion/attack_speed_potion.png", null);
    Game.add(showImage);

    Point keypadPos = new Point(17, 12);
    Point doorPos = keypadPos.add(-2, 2);
    Tile t = LevelSystem.level().tileAt(doorPos);
    LevelSystem.level().changeTileElementType(t, LevelElement.DOOR);
    DoorTile door = (DoorTile)LevelSystem.level().tileAt(doorPos);
    door.close();

    List<Integer> correctDigits = Arrays.asList(2, 3, 4);
    Entity keypad = KeypadFactory.createKeypad(keypadPos, correctDigits, () -> {
      door.open();
      showImage.fetchOrThrow(ShowImageComponent.class).imagePath = "skin/custom_skin.png";
    }, false);
    Game.add(keypad);
  }

  @Override
  protected void onTick() {

  }
}
