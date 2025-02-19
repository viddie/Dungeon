package modules.keypad;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import contrib.components.UIComponent;
import contrib.hud.UIUtils;
import contrib.hud.elements.GUICombination;
import core.Entity;
import core.Game;
import core.System;
import core.components.DrawComponent;

import java.util.Optional;

public class KeypadSystem extends System {

  public KeypadSystem(){
    super(KeypadComponent.class, DrawComponent.class);
  }

  @Override
  public void execute() {
    filteredEntityStream(KeypadComponent.class, DrawComponent.class)
      .map(this::buildDataObject)
      .forEach(this::execute);
  }

  public void execute(Data d){
    Entity overlay = d.kc.overlay;

    if(overlay == null && d.kc.isUIOpen){
      //Dialog is closed but should be open
      Group g = new Group();
      Table t = new Table();
      g.addActor(t);
      t.setPosition(Game.windowWidth() / 2, Game.windowHeight() / 2);
//      t.add(new Label("Test string", UIUtils.defaultSkin())).pad(5);
//      t.add(new Label("Test string", UIUtils.defaultSkin())).pad(5);
//      t.add(new Label("Test string", UIUtils.defaultSkin())).pad(5).row();
//      t.add(new Label("Test string", UIUtils.defaultSkin())).pad(5);
//      t.add(new Label("Test string", UIUtils.defaultSkin())).pad(5).row();
//      t.setFillParent(true);
//      t.add(new Button()).expand().center().left().width(300).height(100).pad(2);
//      t.add(new Label("Test", UIUtils.defaultSkin())).expand().left().width(300).height(100).pad(2);
      t.add(new TextButton("Test1", UIUtils.defaultSkin())).width(300).height(100).pad(2);
      t.add(new TextButton("Test2", UIUtils.defaultSkin())).width(300).height(100).pad(2).row();
      t.add(new TextButton("Test3", UIUtils.defaultSkin())).width(300).height(100).pad(2);
      t.add(new TextButton("Test4", UIUtils.defaultSkin())).width(300).height(100).pad(2).row();
      t.add(new TextButton("Test5", UIUtils.defaultSkin())).width(300).height(100).pad(2);
      t.add(new TextButton("Test6", UIUtils.defaultSkin())).width(300).height(100).pad(2).row();

      Entity newOverlay = new Entity("keypad-overlay");
      newOverlay.add(new UIComponent(g, true, true));
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
      e.fetchOrThrow(DrawComponent.class)
    );
  }
  private record Data(Entity e, KeypadComponent kc, DrawComponent dc) {}
}
