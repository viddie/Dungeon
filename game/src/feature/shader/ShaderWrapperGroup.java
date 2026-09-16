package feature.shader;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import engine.Game;
import engine.systems.DrawSystem;
import engine.utils.components.draw.shader.AbstractShader;
import java.util.Objects;

/**
 * A scene2d group that renders its child actor with an authoritative shader.
 *
 * <p>The batch's previous shader is restored after rendering so that wrapper groups can be nested.
 */
public final class ShaderWrapperGroup extends Group {
  private final AbstractShader shader;

  /**
   * Creates a group that renders the supplied actor with the supplied shader.
   *
   * @param shader the shader to apply
   * @param actor the actor to render
   */
  public ShaderWrapperGroup(AbstractShader shader, Actor actor) {
    this.shader = Objects.requireNonNull(shader, "shader");
    addActor(Objects.requireNonNull(actor, "actor"));
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    if (!shader.enabled()) {
      super.draw(batch, parentAlpha);
      return;
    }

    ShaderProgram previousShader = batch.getShader();
    batch.flush();
    try {
      shader.bind(batch, 1);
      DrawSystem.setGeneralShaderUniforms(
          batch.getShader(), Game.windowWidth(), Game.windowHeight());
      super.draw(batch, parentAlpha);
    } finally {
      batch.flush();
      batch.setShader(previousShader);
    }
  }
}
