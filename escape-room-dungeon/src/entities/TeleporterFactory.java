package entities;

import com.badlogic.gdx.graphics.Color;
import components.LeverComponent;
import components.VicinityComponent;
import contrib.components.InteractionComponent;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.components.draw.Animation;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;
import level.utils.DungeonLoader;
import systems.TransitionSystem;
import utils.Constants;

import java.util.Map;
import java.util.concurrent.Callable;

public class TeleporterFactory {

  private static final float DEFAULT_INTERACTION_RADIUS = 2f;
  private static final Color ACTIVE_COLOR = Color.GREEN;
  private static final IPath TELEPORTER_IMG = new SimpleIPath("objects/teleporter/teleporter_0.png");
  private static final IPath ARRIVAL_IMG = new SimpleIPath("objects/teleporter/arrival_0.png");

  public static Entity createTeleporter(Point pos, DungeonLoader.LevelLabel levelLabel, Point posInLevel, String message, float animationSpeedScale) {
    Entity teleporter = new Entity("teleporter");

    teleporter.add(new PositionComponent(pos.add(Constants.X_OFFSET, Constants.Y_OFFSET)));
    DrawComponent dc = new DrawComponent(Animation.fromSingleImage(TELEPORTER_IMG));
    teleporter.add(new VicinityComponent(DEFAULT_INTERACTION_RADIUS, new VicinityComponent.IVicinityCommand() {
      @Override
      public void onEnterRange() {
        dc.tintColor(Color.rgba8888(ACTIVE_COLOR));
      }
      @Override
      public void onLeaveRange() {
        dc.tintColor(-1);
      }
      @Override
      public void onInRange(double distance) {}
    }, Game.hero().orElseThrow()));
    teleporter.add(dc);
    teleporter.add(
      new InteractionComponent(
        DEFAULT_INTERACTION_RADIUS,
        true,
        (entity, who) -> {
          TransitionSystem.transition(() -> {
            DungeonLoader.loadLevel(levelLabel, 0, posInLevel);
          }, message);
        }));
    return teleporter;
  }
  public static Entity createTeleporter(Point pos, DungeonLoader.LevelLabel levelLabel) {
    return createTeleporter(pos, levelLabel, null, null, 1);
  }
  public static Entity createTeleporter(Point pos, DungeonLoader.LevelLabel levelLabel, Point posInLevel) {
    return createTeleporter(pos, levelLabel, posInLevel, null, 1);
  }
  public static Entity createTeleporter(Point pos, DungeonLoader.LevelLabel levelLabel, Point posInLevel, String message) {
    return createTeleporter(pos, levelLabel, posInLevel, message, 1);
  }
}
