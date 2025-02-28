package systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import contrib.components.CollideComponent;
import contrib.utils.components.skill.SkillTools;
import core.Entity;
import core.Game;
import core.System;
import core.components.VelocityComponent;
import core.level.Tile;
import core.level.TileLevel;
import core.level.elements.ILevel;
import core.level.utils.Coordinate;
import core.level.utils.LevelElement;
import core.systems.LevelSystem;
import core.systems.VelocitySystem;
import core.utils.Point;

import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

import hud.DebugOverlay;
import level.EscapeRoomLevel;
import level.utils.DungeonSaver;
import starter.EscapeRoomDungeon;
import utils.Constants;

/**
 * The LevelEditorSystem is responsible for handling the level editor. It allows the user to change
 * the {@link EscapeRoomLevel} layout by setting different tiles. The user can set the following
 * tiles: skip, pit, floor, wall, hole, exit, door, and custom points. The user can also fill an
 * area with floor tiles and save the current dungeon.
 */
public class LevelEditorSystem extends System {

  private static final Logger LOGGER = EscapeRoomDungeon.LOGGER;

  private static final int SKIP_BUTTON = Input.Keys.NUM_1;
  private static final int PIT_BUTTON = Input.Keys.NUM_2;
  private static final int FLOOR_BUTTON = Input.Keys.NUM_3;
  private static final int WALL_BUTTON = Input.Keys.NUM_4;
  private static final int HOLE_BUTTON = Input.Keys.NUM_5;
  private static final int EXIT_BUTTON = Input.Keys.NUM_6;
  private static final int DOOR_BUTTON = Input.Keys.NUM_7;
  private static final int FILL_WITH_FLOOR = Input.Keys.NUM_9;
  private static final int TOGGLE_ACTIVE = Input.Keys.NUM_0;
  private static final int SAVE_BUTTON = Input.Keys.Y;

  //Complex actions
  private static final int ADD_WIDTH_BUTTON = Input.Keys.G;
  private static final int ADD_HEIGHT_BUTTON = Input.Keys.H;
  private static final int SHIFT_MODIFIER = Input.Keys.CONTROL_RIGHT;
  private static final int TOGGLE_CUSTOM_POINT = Input.Keys.J;

  private static final int maxFillRange = 100;
  private static boolean active = false;

  /**
   * Gets the active status of the LevelEditorSystem.
   *
   * @return true if the LevelEditorSystem is active, false if not.
   */
  public static boolean active() {
    return active;
  }

  /**
   * Sets the active status of the LevelEditorSystem.
   *
   * @param active The active status to set.
   */
  public static void active(boolean active) {
    LevelEditorSystem.active = active;
  }

