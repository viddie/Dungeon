package utils;

public enum Sounds {

  FootSteps("footsteps/retro"),
  KeypadButtonClicked("retro-beep-01"),
  KeypadUnlocked("retro-event-ui-01"),
  KeypadWrong("retro-event-wrong"),
  LeverFlipped("wood-block-1"),
  DoorOpened("creaky-door-open")
  ;

  private String path;
  Sounds(String path){
    this.path = path;
  }

  public String getPath(){
    return "sounds/" + path + ".wav";
  }
}
