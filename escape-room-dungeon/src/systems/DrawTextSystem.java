package systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import components.DrawTextComponent;
import core.Entity;
import core.Game;
import core.System;
import core.components.CameraComponent;
import core.components.PositionComponent;
import core.systems.CameraSystem;
import core.utils.Point;
import core.utils.components.MissingComponentException;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;
import hud.DebugOverlay;

public class DrawTextSystem extends System {

  private static final SpriteBatch BATCH = new SpriteBatch();
  private static final IPath FONT_FNT = new SimpleIPath("skin/myFont.fnt");
  private static final IPath FONT_PNG = new SimpleIPath("skin/myFont.png");
  private static final BitmapFont bitmapFont;
  private static final float MARGIN = 10;
  private static final float OUTLINE_WIDTH = 1;
  private static final float SCALE_CORRECTION = 0.05f;
  private static final float X_OFFSET = 0.5f;
  private static final float Y_OFFSET = 0.25f;

  private static final boolean DEBUG_POSITION = false;


  static {
    bitmapFont =
      new BitmapFont(
        Gdx.files.internal(FONT_FNT.pathString()),
        Gdx.files.internal(FONT_PNG.pathString()),
        false);
    bitmapFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
  }

  public DrawTextSystem(){}

  @Override
  public void execute() {
    BATCH.begin();

    filteredEntityStream(PositionComponent.class, DrawTextComponent.class)
      .map(this::buildDataObject)
      .forEach(this::drawText);

    BATCH.end();
  }

  public void drawText(DTData data){
    String text = data.dtc.text();
    Point heroPos = Game.hero().orElseThrow().fetchOrThrow(PositionComponent.class).position();

    Point pos = data.pc.position();
    Color color = data.dtc.color();
    Color outline = data.dtc.outlineColor();
    float scale = data.dtc.scale();
    float x = pos.x + X_OFFSET;
    float y = pos.y + Y_OFFSET;
    float displayRadius = data.dtc.displayRadius();
    float maxAlpha = data.dtc.maxAlpha();

    float alpha = 0;
    if(displayRadius != 0 && heroPos.distance(pos) < displayRadius){
//      alpha = MathUtils.lerp(1, 0, (float)heroPos.distance(pos) / displayRadius);
      alpha = Interpolation.exp5Out.apply(1 - (float)heroPos.distance(pos) / displayRadius);
      alpha = MathUtils.lerp(0, maxAlpha, alpha);
    }


    x = x / (SCALE_CORRECTION * scale);
    y = y / (SCALE_CORRECTION * scale);

    GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, text);
    float xCentered = x - glyphLayout.width / 2;
    float yCentered = y + glyphLayout.height / 2;

    if(text.startsWith("Very")){
      DebugOverlay.setText(1, "X-Pos: "+xCentered+" | Y-Pos: "+yCentered);
      DebugOverlay.setText(2, "GlyphLayout:\n"+glyphLayout);
    }

    float outlineWidth = OUTLINE_WIDTH;

    Camera cam = CameraSystem.camera();
    Matrix4 m = cam.combined.cpy();
    m.scale(SCALE_CORRECTION * scale,SCALE_CORRECTION * scale,1);
    BATCH.setProjectionMatrix(m);

    bitmapFont.setColor(outline.r, outline.g, outline.b, alpha);
    //Draw black outline
    for (int i = 0; i < 3; i++){
      for (int j = 0; j < 3; j++){
        if(i == 1 && j == 1) continue;
        float drawX = xCentered - outlineWidth * (i - 1) * scale;
        float drawY = yCentered - outlineWidth * (j - 1) * scale;
        if(alpha > 0) bitmapFont.draw(BATCH, text, drawX, drawY);
      }
    }

    bitmapFont.setColor(color.r, color.g, color.b, alpha);
    if(alpha > 0) bitmapFont.draw(BATCH, text, xCentered, yCentered);

    if(DEBUG_POSITION){
      BATCH.end();
      ShapeRenderer renderer = new ShapeRenderer();
      renderer.setProjectionMatrix(m);
      renderer.begin(ShapeRenderer.ShapeType.Line);
      renderer.rect(xCentered, yCentered - glyphLayout.height, glyphLayout.width, glyphLayout.height);
      renderer.circle(x, y, 2);
      renderer.end();
      BATCH.begin();
    }
  }

  private DTData buildDataObject(Entity e){
    return new DTData(
      e,
      e.fetchOrThrow(PositionComponent.class),
      e.fetchOrThrow(DrawTextComponent.class)
    );
  }
  private record DTData(Entity e, PositionComponent pc, DrawTextComponent dtc) {}
}
