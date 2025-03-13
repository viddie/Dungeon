package core.components.states;

import com.badlogic.gdx.graphics.g2d.Sprite;
import core.utils.components.path.IPath;

public class State {

  public final String name;
  private Animation animation;
  private int framesPerSprite = 1;
  private Object data;

  public State(String name, AnimationConfig config){
    if(name == null) throw new IllegalArgumentException("name can't be empty");
    this.name = name;
    animation = new Animation(config);
  }
  public State(String name, IPath path, SpritesheetConfig config){
    this(name, new AnimationConfig(path, config));
  }
  public State(String name, IPath path){
    this(name, path, null);
  }

  public Sprite getSprite(int frameCount){
    int spriteIndex = frameCount / framesPerSprite;
    return null;
  }
  public void setData(Object data){
    this.data = data;
  }
}
