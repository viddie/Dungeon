package entities;

import core.Entity;
import core.Game;
import core.utils.Point;

import java.util.ArrayList;
import java.util.List;

public class CompositeDecoFactory {

  public static List<Entity> createArch(Point pos){
    List<Entity> entities = new ArrayList<>();
    entities.add(DecoFactory.createDeco(pos, Deco.ArchL));
    entities.add(DecoFactory.createDeco(pos.add(1, 1), Deco.ArchC));
    entities.add(DecoFactory.createDeco(pos.add(2, 0), Deco.ArchR));
    return entities;
  }

}
