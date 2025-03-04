package entities;

import com.badlogic.gdx.graphics.Color;
import components.DebugRenderComponent;
import contrib.components.CollideComponent;
import core.Entity;
import core.components.PositionComponent;
import core.level.Tile;
import core.utils.Point;
import core.utils.TriConsumer;
import hud.DebugOverlay;
import utils.Constants;

public class TriggerFactory {

  public static Entity createTrigger(Point pos, float width, float height, TriConsumer<Entity, Entity, Tile.Direction> onEnter, TriConsumer<Entity, Entity, Tile.Direction> onLeave){
    Entity trigger = new Entity("trigger");
    trigger.add(new PositionComponent(Constants.offset(pos)));
    trigger.add(new CollideComponent(new Point(0.25f, 0.25f), new Point(width, height), onEnter, onLeave));
    trigger.add(new DebugRenderComponent(new Color(1, 0, 0, 0.5f), (pc) -> {
      DebugOverlay.renderRect(pc.position(), width, height);
    }));
    return trigger;
  }

}
