package components;

import core.Component;
import core.utils.Point;

public class SpikesComponent implements Component {

  private boolean isActive;
  private boolean isDeadly;
  private Point sendTo;
  private int preventMoveFrames;
  private boolean showBriefly = true;
  private int showTimer = 0;

  public SpikesComponent(boolean isActive, boolean isDeadly, Point sendTo, int preventMoveFrames){
    this.isActive = isActive;
    this.isDeadly = isDeadly;
    this.sendTo = sendTo;
    this.preventMoveFrames = preventMoveFrames;
  }

  public boolean active() {
    return isActive;
  }
  public void active(boolean active) {
    isActive = active;
  }

  public boolean deadly() {
    return isDeadly;
  }
  public void deadly(boolean deadly) {
    isDeadly = deadly;
  }

  public int preventMoveFrames() {
    return preventMoveFrames;
  }
  public void preventMoveFrames(int preventMoveFrames) {
    this.preventMoveFrames = preventMoveFrames;
  }

  public Point sendTo() {
    return sendTo;
  }
  public void sendTo(Point sendTo) {
    this.sendTo = sendTo;
  }

  public boolean showBriefly() {
    return showBriefly;
  }
  public void showBriefly(boolean showBriefly){
    this.showBriefly = showBriefly;
  }

  public int showTimer(){
    return this.showTimer;
  }
  public void showTimer(int showTimer){
    this.showTimer = showTimer;
  }
}
