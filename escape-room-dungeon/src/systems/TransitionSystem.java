package systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import core.Game;
import core.System;
import core.components.PlayerComponent;
import core.utils.IVoidFunction;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;
import hud.HUDText;

public class TransitionSystem extends System {

  public static TransitionSystem INSTANCE;
  private static final float PROGRESS_PER_FRAME = 0.05f; //20f animation stage
  private static final float PROGRESS_HANG_TIME = 0.007f; //142f
  private static final float PROGRESS_HANG_TIME_NO_MSG = 0.07f; //14f

  //Test rendering
  private static final SpriteBatch BATCH = new SpriteBatch();
  //TODO: use different font thats redistributable
  private static final IPath FONT_FNT = new SimpleIPath("fonts/segoe_64.fnt");
  private static final IPath FONT_PNG = new SimpleIPath("fonts/segoe_64.png");
  private static final BitmapFont bitmapFont;
  static {
    bitmapFont =
      new BitmapFont(
        Gdx.files.internal(FONT_FNT.pathString()),
        Gdx.files.internal(FONT_PNG.pathString()),
        false);
    bitmapFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
  }

  private boolean isOpening = false;
  private String hangTimeMessage;
  private IVoidFunction command;
  private State state = State.Waiting;
  private float animationTimer = 0; //Goes from 0 to 1
  private float animationSpeedScale = 1f;

  public TransitionSystem(){
    INSTANCE = this;
  }

  public static void transition(IVoidFunction command, String message, float speedScale){
    INSTANCE.startTransition(command, message, speedScale);
  }
  public static void transition(IVoidFunction command, String message){
    INSTANCE.startTransition(command, message, 1);
  }
  public static void transition(IVoidFunction command){
    transition(command, null);
  }

  public static void openingTransition(String message){
    INSTANCE.startOpeningTransition(message);
  }

  private void startTransition(IVoidFunction command, String message, float speedScale){
    if(state != State.Waiting) return;
    this.isOpening = false;
    this.command = command;
    this.animationTimer = 0;
    this.state = this.state.next();
    this.hangTimeMessage = message;
    this.animationSpeedScale = speedScale;
  }
  private void startOpeningTransition(String message){
    this.command = () -> {}; //Do nothing, just let it fade in
    this.isOpening = true;
    this.animationTimer = 1; //Skip fade out
    this.state = state.next();
    this.hangTimeMessage = message;
    this.animationSpeedScale = 1;
  }

  @Override
  public void execute() {
    if (state == State.Waiting) return;

    float add = state == State.Hang ? (hangTimeMessage == null ? PROGRESS_HANG_TIME_NO_MSG : PROGRESS_HANG_TIME) : PROGRESS_PER_FRAME;
    if(isOpening && state == State.Hang && animationTimer > 0.15f) add /= 2;
    add *= animationSpeedScale;
    animationTimer = Math.min(animationTimer + add, 1);

    if(animationTimer == 1){
      animationTimer = 0;
      state = state.next();
      if(state == State.Hang){
        command.execute();
      }
    }

    float alpha = 0;
    if(state == State.FadeOut){
      alpha = MathUtils.lerp(0, 1, animationTimer);
    } else if(state == State.FadeIn) {
      alpha = MathUtils.lerp(1, 0, animationTimer);
    } else if (state == State.Hang){
      alpha = 1;
    }

    ShapeRenderer renderer = new ShapeRenderer();
    renderer.begin(ShapeRenderer.ShapeType.Filled);
    Gdx.gl.glEnable(GL20.GL_BLEND);
    renderer.setColor(0, 0, 0, alpha);
    renderer.rect(0, 0, Game.windowWidth(), Game.windowHeight());
    renderer.end();

    if(state == State.Hang && hangTimeMessage != null){
      alpha = 0;
      if(animationTimer > 0.15f && animationTimer <= 0.5f){
        alpha = Interpolation.smoother.apply((animationTimer - 0.15f) / 0.35f);
      } else if (animationTimer > 0.5f && animationTimer <= 0.7f){
        alpha = 1;
      } else if (animationTimer > 0.7f){
        alpha = Interpolation.smoother.apply(1, 0, (animationTimer - 0.7f) / 0.3f);
      }

      BATCH.begin();
      Matrix4 matrix = new Matrix4();
      matrix.setToOrtho2D(0, 0, Game.windowWidth(), Game.windowHeight());
      BATCH.setProjectionMatrix(matrix);
      bitmapFont.getData().setScale(1.5f);
      GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, hangTimeMessage);
      float x = (float)Game.windowWidth() / 2 - glyphLayout.width / 2;
      float y = (float)Game.windowHeight() / 2 + glyphLayout.height / 2;
      bitmapFont.setColor(1, 1, 1, alpha);
      bitmapFont.draw(BATCH, hangTimeMessage, x, y);
      BATCH.end();
    }
  }


  private enum State {
    Waiting,
    FadeOut,
    Hang,
    FadeIn;

    public State next(){
      return switch(this){
        case Waiting -> FadeOut;
        case FadeOut -> Hang;
        case Hang -> FadeIn;
        case FadeIn -> Waiting;
      };
    }
  }
}
