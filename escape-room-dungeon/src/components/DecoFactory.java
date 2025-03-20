package components;

import core.Entity;
import core.components.DrawComponent;
import core.components.PositionComponent;
import core.components.states.AnimationConfig;
import core.utils.Point;
import core.utils.components.draw.DepthLayer;
import utils.Constants;

public class DecoFactory {

  public static Entity createDeco(Point pos, Deco deco, int depth, AnimationConfig config){
    Entity entity = new Entity("deco");
    entity.add(new PositionComponent(Constants.offset(pos)));
    DrawComponent dc = new DrawComponent(deco.path(), config);
    dc.depth(depth);
    entity.add(dc);
    return entity;
  }
  public static Entity createDeco(Point pos, Deco deco, AnimationConfig config){
    return createDeco(pos, deco, DepthLayer.BackgroundDeco.depth(), config);
  }
  public static Entity createDeco(Point pos, Deco deco){
    return createDeco(pos, deco, DepthLayer.BackgroundDeco.depth(), deco.config());
  }
  public static Entity createDeco(Point pos, Deco deco, int depth){
    return createDeco(pos, deco, depth, deco.config());
  }
}
