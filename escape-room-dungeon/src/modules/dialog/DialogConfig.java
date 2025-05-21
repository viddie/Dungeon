package modules.dialog;

import core.Entity;

import java.util.ArrayList;
import java.util.List;

public class DialogConfig {

  private List<Line> lines = new ArrayList<>();

  public DialogConfig(){}
  public DialogConfig(String author, String line){
    addLine(author, line);
  }
  public DialogConfig(String author, String... lines){
    for(int i = 0; i < lines.length; i++){
      addLine(author, lines[i]);
    }
  }

  public void addLine(String author, String line){
    lines.add(new Line(author, line));
  }

  public List<Line> getLines() {
    return lines;
  }
  public int lineCount(){
    return lines.size();
  }

  record Line(String author, String line) {}

}
