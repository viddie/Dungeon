package core.components;

import com.badlogic.gdx.graphics.g2d.Sprite;
import core.Component;
import core.components.states.*;
import core.systems.VelocitySystem;
import core.utils.components.draw.CoreAnimations;
import core.utils.components.draw.DepthLayer;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;
import core.utils.logging.CustomLogLevel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Store all {@link Animation}s for an entity.
 *
 * <p>If you want to add your own Animations, create a subdirectory for the animation and add the
 * path to an enum that implements the {@link IPath} interface. So if you want to add a jump
 * animation to the hero, just create a new directory "jump" in the asset directory of your hero
 * (for example character/hero) and then add a new Enum-Value JUMP("jump") to the enum that
 * implements {@link IPath}.
 *
 * <p>Animations will be searched in the default asset directory. Normally, this is "game/assets",
 * but you can change it in the "gradle.build" file if you like.
 *
 * <p>Note: Each entity needs at least a {@link CoreAnimations#IDLE} Animation.
 *
 * @see Animation
 * @see IPath
 */
public final class DrawComponent implements Component {
  private final Logger LOGGER = Logger.getLogger(this.getClass().getSimpleName());

  private final StateMachine stateMachine;
  private int depth = DepthLayer.Normal.depth();
  private int tintColor = -1; // -1 means no tinting
  private boolean isVisible = true;

  /**
   * Create a new DrawComponent.
   *
   * <p>Will read in all subdirectories of the given path and use each file in the subdirectory to
   * create an animation. So each subdirectory should contain only the files for one animation.
   *
   * @param path Path (as a string) to the directory in the assets folder where the subdirectories
   *     containing the animation files are stored. Example: "character/knight".
   * @see Animation
   */
  public DrawComponent(final IPath path, SpritesheetConfig config) {
    stateMachine = new StateMachine(path, config);
  }
  public DrawComponent(final IPath path) {
    this(path, null);
  }
  public DrawComponent(List<State> states) {
    stateMachine = new StateMachine(states);
  }
  public DrawComponent(StateMachine stateMachine) {
    this.stateMachine = stateMachine;
  }

  public void sendSignal(String signal, Object data){
    stateMachine.sendSignal(new Signal(signal, data));
  }
  public void sendSignal(String signal){
    sendSignal(signal, null);
  }

  public void update(){
    stateMachine.update();
  }

  public Sprite getSprite(){
    Sprite s = stateMachine.getSprite();
    return s;
  }

  public Animation currentAnimation(){
    return stateMachine.getCurrentState().getAnimation();
  }

  /**
   * Check if the current animation is a looping animation.
   *
   * @return true if the current animation is looping.
   */
  public boolean isCurrentAnimationLooping() {
    return currentAnimation().isLooping();
  }

  /**
   * Check if the current animation has finished playing.
   *
   * @return true if the current animation has finished playing.
   */
  public boolean isCurrentAnimationFinished() {
    return stateMachine.isAnimationFinished();
  }

  /**
   * Check if the component is visible. If the component is visible, it will be drawn by the {@link
   * core.systems.DrawSystem}.
   *
   * @return true if the component is visible, false if not.
   */
  public boolean isVisible() {
    return isVisible;
  }

  /**
   * Set the visibility of the component. If the component is visible, it will be drawn by the
   * {@link core.systems.DrawSystem}.
   *
   * @param visible The new visibility status to set. True for visible, false for hidden.
   */
  public void setVisible(boolean visible) {
    isVisible = visible;
  }

  /**
   * Returns the tint color of the DrawComponent.
   *
   * @return The tint color of the DrawComponent. If the tint color is -1, no tint is applied.
   */
  public int tintColor() {
    return this.tintColor;
  }

  /**
   * Sets the tint color of the DrawComponent. Set it to -1 to remove the tint.
   *
   * @param tintColor The new tint color to set.
   */
  public void tintColor(int tintColor) {
    this.tintColor = tintColor;
  }

  public StateMachine stateMachine() { return stateMachine; }

  public int depth() { return depth; }
  public void depth(int depth) { this.depth = depth; }
}
