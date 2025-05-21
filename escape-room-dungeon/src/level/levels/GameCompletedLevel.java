package level.levels;

import com.badlogic.gdx.graphics.Color;
import core.Game;
import core.level.elements.tile.DoorTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import entities.*;
import level.EscapeRoomLevel;
import level.utils.DungeonLoader;
import level.utils.LevelLabel;
import modules.dialog.DialogConfig;
import modules.dialog.DialogTriggerFactory;
import systems.TransitionSystem;

import java.util.Map;

public class GameCompletedLevel extends EscapeRoomLevel {



  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout      The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   * @param namedPoints
   */
  public GameCompletedLevel(LevelElement[][] layout, DesignLabel designLabel, Map<String, Point> namedPoints) {
    super(layout, designLabel, namedPoints);
  }

  @Override
  protected void onFirstTick() {
    DoorTile door = (DoorTile) Game.currentLevel().tileAt(getPoint("dungeon-exit"));
    door.close();
    Game.currentLevel().changeTileElementType(Game.currentLevel().endTile(), LevelElement.FLOOR);

    Point triggerStart = getPoint("trigger-start");
    Point triggerEnd = getPoint("trigger-end");
    Game.add(TriggerFactory.createTrigger(triggerStart, triggerEnd.x - triggerStart.x + 1, 1, (e, o, d) -> {
      TransitionSystem.transition(() -> {
        DungeonLoader.loadLevel(LevelLabel.MainMenu, 0);
      }, "Erfolgreich Entkommen!\nDanke fürs Spielen!", 0.5f);
    }, (e, o, d) -> {}));

//    Game.add(DrawTextFactory.createTextEntity("", getPoint("exit-text"), 1, Color.WHITE, 10f, 1f));

    //Deco
    for(int i = 0; i < 8; i++){
      Game.add(DecoFactory.createDeco(getPoint("bushL").add(0, -i), Deco.Bush));
    }
    for(int i = 0; i < 8; i++){
      Game.add(DecoFactory.createDeco(getPoint("bushR").add(0, -i), Deco.Bush));
    }
    listPoints("mushrooms").forEach(tuple -> {
      Game.add(DecoFactory.createDeco(tuple.a(), Deco.Mushrooms0));
      if(tuple.b() % 2 == 0){
        Game.add(DecoFactory.createDeco(tuple.a().add(tuple.b() >= 3 ? 1 : -1, 0), Deco.Mushrooms1));
      }
    });
    listPoints("arch").forEach(tuple -> CompositeDecoFactory.createArch(tuple.a()).forEach(Game::add));
    Game.add(DecoFactory.createDeco(getPoint("bigbush"), Deco.BigBush));

    //Dialog
    Game.add(DialogTriggerFactory.createDialogTrigger(getPoint("dialog0"), 5, 1, new DialogConfig("Du", "Endlich etwas Tageslicht!", "Aber wo ist mein Partner?", "Wahrscheinlich durch einen anderen Ausgang entkommen...", "Nichts wie weg hier!")));
  }

  @Override
  protected void onTick() {

  }
}