  @Override
  public void execute() {
    if (Gdx.input.isKeyJustPressed(TOGGLE_ACTIVE)) {
      active = !active;
      Entity hero = Game.hero().orElseThrow();
      VelocityComponent vc = hero.fetchOrThrow(VelocityComponent.class);
      vc.hasNoClip(active);
    }
    if (!active) {
      return;
    } else {
      DebugOverlay.drawText("~ EDIT MODE ~");
    }
    if (Gdx.input.isKeyPressed(SKIP_BUTTON)) {
      setTile(LevelElement.SKIP);
    }
    if (Gdx.input.isKeyPressed(PIT_BUTTON)) {
      setTile(LevelElement.PIT);
    }
    if (Gdx.input.isKeyPressed(FLOOR_BUTTON)) {
      setTile(LevelElement.FLOOR);
    }
    if (Gdx.input.isKeyPressed(WALL_BUTTON)) {
      setTile(LevelElement.WALL);
    }
    if (Gdx.input.isKeyPressed(HOLE_BUTTON)) {
      setTile(LevelElement.HOLE);
    }
    if (Gdx.input.isKeyJustPressed(EXIT_BUTTON)) {
      setTile(LevelElement.EXIT);
    }
    if (Gdx.input.isKeyJustPressed(DOOR_BUTTON)) {
      setTile(LevelElement.DOOR);
    }
    if (Gdx.input.isKeyJustPressed(SAVE_BUTTON)) {
      if (Game.currentLevel() instanceof EscapeRoomLevel) {
        DungeonSaver.saveCurrentDungeon();
      } else {
        java.lang.System.out.println(Game.currentLevel().printLevel());
      }
    }
    if (Gdx.input.isKeyJustPressed(FILL_WITH_FLOOR)) {
      fillWithFloor();
    }

    if(Gdx.input.isKeyPressed(SHIFT_MODIFIER)){
      int x = 0, y = 0;
      if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
        y = 1;
      } else if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
        x = -1;
      } else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
        y = -1;
      } else if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
        x = 1;
      }
      shiftLevel(x, y);
    }
    if(Gdx.input.isKeyJustPressed(ADD_WIDTH_BUTTON)){
      addSize(1, 0);
    }
    if(Gdx.input.isKeyJustPressed(ADD_HEIGHT_BUTTON)){
      addSize(0, 1);
    }
    if(Gdx.input.isKeyJustPressed(TOGGLE_CUSTOM_POINT)){
      toggleCustomPoint();
    }
  }

  /**
   * Floods area with floor tiles. Only affects tiles within a certain range of the cursor and only
   * skip tiles. It uses a queue to flood the area.
   */
  private void fillWithFloor() {
    Point mosPos = SkillTools.cursorPositionAsPoint();
    mosPos = new Point(mosPos.x - 0.5f, mosPos.y - 0.25f);
    Tile startTile = LevelSystem.level().tileAt(mosPos);
    if (startTile == null) {
      return;
    }
    Queue<Tile> queue = new java.util.LinkedList<>();
    queue.add(startTile);
    int range = 0;
    while (!queue.isEmpty() && range < maxFillRange) {
      Tile currentTile = queue.poll();
      if (currentTile == null) {
        continue;
      }
      if (currentTile.levelElement() == LevelElement.SKIP
          || currentTile.levelElement() == LevelElement.FLOOR) {
        LevelSystem.level().changeTileElementType(currentTile, LevelElement.FLOOR);
        queue.add(
            LevelSystem.level()
                .tileAt(
                    new Coordinate(currentTile.coordinate().x + 1, currentTile.coordinate().y)));
        queue.add(
            LevelSystem.level()
                .tileAt(
                    new Coordinate(currentTile.coordinate().x - 1, currentTile.coordinate().y)));
        queue.add(
            LevelSystem.level()
                .tileAt(
                    new Coordinate(currentTile.coordinate().x, currentTile.coordinate().y + 1)));
        queue.add(
            LevelSystem.level()
                .tileAt(
                    new Coordinate(currentTile.coordinate().x, currentTile.coordinate().y - 1)));
      }
      range++;
    }
  }

  private void setTile(LevelElement element) {
    Point mosPos = SkillTools.cursorPositionAsPoint();
    mosPos = new Point(mosPos.x - 0.5f, mosPos.y - 0.25f);
    Tile mouseTile = LevelSystem.level().tileAt(mosPos);
    if (mouseTile == null) {
      return;
    }
    LevelSystem.level().changeTileElementType(mouseTile, element);
  }

  private void addSize(int addX, int addY){
    LOGGER.info("Resizing layout: x + "+addX+", y + "+addY);

    TileLevel l = (TileLevel) Game.currentLevel();
    Tile[][] layout = l.layout();

    int rows = layout.length;
    int cols = layout[0].length;

    int newRows = rows + addY;
    int newCols = cols + addX;

    LevelElement[][] newLayout = new LevelElement[newRows][newCols];

    for (int i = 0; i < newRows; i++) {
      for (int j = 0; j < newCols; j++) {
        if(i == rows || j == cols){
          newLayout[i][j] = LevelElement.SKIP;
        } else {
          newLayout[i][j] = layout[i][j].levelElement();
        }
      }
    }

    l.setLayout(newLayout);
  }

  private void shiftLevel(int x, int y){
    if(x == 0 && y == 0) return;

    LOGGER.info("Shifting level by: x="+x+", y="+y);

    ILevel l = Game.currentLevel();
    Tile[][] layout = l.layout();

    //ALGORITHM: shift all tiles in the layout by x and y, which are either 1, 0 or -1

    int rows = layout.length;
    int cols = layout[0].length;

    LevelElement[][] newLayout = new LevelElement[rows][cols];

    // Iterate through the current layout and shift tiles accordingly
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        int newI = i - y;
        int newJ = j - x;

        // Check if the new position is within bounds
        if (newI >= 0 && newI < rows && newJ >= 0 && newJ < cols) {
          newLayout[i][j] = layout[newI][newJ].levelElement();
        } else {
          newLayout[i][j] = LevelElement.SKIP; // Empty space for out-of-bounds tiles
        }
      }
    }

    // Copy the shifted layout back to the original layout
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        l.changeTileElementType(layout[i][j], newLayout[i][j]);
      }
    }

    //Set all tiles again to fix the sprites
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        l.changeTileElementType(layout[i][j], layout[i][j].levelElement());
      }
    }

    //Shift all named points
    Map<String, Point> namedPoints = ((EscapeRoomLevel)Game.currentLevel()).getNamedPoints();
    namedPoints.forEach((s, p) -> {
      p.x = p.x + x;
      p.y = p.y + y;
    });
  }

  private void toggleCustomPoint(){
    Point mosPos = SkillTools.cursorPositionAsPoint();
    mosPos = new Point(mosPos.x, mosPos.y);
    Point mosPosOffset = Constants.ioffset(mosPos);
    int mouseXInt = (int)mosPosOffset.x;
    int mouseYInt = (int)mosPosOffset.y;
    Point tilePos = new Point(mouseXInt, mouseYInt);

    Map<String, Point> namedPoints = ((EscapeRoomLevel)Game.currentLevel()).getNamedPoints();
    String key = namedPoints.entrySet().stream()
      .filter(entry -> entry.getValue().equals(tilePos))
      .map(Map.Entry::getKey)
      .findFirst()
      .orElse(null);

    if(key == null){
      //Didnt exist yet
      int i = 0;
      String newKey = "Point"+i;
      while(namedPoints.get(newKey) != null){
        i++;
        newKey = "Point"+i;
      }
      namedPoints.put(newKey, tilePos);

    } else {
      //Did exist
      namedPoints.remove(key);
    }
  }
}
