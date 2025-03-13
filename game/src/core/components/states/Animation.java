package core.components.states;

import com.badlogic.gdx.graphics.g2d.Sprite;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;

public class Animation {

  private static final IPath MISSING_TEXTURE_PATH = new SimpleIPath("animation/missing_texture.png");

  private AnimationConfig config;
  private Sprite[] sprites;

  public Animation(AnimationConfig config){
    this.config = config;
    load();
  }

  private void load(){
    //Load the sprites based on the AnimationConfig
  }

  public Sprite getSprite(int frameCount, int framesPerSprite){
    int spriteIndex = frameCount / framesPerSprite;
    if(config.isLooping){
      spriteIndex = spriteIndex % sprites.length;
    } else {
      spriteIndex = Math.min(spriteIndex, sprites.length - 1);
    }
    return sprites[spriteIndex];
  }

}
