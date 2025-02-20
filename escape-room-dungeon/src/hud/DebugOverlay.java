package hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import contrib.utils.components.skill.SkillTools;
import core.Game;
import core.components.PositionComponent;
import core.level.Tile;
import core.systems.CameraSystem;
import core.systems.LevelSystem;
import core.utils.Point;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;
import level.utils.ITickable;
import starter.EscapeRoomDungeon;
import utils.Constants;
import utils.GameState;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class DebugOverlay implements ITickable {

  private static SpriteBatch BATCH = new SpriteBatch();
  private static final IPath FONT_FNT = new SimpleIPath("fonts/segoe_32.fnt");
  private static final IPath FONT_PNG = new SimpleIPath("fonts/segoe_32.png");
  private static final BitmapFont bitmapFont;
  private static final float OUTLINE_WIDTH = 1;
  private static final float MARGIN = 10;
  static {
    bitmapFont =
      new BitmapFont(
        Gdx.files.internal(FONT_FNT.pathString()),
        Gdx.files.internal(FONT_PNG.pathString()),
        false);
    bitmapFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
  }
  private static boolean SHOW_BOXES = false;

  public static DebugOverlay INSTANCE = null;
  public DebugOverlay(){
    INSTANCE = this;
  }

  private final List<String> toDraw = new ArrayList<>();

  public static void renderRect(Point point, Point point2){
    Point diff = point2.sub(point);
    renderRect(point, diff.x, diff.y, Color.WHITE);
  }
  public static void renderRect(Point point, float width, float height){
    renderRect(point, width, height, Color.WHITE);
  }
  public static void renderRect(Point point, float width, float height, Color color){
    if(!SHOW_BOXES) return;
    ShapeRenderer renderer = new ShapeRenderer();
    renderer.setProjectionMatrix(CameraSystem.camera().combined);
    renderer.begin(ShapeRenderer.ShapeType.Line);
    Gdx.gl.glEnable(GL20.GL_BLEND);
    renderer.setColor(color);
    renderer.rect(point.x, point.y, width, height);
    renderer.end();
  }

  public static void renderCircle(Point point, float radius){
    renderCircle(point, radius, Color.WHITE);
  }
  public static void renderCircle(Point point, float radius, Color color){
    if(!SHOW_BOXES) return;
    point = Constants.toffset(point);
    ShapeRenderer renderer = new ShapeRenderer();
    renderer.setProjectionMatrix(CameraSystem.camera().combined);
    renderer.begin(ShapeRenderer.ShapeType.Line);
    Gdx.gl.glEnable(GL20.GL_BLEND);
    renderer.setColor(color);
    renderer.circle(point.x, point.y, radius, 30);
    renderer.end();
  }

  public static void renderLine(Point point, Point other){
    renderLine(point, other, Color.WHITE);
  }
  public static void renderLine(Point point, Point other, Color color){
    if(!SHOW_BOXES) return;
    point = Constants.toffset(point);
    other = Constants.toffset(other);
    ShapeRenderer renderer = new ShapeRenderer();
    renderer.setProjectionMatrix(CameraSystem.camera().combined);
    renderer.begin(ShapeRenderer.ShapeType.Line);
    Gdx.gl.glEnable(GL20.GL_BLEND);
    renderer.setColor(color);
    renderer.line(point.x, point.y, other.x, other.y);
    renderer.end();
  }

  public static void renderArrow(Point point, Point other){
    renderArrow(point, other, Color.WHITE);
  }
  public static void renderArrow(Point point, Point other, Color color){
    if(!SHOW_BOXES) return;

    //Arrow base
    renderCircle(point, 0.1f, color);

    //Arrow body
    renderLine(point, other, color);

    //Arrow head
    Point dir = Point.unitDirectionalVector(point, other);
    Vector2 vDir = new Vector2(dir.x, dir.y);
    Vector2 rotated = vDir.cpy().rotate90(1).setLength(0.15f);
    vDir.scl(-1).setLength(0.3f);

    Point offset = other.add(-vDir.x, -vDir.y);
    Point left = offset.add(rotated.x, rotated.y);
    Point right = offset.add(-rotated.x, -rotated.y);

    other = Constants.toffset(other);
    left = Constants.toffset(left);
    right = Constants.toffset(right);

    ShapeRenderer renderer = new ShapeRenderer();
    renderer.setProjectionMatrix(CameraSystem.camera().combined);
    renderer.begin(ShapeRenderer.ShapeType.Filled);
    Gdx.gl.glEnable(GL20.GL_BLEND);
    renderer.setColor(color);
    renderer.triangle(other.x, other.y, left.x, left.y, right.x, right.y);
    renderer.end();
  }

  public static void renderText(Point pos, String text, Color color, float scale){
    if(!SHOW_BOXES) return;
    BATCH.begin();
    BATCH.setProjectionMatrix(CameraSystem.camera().combined);
    bitmapFont.setColor(color);
    bitmapFont.getData().setScale(scale / 20f);
    pos = Constants.toffset(pos);
    bitmapFont.draw(BATCH, text, pos.x, pos.y);
    BATCH.end();
  }


  public static void drawText(String text){
    INSTANCE.addText(text);
  }
  public void addText(String text){
    toDraw.add(text);
  }

  private void drawTexts(){
    HUDText.Anchor[] anchors = new HUDText.Anchor[] {HUDText.Anchor.TopLeft, HUDText.Anchor.BottomLeft, HUDText.Anchor.BottomRight, HUDText.Anchor.TopRight};

    BATCH.begin();

    Matrix4 matrix = new Matrix4();
    matrix.setToOrtho2D(0, 0, Game.windowWidth(), Game.windowHeight());
    BATCH.setProjectionMatrix(matrix);

    for(int textIndex = 0; textIndex < toDraw.size(); textIndex++){
      if(textIndex >= 4) break;

      HUDText.Anchor anchor = anchors[textIndex];
      String text = toDraw.get(textIndex);
      GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, text);

      float x = 0, y = 0;
      switch (anchor) {
        case TopLeft -> {
          x = MARGIN;
          y = Game.windowHeight() - MARGIN;
        }
        case BottomLeft -> {
          x = MARGIN;
          y = glyphLayout.height + MARGIN;
        }
        case TopRight -> {
          x = Game.windowWidth() - glyphLayout.width - MARGIN;
          y = Game.windowHeight() - MARGIN;
        }
        case BottomRight -> {
          x = Game.windowWidth() - glyphLayout.width - MARGIN;
          y = glyphLayout.height + MARGIN;
        }
      }

      bitmapFont.setColor(Color.BLACK);
      bitmapFont.getData().setScale(0.75f);
      //Draw black outline
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          if (i == 1 && j == 1) continue;
          float scale = 1;
          float drawX = x - OUTLINE_WIDTH * (i - 1) * scale;
          float drawY = y - OUTLINE_WIDTH * (j - 1) * scale;
          bitmapFont.draw(BATCH, text, drawX, drawY);
        }
      }

      bitmapFont.setColor(Color.WHITE);
      bitmapFont.draw(BATCH, text, x, y);
    }
    BATCH.end();

    toDraw.clear();
  }

  @Override
  public void onTick(boolean isFirstTick) {
    if(Gdx.input.isKeyJustPressed(Input.Keys.O)){
      SHOW_BOXES = !SHOW_BOXES;
      EscapeRoomDungeon.LOGGER.info("Showing debug shapes: "+SHOW_BOXES);
    }

    Point heroPos = Game.hero().orElseThrow().fetchOrThrow(PositionComponent.class).position();
    Point mosPos = SkillTools.cursorPositionAsPoint();
    mosPos = new Point(mosPos.x, mosPos.y);
    Point mosPosOffset = Constants.ioffset(mosPos);
    int mouseXInt = (int)mosPosOffset.x;
    int mouseYInt = (int)mosPosOffset.y;
    Point tilePos = new Point(mouseXInt, mouseYInt);

    if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
      String s = "new Point("+mouseXInt+", "+mouseYInt+")";
      StringSelection stringSel = new StringSelection(s);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSel,stringSel);
      EscapeRoomDungeon.LOGGER.info("Selected Point (+offset): "+s);
    }

    if(SHOW_BOXES){
      drawText("Hero position: "+heroPos+"\nMouse position: "+mosPos+"\nHovering tile: "+tilePos);
      Tile[][] layout = Game.currentLevel().layout();
      int rows = layout.length;
      int cols = layout[0].length;
      DebugOverlay.renderRect(Constants.offset(new Point(0, 0)), cols, rows, new Color(0, 1, 0, 0.5f));

      Tile t = LevelSystem.level().tileAt(Constants.ioffset(mosPos));
      if(t != null){
        DebugOverlay.renderRect(Constants.offset(t.position()), 1, 1, new Color(1, 1, 1, 0.2f));
      }
    }

    drawTexts();
  }
}
