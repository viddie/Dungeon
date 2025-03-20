package core.components.states;

import core.level.Tile;
import core.utils.components.path.IPath;

public class DirectionalState extends State {

//  private Animation down; //Default animation
  private Animation left;
  private Animation up;
  private Animation right;

  public DirectionalState(String name, IPath down, IPath left, IPath up, IPath right, AnimationConfig configDown, AnimationConfig configLeft, AnimationConfig configUp, AnimationConfig configRight) {
    super(name, down, configDown);
    this.left = new Animation(left, configLeft);
    this.up = new Animation(up, configUp);
    this.right = new Animation(right, configRight);
  }
  public DirectionalState(String name, IPath down, IPath left, IPath up, IPath right, AnimationConfig config) {
    this(name, down, left, up, right, config, config, config, config);
  }
  public DirectionalState(String name, IPath path, SpritesheetConfig config) {
    super(name, path, config);
  }
  public DirectionalState(String name, IPath down, IPath left, IPath up, IPath right) {
    this(name, down, left, up, right, null);
  }

  @Override
  public Animation getAnimation() {
    Tile.Direction direction = (Tile.Direction) getData();
    if(direction == null) return super.getAnimation();
    return switch(direction){
      case S -> super.getAnimation();
      case W -> left;
      case N -> up;
      case E -> right;
    };
  }
}
