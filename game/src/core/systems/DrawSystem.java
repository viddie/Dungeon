package core.systems;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import core.Entity;
import core.Game;
import core.System;
import core.components.DrawComponent;
import core.components.PlayerComponent;
import core.components.PositionComponent;
import core.level.Tile;
import core.utils.components.MissingComponentException;
import core.utils.components.draw.Animation;
import core.utils.components.draw.Painter;
import core.utils.components.draw.PainterConfig;
import core.utils.components.path.IPath;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This system draws the entities on the screen.
 *
 * <p>Each entity with a {@link DrawComponent} and a {@link PositionComponent} will be drawn on the
 * screen.
 *
 * <p>The system will get the current animation from the {@link DrawComponent} and will get the next
 * animation frame from the {@link Animation}, and then draw it on the current position stored in
 * the {@link PositionComponent}.
 *
 * <p>This system will not queue animations. This must be done by other systems. The system
 * evaluates the queue and draws the animation with the highest priority in the queue.
 *
 * <p>The DrawSystem can't be paused.
 *
 * @see DrawComponent
 * @see Animation
 * @see Painter
 */
public final class DrawSystem extends System {

  /**
   * The batch is necessary to draw ALL the stuff. Every object that uses draw need to know the
   * batch.
   */
  private static final SpriteBatch BATCH = new SpriteBatch();

  /** Draws objects. */
  private static final Painter PAINTER = new Painter(BATCH);

  private List<DSData> sortedEntities;

  /** Create a new DrawSystem. */
  public DrawSystem() {
    super(DrawComponent.class, PositionComponent.class);
    onEntityAdd = this::updateList;
    onEntityRemove = this::updateList;
  }

  /**
   * Get the {@link Painter} that is used by this system.
   *
   * @return the {@link #PAINTER} of the DrawSystem
   */
  public static Painter painter() {
    return PAINTER;
  }

  /**
   * Get the {@link SpriteBatch} that is used by this system.
   *
   * @return the {@link #BATCH} of the DrawSystem
   */
  public static SpriteBatch batch() {
    return BATCH;
  }

  private void updateList(Entity changed){
    sortedEntities = filteredEntityStream(DrawComponent.class, PositionComponent.class)
      .map(this::buildDataObject).collect(Collectors.toList());
//      .sorted(Comparator.comparingInt(data -> data.dc.depth())).collect(Collectors.toList());
  }
  public void recalculateDepths(){
    updateList(null);
  }

  /**
   * Will draw entities at their position with their current animation.
   *
   * <p>All entities with a {@link PlayerComponent} will be drawn on top.
   *
   * @see DrawComponent
   * @see Animation
   */
  @Override
  public void execute() {
    BATCH.setProjectionMatrix(CameraSystem.camera().combined);
    PAINTER.batch().begin();

    //Optimization idea: Create a depths map which partitions all entities by depth, iterate the keys sorted in here
    //and only sort each value in the depths map. Should dramatically reduce the amount of position comparisons
    //if many entities are in the game.
    sortedEntities.stream().filter(this::shouldDraw).sorted((o1, o2) -> {
      int depthComparison = Integer.compare(o1.dc.depth(), o2.dc.depth());
      if (depthComparison != 0) {
        return depthComparison;
      }
      return Double.compare(o2.pc.position().y, o1.pc.position().y);
    }).forEach(this::draw);

    PAINTER.batch().end();
  }

  /**
   * Checks if an entity should be drawn. By checking:
   *
   * <ol>
   *   <li>The tile the entity is on is visible
   *   <li>The entity itself is visible
   * </ol>
   *
   * @param data the entity to check
   * @return true if the entity should be drawn, false otherwise
   * @see DrawComponent#isVisible()
   */
  private boolean shouldDraw(DSData data) {
    if (Game.currentLevel().tileAt(data.pc.position()) == null) {
      return false;
    }

    if (!data.dc.isVisible()) return false;

    Tile tile = Game.currentLevel().tileAt(data.pc.position());
    return tile.visible();
  }

  private void draw(final DSData dsd) {
    dsd.dc.update();
    Sprite sprite = dsd.dc.getSprite();
    PainterConfig conf = new PainterConfig(0, 0, dsd.dc.getSpriteWidth(), dsd.dc.getSpriteHeight(), dsd.dc.tintColor());
    if(dsd.dc.currentAnimation().getConfig().centered()){
      conf = new PainterConfig(-dsd.dc.getSpriteWidth() / 2, -dsd.dc.getSpriteHeight() / 2, dsd.dc.getSpriteWidth(), dsd.dc.getSpriteHeight(), dsd.dc.tintColor());
    }
    PAINTER.draw(dsd.pc.position(), sprite, conf, dsd.pc.rotation());
  }

  /** DrawSystem can't be paused. */
  @Override
  public void stop() {
    run = true;
  }

  private DSData buildDataObject(final Entity entity) {
    return new DSData(entity, entity.fetchOrThrow(DrawComponent.class), entity.fetchOrThrow(PositionComponent.class));
  }
  private record DSData(Entity e, DrawComponent dc, PositionComponent pc) {}
}
