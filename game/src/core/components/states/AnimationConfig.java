package core.components.states;

public class AnimationConfig {

  private SpritesheetConfig config;
  private int framesPerSprite = 10;
  //Default width/height of 1 tile.
  private float scaleX = 1;
  private float scaleY = 0;
  private boolean isLooping = true;
  private boolean centered = false;

  public AnimationConfig(SpritesheetConfig config){
    this.config = config;
  }
  public AnimationConfig(){
    this(null);
  }

//  public static AnimationConfig[] fromPath(IPath... paths){
//    AnimationConfig[] configs = new AnimationConfig[paths.length];
//    for (int i = 0; i < paths.length; i++) {
//      configs[i] = new AnimationConfig(paths[i]);
//    }
//    return configs;
//  }

  public SpritesheetConfig config(){ return config; }
  public AnimationConfig config(SpritesheetConfig config){
    this.config = config;
    return this;
  }

  public int framesPerSprite(){ return framesPerSprite; }
  public AnimationConfig framesPerSprite(int framesPerSprite){
    if(framesPerSprite <= 0) throw new IllegalArgumentException("framesPerSprite cannot be less than 1");
    this.framesPerSprite = framesPerSprite;
    return this;
  }

  public AnimationConfig scaleX(float scaleX) {
    this.scaleX = scaleX;
    return this;
  }
  public float scaleX() {
    return scaleX;
  }

  public AnimationConfig scaleY(float scaleY) {
    this.scaleY = scaleY;
    return this;
  }
  public float scaleY() {
    return scaleY;
  }

  public AnimationConfig isLooping(boolean looping) {
    isLooping = looping;
    return this;
  }
  public boolean isLooping() {
    return isLooping;
  }

  public AnimationConfig centered(boolean centered) {
    this.centered = centered;
    return this;
  }
  public boolean centered() {
    return centered;
  }
}
