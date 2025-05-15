package puzzles.floor3;

import components.LeverComponent;
import core.Entity;
import core.Game;
import core.System;
import core.level.elements.tile.DoorTile;
import core.utils.Point;
import entities.Deco;
import entities.DecoFactory;
import entities.LeverFactory;
import level.utils.ITickable;
import modules.keypad.KeypadFactory;
import puzzles.PuzzleController;
import utils.ICommand;
import utils.SoundManager;
import utils.Sounds;

import java.util.Arrays;
import java.util.List;

public class Floor3SortingMachinePuzzle extends PuzzleController implements ITickable {

  private static final List<Integer> KEYPAD_CODE = Arrays.asList(1, 2, 3, 4, 5);

  private DoorTile door;
  private SortingMachine sortingMachine;
  private LeverComponent[] bitLevers = new LeverComponent[3];

  public Floor3SortingMachinePuzzle(Point p, int player) { super(p, player); }

  @Override
  public void onTick(boolean isFirstTick) {
    if(sortingMachine != null) sortingMachine.onTick(isFirstTick);
  }

  @Override
  public void loadResources(int player) {

  }

  @Override
  public void loadEntities(int player) {
    if(player == 2){
      //Has the sorting machine
      sortingMachine = new SortingMachine(position);
      Entity lever = LeverFactory.createLever(getPoint("main-lever"), new ICommand() {
        @Override
        public void execute() { sortingMachine.startSorting(); }
        @Override
        public void undo() { sortingMachine.endSorting(); }
      });
      Game.add(lever);

      Point vaseStart = position.add(-0.25f, 2f);
      for(int i = 0; i < 16; i++){
        Game.add(DecoFactory.createDeco(vaseStart.add(i * 0.5f, 0), Deco.VaseFull));
      }

      //Bit levers
      for(int i = 0; i < 3; i++){
        Entity bitLever = LeverFactory.createLever(getPoint("bit-levers").add(i*2, 0), new ICommand() {
          @Override
          public void execute() { checkSortAlgorithm(); }
          @Override
          public void undo() { checkSortAlgorithm(); }
        });
        bitLevers[i] = bitLever.fetchOrThrow(LeverComponent.class);
        Game.add(bitLever);
      }

      //Keypad and door
      door = (DoorTile) parent.tileAt(getPoint("door"));
      door.close();
      Entity keypad = KeypadFactory.createKeypad(getPoint("door-keypad"), KEYPAD_CODE, (fromLoader) -> {
        door.open();
        if(!fromLoader) SoundManager.playSound(Sounds.DoorOpened);
      }, true, "f3_p2_1");
      Game.add(keypad);

    } else {

    }
  }

  @Override
  public void unloadEntities(int player) {

  }

  @Override
  public List<System> createSystems(int player) {
    return null;
  }


  public void checkSortAlgorithm(){
    int selected = (bitLevers[0].isOn() ? 1 : 0) +
      (bitLevers[1].isOn() ? 2 : 0) +
      (bitLevers[2].isOn() ? 4 : 0);
    sortingMachine.setSelectedStrategy(selected);
  }
}
