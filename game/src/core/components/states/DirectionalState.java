package core.components.states;

import core.level.Tile;
import core.utils.components.path.IPath;

public class DirectionalState extends State {

//  private Animation down; //Default animation
  private Animation left;
  private Animation up;
  private Animation right;

  public DirectionalState(String name, AnimationConfig config) {
    super(name, config);
  }
  public DirectionalState(String name, IPath path, SpritesheetConfig config) {
    super(name, path, config);
  }
  public DirectionalState(String name, IPath down, IPath left, IPath up, IPath right) {
    super(name, down);
    this.left = new Animation(new AnimationConfig(left));
    this.up = new Animation(new AnimationConfig(up));
    this.right = new Animation(new AnimationConfig(right));
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
