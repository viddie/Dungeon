package entities;

import components.TorchComponent;
import contrib.components.InteractionComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.components.states.State;
import core.components.states.StateMachine;
import core.utils.Point;
import core.utils.components.MissingComponentException;
import core.utils.components.draw.Animation;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * This class is responsible for creating torch entities in the game. It defines the default
 * interaction radius and the textures for the torch when it is on and off.
 */
public class TorchFactory {

  private static final float DEFAULT_INTERACTION_RADIUS = 2.5f;
  private static final IPath TORCH_TEXTURE_OFF = new SimpleIPath("objects/torch/off/torch_0.png");
  private static final IPath TORCH_TEXTURE_ON = new SimpleIPath("objects/torch/on");

  /**
   * Creates a torch entity at a given position, with a specified initial state (lit or not),
   * interactability, interaction behavior, and value.
   *
   * @param pos The position where the torch will be created.
   * @param lit The initial state of the torch (true if the torch is lit, false otherwise).
   * @param isInteractable Whether the torch can be interacted with.
   * @param onInteract The behavior when the torch is interacted with.
   * @param value The value of the torch.
   * @return The created torch entity.
   */
  public static Entity createTorch(
      Point pos,
      boolean lit,
      boolean isInteractable,
      BiConsumer<Entity, Entity> onInteract,
      int value) {
    Entity torch = new Entity("torch");
    torch.add(new PositionComponent(pos));
    torch.add(new TorchComponent(lit, value));

    State stOff = new State("off", TORCH_TEXTURE_OFF);
    State stOn = new State("on", TORCH_TEXTURE_ON);
    StateMachine sm = new StateMachine(Arrays.asList(stOff, stOn));
    sm.addTransition(stOff, "on", stOn);
    sm.addTransition(stOn, "off", stOff);
    DrawComponent dc = new DrawComponent(sm);
    torch.add(dc);

    if (isInteractable)
      torch.add(
          new InteractionComponent(
              DEFAULT_INTERACTION_RADIUS,
              true,
              (entity, who) -> {
                TorchComponent tc = entity.fetchOrThrow(TorchComponent.class);
                tc.toggle();
                entity.fetchOrThrow(DrawComponent.class).sendSignal(tc.lit() ? "on" : "off");
                onInteract.accept(entity, who);
              }));

    return torch;
  }

  /**
   * Creates an anti-torch entity at a given position, with a specified initial state (lit or not),
   *
   * <p>Anti-torches are torches that, if lit, will amplify the effects of {@link
   * systems.FogOfWarSystem FogOfWar} in the game.
   *
   * @param pos The position where the torch will be created.
   * @param lit The initial state of the torch (true if the torch is lit, false otherwise).
   * @param isInteractable Whether the torch can be interacted with.
   * @param onInteract The behavior when the torch is interacted with.
   * @param value The value of the torch.
   * @return The created torch entity.
   * @see systems.FogOfWarSystem FogOfWarSystem
   * @see level.devlevel.IllusionRiddleLevel IllusionRiddleLevel
   */
  public static Entity createAntiTorch(
      Point pos,
      boolean lit,
      boolean isInteractable,
      BiConsumer<Entity, Entity> onInteract,
      int value) {
    Entity torch = createTorch(pos, lit, isInteractable, onInteract, value);
    torch.name(torch.name().replace("torch", "anti_torch"));
    torch
        .fetch(DrawComponent.class)
        .orElseThrow(() -> MissingComponentException.build(torch, DrawComponent.class))
        .tintColor(0x00FFFFFF);
    return torch;
  }
}
