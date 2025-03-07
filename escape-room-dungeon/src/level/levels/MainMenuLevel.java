package level.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import core.Game;
import core.level.elements.tile.ExitTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.systems.CameraSystem;
import core.utils.Point;
import entities.DrawTextFactory;
import entities.TeleporterFactory;
import level.EscapeRoomLevel;
import level.utils.DungeonLoader;
import level.utils.LevelLabel;
import systems.TransitionSystem;
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
  }

  @Override
  protected void onTick() {
    SpriteBatch batch = new SpriteBatch();

    Texture texture = new Texture(Gdx.files.internal("spritesheets/TilesetProps.png"));
    int x = 5;
    int y = 12;
    int width = 2;
    int height = 1;
    TextureRegion region = new TextureRegion(texture, x * 16, y * 16, width * 16, height * 16);
    Sprite s = new Sprite(region);
    s.setSize(width, height);
    s.setPosition(2.5f, 1.25f);

    batch.begin();
    batch.setProjectionMatrix(CameraSystem.camera().combined);
    s.draw(batch);
    batch.end();
  }

}
