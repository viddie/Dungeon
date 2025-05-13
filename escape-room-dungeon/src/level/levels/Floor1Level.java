package level.levels;

import core.Entity;
import core.Game;
import core.level.Tile;
import core.level.elements.tile.DoorTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.systems.LevelSystem;
import core.utils.Point;
import entities.Deco;
import entities.DecoFactory;
import entities.DecoGroup;
import entities.TriggerFactory;
import level.EscapeRoomLevel;
import modules.keypad.KeypadComponent;
import modules.keypad.KeypadFactory;
import modules.showimage.ShowImageFactory;
import puzzles.floor1.Floor1LeversPuzzle;
import starter.EscapeRoomDungeon;
import utils.GameState;
import utils.SoundManager;
import utils.Sounds;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Floor1Level extends EscapeRoomLevel {

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout      The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   */
  public Floor1Level(LevelElement[][] layout, DesignLabel designLabel, Map<String, Point> namedPoints) {
    super(layout, designLabel, namedPoints);
  }

  @Override
  protected void onFirstTick() {
    Point pos = getPoint("levers");
    Floor1LeversPuzzle puzzle = new Floor1LeversPuzzle(pos, GameState.playerNumber());
    puzzle.load(this);

    Point notePos = getPoint("note");
    if(notePos != null){
      Entity showImage = ShowImageFactory.createShowImage(getPoint("note"), "objects/note/note-sprite.png", "images/note-tutorial.png", 1f);
      Game.add(showImage);
    }

    Point keypadPos = getPoint("keypad");
    Point doorPos = getPoint("keypad-door");
    Tile t = LevelSystem.level().tileAt(doorPos);
    LevelSystem.level().changeTileElementType(t, LevelElement.DOOR);
    DoorTile door = (DoorTile)LevelSystem.level().tileAt(doorPos);
    door.close();

    List<Integer> correctDigits = Arrays.asList(2, 3, 4);
    Entity keypad = KeypadFactory.createKeypad(keypadPos, correctDigits, (fromLoad) -> {
      door.open();
      if(!fromLoad) SoundManager.playSound(Sounds.DoorOpened);
    }, true);
    Game.add(keypad);

    Game.add(TriggerFactory.createTrigger(getPoint("trigger-test"), 3, 1, (e, o, d) -> {
      EscapeRoomDungeon.LOGGER.info("Entered trigger");
      if(door.isOpen()){
        door.close();
        SoundManager.playSound(Sounds.DoorClosedHard);
      }
    }, null));

    KeypadComponent kc = keypad.fetchOrThrow(KeypadComponent.class);
    kc.serializeId = "tutorial";
    kc.load();


    //Deco
    listPoints("rubble").forEach(tuple -> Game.add(DecoFactory.createDeco(tuple.a(), DecoGroup.Rubble.getOne(tuple.b()+1))));
    listPoints("campfire").forEach(tuple -> Game.add(DecoFactory.createDeco(tuple.a(), Deco.Campfire)));
    listPoints("chains").forEach(tuple -> Game.add(DecoFactory.createDeco(tuple.a(), DecoGroup.Chains.getOne(tuple.b()*2+2))));
    listPoints("stonepillar").forEach(tuple -> Game.add(DecoFactory.createDeco(tuple.a(), Deco.StonePillar0)));
    listPoints("fakepillar").forEach(tuple -> Game.add(DecoFactory.createDeco(tuple.a(), DecoGroup.F1FakePillars.getOne(tuple.b()))));
    Game.add(DecoFactory.createDeco(getPoint("stonealtar"), Deco.StoneAltar));
  }

  @Override
  protected void onTick() {

  }
}
