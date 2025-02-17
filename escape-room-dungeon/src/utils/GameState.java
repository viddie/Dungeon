package utils;

import com.badlogic.gdx.utils.Json;
import core.Game;
import core.components.PositionComponent;
import core.utils.Point;
import level.utils.DungeonLoader;
import starter.EscapeRoomDungeon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GameState {

  private static final Logger LOGGER = EscapeRoomDungeon.LOGGER;
  public static GameState INSTANCE;
  private static final String SAVE_PATH = "game_state.json";

  public int testNumber = 5;
  public DungeonLoader.LevelLabel currentLevel = DungeonLoader.LevelLabel.MainMenu;
  public int playerNumber = 0;
  public Point lastHeroPos = null;

  private Map<String, Object> resources = new HashMap<>();

  public GameState(){}

  private static <T> void initResourceObject(Class<?> klass, T obj){
    if(INSTANCE == null) throw new RuntimeException("GameState has not been initialized yet.");
    if(obj == null) throw new IllegalArgumentException("Resource object cannot be null");
    INSTANCE.resources.put(klass.getName(), obj);
  }
  public static <T> T getResourceObject(Class<?> klass, T defaultObj){
    if(INSTANCE == null) throw new RuntimeException("GameState has not been initialized yet.");
    Object obj = INSTANCE.resources.get(klass.getName());
    if(obj == null){
      LOGGER.warning("Didn't find resources object for class: "+klass.getName());
      initResourceObject(klass, defaultObj);
      return defaultObj;
    }
    return (T) obj;
  }


  public void onBeforeApplicationExit(){
    LOGGER.info("Running onBeforeApplicationExit");
    try {
      lastHeroPos = Game.hero().orElseThrow().fetchOrThrow(PositionComponent.class).position();
    }catch(Exception ignored){}
    saveState();
  }

  //=========== STATE LOAD/SAVE ====================
  public static void initialLoadState(){
    if(!loadState()){
      INSTANCE = new GameState();
      saveState();
    }
  }

  public static boolean loadState(){ return loadState(null); }
  public static boolean loadState(String path){
    path = path == null ? SAVE_PATH : path;

    String text = null;
    try {
      text = new String(Files.readAllBytes(Paths.get(path)));
    } catch (IOException ignored){
      return false;
    }

    Json json = new Json();
    INSTANCE = json.fromJson(GameState.class, text);
    LOGGER.info("Loaded resources:\n"+CollectionUtils.mapToString(INSTANCE.resources));
    return true;
  }

  public static void saveState(){ saveState(SAVE_PATH); }
  public static void saveState(String path){
    if(INSTANCE == null) return;

    Json json = new Json();
    String text = json.prettyPrint(INSTANCE);
    LOGGER.info("Writing GameState to file:\n"+text);
    try {
      Files.writeString(Paths.get(path), text);
    } catch (IOException ignored){}
  }
}
