package level.levels;

import com.badlogic.gdx.graphics.Color;
import core.Game;
import core.level.elements.tile.ExitTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import entities.DrawTextFactory;
import entities.TeleporterFactory;
import level.EscapeRoomLevel;
import level.utils.DungeonLoader;
import level.utils.LevelLabel;
import systems.TransitionSystem;

import java.util.Map;

public class MainMenuLevel extends EscapeRoomLevel {

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout      The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   */
  public MainMenuLevel(LevelElement[][] layout, DesignLabel designLabel, Map<String, Point> namedPoints) {
    super(layout, designLabel, namedPoints);
  }

  @Override
  protected void onFirstTick() {
    ((ExitTile) endTile()).open();

    Point p = getPoint("player1");
    Game.add(DrawTextFactory.createTextEntity("Spieler 1", p.add(0.5f, 2), 1, Color.WHITE, 0, 1));
    Game.add(TeleporterFactory.createTeleporter(p, LevelLabel.Floor1, null, "~~~ Floor 1 ~~~", 1, 1));

    p = getPoint("player2");
    Game.add(DrawTextFactory.createTextEntity("Spieler 2", p.add(0.5f, 2), 1, Color.WHITE, 0, 1));
    Game.add(TeleporterFactory.createTeleporter(p, LevelLabel.Floor1, null, "~~~ Floor 1 ~~~", 1, 2));

    p = getPoint("settings");
    Game.add(DrawTextFactory.createTextEntity("Einstellungen", p.add(0.5f, 2), 1, Color.WHITE, 0, 1));
    Game.add(TeleporterFactory.createTeleporter(p, LevelLabel.Settings, null, "Settings", 5));

    Game.add(DrawTextFactory.createTextEntity("Spiel Verlassen?", new Point(27, 8.5f), 0.7f, Color.WHITE, 6, 1));

    p = getPoint("tutorial");
    Game.add(DrawTextFactory.createTextEntity("Tutorial", p.add(0.5f, 2), 1, Color.WHITE, 0, 1));
    Game.add(TeleporterFactory.createTeleporter(p, LevelLabel.Tutorial, null, "-- Tutorial --"));
  }

  @Override
  protected void onTick() {

  }

}
