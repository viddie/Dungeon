package systems;

import com.badlogic.gdx.graphics.Color;
import components.DebugRenderComponent;
import components.VicinityComponent;
import contrib.components.CollideComponent;
import core.Entity;
import core.System;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.components.MissingComponentException;
import hud.DebugOverlay;

public class DebugRenderSystem extends System {

  public DebugRenderSystem(){
    super(PositionComponent.class, DebugRenderComponent.class);
  }

  @Override
  public void execute() {
    filteredEntityStream(PositionComponent.class, DebugRenderComponent.class)
      .map(this::buildDataObject)
      .forEach(this::execute);
  }

  public void execute(Data d){
    if(d.drc.drawPosition){
      DebugOverlay.renderRect(d.pc.position(), 1, 1, d.drc.color);
    }
    if(d.drc.drawCircleRadius > 0){
      DebugOverlay.renderCircle(d.pc.position(), d.drc.drawCircleRadius, d.drc.color);
    }
    if(d.drc.customRender != null){
      d.drc.customRender.accept(d.pc);
    }
    if(d.drc.drawCollider){
      try {
        CollideComponent cc = d.e.fetchOrThrow(CollideComponent.class);
        Point size = cc.size();
        DebugOverlay.renderRect(d.pc.position().add(cc.offset()), size.x, size.y, d.drc.color);
      } catch(MissingComponentException ignored){}
    }
  }

  private DebugRenderSystem.Data buildDataObject(Entity e){
    return new DebugRenderSystem.Data(
      e,
      e.fetchOrThrow(PositionComponent.class),
      e.fetchOrThrow(DebugRenderComponent.class)
    );
  }
  private record Data(Entity e, PositionComponent pc, DebugRenderComponent drc) {}
}
