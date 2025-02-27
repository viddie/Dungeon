package level.levels;

import com.badlogic.gdx.graphics.Color;
import components.DrawTextComponent;
import components.VicinityComponent;
import components.commands.TintEntityCommand;
import contrib.components.InteractionComponent;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.IVoidFunction;
import core.utils.Point;
import core.utils.components.draw.Animation;
import core.utils.components.path.SimpleIPath;
import entities.DrawTextFactory;
import level.EscapeRoomLevel;
import utils.Constants;
import utils.GameState;
import utils.SoundManager;
import utils.Sounds;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class SettingsLevel extends EscapeRoomLevel {

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout      The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   */
  public SettingsLevel(LevelElement[][] layout, DesignLabel designLabel) { super(layout, designLabel); }

  @Override
  protected void onFirstTick() {
    new SettingsSlider("Master\nVolume", new Point(9, 9), GameState.volumeMaster(), 5, GameState::volumeMaster).setBounds(0, 100).addToGame();
    new SettingsSlider("Music", new Point(9, 7), GameState.volumeBgm(), 5, GameState::volumeBgm).setBounds(0, 100).addToGame();
    new SettingsSlider("SFX", new Point(9, 5), GameState.volumeSfx(), 5, GameState::volumeSfx).setBounds(0, 100).addToGame();
  }

  @Override
  protected void onTick() {

  }

  private class SettingsSlider {

    private static final float RADIUS = 1f;

    private Entity valueEntity;
    private Entity leftButton;
    private Entity rightButton;
    private Entity labelEntity;

    private final Point position;
    private int value;
    private final int perStep;
    private int min = 0;
    private int max = 100;
    private String label;
    public Consumer<Integer> onChange;
    public Function<Integer, String> valueFormatter;

    public SettingsSlider(String label, Point position, int value, int perStep, Consumer<Integer> onChange){
      this.label = label;
      this.position = position;
      this.value = value;
      this.perStep = perStep;
      this.onChange = onChange;
    }

    public SettingsSlider setBounds(int min, int max){
      this.min = min;
      this.max = max;
      return this;
    }

    public void addToGame(){
      //Settings slider consists of 4 components:
      //1 value entity (x+0)
      valueEntity = DrawTextFactory.createTextEntity(valueToString(), Constants.toffset(this.position), 0.75f, Color.WHITE, 0, 1);
      Game.add(valueEntity);

      //1 label entity (x-3)
      labelEntity = DrawTextFactory.createTextEntity(label, Constants.toffset(this.position.add(-3, 0)), 1f, Color.WHITE, 0, 1);
      Game.add(labelEntity);

      //2 button entities (left x-1, right x+1)
      leftButton = createButton(Constants.offset(this.position.add(-1, 0)), "images/arrow-left.png", () -> setValue(value - perStep));
      rightButton = createButton(Constants.offset(this.position.add(1, 0)), "images/arrow-right.png", () -> setValue(value + perStep));
      Game.add(leftButton);
      Game.add(rightButton);
    }

    private void setValue(int newValue){
      value = Math.min(max, Math.max(min, newValue));
      DrawTextComponent dtc = valueEntity.fetchOrThrow(DrawTextComponent.class);
      dtc.text(valueToString());
      SoundManager.playSound(Sounds.KeypadButtonClicked);
      onChange.accept(value);
    }

    private static Entity createButton(Point position, String image, IVoidFunction onInteract){
      Entity t = new Entity("settings-button");
      t.add(new DrawComponent(Animation.fromSingleImage(new SimpleIPath(image))));
      t.add(new PositionComponent(position));
      t.add(new InteractionComponent(RADIUS, true, (e1, e2) -> {
        onInteract.execute();
      }));
      t.add(new VicinityComponent(RADIUS, new TintEntityCommand(t)));
      return t;
    }

    private String valueToString(){
      if(valueFormatter != null) return valueFormatter.apply(value);
      return ""+value;
    }

  }
}
