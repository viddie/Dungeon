package utils;

import core.utils.Point;

public class Constants {

  public static final float X_OFFSET = 0.5f;
  public static final float X_OFFSET_TILE = 0.5f;
  public static final float Y_OFFSET = 0.25f;
  public static final float Y_OFFSET_TILE = 0.5f;

  /**
   * Adds 0.5 to x and 0.25 to y
   * @param point the point to add the offset to
   * @return a new Point with the offset applied
   */
  public static Point offset(Point point){
    return point.add(X_OFFSET, Y_OFFSET);
  }

  /**
   * Adds 0.5 to x and 0.5 to y
   * @param point the point to add the tile offset to
   * @return a new Point with the tile offset applied
   */
  public static Point toffset(Point point){
    return point.add(X_OFFSET_TILE, Y_OFFSET_TILE);
  }

}
