package modules.keypad;

import contrib.components.InteractionComponent;
import contrib.components.UIComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.IVoidFunction;
import core.utils.Point;
import core.utils.components.draw.Animation;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;
import utils.Constants;

import java.util.List;
import java.util.Map;

public class KeypadFactory {

  private static final float DEFAULT_INTERACTION_RADIUS = 2f;
  private static final IPath TEXTURE_ON = new SimpleIPath("objects/keypad/on.png");
  private static final IPath TEXTURE_OFF = new SimpleIPath("objects/keypad/off.png");

  /**
   * Creates a keypad at the designated position.
   *
   * @param pos The position where the lever will be created.
   * @param correctDigits The correct digits that will start the action if entered
   * @param action The action to execute when the correct digits are entered
   * @return The created keypad entity.
   */
  public static Entity createKeypad(Point pos, List<Integer> correctDigits, IVoidFunction action, boolean showDigitCount) {
    Entity entity = new Entity("keypad");

    entity.add(new PositionComponent(pos.add(Constants.X_OFFSET, Constants.Y_OFFSET)));
    DrawComponent dc = new DrawComponent(Animation.fromSingleImage(TEXTURE_OFF));
    Map<String, Animation> animationMap =
      Map.of("off", dc.currentAnimation(), "on", Animation.fromSingleImage(TEXTURE_ON));
    dc.animationMap(animationMap);
    dc.currentAnimation("off");
    entity.add(dc);
    entity.add(new KeypadComponent(correctDigits, action, showDigitCount));
    entity.add(
      new InteractionComponent(
        DEFAULT_INTERACTION_RADIUS,
        true,
        (e, who) -> {
          KeypadComponent kc = entity.fetchOrThrow(KeypadComponent.class);
          kc.isUIOpen = true;
          entity.fetchOrThrow(DrawComponent.class).currentAnimation(kc.isUIOpen ? "on" : "off");
        }));
    return entity;
  }
}
