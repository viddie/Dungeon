package modules.keypad;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import org.lwjgl.openal.AL;
import starter.EscapeRoomDungeon;
import utils.SkinUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeypadUI extends Group {

  private static float BACKGROUND_SCALE = 1.5f;
  private static float BACKGROUND_OFFSET_Y = 60;

  private final Entity keypad;
  private final Entity overlay;

  private Image background;
  private Table parentTable;
  private Table tableButtons;
  private List<Cell<TextButton>> buttonCells = new ArrayList<>();
  private Label numberLabel;

  public KeypadUI(Entity keypad, Entity overlay){
    this.keypad = keypad;
    this.overlay = overlay;
    createActors();
  }

  private void createActors(){
    this.setScale(0.75f);
    this.setOrigin(Align.center);
    this.setBounds(0, 0, Game.windowWidth(), Game.windowHeight());

    background = new Image(SkinUtils.customSkin(), "keypad-ui-off");
//    background.setDebug(true);
    background.setOrigin(Align.center);
    background.setScale(1.5f);
    this.addActor(background);

    parentTable = new Table();
    parentTable.setFillParent(true);
//    parentTable.setDebug(true, true);
//    parentTable.setPosition(Game.windowWidth() / 2, Game.windowHeight() / 2);
    this.addActor(parentTable);

    numberLabel = new Label("12345", SkinUtils.customSkin(), "keypad");
    numberLabel.setFontScale(1.25f);
    parentTable.add(numberLabel).height(120).padBottom(60).row();

    tableButtons = new Table();
    parentTable.add(tableButtons);

    List<String> buttons = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "Back", "0", "Submit");

    for (int i = 0; i < buttons.size(); i++) {
      String action = buttons.get(i);
      TextButton btn = new TextButton(action, SkinUtils.customSkin(), "keypad");
      if(!action.equals("Back") && !action.equals("Submit")){
        btn.getLabel().setFontScale(2f);
      } else {
        btn.getLabel().setFontScale(1.25f);
      }
      btn.addListener(new ClickListener(){
        @Override
        public void clicked(InputEvent e, float x, float y){
          onButtonPress(action);
        }
      });
      Cell<TextButton> c = tableButtons.add(btn).height(100).width(100).pad(10);
      if(i % 3 == 2){
        c.row();
      }
      buttonCells.add(c);
    }
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    this.setScale(0.75f);
    this.setOrigin(Align.center);
    this.setBounds(0, 0, Game.windowWidth(), Game.windowHeight());

    KeypadComponent kc = keypad.fetchOrThrow(KeypadComponent.class);

    background.setPosition(getX(Align.center), getY(Align.center), Align.center);
    background.setDrawable(SkinUtils.customSkin(), kc.isUnlocked ? "keypad-ui-on" : "keypad-ui-off");

//    for (Cell<TextButton> c : buttonCells) {
//      c.width(66 * BACKGROUND_SCALE).height(66 * BACKGROUND_SCALE);
//    }

    numberLabel.setText(kc.enteredString());

    super.draw(batch, parentAlpha);

//    BitmapFont font = SkinUtils.getFontLarge();
//    GlyphLayout layout = new GlyphLayout(font, "");
//    font.setColor(new Color(1, 1, 1, parentAlpha));
//    font.draw(batch, "Test", 200, 200);
  }

  private void onButtonPress(String action){
    EscapeRoomDungeon.LOGGER.info("Clicked button: "+action);
    KeypadComponent kc = keypad.fetchOrThrow(KeypadComponent.class);

    try{
      int number = Integer.parseInt(action);
      kc.addDigit(number);
    } catch (NumberFormatException ex){
      switch (action){
        case "Back" -> kc.backspace();
        case "Submit" -> onSubmit();
      }
    }
  }

  private void onSubmit(){
    KeypadComponent kc = keypad.fetchOrThrow(KeypadComponent.class);
    if(kc.isUnlocked) return;
    kc.checkUnlock();
    keypad.fetchOrThrow(DrawComponent.class).currentAnimation(kc.isUnlocked ? "on" : "off");
  }
}
