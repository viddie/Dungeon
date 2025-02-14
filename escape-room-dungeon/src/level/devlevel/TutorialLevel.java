package level.devlevel;

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
import entities.MonsterType;
import hud.DebugOverlay;
import hud.HUDText;
import item.concreteItem.ItemPotionWater;
import item.concreteItem.ItemResourceMushroomRed;
import java.io.IOException;
import java.util.List;
import level.EscapeRoomLevel;
import puzzles.simpleLevers.SimpleLeverPuzzle;
import systems.TickableSystem;
import utils.EntityUtils;

/** The Tutorial Level. */
public class TutorialLevel extends EscapeRoomLevel {

  /**
   * Constructs the Tutorial Level.
   *
   * @param layout The layout of the level.
   * @param designLabel The design label of the level.
   */
  public TutorialLevel(LevelElement[][] layout, DesignLabel designLabel) {
    super(layout, designLabel);
  }

  @Override
  protected void onFirstTick() {
    ((ExitTile) endTile()).open();
    pitTiles().forEach(PitTile::open);

    String movementKeys =
        Input.Keys.toString(core.configuration.KeyboardConfig.MOVEMENT_UP.value())
            + Input.Keys.toString(core.configuration.KeyboardConfig.MOVEMENT_LEFT.value())
            + Input.Keys.toString(core.configuration.KeyboardConfig.MOVEMENT_DOWN.value())
            + Input.Keys.toString(core.configuration.KeyboardConfig.MOVEMENT_RIGHT.value());
    String message = "Verwende " + movementKeys + " (oder RMB),\num dich zu bewegen.";


    DebugOverlay overlay = new DebugOverlay();
    overlay.addDebugOverlayToGame(1.5f);
    TickableSystem.register(overlay);

    Game.add(DrawTextFactory.createTextEntity(message, new Point(5, 6), 0.7f));
    Game.add(DrawTextFactory.createTextEntity("Manche Sachen kannst du\nmit LMB anklicken", new Point(20, 6), 0.7f));
    Game.add(DrawTextFactory.createTextEntity("Lange Nachricht die etwas\nversteckter im Level ist", new Point(30, 9), 0.5f, Color.RED, 7, 0.2f));

    SimpleLeverPuzzle puzzle = new SimpleLeverPuzzle(new Point(59, 4));
    puzzle.load();
  }

  @Override
  protected void onTick() {

  }
}
