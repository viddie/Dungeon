package entities;

import com.badlogic.gdx.graphics.Color;
import components.LeverComponent;
import components.VicinityComponent;
import components.commands.TintEntityCommand;
import contrib.components.InteractionComponent;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.components.states.State;
import core.components.states.StateMachine;
import core.utils.Point;
import core.utils.components.MissingComponentException;
import core.utils.components.draw.Animation;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utils.Constants;
import utils.ICommand;
import utils.SoundManager;
import utils.Sounds;

/** The LeverFactory class is responsible for creating lever entities. */
public class LeverFactory {

  private static final float DEFAULT_INTERACTION_RADIUS = 1.5f;
  private static final IPath LEVER_TEXTURE_ON = new SimpleIPath("objects/lever/on/lever_0.png");
  private static final IPath LEVER_TEXTURE_OFF = new SimpleIPath("objects/lever/off/lever_0.png");

  /**
   * Creates a lever entity at a given position, with a specified behavior when interacted with. The
   * lever is initially off. The lever is interactable and can be toggled on and off.
   *
   * @param pos The position where the lever will be created.
   * @param onInteract The behavior when the lever is interacted with. (isOn, lever, who)
   * @return The created lever entity.
   * @see components.LeverComponent LeverComponent
   * @see systems.LeverSystem LeverSystem
   */
  public static Entity createLever(Point pos, ICommand onInteract) {
    Entity lever = new Entity("lever");

    lever.add(new PositionComponent(pos.add(Constants.X_OFFSET, Constants.Y_OFFSET)));

    List<State> states = new ArrayList<>();
    states.add(new State("off", LEVER_TEXTURE_OFF));
    states.add(new State("on", LEVER_TEXTURE_ON));
    StateMachine sm = new StateMachine(states);
    sm.addTransition("off", "on", "on");
    sm.addTransition("on", "off", "off");
    DrawComponent dc = new DrawComponent(sm);

    lever.add(dc);
    lever.add(new LeverComponent(false, onInteract));
    lever.add(new VicinityComponent(DEFAULT_INTERACTION_RADIUS, new TintEntityCommand(lever), Game.hero().orElseThrow()));
    lever.add(
        new InteractionComponent(
            DEFAULT_INTERACTION_RADIUS,
            true,
            (entity, who) -> {
              LeverComponent lc = entity.fetchOrThrow(LeverComponent.class);
              lc.toggle();
              entity.fetchOrThrow(DrawComponent.class).sendSignal(lc.isOn() ? "off" : "on");
              SoundManager.playSound(Sounds.LeverFlipped);
            }));
    return lever;
  }
}
