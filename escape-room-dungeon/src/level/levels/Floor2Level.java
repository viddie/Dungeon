package level.levels;

import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import level.EscapeRoomLevel;

import java.util.Map;

public class Floor2Level extends EscapeRoomLevel {



  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout      The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   * @param namedPoints A map of names to points in the level
   */
  public Floor2Level(LevelElement[][] layout, DesignLabel designLabel, Map<String, Point> namedPoints) {
    super(layout, designLabel, namedPoints);
  }

  @Override
  protected void onFirstTick() {

  }

  @Override
  protected void onTick() {

  }
}
