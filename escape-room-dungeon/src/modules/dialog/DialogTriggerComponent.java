package modules.dialog;

import core.Component;

public class DialogTriggerComponent implements Component {

  private boolean triggered;

  public void triggered(boolean triggered) {
    this.triggered = triggered;
  }
  public boolean triggered() {
    return triggered;
  }
}
