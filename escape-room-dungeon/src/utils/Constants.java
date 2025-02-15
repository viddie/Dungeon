package utils;

import core.utils.Point;

public class Constants {

  public static final float X_OFFSET = 0.5f;
  public static final float X_OFFSET_TILE = 0.5f;
  public static final float Y_OFFSET = 0.25f;
  public static final float Y_OFFSET_TILE = 0.5f;

  public static Point offset(Point point){
    return point.add(X_OFFSET, Y_OFFSET);
  }
  public static Point toffset(Point point){
    return point.add(X_OFFSET_TILE, Y_OFFSET_TILE);
  }

}
