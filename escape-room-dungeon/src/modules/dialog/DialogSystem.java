package modules.dialog;

import contrib.components.UIComponent;
import core.Entity;
import core.Game;
import core.System;
import modules.showimage.ShowImageUI;

public class DialogSystem extends System {

  private static Entity dialogOverlay;

  public DialogSystem(){
    dialogOverlay = new Entity("dialog-overlay");
    Game.add(dialogOverlay);
  }

  @Override
  public void execute() {
    //If overlay gets removed for some reason, re-add it
    if(Game.entityStream().noneMatch(e -> e == dialogOverlay)){
      Game.add(dialogOverlay);
    }
  }

  public static void startDialog(DialogConfig config){
    if(hasOpenDialog()) return;
    UIComponent uic = new UIComponent(new DialogUI(config), true, false);
    uic.onClose(() -> {
      //On dialog close
    });
    dialogOverlay.add(uic);
  }

  public static void endDialog(){
    if(!hasOpenDialog()) return;
    dialogOverlay.remove(UIComponent.class);
  }

  public static boolean hasOpenDialog(){
    return dialogOverlay.fetch(UIComponent.class).isPresent();
  }

}
