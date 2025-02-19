package modules.keypad;

import core.Component;
import core.Entity;
import core.utils.IVoidFunction;

import java.util.ArrayList;
import java.util.List;

public class KeypadComponent implements Component {

  public final List<Integer> correctDigits;
  public final List<Integer> enteredDigits;
  public boolean isUIOpen = false;
  public boolean isUnlocked = false;
  public boolean showDigitCount = true;
  public IVoidFunction action;
  public Entity overlay;

  public KeypadComponent(){
    this.correctDigits = new ArrayList<>();
    this.enteredDigits = new ArrayList<>();
  }

  public KeypadComponent(List<Integer> correctDigits, IVoidFunction action){
    this.correctDigits = correctDigits;
    this.enteredDigits = new ArrayList<>();
    this.action = action;
  }

  public KeypadComponent(List<Integer> correctDigits, IVoidFunction action, boolean showDigitCount){
    this(correctDigits, action);
    this.showDigitCount = showDigitCount;
  }

}
