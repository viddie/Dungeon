package utils;

import com.badlogic.gdx.utils.Json;
import core.Game;
import core.components.PositionComponent;
import core.utils.Point;
import level.utils.DungeonLoader;
import level.utils.LevelLabel;
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

  private LevelLabel currentLevel = LevelLabel.MainMenu;
  private int playerNumber = 0;
  private Point lastHeroPos = null;
  private int volumeMaster = 50;
  private int volumeBgm = 50;
  private int volumeSfx = 50;

  private Map<String, Object> resources = new HashMap<>();

  public GameState(){}

  public static LevelLabel currentLevel(){
    return INSTANCE.currentLevel;
  }
  public static void currentLevel(LevelLabel label){
    INSTANCE.currentLevel = label;
  }
  public static int playerNumber(){
    return INSTANCE.playerNumber;
  }
  public static void playerNumber(int playerNumber){
    INSTANCE.playerNumber = playerNumber;
  }
  public static Point lastHeroPos(){
    return INSTANCE.lastHeroPos;
  }
  public static void lastHeroPos(Point lastHeroPos){
    INSTANCE.lastHeroPos = lastHeroPos;
  }
  public static int volumeMaster() { return INSTANCE.volumeMaster; }
  public static void volumeMaster(int volumeMaster) {
    INSTANCE.volumeMaster = volumeMaster;
    EscapeRoomDungeon.updateBgmVolume();
  }
  public static int volumeBgm() { return INSTANCE.volumeBgm; }
  public static void volumeBgm(int volumeBgm) {
    INSTANCE.volumeBgm = volumeBgm;
    EscapeRoomDungeon.updateBgmVolume();
  }
  public static int volumeSfx() { return INSTANCE.volumeSfx; }
  public static void volumeSfx(int volumeSfx) {
    INSTANCE.volumeSfx = volumeSfx;
  }

  private static <T> void initResourceObject(String identifier, T obj){
    if(INSTANCE == null) throw new RuntimeException("GameState has not been initialized yet.");
    if(obj == null) throw new IllegalArgumentException("Resource object cannot be null");
    INSTANCE.resources.put(identifier, obj);
  }
  public static <T> T getResourceObject(String identifier, T defaultObj){
    if(INSTANCE == null) throw new RuntimeException("GameState has not been initialized yet.");
    Object obj = INSTANCE.resources.get(identifier);
    if(obj == null){
      LOGGER.warning("Didn't find resources object for identifier: "+identifier);
      initResourceObject(identifier, defaultObj);
      return defaultObj;
    }
    return (T) obj;
  }


  public void onBeforeApplicationExit(){
    LOGGER.info("Running onBeforeApplicationExit");
    try {
      //Store lastHeroPos only if we are in an actual level, and not main menu / settings
      if(DungeonLoader.getCurrentLabel().isActualLevel){
        lastHeroPos = Game.hero().orElseThrow().fetchOrThrow(PositionComponent.class).position();
      }
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
