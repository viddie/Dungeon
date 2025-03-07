package utils;

import com.badlogic.gdx.Gdx;
import starter.EscapeRoomDungeon;

public class InputHandler {

  public static boolean isKeyJustPressed(int key, boolean ignoreDialogs){
    return Gdx.input.isKeyJustPressed(key) && (ignoreDialogs || EscapeRoomDungeon.findTopmostUI(true) == null);
  }
  public static boolean isKeyJustPressed(int key){
    return isKeyJustPressed(key, false);
  }

  public static boolean isKeyPressed(int key, boolean ignoreDialogs){
    return Gdx.input.isKeyPressed(key) && (ignoreDialogs || EscapeRoomDungeon.findTopmostUI(true) == null);
  }
  public static boolean isKeyPressed(int key){
    return isKeyPressed(key, false);
  }

}
