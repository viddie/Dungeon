package core.utils.components.draw;

public enum DepthLayer {
  Background(-9999),
  Level(-1000),
  Normal(0),
  Player(100),
  Foreground(1000),
  UI(9999),
  ;

  private int depth;
  DepthLayer(int depth){
    this.depth = depth;
  }
  public int depth(){ return depth; }
}
