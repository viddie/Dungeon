package modules.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import contrib.configuration.KeyboardConfig;
import core.Game;
import utils.InputHandler;
import utils.SkinUtils;

public class DialogUI extends Group {

  private static final float FONT_SCALE = 0.8f;
  private static final int FRAMES_BETWEEN_LETTERS = 3;
  private static final int IGNORE_INPUTS_AFTER_OPEN = 10;

  private DialogConfig config;
  private int currentLineIndex = 0;
  private int showLetters = 0;
  private int letterTimer = 0;
  private int ignoreInputsTimer = 0;

  private Label author;
  private Label label;

  public DialogUI(DialogConfig config){
    this.config = config;
    createActors();
    ignoreInputsTimer = IGNORE_INPUTS_AFTER_OPEN;
  }

  private void createActors(){
    this.setOrigin(Align.center);
    this.setBounds(0, 0, Game.windowWidth(), Game.windowHeight());

    Table table = new Table(SkinUtils.customSkin());
    table.setFillParent(true);
    table.setBackground("table-background");
    this.addActor(table);

    author = new Label(config.getLines().get(0).author(), SkinUtils.customSkin(), "dialog");
    author.setFontScale(FONT_SCALE * 0.8f);
    author.setColor(Color.WHITE);
    table.add(author).left().row();

    label = new Label("", SkinUtils.customSkin(), "dialog");
    label.setWrap(true);
    label.setAlignment(Align.topLeft);
    label.setFontScale(FONT_SCALE);
    label.setColor(Color.WHITE);
    table.add(label).width(1200).minHeight(220).left().row();
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    checkInputs();

    letterTimer++;
    if(letterTimer >= FRAMES_BETWEEN_LETTERS){
      letterTimer = 0;
      showLetters++;
    }
    DialogConfig.Line line = config.getLines().get(currentLineIndex);
    String text = line.line();
    label.setText(text.substring(0, Math.min(text.length(), showLetters)));
    author.setText(line.author());

    this.setOrigin(Align.center);
    this.setBounds(0, 0, Game.windowWidth(), Game.windowHeight());
    super.draw(batch, parentAlpha);
  }

  private void checkInputs(){
    if(ignoreInputsTimer > 0){
      ignoreInputsTimer--;
      return;
    }

    if(!InputHandler.isKeyJustPressed(KeyboardConfig.INTERACT_WORLD.value()) && !InputHandler.isKeyJustPressed(KeyboardConfig.INTERACT_WORLD.value())) return;

    //Advance the dialog
    String text = config.getLines().get(currentLineIndex).line();
    if(showLetters < text.length()){
      showLetters = text.length();
      return;
    }

    if(currentLineIndex + 1 >= config.lineCount()){
      //End dialog
      DialogSystem.endDialog();
    } else {
      currentLineIndex++;
      showLetters = 0;
      letterTimer = 0;
    }
  }

}
