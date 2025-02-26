package modules.showimage;

import contrib.components.UIComponent;
import core.Entity;
import core.Game;
import core.System;
import core.components.DrawComponent;
import core.components.PositionComponent;

public class ShowImageSystem extends System {

  public ShowImageSystem(){
    super(ShowImageComponent.class, DrawComponent.class, PositionComponent.class);
  }

  @Override
  public void execute() {
    filteredEntityStream(ShowImageComponent.class, DrawComponent.class, PositionComponent.class)
      .map(this::buildDataObject)
      .forEach(this::execute);
  }

  public void execute(Data d){
    Entity overlay = d.sic.overlay;

    if(overlay == null && d.sic.isUIOpen){
      //Dialog is closed but should be open
      Entity newOverlay = new Entity("show-image-overlay");
      UIComponent uic = new UIComponent(new ShowImageUI(d.e, newOverlay), true, true);
      uic.onClose(() -> {
        d.sic.isUIOpen = false;
        d.sic.onClose(d.e, newOverlay);
      });
      newOverlay.add(uic);
      d.sic.overlay = newOverlay;
      Game.add(newOverlay);
      d.sic.onOpen(d.e, newOverlay);

    } else if(overlay != null && !d.sic.isUIOpen){
      //Dialog is open but should be closed
      Game.remove(overlay);
      d.sic.overlay = null;
    }
  }

  private Data buildDataObject(Entity e){
    return new Data(
      e,
      e.fetchOrThrow(ShowImageComponent.class),
      e.fetchOrThrow(DrawComponent.class),
      e.fetchOrThrow(PositionComponent.class)
    );
  }
  private record Data(Entity e, ShowImageComponent sic, DrawComponent dc, PositionComponent pc) {}
}
