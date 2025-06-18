package level.levels;

import com.badlogic.gdx.Input;
import core.Entity;
import core.Game;
import core.level.elements.tile.DoorTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import entities.DrawTextFactory;
import entities.LeverFactory;
import entities.TeleporterFactory;

import java.util.Map;

import level.EscapeRoomLevel;
import level.utils.LevelLabel;
import utils.ICommand;

/** The Tutorial Level. */
public class TutorialLevel extends EscapeRoomLevel {

  private DoorTile interactDoor;

  /**
   * Constructs the Tutorial Level.
   *
   * @param layout The layout of the level.
   * @param designLabel The design label of the level.
   */
  public TutorialLevel(LevelElement[][] layout, DesignLabel designLabel, Map<String, Point> namedPoints) {
    super(layout, designLabel, namedPoints);
  }

  @Override
  protected void onFirstTick() {
//    ((ExitTile) endTile()).open();
    Game.add(TeleporterFactory.createTeleporter(getPoint("teleporter"), LevelLabel.MainMenu, new Point(9.5f, 7.5f), null, 2));

    String movementKeys =
        Input.Keys.toString(core.configuration.KeyboardConfig.MOVEMENT_UP.value())
            + Input.Keys.toString(core.configuration.KeyboardConfig.MOVEMENT_LEFT.value())
            + Input.Keys.toString(core.configuration.KeyboardConfig.MOVEMENT_DOWN.value())
            + Input.Keys.toString(core.configuration.KeyboardConfig.MOVEMENT_RIGHT.value());
    String message = "Verwende " + movementKeys + ", um dich zu bewegen.";
    Game.add(DrawTextFactory.createTextEntity(message, getPoint("move-text"), 0.7f));

//    Game.add(DrawTextFactory.createTextEntity("Manche Sachen kannst du\nmit LMB anklicken", new Point(20, 6), 0.7f));

    Point leverPos = getPoint("interact-lever");
    if(leverPos != null){
      interactDoor = (DoorTile) Game.currentLevel().tileAt(getPoint("interact-door"));
      interactDoor.close();
      Entity lever = LeverFactory.createLever(leverPos, new ICommand() {
        @Override
        public void execute() { interactDoor.open(); }
        @Override
        public void undo() { interactDoor.close(); }
      });
      Game.add(lever);
      Game.add(DrawTextFactory.createTextEntity("Mit E interagierst du mit\nSachen in der Umgebung", getPoint("interact-text"), 0.7f));
    }

    Game.add(DrawTextFactory.createTextEntity("Das Spiel ist in mehrere Level aufgeteilt. Das\nEnde jedes Levels ist eine Tür zum nächsten Level.", getPoint("level-text"), 0.7f));
  }

  @Override
  protected void onTick() {

  }
}
