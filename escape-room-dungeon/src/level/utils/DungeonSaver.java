package level.utils;

import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.level.utils.Coordinate;
import core.level.utils.LevelElement;
import core.utils.Point;
import core.utils.components.MissingComponentException;

import java.util.*;

import level.EscapeRoomLevel;

/**
 * This class is responsible for saving the current state of the dungeon in the game. The "saving"
 * is done by printing the design label of the current level, the position of the hero, and the
 * layout of the current level to the console. This String can then be copied and pasted into a
 * .level file to be loaded later by the {@link DungeonLoader}.
 *
 * @see DungeonLoader
 */
public class DungeonSaver {

  /**
   * The saveCurrentDungeon method is responsible for saving the current state of the dungeon. It
   * does this by first getting the design label of the current level. Then it gets the position of
   * the hero in the game. After that, it compresses the layout of the current level by removing all
   * lines that only contain Empty Tiles. Finally, it concatenates all this information into a
   * single string and prints it.
   */
  public static void saveCurrentDungeon() {
    EscapeRoomLevel level = (EscapeRoomLevel)Game.currentLevel();
    String designLabel;
    if (level.endTile() == null) {
      designLabel = level.randomTile(LevelElement.FLOOR).orElseThrow().designLabel().name();
    } else {
      designLabel = level.endTile().designLabel().name();
    }

    Entity hero = Game.hero().orElse(null);
    Point heroPos;
    if (hero != null) {
      heroPos = hero.fetchOrThrow(PositionComponent.class).position();
    } else {
      heroPos = new Point(0, 0);
    }

    // Serialize named points
    String namedPoints = level.serializeNamedPoints();

    // Compress the layout of the current level by removing all lines that only contain 'S'
//    String dunLayout = reverseLineOrder(compressDungeonLayout(level.printLevel()));
    String dunLayout = reverseLineOrder(level.printLevel());

    String result = designLabel + "\n"
            + heroPos.x + "," + heroPos.y + "\n"
            + namedPoints + "\n"
            + dunLayout;

    System.out.println(result);
  }

  /**
   * The compressDungeonLayout method takes a multi-line string as input and returns a string where
   * all lines containing only 'S' are removed. It does this by using the replaceAll method with a
   * regular expression that matches lines containing only 'S' and replaces them with an empty
   * string.
   *
   * @param layout The dungeon layout to compress.
   * @return The compressed dungeon layout.
   */
  private static String compressDungeonLayout(String layout) {
    return layout.replaceAll("(?m)^S+$\\n", "");
  }

  public static String reverseLineOrder(String input) {
    String[] lines = input.split("\n");
    List<String> lineList = Arrays.asList(lines);
    Collections.reverse(lineList);
    return String.join("\n", lineList);
  }
}
