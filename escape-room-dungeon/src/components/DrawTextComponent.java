package components;

import core.Component;

public class DrawTextComponent implements Component {

  private String text;

  public DrawTextComponent(String text){
    this.text = text;
  }

  public String getText() {
    return text;
  }
  public void setText(String text) {
    this.text = text;
  }
}
