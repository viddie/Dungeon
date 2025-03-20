package core.components.states;

import com.badlogic.gdx.graphics.g2d.Sprite;
import core.utils.components.path.IPath;

public class State {

  public final String name;
  protected Animation animation;
  protected Object data;

  public State(String name, IPath path, AnimationConfig config){
    if(name == null) throw new IllegalArgumentException("name can't be empty");
    this.name = name;
    animation = new Animation(path, config);
  }
  public State(String name, IPath path, SpritesheetConfig config){
    this(name, path, new AnimationConfig(config));
  }
  public State(String name, IPath path){
    this(name, path, new AnimationConfig());
  }

  public void update(){
    getAnimation().update();
  }
  public void frameCount(int frameCount){
    getAnimation().frameCount(frameCount);
  }

  public Sprite getSprite(){
    return getAnimation().getSprite();
  }
  public float getSpriteWidth(){
    return getAnimation().getSpriteWidth();
  }
  public float getSpriteHeight(){
    return getAnimation().getSpriteHeight();
  }

  public boolean isAnimationFinished(){
    return getAnimation().isFinished();
  }

  public Animation getAnimation(){
    return animation;
  }

  public Object getData(){ return data; }
  public void setData(Object data){
    this.data = data;
  }
}
