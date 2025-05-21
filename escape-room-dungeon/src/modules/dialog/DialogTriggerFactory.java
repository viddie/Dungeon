package modules.dialog;

import core.Entity;
import core.utils.Point;
import entities.TriggerFactory;

public class DialogTriggerFactory {

  public static Entity createDialogTrigger(Point pos, float width, float height, DialogConfig dialog){
    DialogTriggerComponent dtc = new DialogTriggerComponent();
    Entity trigger = TriggerFactory.createTrigger(pos, width, height, (e, o, d) -> {
      if(dtc.triggered()) return;
      DialogSystem.startDialog(dialog);
      dtc.triggered(true);
    }, (e, o, d) -> {});
    trigger.add(dtc);
    return trigger;
  }

}
