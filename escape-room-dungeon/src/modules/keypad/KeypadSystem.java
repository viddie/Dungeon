package modules.keypad;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import contrib.components.UIComponent;
import contrib.hud.UIUtils;
import contrib.hud.elements.GUICombination;
import core.Entity;
import core.Game;
import core.System;
import core.components.DrawComponent;
import core.components.PositionComponent;
import hud.DebugOverlay;
import utils.SkinUtils;

import java.util.Optional;

public class KeypadSystem extends System {

  public KeypadSystem(){
    super(KeypadComponent.class, DrawComponent.class, PositionComponent.class);
  }

  @Override
  public void execute() {
    filteredEntityStream(KeypadComponent.class, DrawComponent.class, PositionComponent.class)
      .map(this::buildDataObject)
      .forEach(this::execute);
  }

  public void execute(Data d){
    Entity overlay = d.kc.overlay;

    DebugOverlay.renderCircle(d.pc.position(), 2, new Color(1, 1, 1, 0.3f));
//    DebugOverlay.renderText(d.pc.position().add(0, 1), d.kc.correctString()+"\n"+d.kc.enteredString(), new Color(1, 1, 1, 0.3f), 1f);

    if(overlay == null && d.kc.isUIOpen){
      //Dialog is closed but should be open
      Entity newOverlay = new Entity("keypad-overlay");
      UIComponent uic = new UIComponent(new KeypadUI(d.e, newOverlay), true, true);
      uic.onClose(() -> {
        d.kc.isUIOpen = false;
      });
      newOverlay.add(uic);
      d.kc.overlay = newOverlay;
      Game.add(newOverlay);

    } else if(overlay != null && !d.kc.isUIOpen){
      //Dialog is open but should be closed
      Game.remove(overlay);
      d.kc.overlay = null;
    }
  }

  private Data buildDataObject(Entity e){
    return new Data(
      e,
      e.fetchOrThrow(KeypadComponent.class),
      e.fetchOrThrow(DrawComponent.class),
      e.fetchOrThrow(PositionComponent.class)
    );
  }
  private record Data(Entity e, KeypadComponent kc, DrawComponent dc, PositionComponent pc) {}
}
