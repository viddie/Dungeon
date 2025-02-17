package level.utils;

import core.Game;
import core.components.PositionComponent;
import core.level.elements.ILevel;
import core.utils.Point;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;
import level.EscapeRoomLevel;
import systems.TickableSystem;

/**
 * The DungeonLoader class is used to load levels in the game. It is used to load levels in a
 * specific order or to load a specific level. The DungeonLoader class is used to load levels from
 * the file system or from a jar file.
 *
 * @see EscapeRoomLevel
 */
public class DungeonLoader {

  private static final String LEVELS_PATH_PREFIX = "/levels";

  private static LevelLabel currentLabel;
  private static EscapeRoomLevel currentLevel;

  public DungeonLoader() {}

  private static boolean isRunningFromJar() {
    return Objects.requireNonNull(
        DungeonLoader.class.getResource(DungeonLoader.class.getSimpleName() + ".class"))
      .toString()
      .startsWith("jar:");
  }

  private static String getLevelPathFromFiles(String fileName) {
    try{
      URI uri = Objects.requireNonNull(DungeonLoader.class.getResource(LEVELS_PATH_PREFIX)).toURI();
      Path path = Paths.get(uri);
      return path.resolve(fileName).toString();
    } catch (URISyntaxException ignored){}
    return null;
}

  private static String getLevelPathFromJar(String fileName) {
    try{
      URI uri = Objects.requireNonNull(DungeonLoader.class.getResource(LEVELS_PATH_PREFIX)).toURI();
      FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
      Path path = fileSystem.getPath(LEVELS_PATH_PREFIX);
      return path.resolve(fileName).toString();
    } catch (IOException | URISyntaxException ignored){}
    return null;
  }

  /**
   * Loads a new level
   * @param label The level type or floor to load
   * @param player For which player (1 or 2) this level should load. Pass 0 if there is no difference between players
   */
  public static void loadLevel(LevelLabel label, int player){
    loadLevel(label, player, null);
  }
  public static void loadLevel(LevelLabel label, int player, Point point){
    if(label == currentLabel){
      if(point != null) {
        Game.hero().orElseThrow().fetchOrThrow(PositionComponent.class).position(point);
      }
      return;
    }

    String fileName = label.fileName + ".level";
    //All floors will be split for the players
    if(player > 0) {
      fileName = player + "_" + fileName;
    }

    //Get full file path
    String absPath = isRunningFromJar() ? getLevelPathFromJar(fileName) : getLevelPathFromFiles(fileName);

    EscapeRoomLevel level = EscapeRoomLevel.loadFromPath(label, new SimpleIPath(absPath));
    TickableSystem.clearLevel();
    TickableSystem.registerInLevel(level);

    currentLabel = label;
    currentLevel = level;

    Game.currentLevel(level);

    if(point != null){
      Game.hero().orElseThrow().fetchOrThrow(PositionComponent.class).position(point);
    }
  }

  public static void loadNextLevel(){
    //Player stepped on an end tile in any level
    try {
      LevelLabel next = currentLabel.next();
      loadLevel(next, 0); //TODO: Load current player state via static GameState
    } catch (MissingLevelException e){
      System.out.println("No next level found, exiting game...");
      Game.exit();
    }
  }

  /**
   * Holds all available Levels
   */
  public enum LevelLabel {
    MainMenu("main_menu"), //End tile: Exit game
    Settings("settings"), //End tile: Back to main menu
    Tutorial("tutorial"), //End tile: Back to main menu
    Floor1("floor1"), //To next floor
    ;

    public final String fileName;
    private LevelLabel(String fileName){
      this.fileName = fileName;
    }

    public LevelLabel next(){
      return switch (this){
        case Settings, Tutorial -> MainMenu;
        default -> throw new MissingLevelException("");
      };
    }
  }
}
