package entities;

import com.badlogic.gdx.graphics.Color;
import components.DrawTextComponent;
import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.utils.Point;

public class DrawTextFactory {

  public static Entity createTextEntity(String text, Point pos, float scale, Color color, float displayRadius, float maxAlpha){
    Entity entity = new Entity("Text");
    DrawTextComponent dtc = new DrawTextComponent(text, scale, color);
    dtc.displayRadius(displayRadius);
    dtc.maxAlpha(maxAlpha);
    entity.add(dtc);
    entity.add(new PositionComponent(pos));
    return entity;
  }
  public static Entity createTextEntity(String text, Point pos, float scale, Color color){
    return createTextEntity(text, pos, scale, color, 6, 1);
  }
  public static Entity createTextEntity(String text, Point pos, float scale){
    return createTextEntity(text, pos, scale, Color.WHITE, 6, 1);
  }
  public static Entity createTextEntity(String text, Point pos){
    return createTextEntity(text, pos, 1, Color.WHITE, 6, 1);
  }

}
