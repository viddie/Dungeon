package level.levels;

import core.Entity;
import core.Game;
import core.level.elements.tile.DoorTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import level.EscapeRoomLevel;
import modules.keypad.KeypadFactory;
import modules.showimage.ShowImageFactory;
import modules.showimage.ShowImageText;
import utils.GameState;
import utils.SoundManager;
import utils.Sounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Floor3Level extends EscapeRoomLevel {

  DoorTile door;

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout      The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   * @param namedPoints A map of names to points in the level
   */
  public Floor3Level(LevelElement[][] layout, DesignLabel designLabel, Map<String, Point> namedPoints) {
    super(layout, designLabel, namedPoints);
  }

  @Override
  protected void onFirstTick() {

  }

  @Override
  protected void onTick() {

  }
}
