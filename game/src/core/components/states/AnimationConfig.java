package core.components.states;

import core.utils.components.path.IPath;

public class AnimationConfig {

  private IPath path;
  private SpritesheetConfig config;
  public boolean isLooping = true;

  public AnimationConfig(IPath path, SpritesheetConfig config){
    this.path = path;
    this.config = config;
  }
  public AnimationConfig(IPath path){
    this(path, null);
  }

  public static AnimationConfig[] fromPath(IPath... paths){
    AnimationConfig[] configs = new AnimationConfig[paths.length];
    for (int i = 0; i < paths.length; i++) {
      configs[i] = new AnimationConfig(paths[i]);
    }
    return configs;
  }

}
