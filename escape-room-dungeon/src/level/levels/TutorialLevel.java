package level.levels;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import components.DrawTextComponent;
import contrib.components.HealthComponent;
import contrib.components.InventoryComponent;
import contrib.components.UIComponent;
import contrib.configuration.KeyboardConfig;
import contrib.entities.MiscFactory;
import contrib.hud.DialogUtils;
import contrib.hud.UIUtils;
import contrib.hud.elements.GUICombination;
import contrib.item.HealthPotionType;
import contrib.item.concreteItem.ItemPotionHealth;
import contrib.utils.components.skill.SkillTools;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.level.Tile;
import core.level.elements.tile.DoorTile;
import core.level.elements.tile.ExitTile;
import core.level.elements.tile.PitTile;
import core.level.utils.Coordinate;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.MissingHeroException;
import core.utils.Point;
import core.utils.components.MissingComponentException;
import entities.DrawTextFactory;
import entities.LeverFactory;
import entities.MonsterType;
import entities.TeleporterFactory;
import hud.DebugOverlay;
import hud.HUDText;
import item.concreteItem.ItemPotionWater;
import item.concreteItem.ItemResourceMushroomRed;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import level.EscapeRoomLevel;
import level.utils.DungeonLoader;
import level.utils.LevelLabel;
import puzzles.simpleLevers.SimpleLeverPuzzle;
import systems.TickableSystem;
import utils.EntityUtils;
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
