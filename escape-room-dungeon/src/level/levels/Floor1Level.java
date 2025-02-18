package level.levels;

import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import level.EscapeRoomLevel;
import puzzles.floor1.Floor1LeversPuzzle;
import utils.GameState;

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
    Floor1LeversPuzzle puzzle = new Floor1LeversPuzzle(new Point(15, 26), GameState.INSTANCE.playerNumber);
    puzzle.load();
  }

  @Override
  protected void onTick() {

  }
}
