package modules.keypad;

import core.Component;
import core.Entity;
import core.utils.IVoidFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

  public String enteredString(){
    String s = enteredDigits.stream().map(Object::toString).collect(Collectors.joining(""));
    if(showDigitCount){
      while(s.length() < correctDigits.size()){
        s += "*";
      }
    }
    return s;
  }
  public String correctString(){
    return correctDigits.stream().map(Object::toString).collect(Collectors.joining(""));
  }

  public void backspace(){
    if(enteredDigits.size() == 0 || isUnlocked) return;
    enteredDigits.remove(enteredDigits.size()-1);
  }

  public void addDigit(int digit){
    if(enteredDigits.size() >= 8 || isUnlocked) return;
    else if (enteredDigits.size() >= correctDigits.size() && showDigitCount) return;
    enteredDigits.add(digit);
  }

  public void checkUnlock(){
    boolean isCorrect = true;
    if(enteredDigits.size() == correctDigits.size()){
      for(int i = 0; i < enteredDigits.size(); i++){
        if(enteredDigits.get(i) != correctDigits.get(i)){
          isCorrect = false;
          break;
        }
      }
    } else {
      isCorrect = false;
    }

    if(isCorrect){
      isUnlocked = true;
      action.execute();
    }
  }
}
