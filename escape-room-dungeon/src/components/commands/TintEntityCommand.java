package components.commands;


import com.badlogic.gdx.graphics.Color;
import components.VicinityComponent;
import core.Entity;
import core.components.DrawComponent;

public class TintEntityCommand implements VicinityComponent.IVicinityCommand {

  private static final Color DEFAULT_COLOR = new Color(0.6f, 0.6f, 1, 1);

  private Entity parent;
  private Color tint;

  public TintEntityCommand(Entity parent, Color tint){
    this.parent = parent;
    this.tint = tint;
  }
  public TintEntityCommand(Entity parent){
    this(parent, DEFAULT_COLOR);
  }

  @Override
  public void onEnterRange() {
    DrawComponent dc = parent.fetchOrThrow(DrawComponent.class);
    dc.tintColor(Color.rgba8888(tint));
  }
  @Override
  public void onLeaveRange() {
    DrawComponent dc = parent.fetchOrThrow(DrawComponent.class);
    dc.tintColor(-1);
  }
  @Override
  public void onInRange(double distance) {}
}
