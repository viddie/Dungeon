package components;

import com.badlogic.gdx.graphics.Color;
import core.Component;

public class DrawTextComponent implements Component {

  private String text;
  private float scale;
  private Color color;
  private Color outlineColor;
  private float displayRadius = 6;
  private float maxAlpha = 1;

  public DrawTextComponent(String text, float scale, Color color){
    this(text, scale, color, Color.BLACK);
  }
  public DrawTextComponent(String text, float scale, Color color, Color outlineColor){
    this.text = text;
    this.scale = scale;
    this.color = color;
    this.outlineColor = outlineColor;
  }

  public String text() {
    return text;
  }
  public void text(String text) {
    this.text = text;
  }

  public float scale(){
    return scale;
  }
  public void scale(float scale){
    this.scale = scale;
  }

  public Color color(){
    return color;
  }
  public void color(Color color){
    this.color = color;
  }

  public float displayRadius(){
    return displayRadius;
  }
  public void displayRadius(float displayRadius) {
    this.displayRadius = displayRadius;
  }

  public float maxAlpha(){
    return maxAlpha;
  }
  public void maxAlpha(float maxAlpha) {
    this.maxAlpha = maxAlpha;
  }

  public Color outlineColor(){
    return outlineColor;
  }
  public void outlineColor(Color outlineColor){
    this.outlineColor = outlineColor;
  }
}
