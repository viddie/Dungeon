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
import entities.MonsterType;
import hud.DebugOverlay;
import hud.HUDText;
import item.concreteItem.ItemPotionWater;
import item.concreteItem.ItemResourceMushroomRed;
import java.io.IOException;
import java.util.List;
import level.EscapeRoomLevel;
import systems.TickableSystem;
import utils.EntityUtils;

/** The Tutorial Level. */
public class TutorialLevel extends EscapeRoomLevel {

  // Entity spawn points
  private final Point chestSpawn;
  private Coordinate lastHeroCoords = new Coordinate(0, 0);

  /**
   * Constructs the Tutorial Level.
   *
   * @param layout The layout of the level.
   * @param designLabel The design label of the level.
   * @param customPoints The custom points of the level.
   */
  public TutorialLevel(
      LevelElement[][] layout, DesignLabel designLabel, List<Coordinate> customPoints) {
    super(
        layout,
        designLabel,
        customPoints,
        "Tutorial",
        "Willkommen im Tutorial! Hier lernst Du die Grundlagen des Spiels kennen.");

    this.chestSpawn = customPoints.get(0).toCenteredPoint();
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

    Entity inWorldText = new Entity();
    inWorldText.add(new DrawTextComponent(message, 0.7f, Color.WHITE));
    inWorldText.add(new PositionComponent(5, 6));
    Game.add(inWorldText);

    Entity inWorldText2 = new Entity();
    inWorldText2.add(new DrawTextComponent("Click stuff with left mouse button", 0.7f, Color.WHITE));
    inWorldText2.add(new PositionComponent(20, 6));
    Game.add(inWorldText2);

    Entity iwt3 = new Entity();
    iwt3.add(new DrawTextComponent("Very long text message\nDisplayed in the game", 0.5f, Color.RED));
    iwt3.fetchOrThrow(DrawTextComponent.class).displayRadius(7);
    iwt3.fetchOrThrow(DrawTextComponent.class).maxAlpha(0.2f);
    iwt3.add(new PositionComponent(30, 9));
    Game.add(iwt3);
  }

  @Override
  protected void onTick() {
    if (lastHeroCoords != null && !lastHeroCoords.equals(EntityUtils.getHeroCoords())) {
      // Only handle text popups if the hero has moved
      handleTextPopups();
    }
    handleDoors();
    this.lastHeroCoords = EntityUtils.getHeroCoords();
  }

  private void handleTextPopups() {
    DoorTile frontDoor = (DoorTile) tileAt(customPoints().get(1));
    if (EntityUtils.getHeroCoords() == null) return;
    Tile heroTile = tileAt(EntityUtils.getHeroCoords());
    if (heroTile == null) return;

    if (frontDoor.coordinate().equals(heroTile.coordinate())) {
      DialogUtils.showTextPopup(
          "Mit "
              + Input.Keys.toString(KeyboardConfig.FIRST_SKILL.value())
              + " (oder LMB) kannst du angreifen.",
          "Kampf");
    }
  }

  /**
   * Handles the opening of the doors. Except for the mob door, this is handle in the onDeath of the
   * mob.
   */
  private void handleDoors() {
    DoorTile frontDoor = (DoorTile) tileAt(customPoints().get(1));
    Point heroPos;
    try {
      heroPos = SkillTools.heroPositionAsPoint();
    } catch (MissingHeroException e) {
      return;
    }
    if (!frontDoor.isOpen() && frontDoor.position().distance(heroPos) < 2) {
      frontDoor.open();
    }

    Entity hero = Game.hero().orElseThrow(MissingHeroException::new);
    InventoryComponent ic =
        hero.fetch(InventoryComponent.class)
            .orElseThrow(() -> MissingComponentException.build(hero, InventoryComponent.class));
  }

  /**
   * Sets up the chest with the necessary items for the tutorial.
   *
   * @param chest The chest to set up
   */
  private void setupChest(Entity chest) {
    PositionComponent pc = chest.fetchOrThrow(PositionComponent.class);
    InventoryComponent ic = chest.fetchOrThrow(InventoryComponent.class);

    pc.position(chestSpawn);
    ic.add(new ItemPotionWater());
    ic.add(new ItemResourceMushroomRed());

    Game.add(chest);
  }
}
