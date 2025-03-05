package level.levels;

import core.Entity;
import core.Game;
import core.level.elements.tile.DoorTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import level.EscapeRoomLevel;
import modules.keypad.KeypadFactory;
import modules.showimage.ShowImageFactory;
import modules.showimage.ShowImageText;
import utils.GameState;
import utils.SoundManager;
import utils.Sounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Floor2Level extends EscapeRoomLevel {

  DoorTile door;

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout      The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   * @param namedPoints A map of names to points in the level
   */
  public Floor2Level(LevelElement[][] layout, DesignLabel designLabel, Map<String, Point> namedPoints) {
    super(layout, designLabel, namedPoints);
  }

  @Override
  protected void onFirstTick() {
    door = (DoorTile) Game.currentLevel().tileAt(getPoint("door"));
    door.close();
    ((DoorTile) Game.currentLevel().tileAt(getPoint("old-door"))).close();

    List<Integer> correctDigits = Arrays.asList(5, 3, 7, 2, 4, 1, 6);
    Entity keypad = KeypadFactory.createKeypad(getPoint("keypad"), correctDigits, (fromLoad) -> {
      door.open();
      if(!fromLoad) SoundManager.playSound(Sounds.DoorOpened);
    }, true);
    Game.add(keypad);

    if(GameState.playerNumber() == 1){
      Game.add(ShowImageFactory.createShowImage(getPoint("note"), "objects/note/book-closed-brown.png", "images/git-book-1.png", (e, o) -> {}, 0.95f));

    } else {
      //Code: 5372416
      List<String> gitCommands = new ArrayList<>();
      gitCommands.add("git reflog");
      gitCommands.add("git rebase geheimer-pfad");
      gitCommands.add("git commit --amend --no-edit");
      gitCommands.add("git push --force");
      gitCommands.add("git checkout uralter-zweig");
      gitCommands.add("git merge verlorener-ast");
      gitCommands.add("git reset --hard HEAD~2");

      for(int i = 0; i < 7; i++){
        Point pos = getPoint("note-"+i);
        ShowImageText sit = new ShowImageText(gitCommands.get(i), 1.8f);
        Game.add(ShowImageFactory.createShowImage(pos, "objects/note/note-"+(i+1)+".png", "images/note-horizontal-blank.png", (e, o) ->{}, 1.2f, 1f, sit));
      }
    }
  }

  @Override
  protected void onTick() {

  }
}
