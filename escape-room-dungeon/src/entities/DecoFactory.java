package entities;

import components.DebugRenderComponent;
import contrib.components.CollideComponent;
import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.components.states.AnimationConfig;
import core.level.Tile;
import core.utils.Point;
import core.utils.components.draw.DepthLayer;
import utils.Constants;

public class DecoFactory {

  private static final float COLLIDE_SET_DISTANCE = 0.01f;

  public static Entity createDeco(Point pos, Deco deco, int depth, AnimationConfig config, Point solidCollider, Point colliderOffset){
    Entity entity = new Entity(deco.name());
    entity.add(new PositionComponent(Constants.offset(pos)));
    DrawComponent dc = new DrawComponent(deco.path(), config);
    dc.depth(depth);
    entity.add(dc);

    if(solidCollider != null){
      colliderOffset = colliderOffset == null ? new Point(0, 0) : colliderOffset;
      CollideComponent cc = new CollideComponent(colliderOffset, solidCollider.copy(), null, null);
      cc.collideMove(DecoFactory::solidCollide);
      entity.add(cc);
    }
    entity.add(new DebugRenderComponent().drawCollider(true));

    return entity;
  }
  public static Entity createDeco(Point pos, Deco deco, AnimationConfig config){
    return createDeco(pos, deco, DepthLayer.BackgroundDeco.depth(), config, null, null);
  }
  public static Entity createDeco(Point pos, Deco deco, Point solidCollider){
    return createDeco(pos, deco, DepthLayer.BackgroundDeco.depth(), deco.config(), solidCollider, null);
  }
  public static Entity createDeco(Point pos, Deco deco){
    return createDeco(pos, deco, deco.defaultDepth(), deco.config(), deco.defaultCollider(), deco.defaultColliderOffset());
  }
  public static Entity createDeco(Point pos, Deco deco, int depth){
    return createDeco(pos, deco, depth, deco.config(), null, null);
  }
  public static Entity createDeco(Point pos, Deco deco, int depth, Point solidCollider){
    return createDeco(pos, deco, depth, deco.config(), solidCollider, null);
  }
  public static Entity createDeco(Point pos, Deco deco, int depth, Point solidCollider, Point colliderOffset){
    return createDeco(pos, deco, depth, deco.config(), solidCollider, colliderOffset);
  }

  public static void solidCollide(Entity entity, Entity other, Tile.Direction direction){
    //other = Hero
    CollideComponent decoCollider = entity.fetchOrThrow(CollideComponent.class);
    PositionComponent decoPc = entity.fetchOrThrow(PositionComponent.class);
    CollideComponent otherCollider = other.fetchOrThrow(CollideComponent.class);
    PositionComponent otherPc = other.fetchOrThrow(PositionComponent.class);

    Point decoColliderPos = decoPc.position().add(decoCollider.offset());
    Point decoSize = decoCollider.size();
    Point otherColliderPos = otherPc.position().add(otherCollider.offset());
    Point otherSize = otherCollider.size();

    Point newColliderPos = switch(direction){
      case N -> new Point(otherColliderPos.x, decoColliderPos.y - otherSize.y - COLLIDE_SET_DISTANCE);
      case W -> new Point(decoColliderPos.x - otherSize.x - COLLIDE_SET_DISTANCE, otherColliderPos.y);
      case S -> new Point(otherColliderPos.x, decoColliderPos.y + decoSize.y + COLLIDE_SET_DISTANCE);
      case E -> new Point(decoColliderPos.x + decoSize.x + COLLIDE_SET_DISTANCE, otherColliderPos.y);
    };

    otherPc.position(newColliderPos.sub(otherCollider.offset()));
  }
}
