package modules.showimage;

import components.VicinityComponent;
import components.commands.TintEntityCommand;
import contrib.components.InteractionComponent;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.components.draw.Animation;
import core.utils.components.path.SimpleIPath;
import utils.Constants;

import java.util.function.BiConsumer;

public class ShowImageFactory {

  private static final float INTERACTION_RADIUS = 1.5f;

  /**
   * Creates a keypad at the designated position.
   *
   * @param pos The position where the lever will be created.
   * @return The created keypad entity.
   */
  public static Entity createShowImage(Point pos, String spriteImage, String imagePath, BiConsumer<Entity, Entity> onClose, float maxSize, float radius, ShowImageText text) {
    Entity entity = new Entity("show-image");
    entity.add(new PositionComponent(pos.add(Constants.X_OFFSET, Constants.Y_OFFSET)));

    DrawComponent dc = new DrawComponent(Animation.fromSingleImage(new SimpleIPath(spriteImage)));
    entity.add(dc);

    ShowImageComponent sic = new ShowImageComponent(imagePath);
    sic.onCloseAction = onClose;
    sic.maxSize = maxSize;
    sic.textConfig = text;
    entity.add(sic);

    entity.add(new VicinityComponent(radius, new TintEntityCommand(entity), Game.hero().orElseThrow()));
    entity.add(new InteractionComponent(radius, true, (e, who) -> sic.isUIOpen = true));
    return entity;
  }
  public static Entity createShowImage(Point pos, String spriteImage, String imagePath, BiConsumer<Entity, Entity> onClose, float maxSize, float radius) {
    return createShowImage(pos, spriteImage, imagePath, onClose, maxSize, radius, null);
  }
  public static Entity createShowImage(Point pos, String spriteImage, String imagePath, BiConsumer<Entity, Entity> onClose, float maxSize) {
    return createShowImage(pos, spriteImage, imagePath, onClose, maxSize, INTERACTION_RADIUS, null);
  }
  public static Entity createShowImage(Point pos, String spriteImage, String imagePath, float maxSize) {
    return createShowImage(pos, spriteImage, imagePath, null, maxSize, INTERACTION_RADIUS, null);
  }
  public static Entity createShowImage(Point pos, String spriteImage, String imagePath) {
    return createShowImage(pos, spriteImage, imagePath, null, 0.85f, INTERACTION_RADIUS, null);
  }
}
