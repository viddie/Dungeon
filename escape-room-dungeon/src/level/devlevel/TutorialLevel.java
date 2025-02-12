package level.devlevel;

import com.badlogic.gdx.Input;
import contrib.components.HealthComponent;
import contrib.components.InventoryComponent;
import contrib.components.UIComponent;
import contrib.configuration.KeyboardConfig;
import contrib.entities.MiscFactory;
import contrib.hud.DialogUtils;
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
import hud.HUDText;
import item.concreteItem.ItemPotionWater;
import item.concreteItem.ItemResourceMushroomRed;
import java.io.IOException;
import java.util.List;
import level.EscapeRoomLevel;
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
    DialogUtils.showTextPopup(
        "Verwende " + movementKeys + " (oder RMB), um dich zu bewegen.", "Bewegung");

    Entity chest;
    try {
      chest = MiscFactory.newChest(MiscFactory.FILL_CHEST.EMPTY);
    } catch (IOException e) {
      throw new RuntimeException("Failed to create tutorial chest");
    }

    Entity debugOverlay = new Entity();
    HUDText hudText = new HUDText("This is a test string",  HUDText.Anchor.TopLeft, 2);
    HUDText hudText2 = new HUDText("A different text",  HUDText.Anchor.BottomLeft, 2);
    HUDText hudText3 = new HUDText("A third text anchored\nelsewhere",  HUDText.Anchor.BottomRight, 2);
    debugOverlay.add(new UIComponent(new GUICombination(hudText, hudText2, hudText3), false, false));
    Game.add(debugOverlay);

    setupChest(chest);
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
