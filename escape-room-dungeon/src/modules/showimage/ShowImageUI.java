package modules.showimage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import starter.EscapeRoomDungeon;
import utils.SkinUtils;
import utils.SoundManager;
import utils.Sounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowImageUI extends Group {

  private static final float SCALE = 1f;
  private static final float MAX_SCREEN_COVERAGE = 0.85f;
  private static final int ANIMATION_OFFSET_X = -5;
  private static final int ANIMATION_OFFSET_Y = -50;
  private static final float SHOW_TRANSITION_PROGRESS = 1f / 60;

  private final Entity sprite;
  private final Entity overlay;

  private Image background;
  private String currentImagePath = null;
  private float animation;

  public ShowImageUI(Entity keypad, Entity overlay){
    this.sprite = keypad;
    this.overlay = overlay;
    createActors();
    animation = 0;
  }

  private void createActors(){
    this.setScale(SCALE);
    this.setOrigin(Align.center);
    this.setBounds(0, 0, Game.windowWidth(), Game.windowHeight());

    ShowImageComponent sic = sprite.fetchOrThrow(ShowImageComponent.class);

    currentImagePath = sic.imagePath;
    background = new Image(new Texture(Gdx.files.internal(currentImagePath)));
    background.setOrigin(Align.center);
    this.addActor(background);
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    this.setScale(SCALE);
    this.setOrigin(Align.center);
    this.setBounds(0, 0, Game.windowWidth(), Game.windowHeight());

    ShowImageComponent sic = sprite.fetchOrThrow(ShowImageComponent.class);
    if(!currentImagePath.equals(sic.imagePath)){
      currentImagePath = sic.imagePath;
      background.setDrawable(new TextureRegionDrawable(new Texture(Gdx.files.internal(currentImagePath))));
    }
    float imageWidth = background.getImageWidth();
    float imageHeight = background.getImageHeight();
    float maxWidth = Game.windowWidth() * MAX_SCREEN_COVERAGE;
    float maxHeight = Game.windowHeight() * MAX_SCREEN_COVERAGE;

    float scaleX = maxWidth / imageWidth;
    float scaleY = maxHeight / imageHeight;

    float minScale = Math.min(scaleX, scaleY);
    background.setScale(minScale);
    background.setPosition(getX(Align.center) + animationOffsetX(), getY(Align.center) + animationOffsetY(), Align.center);
    background.setColor(1, 1, 1, animation);

    animation = Math.min(1, animation + SHOW_TRANSITION_PROGRESS);

    super.draw(batch, parentAlpha);
  }

  private float animationOffsetX(){
    return Interpolation.smooth.apply(ANIMATION_OFFSET_X, 0, animation);
  }
  private float animationOffsetY(){
    return Interpolation.smooth.apply(ANIMATION_OFFSET_Y, 0, animation);
  }
}
