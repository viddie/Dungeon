package engine.utils.components.draw.shader;

import engine.utils.Rectangle;
import java.util.List;
import java.util.Map;

/** A shader that applies a curved-screen CRT effect with configurable scanlines. */
public class CrtShader extends AbstractShader {

  private static final String VERT_PATH = "shaders/passthrough.vert";
  private static final String FRAG_PATH = "shaders/crt.frag";

  private boolean vertical = false;
  private float lineWidth = 1.0f;
  private float warpStrength = 1.0f;

  /** Creates a CrtShader with default parameters. */
  public CrtShader() {
    super(VERT_PATH, FRAG_PATH);
  }

  @Override
  protected List<UniformBinding> getUniforms(int actualUpscale) {
    return List.of(
        new BoolUniform("u_vertical", vertical),
        new FloatUniform("u_lineWidth", lineWidth),
        new FloatUniform("u_warpStrength", warpStrength));
  }

  @Override
  public int padding() {
    return 0;
  }

  @Override
  public Rectangle worldBounds() {
    return null;
  }

  /**
   * Gets whether scanlines run vertically.
   *
   * @return true for vertical scanlines, false for horizontal scanlines
   */
  public boolean vertical() {
    return vertical;
  }

  /**
   * Sets whether scanlines run vertically.
   *
   * @param vertical true for vertical scanlines, false for horizontal scanlines
   * @return this shader for chaining
   */
  public CrtShader vertical(boolean vertical) {
    this.vertical = vertical;
    return this;
  }

  /**
   * Gets the scanline width in pixels.
   *
   * @return the scanline width
   */
  public float lineWidth() {
    return lineWidth;
  }

  /**
   * Sets the scanline width in pixels.
   *
   * @param lineWidth the scanline width
   * @return this shader for chaining
   */
  public CrtShader lineWidth(float lineWidth) {
    this.lineWidth = lineWidth;
    return this;
  }

  /**
   * Gets the curved-screen warp strength.
   *
   * @return the warp strength
   */
  public float warpStrength() {
    return warpStrength;
  }

  /**
   * Sets the curved-screen warp strength.
   *
   * @param warpStrength the warp strength
   * @return this shader for chaining
   */
  public CrtShader warpStrength(float warpStrength) {
    this.warpStrength = warpStrength;
    return this;
  }

  @Override
  protected void writeProperties(Map<String, String> properties) {
    properties.put("vertical", Boolean.toString(vertical));
    properties.put("lineWidth", Float.toString(lineWidth));
    properties.put("warpStrength", Float.toString(warpStrength));
  }

  @Override
  protected void readProperties(Map<String, String> properties) {
    vertical = booleanProperty(properties, "vertical");
    lineWidth = floatProperty(properties, "lineWidth");
    warpStrength = floatProperty(properties, "warpStrength");
  }
}
