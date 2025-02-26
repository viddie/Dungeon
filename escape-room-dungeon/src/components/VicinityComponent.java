package components;

import com.badlogic.gdx.graphics.Color;
import core.Component;
import core.Entity;
import core.components.DrawComponent;

/**
 * This component is used to control bevhavior for two entities getting close to each other.
 */
public class VicinityComponent implements Component {

  private Entity watchEntity;
  private float radius;
  private IVicinityCommand command;

  private boolean inRange = false;

  public VicinityComponent(float radius, IVicinityCommand command, Entity watchEntity){
    this.radius = radius;
    this.command = command;
    this.watchEntity = watchEntity;
  }

  public Entity watchEntity() {
    return watchEntity;
  }
  public void watchEntity(Entity watchEntity) {
    this.watchEntity = watchEntity;
  }

  public void command(IVicinityCommand command) {
    this.command = command;
  }
  public IVicinityCommand command() {
    return command;
  }

  public void radius(float radius) {
    this.radius = radius;
  }
  public float radius() {
    return radius;
  }

  public boolean isInRange() {
    return inRange;
  }
  public void setInRange(boolean inRange) {
    this.inRange = inRange;
  }

  public interface IVicinityCommand {
    void onEnterRange();
    void onLeaveRange();
    void onInRange(double distance);
  }
}
