package utils;

import com.badlogic.gdx.math.MathUtils;

public class Wiggler {

  private boolean isActive = true;
  private float progress;
  private final float progressPerStep;

  public Wiggler(float progressPerStep) {
    progress = 0;
    this.progressPerStep = progressPerStep;
  }
  public Wiggler(){
    this(1 / 60f);
  }

  public void setActive(boolean isActive){
    this.isActive = isActive;
  }

  public float calculate(){
    if(!isActive) return 0f;

    progress += progressPerStep;
    if(progress > 1) progress = 0;

    float rads = MathUtils.lerp(0, (float)Math.PI * 2, progress);
    return (float)Math.sin(rads);
  }
}
