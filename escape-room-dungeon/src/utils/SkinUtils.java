package utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;

public class SkinUtils {

  private static final IPath SKIN_PATH = new SimpleIPath("skin/custom_skin.json");

  private static final IPath FONT_SMALL_FNT = new SimpleIPath("fonts/segoe_16.fnt");
  private static final IPath FONT_SMALL_PNG = new SimpleIPath("fonts/segoe_16.png");
  private static final BitmapFont FONT_SMALL;

  private static final IPath FONT_MEDIUM_FNT = new SimpleIPath("fonts/segoe_32.fnt");
  private static final IPath FONT_MEDIUM_PNG = new SimpleIPath("fonts/segoe_32.png");
  private static final BitmapFont FONT_MEDIUM;

  private static final IPath FONT_LARGE_FNT = new SimpleIPath("fonts/segoe_64.fnt");
  private static final IPath FONT_LARGE_PNG = new SimpleIPath("fonts/segoe_64.png");
  private static final BitmapFont FONT_LARGE;

  static {
    FONT_SMALL =
      new BitmapFont(
        Gdx.files.internal(FONT_SMALL_FNT.pathString()),
        Gdx.files.internal(FONT_SMALL_PNG.pathString()),
        false);
    FONT_SMALL.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    FONT_MEDIUM =
      new BitmapFont(
        Gdx.files.internal(FONT_MEDIUM_FNT.pathString()),
        Gdx.files.internal(FONT_MEDIUM_PNG.pathString()),
        false);
    FONT_MEDIUM.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    FONT_LARGE =
      new BitmapFont(
        Gdx.files.internal(FONT_LARGE_FNT.pathString()),
        Gdx.files.internal(FONT_LARGE_PNG.pathString()),
        false);
    FONT_LARGE.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
  }

  private static Skin CUSTOM_SKIN;

  /**
   * Retrieve the custom skin.
   *
   * <p>Load the skin on demand (singleton with lazy initialisation). This allows to write JUnit
   * tests for this class w/o mocking libGDX.
   *
   * @return my custom skin for the Escape Room.
   */
  public static Skin customSkin() {
    if (CUSTOM_SKIN == null) {
      CUSTOM_SKIN = new Skin(Gdx.files.internal(SKIN_PATH.pathString()));
    }
    return CUSTOM_SKIN;
  }

  public static BitmapFont getFontSmall(){
    return FONT_SMALL;
  }
  public static BitmapFont getFontMedium(){
    return FONT_MEDIUM;
  }
  public static BitmapFont getFontLarge(){
    return FONT_LARGE;
  }

}
