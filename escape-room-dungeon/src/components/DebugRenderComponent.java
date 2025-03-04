package components;

import com.badlogic.gdx.graphics.Color;
import core.Component;
import core.components.PositionComponent;
import core.utils.IVoidFunction;

import java.util.function.Consumer;

public class DebugRenderComponent implements Component {

  public Color color = new Color(1, 1, 1, 0.5f);
  public boolean drawPosition = false;
  /**
   * Set to >0 if a circle should be rendered, =0 if disabled
   */
  public float drawCircleRadius = 0;
  public Consumer<PositionComponent> customRender;

  public DebugRenderComponent(){}
  public DebugRenderComponent(Consumer<PositionComponent> customRender){
    this.customRender = customRender;
  }
  public DebugRenderComponent(Color color, Consumer<PositionComponent> customRender){
    this.color = color;
    this.customRender = customRender;
  }

}
