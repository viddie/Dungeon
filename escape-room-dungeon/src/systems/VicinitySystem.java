package systems;

import com.badlogic.gdx.graphics.Color;
import components.DrawTextComponent;
import components.VicinityComponent;
import core.Entity;
import core.System;
import core.components.PositionComponent;
import core.utils.Point;
import core.utils.components.MissingComponentException;
import hud.DebugOverlay;

public class VicinitySystem extends System {

  public VicinitySystem(){
    super(VicinityComponent.class, PositionComponent.class);
  }

  @Override
  public void execute() {
    filteredEntityStream(PositionComponent.class, VicinityComponent.class)
      .map(this::buildDataObject)
      .forEach(this::execute);
  }

  public void execute(Data d){
    float radius = d.vc.radius();
    VicinityComponent.IVicinityCommand command = d.vc.command();
    Entity other = d.vc.watchEntity();

    Point otherPos = null;
    try{
      otherPos = other.fetchOrThrow(PositionComponent.class).position();
    }catch(MissingComponentException ignored){
      return;
    }

    double distance = d.pc.position().distance(otherPos);
    boolean isInRange = distance <= radius;

    if(d.vc.isInRange() != isInRange){
      if(isInRange) command.onEnterRange();
      else command.onLeaveRange();
    }
    if(isInRange){
      command.onInRange((float) distance);
    }
    d.vc.setInRange(isInRange);

    DebugOverlay.renderCircle(d.pc.position(), radius, isInRange ? Color.GREEN : Color.WHITE);
    if(distance <= 10){
//      DebugOverlay.renderArrow(d.pc.position(), otherPos, Color.WHITE);
//      DebugOverlay.renderLine(d.pc.position(), otherPos, isInRange ? Color.GREEN : Color.WHITE);
    }
    DebugOverlay.renderRect(d.pc.position(), 1, 1);
  }

  private VicinitySystem.Data buildDataObject(Entity e){
    return new VicinitySystem.Data(
      e,
      e.fetchOrThrow(PositionComponent.class),
      e.fetchOrThrow(VicinityComponent.class)
    );
  }
  private record Data(Entity e, PositionComponent pc, VicinityComponent vc) {}
}
