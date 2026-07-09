package level;

import core.level.DungeonLevel;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import java.util.Map;

/** Minimal template level that only loads the map and named points from the level file. */
public class TemplateLevel extends DungeonLevel {

  /**
   * Creates the template level instance.
   *
   * @param layout The layout of the level.
   * @param designLabel The design label of the level.
   * @param namedPoints The custom points of the level.
   */
  public TemplateLevel(
      LevelElement[][] layout, DesignLabel designLabel, Map<String, Point> namedPoints) {
    super(layout, designLabel, namedPoints, "last-hour-1");
  }

  @Override
  protected void onFirstTick() {}

  @Override
  protected void onTick() {}

  /** Timer hook kept for template compatibility. */
  public static void onTimerExpired() {}
}
