package modules.keypad;

import com.badlogic.gdx.utils.Array;
import core.Component;
import core.Entity;
import core.utils.IVoidFunction;
import starter.EscapeRoomDungeon;
import utils.GameState;
import utils.ISavable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class KeypadComponent implements Component, ISavable {

  public final List<Integer> correctDigits;
  public final List<Integer> enteredDigits;
  public boolean isUIOpen = false;
  public boolean isUnlocked = false;
  public boolean showDigitCount;
  public Consumer<Boolean> action;
  public Entity overlay;
  public String serializeId = null;

  public KeypadComponent(List<Integer> correctDigits, Consumer<Boolean> action, boolean showDigitCount){
    this.correctDigits = correctDigits;
    this.enteredDigits = new ArrayList<>();
    this.action = action;
    this.showDigitCount = showDigitCount;
    GameState.addSaveCallback(this);
  }
  public KeypadComponent(List<Integer> correctDigits, Consumer<Boolean> action){
    this(correctDigits, action, true);
  }
  public KeypadComponent(){
    this(new ArrayList<>(), null);
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

  public void checkUnlock(boolean fromLoad){
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
      action.accept(fromLoad);
    }
  }
  public void checkUnlock(){
    checkUnlock(false);
  }

  @Override
  public void save(){
    if(serializeId == null) return;
    GameState.setResourceObject("keypad_"+serializeId, enteredDigits);
  }

  @Override
  public void load() {
    if(serializeId == null) return;
    Array<Integer> empty = new Array<>();
    Array<Integer> digits = GameState.getResourceObject("keypad_"+serializeId, empty);
    digits.forEach(enteredDigits::add);
    checkUnlock(true);
  }
}
