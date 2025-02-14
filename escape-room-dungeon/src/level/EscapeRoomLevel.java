package level;

import core.level.Tile;
import core.level.TileLevel;
import core.level.elements.tile.DoorTile;
import core.level.elements.tile.ExitTile;
import core.level.utils.Coordinate;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import core.utils.components.path.IPath;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import level.devlevel.*;
import level.utils.ITickable;
import level.utils.MissingLevelException;
import starter.EscapeRoomDungeon;

/**
 * Represents a level in the DevDungeon game. This class extends the {@link TileLevel} class and
 * adds functionality for handling custom points. These points are used to add spawn points, door
 * logic or any other custom logic to the level.
 */
public abstract class EscapeRoomLevel extends TileLevel implements ITickable {

  protected static final Random RANDOM = new Random();

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   */
  public EscapeRoomLevel(
      LevelElement[][] layout,
      DesignLabel designLabel) {
    super(layout, designLabel);
  }

  @Override
  public final void onTick(boolean isFirstTick) {
    if (isFirstTick) {
      ((ExitTile) endTile()).close(); // close exit at start (to force defeating the boss)
      doorTiles().forEach(DoorTile::close);
      onFirstTick();
    }
    onTick();
  }

  /**
   * Called when the level is first ticked.
   *
   * @see #onTick()
   * @see ITickable
   */
  protected abstract void onFirstTick();

  /**
   * Called when the level is ticked.
   *
   * @see #onFirstTick()
   * @see ITickable
   */
  protected abstract void onTick();

  /**
   * Loads a DevDungeonLevel from the given path.
   *
   * @param path The path to the level file.
   * @return The loaded DevDungeonLevel.
   */
  public static EscapeRoomLevel loadFromPath(IPath path) {
    try {
      BufferedReader reader;
      if (path.pathString().startsWith("jar:")) {
        InputStream is = EscapeRoomLevel.class.getResourceAsStream(path.pathString().substring(4));
        reader = new BufferedReader(new InputStreamReader(is));
      } else {
        File file = new File(path.pathString());
        if (!file.exists()) {
          throw new MissingLevelException(path.toString());
        }
        reader = new BufferedReader(new FileReader(file));
      }

      // Parse DesignLabel
      String designLabelLine = readLine(reader);
      DesignLabel designLabel = parseDesignLabel(designLabelLine);

      // Parse Hero Position
      String heroPosLine = readLine(reader);
      Point heroPos = parseHeroPosition(heroPosLine);

      // Parse LAYOUT
      List<String> layoutLines = new ArrayList<>();
      String line;
      while (!(line = readLine(reader)).isEmpty()) {
        layoutLines.add(line);
      }
      LevelElement[][] layout = loadLevelLayoutFromString(layoutLines);

      EscapeRoomLevel newLevel;
      newLevel = getDevLevel(EscapeRoomDungeon.DUNGEON_LOADER.currentLevel(), layout, designLabel);

      // Set Hero Position
      Tile heroTile = newLevel.tileAt(heroPos);
      if (heroTile == null) {
        throw new RuntimeException("Invalid Hero Position: " + heroPos);
      }
      newLevel.startTile(heroTile);

      return newLevel;
    } catch (IOException e) {
      throw new RuntimeException("Error reading level file", e);
    }
  }

  /**
   * Read a line from the reader, ignoring comments. It skips lines that start with a '#' (comments)
   * and returns the next non-empty line.
   *
   * @param reader The reader to read from
   * @return The next non-empty, non-comment line without any comments
   * @throws IOException If an error occurs while reading from the reader
   */
  private static String readLine(BufferedReader reader) throws IOException {
    String line = reader.readLine();
    if (line == null) return "";
    while (line.trim().startsWith("#")) {
      line = reader.readLine();
    }
    line = line.trim().split("#")[0].trim();

    return line;
  }

  private static Point parseHeroPosition(String heroPositionLine) {
    if (heroPositionLine.isEmpty()) throw new RuntimeException("Missing Hero Position");
    String[] parts = heroPositionLine.split(",");
    if (parts.length != 2) throw new RuntimeException("Invalid Hero Position: " + heroPositionLine);
    try {
      float x = Float.parseFloat(parts[0]);
      float y = Float.parseFloat(parts[1]);
      return new Point(x, y);
    } catch (NumberFormatException e) {
      throw new RuntimeException("Invalid Hero Position: " + heroPositionLine);
    }
  }

  private static DesignLabel parseDesignLabel(String line) {
    if (line.isEmpty()) return DesignLabel.randomDesign();
    try {
      return DesignLabel.valueOf(line);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid DesignLabel: " + line);
    }
  }

  private static LevelElement[][] loadLevelLayoutFromString(List<String> lines) {
    LevelElement[][] layout = new LevelElement[lines.size()][lines.getFirst().length()];

    for (int y = 0; y < lines.size(); y++) {
      for (int x = 0; x < lines.getFirst().length(); x++) {
        //(0,0) is at the bottom left, so at the *end* of the file
        char c = lines.get(lines.size() - y - 1).charAt(x);
        switch (c) {
          case 'F':
            layout[y][x] = LevelElement.FLOOR;
            break;
          case 'W':
            layout[y][x] = LevelElement.WALL;
            break;
          case 'E':
            layout[y][x] = LevelElement.EXIT;
            break;
          case 'S':
            layout[y][x] = LevelElement.SKIP;
            break;
          case 'P':
            layout[y][x] = LevelElement.PIT;
            break;
          case 'H':
            layout[y][x] = LevelElement.HOLE;
            break;
          case 'D':
            layout[y][x] = LevelElement.DOOR;
            break;
          default:
            throw new IllegalArgumentException("Invalid character in level layout: " + c);
        }
      }
    }

    return layout;
  }

  private static EscapeRoomLevel getDevLevel(
      String levelName,
      LevelElement[][] layout,
      DesignLabel designLabel) {
    return switch (levelName) {
      case "tutorial" -> new TutorialLevel(layout, designLabel);
      default ->
          throw new IllegalArgumentException("Invalid level name for levelHandler: " + levelName);
    };
  }
}
