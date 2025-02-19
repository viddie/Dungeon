package level.levels;

import core.Entity;
import core.Game;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import level.EscapeRoomLevel;
import modules.keypad.KeypadFactory;
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

    List<Integer> correctDigits = Arrays.asList(2, 3, 4);
    Entity keypad = KeypadFactory.createKeypad(new Point(17, 12), correctDigits, () -> {}, true);
    Game.add(keypad);
  }

  @Override
  protected void onTick() {

  }
}
