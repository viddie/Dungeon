package hud;

import contrib.components.UIComponent;
import contrib.hud.elements.GUICombination;
import contrib.utils.components.skill.SkillTools;
import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.utils.Point;
import level.utils.ITickable;

import java.util.ArrayList;
import java.util.List;

public class DebugOverlay implements ITickable {

  public static DebugOverlay INSTANCE = null;
  public DebugOverlay(){
    INSTANCE = this;
  }

  private Entity entity;
  private final List<HUDText> texts = new ArrayList<>();

  public void addDebugOverlayToGame(float scale){
    if (entity != null){
      Game.remove(entity);
    }

    entity = new Entity();
    HUDText hudText = new HUDText("",  HUDText.Anchor.TopLeft, scale);
    texts.add(hudText);
    HUDText hudText2 = new HUDText("",  HUDText.Anchor.BottomLeft, scale);
    texts.add(hudText2);
    HUDText hudText3 = new HUDText("",  HUDText.Anchor.BottomRight, scale);
    texts.add(hudText3);
    entity.add(new UIComponent(new GUICombination(hudText, hudText2, hudText3), false, false));
    Game.add(entity);
  }

  public static void setText(int textIndex, String text){
    DebugOverlay instance = INSTANCE;
    if(instance == null) return;

    if(textIndex < 0 || textIndex >= instance.texts.size()) return;
    HUDText hud = instance.texts.get(textIndex);
    hud.setText(text);
  }

  public void text(int textIndex, String text){
    if(textIndex < 0 || textIndex >= texts.size()) return;
    HUDText hud = texts.get(textIndex);
    hud.setText(text);
  }

  @Override
  public void onTick(boolean isFirstTick) {
    Point heroPos = Game.hero().orElseThrow().fetchOrThrow(PositionComponent.class).position();

    Point mosPos = SkillTools.cursorPositionAsPoint();
    mosPos = new Point(mosPos.x - 0.5f, mosPos.y - 0.25f);

    setText(0, "Hero position: "+heroPos+"\nMouse position: "+mosPos);
  }
}
