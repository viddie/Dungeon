package hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import contrib.hud.elements.CombinableGUI;
import contrib.hud.elements.GUICombination;
import core.Component;
import core.Game;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;

public class HUDText extends CombinableGUI {

  private static final IPath FONT_FNT = new SimpleIPath("skin/myFont.fnt");
  private static final IPath FONT_PNG = new SimpleIPath("skin/myFont.png");
  private static final BitmapFont bitmapFont;
  private static final float MARGIN = 10;

  static {
    bitmapFont =
      new BitmapFont(
        Gdx.files.internal(FONT_FNT.pathString()),
        Gdx.files.internal(FONT_PNG.pathString()),
        false);
    bitmapFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
  }

  private String text;
  private Anchor anchor;
  private float outlineWidth;
  private float scale;

  public HUDText(String text, Anchor anchor, float scale){
    this.text = text;
    this.anchor = anchor;
    this.outlineWidth = 1f;
    this.scale = scale;
  }

  @Override
  protected void initDragAndDrop(DragAndDrop dragAndDrop) {

  }

  @Override
  protected void draw(Batch batch) {
    GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, this.text);

    float x = 0, y = 0;
    switch (anchor){
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

    String toDraw = this.text;

    bitmapFont.getData().setScale(this.scale);
    bitmapFont.setColor(Color.BLACK);
    //Draw black outline
    for (int i = 0; i < 3; i++){
      for (int j = 0; j < 3; j++){
        if(i == 1 && j == 1) continue;
        float drawX = x - outlineWidth * (i - 1) * this.scale;
        float drawY = y - outlineWidth * (j - 1) * this.scale;
        bitmapFont.draw(batch, toDraw, drawX, drawY);
      }
    }

    bitmapFont.setColor(Color.WHITE);
    bitmapFont.draw(batch, toDraw, x, y);
  }

  @Override
  protected Vector2 preferredSize(GUICombination.AvailableSpace availableSpace) {
    GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, this.text);
    return new Vector2(glyphLayout.width, glyphLayout.height);
  }

  public void setText(String text) {
    this.text = text;
  }
  public String getText() {
    return text;
  }

  public enum Anchor {
    TopLeft,
    BottomLeft,
    TopRight,
    BottomRight
  }
}
