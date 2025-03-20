package core.components.states;

public class SpritesheetConfig {

  private int spriteWidth = 16;
  private int spriteHeight = 16;
  private int x = 0;
  private int y = 0;
  private int rows = 1;
  private int columns = 1;

  public SpritesheetConfig(){}
  public SpritesheetConfig(int width, int height, int x, int y, int rows, int columns){
    this.spriteWidth = width;
    this.spriteHeight = height;
    this.x = x;
    this.y = y;
    this.rows = rows;
    this.columns = columns;
  }
  public SpritesheetConfig(int x, int y, int rows, int columns){
    this(16, 16, x, y, rows, columns);
  }
  public SpritesheetConfig(int rows, int columns){
    this(16, 16, 0, 0, rows, columns);
  }

  public int spriteWidth() { return spriteWidth; }
  public SpritesheetConfig spriteWidth(int spriteWidth) {
    this.spriteWidth = spriteWidth;
    return this;
  }

  public int spriteHeight() { return spriteHeight; }
  public SpritesheetConfig spriteHeight(int spriteHeight) {
    this.spriteHeight = spriteHeight;
    return this;
  }

  public int x() { return x; }
  public SpritesheetConfig x(int x) {
    this.x = x;
    return this;
  }
  public int y() { return y; }
  public SpritesheetConfig y(int y) {
    this.y = y;
    return this;
  }
  public int rows() { return rows; }
  public SpritesheetConfig rows(int rows) {
    this.rows = rows;
    return this;
  }
  public int columns() { return columns; }
  public SpritesheetConfig columns(int columns) {
    this.columns = columns;
    return this;
  }
}
