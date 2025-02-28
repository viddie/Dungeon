package level;

import com.badlogic.gdx.graphics.Color;
import core.level.Tile;
import core.level.TileLevel;
import core.level.elements.tile.DoorTile;
import core.level.elements.tile.ExitTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import core.utils.components.path.IPath;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import hud.DebugOverlay;
import level.levels.Floor1Level;
import level.levels.MainMenuLevel;
import level.levels.SettingsLevel;
import level.levels.TutorialLevel;
import level.utils.DungeonLoader;
import level.utils.ITickable;
import level.utils.LevelLabel;
import level.utils.MissingLevelException;
import utils.Constants;

/**
 * Represents a level in the DevDungeon game. This class extends the {@link TileLevel} class and
 * adds functionality for handling custom points. These points are used to add spawn points, door
 * logic or any other custom logic to the level.
 */
public abstract class EscapeRoomLevel extends TileLevel implements ITickable {

  protected final Map<String, Point> namedPoints;

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   */
  public EscapeRoomLevel(
      LevelElement[][] layout,
      DesignLabel designLabel,
      Map<String, Point> namedPoints) {
    super(layout, designLabel);
    this.namedPoints = namedPoints;
  }

  @Override
  public final void onTick(boolean isFirstTick) {
    if (isFirstTick) {
      onFirstTick();
    }
    drawDebug();
    onTick();
  }

  private void drawDebug(){
    Color color = new Color(0, 1, 0, 0.8f);
    //Draw named points on debug screen
    namedPoints.forEach((s, p) -> {
      DebugOverlay.renderCircle(Constants.offset(p), 0.1f, color);
      DebugOverlay.renderText(Constants.offset(p).add(0, 0.5f), s, color, 1);
    });
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

  public Point getPoint(String name){
    return namedPoints.get(name);
  }
  public Point getPointOrDefault(String name){
    Point p = namedPoints.get(name);
    return p == null ? new Point(0, 0) : p;
  }
  public Map<String, Point> getNamedPoints(){
    return namedPoints;
  }

  private static boolean isRunningFromJar() {
    return Objects.requireNonNull(
        EscapeRoomLevel.class.getResource(EscapeRoomLevel.class.getSimpleName() + ".class"))
      .toString()
      .startsWith("jar:");
  }

  /**
   * Loads a new EscapeRoomLevel.
   * @param label The level type
   * @param path The path of the level to load
   * @return
   */
  public static EscapeRoomLevel loadFromPath(LevelLabel label, IPath path) {
    try {
      BufferedReader reader;
      if (isRunningFromJar()) {
        InputStream is = EscapeRoomLevel.class.getResourceAsStream(path.pathString());
        reader = new BufferedReader(new InputStreamReader(is));
      } else {
        File file = new File(path.pathString());
        if (!file.exists()) {
          throw new MissingLevelException(path.toString());
        }
        reader = new BufferedReader(new FileReader(file));
      }

      // Parse DesignLabel
      DesignLabel designLabel = parseDesignLabel(readLine(reader));

      // Parse Hero Position
      Point heroPos = parseHeroPosition(readLine(reader));

      // Parse Named Points
      Map<String, Point> namedPoints = parseNamedPoints(readLine(reader));

      // Parse LAYOUT
      List<String> layoutLines = new ArrayList<>();
      String line;
      while (!(line = readLine(reader)).isEmpty()) {
        layoutLines.add(line);
      }

      LevelElement[][] layout;
      boolean isDefaultLayout = false;

      //Special case: if theres only 1 line and it has the form of <int>x<int>, generate a new empty level
      if(layoutLines.size() == 1){
        isDefaultLayout = true;
        line = layoutLines.get(0);
        String[] split = line.split("x");
        int width = Integer.parseInt(split[0]);
        int height = Integer.parseInt(split[1]);
        layout = new LevelElement[height][width];

        int centerX = width / 2;
        int centerY = height / 2;

        for(int y = 0; y < height; y++){
          for(int x = 0; x < width; x++){
            LevelElement toFill = LevelElement.SKIP;
            if(y == 0 || y == height - 1 || x == 0 || x == width - 1){
              toFill = LevelElement.WALL;
            } else if (Math.abs(x - centerX) <= 1 && Math.abs(y - centerY) <= 1){
              toFill = LevelElement.FLOOR;
            }
            layout[y][x] = toFill;
          }
        }
        heroPos = new Point(centerX, centerY);
      } else {
        layout = loadLevelLayoutFromString(layoutLines);
      }


      EscapeRoomLevel newLevel;
      newLevel = getLevelMapping(label, layout, designLabel, namedPoints);

      // Set Hero Position
      Tile heroTile = newLevel.tileAt(heroPos);
      if (heroTile == null) {
        throw new RuntimeException("Invalid Hero Position: " + heroPos);
      }
      newLevel.startTile(heroTile);

      if(isDefaultLayout){
        //Remove end tile
        for(int i = newLevel.exitTiles().size()-1; i >= 0; i--){
          newLevel.changeTileElementType(newLevel.exitTiles().get(i), LevelElement.FLOOR);
        }
      }

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

  private static DesignLabel parseDesignLabel(String line) {
    if (line.isEmpty()) return DesignLabel.randomDesign();
    try {
      return DesignLabel.valueOf(line);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid DesignLabel: " + line);
    }
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

  public static Map<String, Point> parseNamedPoints(String input) {
    Map<String, Point> pointsMap = new HashMap<>();

    String[] entries = input.split(";"); // Split by semicolon
    for (String entry : entries) {
      String[] parts = entry.split(":"); // Split by colon
      if (parts.length == 2) {
        String name = parts[0].trim();
        String[] coordinates = parts[1].split(","); // Split coordinates

        if (coordinates.length == 2) {
          try {
            float x = Float.parseFloat(coordinates[0].trim());
            float y = Float.parseFloat(coordinates[1].trim());
            pointsMap.put(name, new Point(x, y));
          } catch (NumberFormatException e) {
            System.err.println("Invalid number format in entry: " + entry);
          }
        }
      }
    }
    return pointsMap;
  }

  public String serializeNamedPoints() {
    return namedPoints.entrySet().stream()
      .map(entry -> entry.getKey() + ":" + entry.getValue().x + "," + entry.getValue().y)
      .collect(Collectors.joining(";"));
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

  private static EscapeRoomLevel getLevelMapping(
      LevelLabel label,
      LevelElement[][] layout,
      DesignLabel designLabel,
      Map<String, Point> namedPoints) {
    return switch (label) {
      case MainMenu -> new MainMenuLevel(layout, designLabel, namedPoints);
      case Settings -> new SettingsLevel(layout, designLabel, namedPoints);
      case Tutorial -> new TutorialLevel(layout, designLabel, namedPoints);
      case Floor1 -> new Floor1Level(layout, designLabel, namedPoints);
      default -> throw new IllegalArgumentException("Invalid level name for levelHandler: " + label.name());
    };
  }
}
