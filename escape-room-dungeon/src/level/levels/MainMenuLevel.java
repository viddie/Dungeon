package level.levels;

import com.badlogic.gdx.graphics.Color;
import entities.*;
import core.Game;
import core.level.elements.tile.ExitTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import core.utils.components.draw.DepthLayer;
import level.EscapeRoomLevel;
import level.utils.LevelLabel;
import utils.GameState;

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

    LevelLabel levelContinue = GameState.currentLevel();
    boolean disable1 = false;
    boolean disable2 = false;
    if(levelContinue == null){
      levelContinue = LevelLabel.Floor1;
    } else {
      disable1 = GameState.playerNumber() != 1;
      disable2 = GameState.playerNumber() != 2;
    }

    String continueText = "-> "+levelContinue.displayName;

    Point p = getPoint("player1");
    Game.add(DrawTextFactory.createTextEntity("Spieler 1", p.add(0.5f, 2), 1, Color.WHITE, 0, 1));
    if(disable2){
      Game.add(DrawTextFactory.createTextEntity(continueText, p.add(0.5f, 1.35f), 0.5f, Color.LIGHT_GRAY, 0, 1));
    }
    Game.add(TeleporterFactory.createTeleporter(p, levelContinue, null, levelContinue.displayName, 1, 1, disable1));

    p = getPoint("player2");
    Game.add(DrawTextFactory.createTextEntity("Spieler 2", p.add(0.5f, 2), 1, Color.WHITE, 0, 1));
    if(disable1){
      Game.add(DrawTextFactory.createTextEntity(continueText, p.add(0.5f, 1.35f), 0.5f, Color.LIGHT_GRAY, 0, 1));
    }
    Game.add(TeleporterFactory.createTeleporter(p, levelContinue, null, levelContinue.displayName, 1, 2, disable2));

    p = getPoint("settings");
    Game.add(DrawTextFactory.createTextEntity("Einstellungen", p.add(0.5f, 2), 1, Color.WHITE, 0, 1));
    Game.add(TeleporterFactory.createTeleporter(p, LevelLabel.Settings, null, null, 5));

    Game.add(DrawTextFactory.createTextEntity("Spiel Verlassen?", getPoint("exit-text").add(0.5f, 0.5f), 0.7f, Color.WHITE, 6, 1));

    p = getPoint("tutorial");
    Game.add(DrawTextFactory.createTextEntity("Tutorial", p.add(0.5f, 2), 1, Color.WHITE, 0, 1));
    Game.add(TeleporterFactory.createTeleporter(p, LevelLabel.Tutorial, null, "-- Tutorial --"));

    Game.add(DecoFactory.createDeco(new Point(19, 9), Deco.SignBig));
    Game.add(DecoFactory.createDeco(new Point(8, 9), Deco.BookshelfLarge));
    Game.add(DecoFactory.createDeco(new Point(10, 9), Deco.BookshelfLarge));

    Game.add(DecoFactory.createDeco(new Point(9.75f, 6), Deco.VaseFull));
    Game.add(DecoFactory.createDeco(new Point(10.25f, 6), Deco.VaseEmpty));
    Game.add(DecoFactory.createDeco(new Point(10.75f, 6), Deco.VaseFull));
    Game.add(DecoFactory.createDeco(new Point(11.25f, 6), Deco.VaseEmpty));

    Game.add(DecoFactory.createDeco(new Point(19f, 6), Deco.TreeStump));
    Game.add(DecoFactory.createDeco(new Point(20f, 6), Deco.Logs));
    Game.add(DecoFactory.createDeco(new Point(19f, 4), Deco.TreeTrunk));
    Game.add(DecoFactory.createDeco(new Point(18f, 2), Deco.StonePillar1));
    Game.add(DecoFactory.createDeco(new Point(19f, 2), Deco.StonePillar0));
    Game.add(DecoFactory.createDeco(new Point(20f, 2), Deco.StonePillar1, DepthLayer.Player.depth(), null));
    Game.add(DecoFactory.createDeco(new Point(21f, 2), Deco.StonePillar2));
    Game.add(DecoFactory.createDeco(new Point(22f, 2), Deco.StonePillar1));

    CompositeDecoFactory.createArch(new Point(9, 2)).forEach(Game::add);
  }

  @Override
  protected void onTick() {}

}
