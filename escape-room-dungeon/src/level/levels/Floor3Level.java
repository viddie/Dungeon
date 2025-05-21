package level.levels;

import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.level.elements.tile.DoorTile;
import core.level.utils.DesignLabel;
import core.level.utils.LevelElement;
import core.utils.Point;
import entities.Deco;
import entities.DecoFactory;
import entities.DecoGroup;
import entities.SpikesFactory;
import level.EscapeRoomLevel;
import modules.dialog.DialogConfig;
import modules.dialog.DialogTriggerFactory;
import modules.keypad.KeypadFactory;
import modules.showimage.ShowImageFactory;
import modules.showimage.ShowImageText;
import puzzles.floor3.Floor3SortingMachinePuzzle;
import systems.TickableSystem;
import utils.Constants;
import utils.GameState;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Floor3Level extends EscapeRoomLevel {

  private static final List<Integer> BOOKSHELF_CODE = Arrays.asList(8, 1, 9, 2);
  private static final int BOOKSHELF_MOVE_FRAMES = 120;
  private static final int[] STEPPING_PATH = new int[] {
    1,
    10, 11, 13, 14, 15, 16, 17, 18, 19,
    20, 23, 29,
    30, 31, 32, 33, 35, 36, 37, 39,
    45, 47, 48, 49,
    50, 51, 52, 53, 54, 55,
    60,
    70
  };

  DoorTile door;
  private Entity movableBookshelf;
  private float bookshelfMoveProgress = -1f;
  private boolean revealedPassage = false;

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
        int startIndex = index == 0 ? 0 : index == 1 ? 5 : 8;
        for(int i = 0; i < maxBooks; i++){
          boolean isKeyBook = index == 2 && i == 2;
          int combinedIndex = startIndex + i;
          String img = isKeyBook ? "images/sorting-book.png" : "images/fake-books/sorting-book-fake-"+(combinedIndex+1)+".png";
          ShowImageText text = null;
//          ShowImageText text = isKeyBook ? null : new ShowImageText("In diesem Bücherregal ist\nkein interessantes Buch.");
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
          int index = y*10 + x;
          boolean isSave = Arrays.stream(STEPPING_PATH).anyMatch((i) -> i == index);
          Point spikePos = stepStart.add(x, y);
          Game.add(SpikesFactory.createSpikes(spikePos, false, !isSave, deathPoint));
        }
      }

      //Dialog
      Game.add(DialogTriggerFactory.createDialogTrigger(getPoint("dialog0"), 1, 4, new DialogConfig("-----", "Als du den Raum betrittst, bemerkst du rechts von dir eine grosse, schwarze Fläche auf dem Boden.", "Du weisst nicht, was es ist, aber es sieht gefährlich aus...")));
    }

    String escapeImage = GameState.playerNumber() == 2 ? "images/path-note.png" : "images/escape-note.png";
    Entity escapeNote = ShowImageFactory.createShowImage(getPoint("escape-note"), "objects/note/note-sprite.png", escapeImage, null, 1.25f, 1.25f);
    Game.add(escapeNote);

    //TODO: change the important book images for both players
    String importantBookPath = GameState.playerNumber() == 2 ? "images/git-book-1.png" : "images/sorting-book.png";
    for(int i = 0; i < 4; i++){
      int bookNumber = ((GameState.playerNumber()) % 2) * 4 + i + 1;
      String img = "images/fake-books/sorting-book-fake-"+bookNumber+".png";
      Entity bookshelf = ShowImageFactory.createShowImage(getPoint("end-bookshelves").add(i*2, 0), Deco.BookshelfLarge, img, null, 0.95f, 1.25f, null);
      if(i == 2){
        movableBookshelf = bookshelf;
      }
      Game.add(bookshelf);
    }

    Entity keypad = KeypadFactory.createKeypad(getPoint("bookshelf-keypad"), BOOKSHELF_CODE, (fromLoader) -> {
      moveBookshelf();
    }, true, "f3_p2_2");
    Game.add(keypad);


    //Deco
    listPoints("rubble").forEach(tuple -> Game.add(DecoFactory.createDeco(tuple.a(), DecoGroup.Rubble.getOne(tuple.b()))));
    listPoints("chains").forEach(tuple -> Game.add(DecoFactory.createDeco(tuple.a(), DecoGroup.Chains.getOne(tuple.b()*2+2))));
    listPoints("campfire").forEach(tuple -> Game.add(DecoFactory.createDeco(tuple.a(), Deco.Campfire)));
    listPoints("bookshelf").forEach(tuple -> Game.add(DecoFactory.createDeco(tuple.a(), Deco.BookshelfLarge)));
    listPoints("stonepillar").forEach(tuple -> Game.add(DecoFactory.createDeco(tuple.a(), DecoGroup.StonePillars.getOne(tuple.b()))));
  }

  @Override
  protected void onTick() {
    if(bookshelfMoveProgress == -1f || bookshelfMoveProgress == 2f) return;

    bookshelfMoveProgress += 1f / BOOKSHELF_MOVE_FRAMES;

    Point bookshelfPoint = Constants.offset(getPoint("end-bookshelves"));
    PositionComponent pc = movableBookshelf.fetchOrThrow(PositionComponent.class);

    if(bookshelfMoveProgress < 1f){
      Point startingPos = bookshelfPoint.add(2 * 2, 0);
      Point newPos = startingPos.add(0, bookshelfMoveProgress * -1);
      pc.position(newPos);

    } else if (bookshelfMoveProgress < 2f){
      Point startingPos = bookshelfPoint.add(2 * 2, -1);
      Point newPos = startingPos.add((bookshelfMoveProgress - 1) * -2, 0);
      pc.position(newPos);

      if(!revealedPassage){
        revealPassage();
      }

    } else {
      bookshelfMoveProgress = 2f;
      Point endPos = bookshelfPoint.add(2, -1);
      pc.position(endPos);
    }
  }

  private void moveBookshelf(){
    bookshelfMoveProgress = 0f;
  }
  private void revealPassage(){
    revealedPassage = true;
    //Edit level to show passage
    Point anchor = getPoint("passage-anchor");

    //Update all other tiles except the first 2 wall tiles twice
    changeTileElementType(tileAt(anchor.add(-1, 0)), LevelElement.WALL);
    changeTileElementType(tileAt(anchor.add(1, 0)), LevelElement.WALL);

    for(int i = 0; i < 2; i++){
      changeTileElementType(tileAt(anchor), LevelElement.FLOOR);

      changeTileElementType(tileAt(anchor.add(-1, 1)), LevelElement.WALL);
      changeTileElementType(tileAt(anchor.add(0, 1)), LevelElement.FLOOR);
      changeTileElementType(tileAt(anchor.add(1, 1)), LevelElement.WALL);

      changeTileElementType(tileAt(anchor.add(-1, 2)), LevelElement.WALL);
      changeTileElementType(tileAt(anchor.add(0, 2)), LevelElement.FLOOR);
      changeTileElementType(tileAt(anchor.add(1, 2)), LevelElement.WALL);

      changeTileElementType(tileAt(anchor.add(-1, 3)), LevelElement.WALL);
      changeTileElementType(tileAt(anchor.add(0, 3)), LevelElement.EXIT);
      changeTileElementType(tileAt(anchor.add(1, 3)), LevelElement.WALL);

      changeTileElementType(tileAt(anchor.add(-1, 4)), LevelElement.WALL);
      changeTileElementType(tileAt(anchor.add(0, 4)), LevelElement.WALL);
      changeTileElementType(tileAt(anchor.add(1, 4)), LevelElement.WALL);
    }
  }
}
