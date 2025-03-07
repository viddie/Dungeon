package dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import core.Entity;
import core.Game;
import modules.showimage.ShowImageComponent;
import utils.SkinUtils;

public class InputDialog extends Group {

  private static final float SCALE = 1f;
  private static final int ANIMATION_OFFSET_X = 0;
  private static final int ANIMATION_OFFSET_Y = -10;
  private static final float SHOW_TRANSITION_PROGRESS = 1f / 20;

  private float animation;

  private Entity parent;
  private String title;
  private String message;
  private TextField textfield;

  public InputDialog(Entity parent, String title, String message){
    this.parent = parent;
    this.title = title;
    this.message = message;
    createActors();
    animation = 0;
  }

  private void createActors(){
    this.setScale(SCALE);
    this.setOrigin(Align.center);
    this.setBounds(0, 0, Game.windowWidth(), Game.windowHeight());

    Table table = new Table();
    table.setFillParent(true);
    this.addActor(table);

    Label label = new Label(title, SkinUtils.customSkin());
    label.setFontScale(1.3f);
    table.add(label).row();

    table.add(new Label(message, SkinUtils.customSkin())).row();

    textfield = new TextField("", SkinUtils.customSkin());
    Game.stage().orElseThrow().setKeyboardFocus(textfield);
    table.add(textfield).row();
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    this.setScale(SCALE);
    this.setOrigin(Align.center);
    this.setBounds(0, 0, Game.windowWidth(), Game.windowHeight());

    this.setPosition(animationOffsetX(), animationOffsetY());
    this.setColor(1, 1, 1, animation);
    animation = Math.min(1, animation + SHOW_TRANSITION_PROGRESS);

    super.draw(batch, parentAlpha);
  }

  public String getInput(){
    return textfield.getText();
  }

  public boolean isInputFocused(){
    return Game.stage().orElseThrow().getKeyboardFocus() == textfield;
  }

  private float animationOffsetX(){
    return Interpolation.smooth.apply(ANIMATION_OFFSET_X, 0, animation);
  }
  private float animationOffsetY(){
    return Interpolation.smooth.apply(ANIMATION_OFFSET_Y, 0, animation);
  }
}
