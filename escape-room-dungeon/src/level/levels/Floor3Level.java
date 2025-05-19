package level.levels;

import components.VicinityComponent;
import components.commands.TintEntityCommand;
import contrib.components.InteractionComponent;
import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.level.elements.tile.DoorTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import entities.Deco;
import entities.DecoFactory;
import entities.SpikesFactory;
import level.EscapeRoomLevel;
import modules.keypad.KeypadFactory;
import modules.showimage.ShowImageComponent;
import modules.showimage.ShowImageFactory;
import modules.showimage.ShowImageText;
import puzzles.floor1.Floor1LeversPuzzle;
import puzzles.floor3.Floor3SortingMachinePuzzle;
import systems.TickableSystem;
import utils.Constants;
import utils.GameState;
import utils.SoundManager;
import utils.Sounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Floor3Level extends EscapeRoomLevel {

  private static final List<Integer> BOOKSHELF_CODE = Arrays.asList(1, 2, 3, 4);
  private static final int BOOKSHELF_MOVE_FRAMES = 120;

  DoorTile door;
  private Entity movableBookshelf;
  private float bookshelfMoveProgress = -1f;

  /**
   * Constructs a new DevDungeonLevel with the given layout, design label, and custom points.
   *
   * @param layout      The layout of the level, represented as a 2D array of LevelElements.
   * @param designLabel The design label of the level.
   * @param namedPoints A map of names to points in the level
   */
  public Floor3Level(LevelElement[][] layout, DesignLabel designLabel, Map<String, Point> namedPoints) {
    super(layout, designLabel, namedPoints);
  }

  @Override
  protected void onFirstTick() {
    if(GameState.playerNumber() == 2){
      Floor3SortingMachinePuzzle f3SortingPuzzle = new Floor3SortingMachinePuzzle(getPoint("sorting-machine"), GameState.playerNumber());
      f3SortingPuzzle.load(this);
      TickableSystem.registerInLevel(f3SortingPuzzle);

    } else {
      listPoints("bookshelves").forEach((tuple) -> {
        Point pos = tuple.a();
        int index = tuple.b();
        int maxBooks = index == 0 ? 5 : 3;
        for(int i = 0; i < maxBooks; i++){
          boolean isKeyBook = index == 2 && i == 2;
          String img = isKeyBook ? "images/git-book-1.png" : "images/note-horizontal-blank.png";
          ShowImageText text = isKeyBook ? null : new ShowImageText("In diesem Bücherregal ist\nkein interessantes Buch.");
          Entity bookshelf = ShowImageFactory.createShowImage(pos.add(i*2, 0), Deco.BookshelfLarge, img, null, 0.95f, 1.25f, text);
          Game.add(bookshelf);
        }
      });

      Point stepStart = getPoint("step-grid-start");
      Point stepEnd = getPoint("step-grid-end");
      Point deathPoint = getPoint("step-send");
      int yDiff = (int)(stepEnd.y - stepStart.y);
      for(int y = 0; y <= yDiff; y++){
        for(int x = 0; x < 10; x++){
          Point spikePos = stepStart.add(x, y);
//          Game.add(SpikesFactory.createSpikes(spikePos, (x+y) % 2 == 0, true, deathPoint));
          Game.add(SpikesFactory.createSpikes(spikePos, false, true, deathPoint));
        }
      }
    }

    //TODO: change the important book images for both players
    String importantBookPath = GameState.playerNumber() == 2 ? "images/git-book-1.png" : "images/git-book-1.png";
    for(int i = 0; i < 4; i++){
      String img = i == 2 ? importantBookPath : "images/note-horizontal-blank.png";
      ShowImageText text = i == 2 ? null : new ShowImageText("In diesem Bücherregal ist\nkein interessantes Buch.");
      Entity bookshelf = ShowImageFactory.createShowImage(getPoint("end-bookshelves").add(i*2, 0), Deco.BookshelfLarge, img, null, 0.95f, 1.25f, text);
      if(i == 2){
        movableBookshelf = bookshelf;
      }
      Game.add(bookshelf);
    }

    Entity keypad = KeypadFactory.createKeypad(getPoint("bookshelf-keypad"), BOOKSHELF_CODE, (fromLoader) -> {
      moveBookshelf();
    }, true, "f3_p2_2");
    Game.add(keypad);
  }

  @Override
  protected void onTick() {
    if(bookshelfMoveProgress == -1f || bookshelfMoveProgress == 2f) return;

    bookshelfMoveProgress += 1f / BOOKSHELF_MOVE_FRAMES;

    Point bookshelfPoint = Constants.offset(getPoint("end-bookshelves"));

    if(bookshelfMoveProgress < 1f){
      Point startingPos = bookshelfPoint.add(2 * 2, 0);
      Point newPos = startingPos.add(0, bookshelfMoveProgress * -1);
      movableBookshelf.fetchOrThrow(PositionComponent.class).position(newPos);

    } else if (bookshelfMoveProgress < 2f){
      Point startingPos = bookshelfPoint.add(2 * 2, -1);
      Point newPos = startingPos.add((bookshelfMoveProgress - 1) * -2, 0);
      movableBookshelf.fetchOrThrow(PositionComponent.class).position(newPos);

    } else {
      bookshelfMoveProgress = 2f;
      Point endPos = bookshelfPoint.add(2, -1);
      movableBookshelf.fetchOrThrow(PositionComponent.class).position(endPos);
    }
  }

  private void moveBookshelf(){
    bookshelfMoveProgress = 0f;
  }
}
